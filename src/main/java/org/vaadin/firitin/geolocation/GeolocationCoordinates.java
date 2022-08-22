package org.vaadin.firitin.geolocation;

public class GeolocationCoordinates {
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private Double altitude;
    private Double altitudeAccuracy;
    private Double heading;
    private Double speed;

    /**
     * @return latitude specified in decimal degrees
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return longitude specified in decimal degrees
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return the accuracy level of the latitude and longitude coordinates in meters (e.g., 65 meters).
     */
    public Double getAccuracy() {
        return accuracy;
    }

    /**
     * @return the height of the position, specified in meters above the WGS84 ellipsoid.
     */
    public Double getAltitude() {
        return altitude;
    }

    /**
     * @return the altitude accuracy in meters (e.g., 10 meters).
     */
    public Double getAltitudeAccuracy() {
        return altitudeAccuracy;
    }

    /**
     * @return denotes the direction of travel of the hosting device and is specified in degrees, where 0° ≤ heading &lt; 360°, counting clockwise relative to the true north.
     */
    public Double getHeading() {
        return heading;
    }

    /**
     * @return the magnitude of the horizontal component of the hosting device's current velocity in meters per second.
     */
    public Double getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "GeolocationCoordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", accuracy=" + accuracy +
                ", altitude=" + altitude +
                ", altitudeAccuracy=" + altitudeAccuracy +
                ", heading=" + heading +
                ", speed=" + speed +
                '}';
    }
}
