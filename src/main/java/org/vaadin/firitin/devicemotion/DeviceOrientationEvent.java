package org.vaadin.firitin.devicemotion;

/**
 * Represents a device orientation event containing the physical orientation of the device.
 * The orientation is expressed as rotation angles around three axes.
 *
 * @author mstahv
 */
public class DeviceOrientationEvent {

    private Double alpha;
    private Double beta;
    private Double gamma;
    private Boolean absolute;
    private Double webkitCompassHeading;
    private Double webkitCompassAccuracy;

    /**
     * @return rotation around the Z axis, in degrees, ranging from 0 to 360 (can be null if not available)
     */
    public Double getAlpha() {
        return alpha;
    }

    /**
     * @return rotation around the X axis, in degrees, ranging from -180 to 180 (can be null if not available)
     */
    public Double getBeta() {
        return beta;
    }

    /**
     * @return rotation around the Y axis, in degrees, ranging from -90 to 90 (can be null if not available)
     */
    public Double getGamma() {
        return gamma;
    }

    /**
     * @return true if the orientation is provided as the difference between the device coordinate frame
     * and the Earth coordinate frame; false if the orientation is provided relative to an arbitrary
     * coordinate frame (can be null if not available)
     */
    public Boolean getAbsolute() {
        return absolute;
    }

    /**
     * @return compass heading in degrees from 0-360 (iOS Safari specific property, can be null if not available)
     */
    public Double getWebkitCompassHeading() {
        return webkitCompassHeading;
    }

    /**
     * @return accuracy of the compass heading in degrees (iOS Safari specific property, can be null if not available).
     * A value of -1 indicates that the compass heading is unreliable.
     */
    public Double getWebkitCompassAccuracy() {
        return webkitCompassAccuracy;
    }

    @Override
    public String toString() {
        return "DeviceOrientationEvent{" +
                "alpha=" + alpha +
                ", beta=" + beta +
                ", gamma=" + gamma +
                ", absolute=" + absolute +
                ", webkitCompassHeading=" + webkitCompassHeading +
                ", webkitCompassAccuracy=" + webkitCompassAccuracy +
                '}';
    }
}
