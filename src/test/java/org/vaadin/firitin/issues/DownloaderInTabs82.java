package org.vaadin.firitin.issues;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.DynamicFileDownloader;

import java.io.IOException;
import java.io.OutputStream;

@Route
public class DownloaderInTabs82 extends VerticalLayout {
    public DownloaderInTabs82() {
        add(createRegularExample(), createTabExample());
    }

    private Div createRegularExample() {

        var button = new Button("Normal (should work)");

        var downloader = new DynamicFileDownloader();
        downloader.withFileNameGenerator(request -> "Simple-doc.docx").setFileHandler(this::fileHandler);

        downloader.add(button);

        return new Div(downloader);
    }

    private Div createTabExample() {
        var tabs = new TabSheet();

        var button1 = new Button("In tabsheet (should work)");
        var button2 = new Button("In tabsheet (should not work)");

        var downloader2 = new DynamicFileDownloader(){{

        }


            @Override
            public void setEnabled(boolean enabled) {
                System.out.println("setEnabled: " + enabled);
                super.setEnabled(enabled);
            }

            @Override
            public void onEnabledStateChanged(boolean enabled) {
                System.out.println("onEnabledStateChanged: " + enabled);
                super.onEnabledStateChanged(enabled);
            }

            @Override
            protected void onAttach(AttachEvent attachEvent) {
                boolean enabled = isEnabled();
                System.out.println("onAttach: " + attachEvent + " enabled: " + enabled);
                super.onAttach(attachEvent);
            }

            @Override
            protected void onDetach(DetachEvent detachEvent) {
                System.out.println("onDetach: " + detachEvent);
                super.onDetach(detachEvent);
            }
        };
        downloader2.withFileNameGenerator(request -> "Simple-doc.docx").setFileHandler(this::fileHandler);
        downloader2.add(button2);

        tabs.add(new Tab("First / initally open tab"), new Paragraph("This is a test"));
        tabs.add(new Tab("Second / initially hidden tab"), downloader2);
        tabs.setWidthFull();
        tabs.setHeight("400px");

        return new Div(tabs);
    }

    private void fileHandler(OutputStream outputStream) {
        try {
            outputStream.write("Hello world".getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}