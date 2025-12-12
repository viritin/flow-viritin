package org.vaadin.firitin.geolocation;

import java.time.Instant;

public record GeolocationEvent(GeolocationCoordinates coords, long timestamp) {

    /**
     * @return geographic coordinates.
     * @deprecated use coords(), getter left for backwards compatiblity (changed to record)
     */
    @Deprecated
    public GeolocationCoordinates getCoords() {
        return coords;
    }

    /**
     * @return the time when the geographic position of the device was acquired.
     * @deprecated use timestamp(), getter left for backwards compatiblity (changed to record)
     */
    @Deprecated
    public long getTimestamp() {
        return timestamp;
    }

    public Instant getInstant() {
        return Instant.ofEpochMilli(timestamp);
    }
}
