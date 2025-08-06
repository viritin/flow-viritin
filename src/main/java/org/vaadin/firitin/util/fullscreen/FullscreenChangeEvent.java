package org.vaadin.firitin.util.fullscreen;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;


public class FullscreenChangeEvent extends ComponentEvent<UI> {

    private final boolean fullscreen;

    public FullscreenChangeEvent(UI source, boolean fullscreen) {
        super(source, true);
        this.fullscreen = fullscreen;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }
}
