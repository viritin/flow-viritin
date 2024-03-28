package org.vaadin.firitin.geolocation;

public class GeolocationErrorEvent {

    /**
     * See https://developer.mozilla.org/en-US/docs/Web/API/GeolocationPositionError
     */
    public enum GeolocationPositionError {
        UNKNOWN, PERMISSION_DENIED, POSITION_UNAVAILABLE, TIMEOUT
    }

    private int errorCode;
    private String errorMessage;

    public GeolocationErrorEvent(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public GeolocationPositionError getError() {
        return GeolocationPositionError.values()[errorCode];
    }

    public int getRawErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return errorMessage.toString();
    }
}
