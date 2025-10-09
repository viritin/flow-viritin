package org.vaadin.firitin.devicemotion;

/**
 * Represents the acceleration of a device along three axes (x, y, z).
 * The acceleration excludes the effect of gravity.
 *
 * @author mstahv
 */
public class DeviceMotionAcceleration {

    private Double x;
    private Double y;
    private Double z;

    /**
     * @return acceleration along the X axis in m/s² (can be null if not available)
     */
    public Double getX() {
        return x;
    }

    /**
     * @return acceleration along the Y axis in m/s² (can be null if not available)
     */
    public Double getY() {
        return y;
    }

    /**
     * @return acceleration along the Z axis in m/s² (can be null if not available)
     */
    public Double getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "DeviceMotionAcceleration{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}