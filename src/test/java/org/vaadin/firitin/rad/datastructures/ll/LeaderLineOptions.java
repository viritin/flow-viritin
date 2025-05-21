package org.vaadin.firitin.rad.datastructures.ll;

public class LeaderLineOptions {

    private Double size;
    private String color;
    private PathType path;
    private SocketType startSocket;
    private SocketType endSocket;

    private SocketGravityRecord startSocketGravity;
    private SocketGravityRecord endSocketGravity;

    private PlugType startPlug;
    private PlugType endPlug;
    private String startPlugColor;
    private String endPlugColor;

    private Double startPlugSize;
    private Double endPlugSize;

    private Boolean outline;
    private String outlineColor;
    private Double outlineSize;

    private Boolean startPlugOutline;
    private Boolean endPlugOutline;
    private String startPlugOutlineColor;
    private String endPlugOutlineColor;
    private Double startPlugOutlineSize;
    private Double endPlugOutlineSize;

    private String startLabel;
    private String middleLabel;
    private String endLabel;

    private Dash dash;
    private Gradient gradient;
    private DropShadow dropShadow;

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    // TODO CssColor typing via shared library!!
    public void setColor(String color) {
        this.color = color;
    }

    public PathType getPath() {
        return path;
    }

    public void setPath(PathType path) {
        this.path = path;
    }

    public SocketType getStartSocket() {
        return startSocket;
    }

    public void setStartSocket(SocketType startSocket) {
        this.startSocket = startSocket;
    }

    public SocketType getEndSocket() {
        return endSocket;
    }

    public void setEndSocket(SocketType endSocket) {
        this.endSocket = endSocket;
    }

    public SocketGravityRecord getStartSocketGravity() {
        return startSocketGravity;
    }

    public void setStartSocketGravity(SocketGravityRecord startSocketGravity) {
        this.startSocketGravity = startSocketGravity;
    }

    public SocketGravityRecord getEndSocketGravity() {
        return endSocketGravity;
    }

    public void setEndSocketGravity(SocketGravityRecord endSocketGravity) {
        this.endSocketGravity = endSocketGravity;
    }

    public PlugType getStartPlug() {
        return startPlug;
    }

    public void setStartPlug(PlugType startPlug) {
        this.startPlug = startPlug;
    }

    public PlugType getEndPlug() {
        return endPlug;
    }

    public void setEndPlug(PlugType endPlug) {
        this.endPlug = endPlug;
    }

    public String getStartPlugColor() {
        return startPlugColor;
    }

    public void setStartPlugColor(String startPlugColor) {
        this.startPlugColor = startPlugColor;
    }

    public String getEndPlugColor() {
        return endPlugColor;
    }

    public void setEndPlugColor(String endPlugColor) {
        this.endPlugColor = endPlugColor;
    }

    public Double getStartPlugSize() {
        return startPlugSize;
    }

    public void setStartPlugSize(Double startPlugSize) {
        this.startPlugSize = startPlugSize;
    }

    public Double getEndPlugSize() {
        return endPlugSize;
    }

    public void setEndPlugSize(Double endPlugSize) {
        this.endPlugSize = endPlugSize;
    }

    public Boolean getOutline() {
        return outline;
    }

    public void setOutline(Boolean outline) {
        this.outline = outline;
    }

    public String getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(String outlineColor) {
        this.outlineColor = outlineColor;
    }

    public Double getOutlineSize() {
        return outlineSize;
    }

    public void setOutlineSize(Double outlineSize) {
        this.outlineSize = outlineSize;
    }

    public Boolean getStartPlugOutline() {
        return startPlugOutline;
    }

    public void setStartPlugOutline(Boolean startPlugOutline) {
        this.startPlugOutline = startPlugOutline;
    }

    public Boolean getEndPlugOutline() {
        return endPlugOutline;
    }

    public void setEndPlugOutline(Boolean endPlugOutline) {
        this.endPlugOutline = endPlugOutline;
    }

    public String getStartPlugOutlineColor() {
        return startPlugOutlineColor;
    }

    public void setStartPlugOutlineColor(String startPlugOutlineColor) {
        this.startPlugOutlineColor = startPlugOutlineColor;
    }

    public String getEndPlugOutlineColor() {
        return endPlugOutlineColor;
    }

    public void setEndPlugOutlineColor(String endPlugOutlineColor) {
        this.endPlugOutlineColor = endPlugOutlineColor;
    }

    public Double getStartPlugOutlineSize() {
        return startPlugOutlineSize;
    }

    public void setStartPlugOutlineSize(Double startPlugOutlineSize) {
        this.startPlugOutlineSize = startPlugOutlineSize;
    }

    public Double getEndPlugOutlineSize() {
        return endPlugOutlineSize;
    }

    public void setEndPlugOutlineSize(Double endPlugOutlineSize) {
        this.endPlugOutlineSize = endPlugOutlineSize;
    }

    public String getStartLabel() {
        return startLabel;
    }

    public void setStartLabel(String startLabel) {
        this.startLabel = startLabel;
    }

    public String getMiddleLabel() {
        return middleLabel;
    }

    public void setMiddleLabel(String middleLabel) {
        this.middleLabel = middleLabel;
    }

    public String getEndLabel() {
        return endLabel;
    }

    public void setEndLabel(String endLabel) {
        this.endLabel = endLabel;
    }

    public Dash getDash() {
        return dash;
    }

    public void setDash(Dash dash) {
        this.dash = dash;
    }

    public Gradient getGradient() {
        return gradient;
    }

    public void setGradient(Gradient gradient) {
        this.gradient = gradient;
    }

    public DropShadow getDropShadow() {
        return dropShadow;
    }

    public void setDropShadow(DropShadow dropShadow) {
        this.dropShadow = dropShadow;
    }
}
