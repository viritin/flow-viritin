package org.vaadin.firitin;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Inline;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.TargetElement;
import com.vaadin.flow.server.AppShellSettings;

@Push
public class AppShell implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        // theme-color tints the mobile browser's top chrome / status-bar area so
        // it blends with the page instead of showing a different shade behind the
        // dynamic island (the non-PWA equivalent of a PWA status-bar style).
        // Values approximate the Aura page background (light/dark). NOTE: this
        // test app switches theme at runtime via the ?theme/?dark params, which
        // does not change prefers-color-scheme, so on a light OS the dark value
        // only kicks in when the OS itself is dark.
        settings.addInlineWithContents(TargetElement.HEAD, Inline.Position.APPEND,
                "<meta name=\"theme-color\" media=\"(prefers-color-scheme: light)\" content=\"#f7f9fa\">"
                        + "<meta name=\"theme-color\" media=\"(prefers-color-scheme: dark)\" content=\"#1a1d23\">",
                Inline.Wrapping.NONE);
    }
}
