package org.vaadin.firitin.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.firitin.util.WebStorage;

import java.util.Collection;
import java.util.logging.Logger;

public class SessionExpirationWarningDialog extends Div {

    private final Button extendButton;
    private int warningThreshold = 10*60; // 10 minutes
    private Runnable destroyAction;

    private String beforeMsg = "Session expires in ";
    private String afterMsg = " seconds.";

    public SessionExpirationWarningDialog() {
        setId("session-expiration-dialog");
        basicInlineStyling();

        Div msg = new Div();
        msg.setId("session-expiration-dialog-msg");
        add(msg);

        extendButton = new Button("Extend session...", e -> {/* NOOP */});
        extendButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        add(extendButton);

    }

    protected void basicInlineStyling() {
        getStyle().setPosition(Style.Position.ABSOLUTE);
        getStyle().setDisplay(Style.Display.NONE);
        getStyle().setTop("1em");
        getStyle().setRight("1em");
        getStyle().setBackground("white");
        getStyle().setPadding("1em");
        getStyle().setColor("red");
        getStyle().setBorder("1px solid red");
        getStyle().setZIndex(1);
    }

    public String getBeforeMsg() {
        return beforeMsg;
    }

    public void setBeforeMsg(String beforeMsg) {
        this.beforeMsg = beforeMsg;
    }

    public String getAfterMsg() {
        return afterMsg;
    }

    public void setAfterMsg(String afterMsg) {
        this.afterMsg = afterMsg;
    }

    public Button getExtendButton() {
        return extendButton;
    }

    public void setWarningThreshold(int warningThreshold) {
        this.warningThreshold = warningThreshold;
    }

    public int getWarningThreshold() {
        return warningThreshold;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        int maxInactiveInterval = attachEvent.getSession().getSession().getMaxInactiveInterval();
        boolean hasDestroyAction = destroyAction != null;
        if(hasDestroyAction) {
            maxInactiveInterval -= 10;
        };
        WebStorage.setItem("maxInactiveInterval", String.valueOf(maxInactiveInterval));

        registerActivityTracker();

        getElement().executeJs("""
                var dialog = this;
                var hasDestroyAction = %b;
                var msg = this.querySelector('#session-expiration-dialog-msg');
                localStorage.setItem("lastActive", Date.now());
                
                setInterval(function() {
                    var maxInactiveInterval = localStorage.getItem('maxInactiveInterval');
                    var lastActive = parseInt(localStorage.getItem('lastActive'));
                    if(maxInactiveInterval && lastActive) {
                    var now = Date.now();
                    var sessionRemaining = maxInactiveInterval*1000 - (now - lastActive);
                    if(sessionRemaining < %d*1000 ) {
                        msg.textContent = "%s" + Math.round(sessionRemaining/1000) + "%s";
                        dialog.style.display = 'block';
                        if(hasDestroyAction && sessionRemaining < 0) {
                            debugger;
                            dialog.$server.beforeDestroy();
                        }
                    } else {
                        dialog.style.display = 'none';
                    }
                    }
                }, 1000);
        
        """.formatted(hasDestroyAction, warningThreshold, beforeMsg, afterMsg));
    }

    static boolean uiInitListenerRegistered = false;

    private static void registerActivityTracker() {
        UI ui = UI.getCurrent();
        if(isLastActiveTrackerSet(ui)) {
            // already registered
            return;
        }
        // Also register for other UIs, regardless if they use the component
        Collection<UI> uIs = ui.getSession().getUIs();
        for (UI u : uIs) {
            registerForUI(u, ui);
        }
        // register for all new UIs
        if(!uiInitListenerRegistered) {
            uiInitListenerRegistered = true;
            ui.getSession().getService().addUIInitListener(e -> {
                registerForUI(e.getUI(), ui);
            });
        }
    }

    private static void registerForUI(UI u, UI current) {
        Command r = () -> {
            Logger.getAnonymousLogger().info("Registering for UI " + u.getUIId());
            if(!isLastActiveTrackerSet(u)) {
                ComponentUtil.setData(u, "lastActiveTrackerSet", true);
                u.getElement().executeJs("""
                    localStorage.setItem("lastActive", Date.now());
                    Vaadin.connectionState.addStateChangeListener( s => {
                        localStorage.setItem("lastActive", Date.now());
                    });
                    """);
            }
        };
        if(current == u) {
            r.execute();
        } else {
            u.access(r);
        }
    }

    private static boolean isLastActiveTrackerSet(UI u) {
        return ComponentUtil.getData(u, "lastActiveTrackerSet") != null;
    }

    /**
     * Override this method to set a custom action to be executed when the session is about to be destroyed.
     * Note, that setting this action shortens the session timeout with 10 seconds.
     */
    public void setSessionDestroyAction(Runnable action) {
        if(isAttached()) {
            throw new IllegalStateException("Session destroy action must be set before the component is attached");
        }
        this.destroyAction = action;
    }

    @ClientCallable
    public void beforeDestroy() {
        destroyAction.run();
        VaadinSession session = getUI().get().getSession();
        session.close();
        // These are probably not needed, but just in case
        getUI().get().close();
        session.getSession().invalidate();
    }
}
