package org.vaadin.firitin.components.button;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.streams.ElementRequestHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * A button that, when clicked, generates a PDF on the server and
 * immediately opens the browser's native print dialog for it.
 * <p>
 * The PDF is produced by a {@link PdfWriterCallback} supplied at
 * construction time; implementations write PDF bytes to the given
 * {@link OutputStream}. The bytes are streamed to a hidden
 * {@code <iframe>} whose {@code contentWindow.print()} is invoked
 * once loading completes -- the classic hidden-iframe print trick
 * that web applications have used for decades.
 * <p>
 * A new iframe is created on every click and the previous one is
 * removed first, so repeated prints do not accumulate DOM nodes.
 */
public class PrintPdfButton extends VButton {

    private Element iframe;

    /**
     * Creates a print-PDF button with the given text label. The default
     * printer icon is removed.
     *
     * @param buttonText        the button label
     * @param pdfWriterCallback invoked on click to write PDF bytes to
     *                          the response output stream
     */
    public PrintPdfButton(String buttonText, PdfWriterCallback pdfWriterCallback) {
        this(pdfWriterCallback);
        setText(buttonText);
        setIcon(null);
    }

    /**
     * Creates an icon-only print-PDF button showing the default
     * {@link VaadinIcon#PRINT} icon.
     *
     * @param pdfWriterCallback invoked on click to write PDF bytes to
     *                          the response output stream
     */
    public PrintPdfButton(PdfWriterCallback pdfWriterCallback) {
        super(VaadinIcon.PRINT);
        addClickListener(event -> {
            if(iframe != null) {
                // clear old element
                iframe.removeFromParent();
            }
            // This is typically done in JS, but here using Element API from Vaadin
            iframe = new Element("iframe");
            // hidden, just a hack to print
            iframe.getStyle().setDisplay(Style.Display.NONE);
            iframe.setAttribute("src", (ElementRequestHandler) (request, response, session, owner) -> {
                writePdf(pdfWriterCallback, response);
            });
            iframe.executeJs("""
                    const iframe = this;
                    iframe.onload = function() {
                        setTimeout(function() {
                            iframe.focus();
                            iframe.contentWindow.print();
                        }, 1);
                    };
                    """);
            // Add it "somewhere", here adding in button
            getElement().appendChild(iframe);
        });

    }

    /**
     * Writes the PDF response served to the hidden iframe. Sets the
     * {@code application/pdf} content type and delegates payload
     * generation to the supplied {@link PdfWriterCallback}.
     * <p>
     * Subclasses can override this to set additional response headers
     * (for example {@code Content-Disposition} to influence the
     * suggested filename) or to wrap the output stream.
     *
     * @param pdfWriterCallback the callback that produces the PDF bytes
     * @param response          the Vaadin response to write to
     * @throws IOException if writing to the response fails
     */
    protected void writePdf(PdfWriterCallback pdfWriterCallback, VaadinResponse response) throws IOException {
        response.setContentType("application/pdf");
        pdfWriterCallback.writePdf(response.getOutputStream());
    }

    /**
     * Callback that writes a PDF document to an output stream. Typically
     * implemented with a PDF library such as Apache PDFBox, OpenPDF or
     * iText.
     */
    public interface PdfWriterCallback extends Serializable {
        /**
         * Writes the PDF document to the given output stream.
         *
         * @param outputStream the stream to write PDF bytes to
         * @throws IOException if writing fails
         */
        void writePdf(OutputStream outputStream) throws IOException;
    }
}