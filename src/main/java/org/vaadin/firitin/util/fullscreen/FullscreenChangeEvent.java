package org.vaadin.firitin.util.fullscreen;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;


/**
 * @deprecated Part of the deprecated {@link FullScreen} helper. Use the native
 *             {@code com.vaadin.flow.component.fullscreen.Fullscreen} and observe
 *             state changes via {@code Fullscreen.stateSignal()}.
 */
@Deprecated(since = "3.6", forRemoval = true)
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
