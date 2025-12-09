package org.vaadin.firitin.devicemotion;

/**
 * Represents the rate of rotation of a device around three axes (alpha, beta, gamma).
 *
 * @author mstahv
 */
public class DeviceMotionRotationRate {

    private Double alpha;
    private Double beta;
    private Double gamma;

    /**
     * @return rotation rate around the Z axis in degrees per second (can be null if not available)
     */
    public Double getAlpha() {
        return alpha;
    }

    /**
     * @return rotation rate around the X axis in degrees per second (can be null if not available)
     */
    public Double getBeta() {
        return beta;
    }

    /**
     * @return rotation rate around the Y axis in degrees per second (can be null if not available)
     */
    public Double getGamma() {
        return gamma;
    }

    @Override
    public String toString() {
        return "DeviceMotionRotationRate{" +
                "alpha=" + alpha +
                ", beta=" + beta +
                ", gamma=" + gamma +
                '}';
    }
}