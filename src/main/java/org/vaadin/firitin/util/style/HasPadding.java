package org.vaadin.firitin.util.style;

import org.vaadin.firitin.util.style.Padding.Side;
import org.vaadin.firitin.util.style.Padding.Size;

import com.vaadin.flow.component.HasStyle;

public interface HasPadding<S extends HasPadding<S>> extends HasStyle {
    
    default void setPadding(Padding padding) {
        padding.apply(this);
    }
    
    default void setPadding(Side side) {
        setPadding(side, Size.MEDIUM);
    }
    
    default void setPadding(Side side, Size size) {
        setPadding(Padding.of(side, size));
    }
    
    default S withPadding(Padding padding) {
        setPadding(padding);
        return (S) this;
    }
    
    default S withPadding(Side side){
        setPadding(side);
        return (S) this;
    }
    
    default S withPadding(Side side, Size size) {
        setPadding(side, size);
        return (S) this;
    }
}
