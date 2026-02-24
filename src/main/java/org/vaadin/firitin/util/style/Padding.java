package org.vaadin.firitin.util.style;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.HasStyle;

public class Padding {
    private Side side;
    private Size size;

    public enum Size {
        EXTRA_SMALL(VaadinCssProps.GAP_XS),
        SMALL(VaadinCssProps.GAP_S),
        MEDIUM(VaadinCssProps.GAP_M),
        LARGE(VaadinCssProps.GAP_L),
        EXTRA_LARGE(VaadinCssProps.GAP_XL);

        private final VaadinCssProps prop;

        private Size(VaadinCssProps prop) {
            this.prop = prop;
        }

        public String getVariableValue() {
            return prop.var();
        }
    }

    public enum Side {
        ALL("padding"),
        LEFT("padding-left"),
        RIGHT("padding-right"),
        BOTTOM("padding-bottom"),
        TOP("padding-top"),
        VERTICAL(TOP, BOTTOM),
        HORIZONTAL(LEFT, RIGHT);

        private String padding;
        private List<Side> sides;

        private Side(String padding) {
            this.padding = padding;
            sides = Arrays.asList(this);
        }

        private Side(Side... sides) {
            this.padding = "";
            this.sides = Arrays.asList(sides);
        }
    }

    public Padding(Side side, Size size) {
        this.side = side;
        this.size = size;
    }

    public static Padding of(Side side, Size size) {
        return new Padding(side, size);
    }

    public Side getSide() {
        return side;
    }

    public Size getSize() {
        return size;
    }

    public void apply(HasStyle element) {
        side.sides.forEach(currentSide -> element.getStyle().set(currentSide.padding, size.getVariableValue()));
    }
}
