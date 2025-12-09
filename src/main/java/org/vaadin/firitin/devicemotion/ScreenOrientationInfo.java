package org.vaadin.firitin.devicemotion;

/**
 * Represents screen orientation information.
 *
 * @author mstahv
 */
public class ScreenOrientationInfo {

    private String type;
    private Integer angle;

    /**
     * Orientation types as defined by the Screen Orientation API
     */
    public enum OrientationType {
        PORTRAIT_PRIMARY("portrait-primary"),
        PORTRAIT_SECONDARY("portrait-secondary"),
        LANDSCAPE_PRIMARY("landscape-primary"),
        LANDSCAPE_SECONDARY("landscape-secondary");

        private final String value;

        OrientationType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static OrientationType fromString(String value) {
            for (OrientationType type : values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return PORTRAIT_PRIMARY; // Default
        }

        public boolean isPortrait() {
            return this == PORTRAIT_PRIMARY || this == PORTRAIT_SECONDARY;
        }

        public boolean isLandscape() {
            return this == LANDSCAPE_PRIMARY || this == LANDSCAPE_SECONDARY;
        }
    }

    /**
     * @return the orientation type as a string (e.g., "portrait-primary", "landscape-primary")
     */
    public String getType() {
        return type;
    }

    /**
     * @return the orientation type as an enum
     */
    public OrientationType getOrientationType() {
        return OrientationType.fromString(type);
    }

    /**
     * @return the angle of the screen orientation in degrees (0, 90, 180, or 270)
     */
    public Integer getAngle() {
        return angle;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAngle(Integer angle) {
        this.angle = angle;
    }

    @Override
    public String toString() {
        return "ScreenOrientationInfo{" +
                "type='" + type + '\'' +
                ", angle=" + angle +
                '}';
    }
}
