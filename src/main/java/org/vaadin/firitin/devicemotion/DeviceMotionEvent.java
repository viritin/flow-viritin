package org.vaadin.firitin.devicemotion;

/**
 * Represents a device motion event containing acceleration and rotation data.
 *
 * @author mstahv
 */
public class DeviceMotionEvent {

    private DeviceMotionAcceleration acceleration;
    private DeviceMotionAcceleration accelerationIncludingGravity;
    private DeviceMotionRotationRate rotationRate;
    private Double interval;

    /**
     * @return acceleration of the device excluding the effect of gravity (can be null if not available)
     */
    public DeviceMotionAcceleration getAcceleration() {
        return acceleration;
    }

    /**
     * @return acceleration of the device including the effect of gravity (can be null if not available)
     */
    public DeviceMotionAcceleration getAccelerationIncludingGravity() {
        return accelerationIncludingGravity;
    }

    /**
     * @return rotation rate of the device around three axes (can be null if not available)
     */
    public DeviceMotionRotationRate getRotationRate() {
        return rotationRate;
    }

    /**
     * @return interval (in milliseconds) at which data is obtained from the device
     */
    public Double getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return "DeviceMotionEvent{" +
                "acceleration=" + acceleration +
                ", accelerationIncludingGravity=" + accelerationIncludingGravity +
                ", rotationRate=" + rotationRate +
                ", interval=" + interval +
                '}';
    }
}