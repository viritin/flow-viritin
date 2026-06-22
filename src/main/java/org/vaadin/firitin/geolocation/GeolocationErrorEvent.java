package org.vaadin.firitin.geolocation;

/**
 * @deprecated Part of the deprecated {@link Geolocation} helper. Use the native
 *             {@code com.vaadin.flow.component.geolocation.Geolocation} error API
 *             ({@code error.debugInfo()} / {@code error.errorCode()}).
 */
@Deprecated(since = "3.6", forRemoval = true)
public class GeolocationErrorEvent {

    /**
     * See https://developer.mozilla.org/en-US/docs/Web/API/GeolocationPositionError
     */
    public enum GeolocationPositionError {
        UNKNOWN, PERMISSION_DENIED, POSITION_UNAVAILABLE, TIMEOUT
    }

    private int code;
    private String message;

    public GeolocationErrorEvent(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public GeolocationPositionError getError() {
        return GeolocationPositionError.values()[code];
    }

    public int getRawErrorCode() {
        return code;
    }

    public String getErrorMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message.toString();
    }
}
