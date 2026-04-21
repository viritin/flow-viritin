## PrintPdfButton

`PrintPdfButton` is a ready-made button that opens the browser's
native print dialog for a PDF you generate on the server. You supply
a callback that writes PDF bytes to an `OutputStream`; the component
takes care of streaming the bytes to the browser and triggering
print via the classic hidden-iframe trick that web apps have used
for decades.

### When to use it

* You want to print a server-side generated PDF (invoice, report,
  label) directly from a Vaadin view without first downloading it
  to disk.
* You already have, or are happy to add, a PDF library such as
  Apache PDFBox, OpenPDF, iText, Flying Saucer, etc.

If you only need to offer the PDF as a download, use
`DynamicFileDownloader` instead.

### Basic usage

Pass a lambda that writes PDF bytes to the given `OutputStream`. The
stream is connected to the response body, so whatever you write ends
up in the browser's print preview. The callback may throw
`IOException`, so you can delegate straight to PDF-library calls
without extra try/catch plumbing:

```java
add(new PrintPdfButton(out ->
    writePdf("It is " + LocalDateTime.now(), out)));
```

The default constructor gives you a printer-icon-only button. A
second constructor adds a text label (and drops the icon):

```java
add(new PrintPdfButton("Print receipt", out -> writePdf(receipt, out)));
```

### Generating the PDF

Any library that can write to an `OutputStream` works. The following
example uses Apache PDFBox:

```java
private static void writePdf(String content, OutputStream out) throws IOException {
    PDDocument document = new PDDocument();
    PDPage page = new PDPage(PDRectangle.A4);
    document.addPage(page);
    PDPageContentStream contentStream =
        new PDPageContentStream(document, page);
    contentStream.beginText();
    contentStream.newLineAtOffset(20, 700);
    contentStream.setFont(
        new PDType1Font(Standard14Fonts.FontName.COURIER), 16);
    contentStream.showText(content);
    contentStream.endText();
    contentStream.close();
    document.save(out);
    document.close();
}
```

PDFBox is not a dependency of Flow Viritin -- add your PDF library of
choice to your own project.

### Customising the response

For extra control over the HTTP response -- for instance, to set a
`Content-Disposition` header that influences the suggested filename
in the browser's print dialog -- subclass and override the protected
`writePdf(PdfWriterCallback, VaadinResponse)` hook:

```java
PrintPdfButton button = new PrintPdfButton(out -> writePdf(receipt, out)) {
    @Override
    protected void writePdf(PdfWriterCallback cb, VaadinResponse response) throws IOException {
        response.setHeader("Content-Disposition", "inline; filename=\"receipt.pdf\"");
        super.writePdf(cb, response);
    }
};
```

### How it works

On click, the component:

1. Creates a hidden `<iframe>` whose `src` is served by a dynamic
   Vaadin `ElementRequestHandler`.
2. Writes `application/pdf` to the response via your callback.
3. Once the iframe finishes loading, calls `contentWindow.print()`
   from a small inline script.

A new iframe is created for every click; the previous one is removed
first, so repeated prints do not accumulate stale DOM nodes.

### Notes and caveats

* The browser's print dialog pops up, not a download. If the user
  cancels, nothing is saved.
* Popup- or iframe-blocking browser extensions can interfere; the
  technique itself is well established and works in current Chrome,
  Firefox, Edge and Safari.
* The suggested filename in the print dialog is whatever the browser
  picks from the iframe URL by default; override `writePdf` and set
  a `Content-Disposition` header to control it explicitly.
