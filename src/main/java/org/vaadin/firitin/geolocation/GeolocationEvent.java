package org.vaadin.firitin.geolocation;

public class GeolocationEvent {
    private GeolocationCoordinates coords;
    private long timestamp;

    /**
     * @return geographic coordinates.
     */
    public GeolocationCoordinates getCoords() {
        return coords;
    }

    /**
     * @return the time when the geographic position of the device was acquired.
     */
    public long getTimestamp() {
        return timestamp;
    }
}
