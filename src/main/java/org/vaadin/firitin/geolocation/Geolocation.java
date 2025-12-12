package org.vaadin.firitin.geolocation;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.dom.Element;

/**
 * A helper class to detect the geographical position of the end users.
 * 
 * <p>
 * This class uses the 
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API">Geolocation API</a> 
 * in the browser to detect the position of the user. The API mimics the JS 
 * counterpart.
 * </p>
 * <p>
 * Note that the availability and quality of the position data can vary a lot.
 * Users can decline the geolocation request in the browser altogether, but 
 * developers can also affect the settings using GeolocationOptions.
 * </p>
 * @author mstahv
 */
public class Geolocation {

    private DomListenerRegistration geoupdate;
    private DomListenerRegistration geoerror;

    private UI ui;

    private Integer id;

    public interface UpdateListener {
        void geolocationUpdate(GeolocationEvent event);
    }

    public interface ErrorListener {
        void geolocationError(GeolocationErrorEvent event);
    }

    /**
     * Starts to repeatedly watch the geolocation of the device and notifies 
     * listener with the data.
     * 
     * @param listener the listener called on succesful geolocation request
     * @param errorListener the listener called in case the request failed (e.g. user declined the request in the browser)
     * @return a Geolocation instance that can be used to cancel requesting the data
     */
    public static Geolocation watchPosition(UpdateListener listener, ErrorListener errorListener) {
        return watchPosition(listener, errorListener, new GeolocationOptions());
    }

    /**
     * 
     * Starts to repeatedly watch the geolocation of the device and notifies 
     * listener with the data.
     * 
     * @param listener the listener called on succesful geolocation request
     * @param errorListener the listener called in case the request failed (e.g. user declined the request in the browser)
     * @param options options for the geolocation request
     * @return a Geolocation instance that can be used to cancel requesting the data
     */
    public static Geolocation watchPosition(UpdateListener listener, ErrorListener errorListener, GeolocationOptions options) {
        UI ui = UI.getCurrent();
        return watchPosition(ui, listener, errorListener, options);
    }

    /**
     * Starts to repeatedly watch the geolocation of the device and notifies 
     * listener with the data.
     * 
     * @param ui the UI in which context the geolocation request is to be executed
     * @param listener the listener called on succesful geolocation request
     * @param errorListener the listener called in case the request failed (e.g. user declined the request in the browser)
     * @param options options for the geolocation request
     * @return a Geolocation instance that can be used to cancel requesting the data
     */
    public static Geolocation watchPosition(UI ui, UpdateListener listener, ErrorListener errorListener, GeolocationOptions options) {
        return checkPosition(ui,listener,errorListener,options,false);
    }

    /**
     * Determines the device's current location once and notifies listener with the data.
     * 
     * @param listener the listener called on succesful geolocation request
     * @param errorListener the listener called in case the request failed (e.g. user declined the request in the browser)
     * @param options options for the geolocation request
     */
    public static void getCurrentPosition(UpdateListener listener, ErrorListener errorListener, GeolocationOptions options) {
        checkPosition(UI.getCurrent(), listener, errorListener, options, true);
    }
    
    /**
     * Determines the device's current location once and notifies listener with the data.
     * 
     * @param ui the UI in which context the geolocation request is to be executed
     * @param listener the listener called on succesful geolocation request
     * @param errorListener the listener called in case the request failed (e.g. user declined the request in the browser)
     * @param options options for the geolocation request
     */
    public static void getCurrentPosition(UI ui, UpdateListener listener, ErrorListener errorListener, GeolocationOptions options) {
        checkPosition(ui,listener,errorListener,options, true);
    }

    /**
     * Determines the device's current location once and notifies listener with the data.
     * 
     * @param listener the listener called on succesful geolocation request
     * @param errorListener the listener called in case the request failed (e.g. user declined the request in the browser)
     */
    public static void getCurrentPosition(UpdateListener listener, ErrorListener errorListener) {
        getCurrentPosition(listener, errorListener, new GeolocationOptions());
    }

    private static Geolocation checkPosition(UI ui, UpdateListener listener, ErrorListener errorListener, GeolocationOptions options, boolean get) {
        Geolocation geolocation = new Geolocation();
        geolocation.ui = ui;

        Element eventSourceElement = ui.getElement();
        Component activeModalComponent = ui.getInternals().getActiveModalComponent();
        if(activeModalComponent != null) {
            eventSourceElement = activeModalComponent.getElement();
        }

        String method = get ? "getCurrentPosition" : "watchPosition";

        /*
         * Implementation note: would be much nicer/simpler if one could
         * simply call a server side method/lambda as callback
         * function, instead of arbitrary communicating with
         * custom dom events.
         *
         * This is a limitation in Vaadin, that don't matter with
         * most of our core components as they are heavily based
         * on web components. But with non-component related and
         * non-webcomponent component related client side integrations
         * call back support would be great.
         */

        geolocation.geoupdate = eventSourceElement.addEventListener("geoupdate", e -> {
            try {
                GeolocationEvent geolocationEvent = e.getEventDetail(GeolocationEvent.class);
                listener.geolocationUpdate(geolocationEvent);
                if(get) {
                    geolocation.clearListeners();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        geolocation.geoupdate.addEventDetail();

        geolocation.geoerror = eventSourceElement.addEventListener("geoerror", e -> {

            GeolocationErrorEvent error = e.getEventDetail(GeolocationErrorEvent.class);
            errorListener.geolocationError(error);
            if(get) {
                geolocation.clearListeners();
            }
        });
        geolocation.geoerror.addEventDetail();
        ui.getElement().executeJs("""
                const el = $1;
                const options = $0;
                return navigator.geolocation.""" + method + """
                (
                        p => {
                          var timestamp = p.timestamp;
                          // Desktop Safari has weird epoch of 2001-1-1 ...
                          const safari = (Date.now() - timestamp) > 1000*60*60*24*1000;
                          if(safari) {
                            timestamp = timestamp + 978307200000;
                          }
                          const event = new CustomEvent('geoupdate', {
                            detail: {
                                 coords : {
                                     longitude : p.coords.longitude,
                                     latitude : p.coords.latitude,
                                     accuracy : p.coords.accuracy,
                                     altitude : p.coords.altitude,
                                     altitudeAccuracy : p.coords.altitudeAccuracy,
                                     heading : p.coords.heading,
                                     speed : p.coords.speed
                                 },
                                 timestamp: timestamp
                            }
                         });
                         el.dispatchEvent(event);
                       },
                       e => {
                         const event = new CustomEvent('geoerror', {detail: {code: e.code, message: e.message}});
                         el.dispatchEvent(event);
                       },
                       options
                     );
                """
                 , options, eventSourceElement).then(Integer.class, s -> geolocation.setId(s));
        return geolocation;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    /**
     * Stops polling the listeners with the new geolocation data.
     */
    public void cancel() {
        ui.getElement().executeJs("navigator.geolocation.clearWatch($0);", id);
        clearListeners();
    }

    private void clearListeners() {
        if(geoerror != null) {
            geoerror.remove();
            geoerror = null;
        }
        if(geoupdate != null) {
            geoupdate.remove();
            geoupdate = null;
        }
        id = null;
    }

}
