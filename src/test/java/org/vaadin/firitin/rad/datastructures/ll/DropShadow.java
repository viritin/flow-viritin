package org.vaadin.firitin.rad.datastructures.ll;

public class DropShadow {
    private String color;
    private Double dx;
    private Double dy;
    private Double blur;

    public DropShadow(String color, Double dx, Double dy, Double blur) {
        this.color = color;
        this.dx = dx;
        this.dy = dy;
        this.blur = blur;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getDx() {
        return dx;
    }

    public void setDx(Double dx) {
        this.dx = dx;
    }

    public Double getDy() {
        return dy;
    }

    public void setDy(Double dy) {
        this.dy = dy;
    }

    public Double getBlur() {
        return blur;
    }

    public void setBlur(Double blur) {
        this.blur = blur;
    }
}
