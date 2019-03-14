package org.vaadin.firitin.util.style;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.HasStyle;

public class Padding {
    private Side side;
    private Size size;

    public enum Size {
        EXTRA_SMALL("--lumo-space-xs"),
        SMALL("--lumo-space-s"),
        MEDIUM("--lumo-space-m"),
        LARGE("--lumo-space-l"),
        EXTRA_LARGE("--lumo-space-xl");

        private String cssVariableName;

        private Size(String cssVariableName) {
            this.cssVariableName = cssVariableName;
        }

        public String getVariableValue() {
            return String.format("var(%s)", cssVariableName);
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
