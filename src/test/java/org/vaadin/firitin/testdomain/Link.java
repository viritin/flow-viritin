package org.vaadin.firitin.testdomain;

public class Link {

    public Link(String href, String text) {
        this.href = href;
        this.text = text;
    }

    public Link() {

    }

    public enum Target {
        _blank, _self, _parent, _top
    }

    private String text;
    private String href;
    private Target target;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }
}
