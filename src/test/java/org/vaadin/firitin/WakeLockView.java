package org.vaadin.firitin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.util.PageVisibility;
import org.vaadin.firitin.util.ScreenWakeLock;

@Route
public class WakeLockView extends VVerticalLayout {

    Div status = new Div();

    public WakeLockView() {


        status.setText("Initial (not set)");

        add(status);

        add(new VButton("Request wakelock"){{

            addClickListener(event -> {
                status.setText("Requesting Wake Lock...");

                ScreenWakeLock.request().thenAccept(state -> {
                    if(state == ScreenWakeLock.State.ACTIVE) {
                        status.setText("Wake Lock is ACTIVE");
                    } else {
                        status.setText("Wake Lock request ERROR");
                    }
                });
            });

        }});

        add(new VButton("Request wakelock with release listener"){{

            addClickListener(event -> {
                ScreenWakeLock.request(UI.getCurrent(), () -> {
                    status.setText("Wake Lock was released (listener)");
                    Notification.show("Wake Lock was released");
                });
            });

        }});

        add(new VButton("Release"){{

            addClickListener(event -> {
                ScreenWakeLock.release();
                status.setText("Released");
            });

        }});

        add(new VButton("Refresh wake lock state"){{

            addClickListener(event -> {

                ScreenWakeLock.checkState().thenAccept(state -> {
                    status.setText("State checked: " + state);
                });
            });
        }});

        Checkbox reRequestWhenBecomingVisible = new Checkbox("Re-request when becoming visible");
        reRequestWhenBecomingVisible.setValue(false);
        add(reRequestWhenBecomingVisible);

        PageVisibility.get().addVisibilityChangeListener(visibility -> {
            if(visibility == PageVisibility.Visibility.VISIBLE && reRequestWhenBecomingVisible.getValue()) {
                status.setText("Page became visible, re-requesting Wake Lock...");
                ScreenWakeLock.request().thenAccept(state -> {
                    if(state == ScreenWakeLock.State.ACTIVE) {
                        status.setText("Wake Lock is ACTIVE (re-requested as became visible again)");
                    } else {
                        status.setText("Wake Lock request ERROR");
                    }
                });
            }
        });


    }


}
