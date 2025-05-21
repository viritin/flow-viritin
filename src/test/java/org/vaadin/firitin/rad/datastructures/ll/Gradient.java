package org.vaadin.firitin.rad.datastructures.ll;

public class Gradient {
    private String startColor;
    private String endColor;
    private Double angle;

    public Gradient(String startColor, String endColor, Double angle) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.angle = angle;
    }

    public String getStartColor() {
        return startColor;
    }

    public void setStartColor(String startColor) {
        this.startColor = startColor;
    }

    public String getEndColor() {
        return endColor;
    }

    public void setEndColor(String endColor) {
        this.endColor = endColor;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }
}
