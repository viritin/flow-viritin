package org.vaadin.firitin.element.svg;

import java.time.Duration;

/**
 * A typed Java API for the SVG {@code <animate>} element.
 * <p>
 * The {@code <animate>} element provides a way to animate an attribute of an element over time.
 * It is part of SVG's SMIL (Synchronized Multimedia Integration Language) animation support.
 * </p>
 * <p>
 * To use, create an AnimateElement and append it as a child of the element you want to animate.
 * </p>
 * <p><b>Example:</b></p>
 * <pre>{@code
 * CircleElement circle = new CircleElement().cx(50).cy(50).r(20);
 * circle.appendChild(new AnimateElement()
 *     .attributeName("r")
 *     .from(20)
 *     .to(40)
 *     .dur("1s")
 *     .repeatCount("indefinite"));
 * }</pre>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/animate">MDN: animate element</a>
 */
public class AnimateElement extends SvgElement {

    /**
     * Calc mode options for animation interpolation.
     */
    public enum CalcMode {
        /** Linear interpolation between values (default) */
        LINEAR("linear"),
        /** Discrete interpolation - jumps from value to value */
        DISCRETE("discrete"),
        /** Spline interpolation using keySplines */
        SPLINE("spline"),
        /** Paced interpolation - even timing regardless of value distances */
        PACED("paced");

        private final String value;

        CalcMode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Fill mode options for what happens after the animation ends.
     */
    public enum FillMode {
        /** The animation effect is removed after the animation ends */
        REMOVE("remove"),
        /** The animation effect is maintained after the animation ends */
        FREEZE("freeze");

        private final String value;

        FillMode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Additive mode options for how the animation combines with the base value.
     */
    public enum Additive {
        /** The animation replaces the base value (default) */
        REPLACE("replace"),
        /** The animation adds to the base value */
        SUM("sum");

        private final String value;

        Additive(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Accumulate mode options for repeated animations.
     */
    public enum Accumulate {
        /** Each repeat starts from the original base value (default) */
        NONE("none"),
        /** Each repeat builds upon the previous iteration */
        SUM("sum");

        private final String value;

        Accumulate(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public AnimateElement() {
        super("animate");
    }

    protected AnimateElement(String tag) {
        super(tag);
    }

    // ========== Target Attribute ==========

    /**
     * Sets the name of the attribute to animate.
     *
     * @param attributeName the attribute name (e.g., "cx", "fill", "opacity")
     * @return this element for method chaining
     */
    public AnimateElement attributeName(String attributeName) {
        setWriteOnlyAttribute("attributeName", attributeName);
        return this;
    }

    // ========== Animation Values ==========

    /**
     * Sets the starting value for the animation.
     *
     * @param from the starting value
     * @return this element for method chaining
     */
    public AnimateElement from(String from) {
        setWriteOnlyAttribute("from", from);
        return this;
    }

    /**
     * Sets the starting value for the animation (numeric).
     *
     * @param from the starting value
     * @return this element for method chaining
     */
    public AnimateElement from(double from) {
        setWriteOnlyAttribute("from", String.valueOf(from));
        return this;
    }

    /**
     * Sets the ending value for the animation.
     *
     * @param to the ending value
     * @return this element for method chaining
     */
    public AnimateElement to(String to) {
        setWriteOnlyAttribute("to", to);
        return this;
    }

    /**
     * Sets the ending value for the animation (numeric).
     *
     * @param to the ending value
     * @return this element for method chaining
     */
    public AnimateElement to(double to) {
        setWriteOnlyAttribute("to", String.valueOf(to));
        return this;
    }

    /**
     * Sets the change in value for relative animations.
     *
     * @param by the value change
     * @return this element for method chaining
     */
    public AnimateElement by(String by) {
        setWriteOnlyAttribute("by", by);
        return this;
    }

    /**
     * Sets the change in value for relative animations (numeric).
     *
     * @param by the value change
     * @return this element for method chaining
     */
    public AnimateElement by(double by) {
        setWriteOnlyAttribute("by", String.valueOf(by));
        return this;
    }

    /**
     * Sets multiple values for keyframe animation.
     * <p>
     * The values are semicolon-separated.
     * </p>
     *
     * @param values the keyframe values (e.g., "0;50;100;50;0")
     * @return this element for method chaining
     */
    public AnimateElement values(String values) {
        setWriteOnlyAttribute("values", values);
        return this;
    }

    /**
     * Sets multiple values for keyframe animation.
     *
     * @param values the keyframe values
     * @return this element for method chaining
     */
    public AnimateElement values(double... values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(";");
            sb.append(values[i]);
        }
        setWriteOnlyAttribute("values", sb.toString());
        return this;
    }

    // ========== Timing ==========

    /**
     * Sets the duration of the animation.
     *
     * @param dur the duration (e.g., "1s", "500ms", "indefinite")
     * @return this element for method chaining
     */
    public AnimateElement dur(String dur) {
        setWriteOnlyAttribute("dur", dur);
        return this;
    }

    /**
     * Sets the duration of the animation using a Duration object.
     * <p>
     * The duration is converted to milliseconds for SVG.
     * </p>
     *
     * @param dur the duration
     * @return this element for method chaining
     */
    public AnimateElement dur(Duration dur) {
        setWriteOnlyAttribute("dur", dur.toMillis() + "ms");
        return this;
    }

    /**
     * Sets the begin time for the animation.
     *
     * @param begin the begin time (e.g., "0s", "2s", "click", "mouseenter")
     * @return this element for method chaining
     */
    public AnimateElement begin(String begin) {
        // Not using JS here as it can be problematic with timing
        setAttribute("begin", begin);
        return this;
    }

    /**
     * Sets the end time for the animation.
     *
     * @param end the end time
     * @return this element for method chaining
     */
    public AnimateElement end(String end) {
        // Not using JS here as it can be problematic with timing
        setAttribute("end", end);
        return this;
    }

    /**
     * Sets the minimum duration for the animation.
     *
     * @param min the minimum duration
     * @return this element for method chaining
     */
    public AnimateElement min(String min) {
        setWriteOnlyAttribute("min", min);
        return this;
    }

    /**
     * Sets the maximum duration for the animation.
     *
     * @param max the maximum duration
     * @return this element for method chaining
     */
    public AnimateElement max(String max) {
        setWriteOnlyAttribute("max", max);
        return this;
    }

    /**
     * Sets the repeat count for the animation.
     *
     * @param repeatCount the number of repeats or "indefinite"
     * @return this element for method chaining
     */
    public AnimateElement repeatCount(String repeatCount) {
        setWriteOnlyAttribute("repeatCount", repeatCount);
        return this;
    }

    /**
     * Sets the repeat count for the animation.
     *
     * @param repeatCount the number of repeats
     * @return this element for method chaining
     */
    public AnimateElement repeatCount(int repeatCount) {
        setWriteOnlyAttribute("repeatCount", String.valueOf(repeatCount));
        return this;
    }

    /**
     * Sets the animation to repeat indefinitely.
     *
     * @return this element for method chaining
     */
    public AnimateElement repeatIndefinitely() {
        setWriteOnlyAttribute("repeatCount", "indefinite");
        return this;
    }

    /**
     * Sets the repeat duration for the animation.
     *
     * @param repeatDur the repeat duration (e.g., "10s", "indefinite")
     * @return this element for method chaining
     */
    public AnimateElement repeatDur(String repeatDur) {
        setWriteOnlyAttribute("repeatDur", repeatDur);
        return this;
    }

    // ========== Fill Mode ==========

    /**
     * Sets what happens after the animation ends.
     *
     * @param fill the fill mode (REMOVE or FREEZE)
     * @return this element for method chaining
     */
    public AnimateElement fill(FillMode fill) {
        setWriteOnlyAttribute("fill", fill.toString());
        return this;
    }

    /**
     * Sets the animation to freeze at the end value after completion.
     *
     * @return this element for method chaining
     */
    public AnimateElement freeze() {
        return fill(FillMode.FREEZE);

    }

    // ========== Interpolation ==========

    /**
     * Sets the calculation mode for interpolation.
     *
     * @param calcMode the calculation mode
     * @return this element for method chaining
     */
    public AnimateElement calcMode(CalcMode calcMode) {
        setWriteOnlyAttribute("calcMode", calcMode.toString());
        return this;
    }

    /**
     * Sets the key times for keyframe animations.
     * <p>
     * Values should be between 0 and 1, semicolon-separated.
     * The number of key times must match the number of values.
     * </p>
     *
     * @param keyTimes the key times (e.g., "0;0.25;0.5;0.75;1")
     * @return this element for method chaining
     */
    public AnimateElement keyTimes(String keyTimes) {
        setWriteOnlyAttribute("keyTimes", keyTimes);
        return this;
    }

    /**
     * Sets the key times for keyframe animations.
     *
     * @param keyTimes the key times (values between 0 and 1)
     * @return this element for method chaining
     */
    public AnimateElement keyTimes(double... keyTimes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyTimes.length; i++) {
            if (i > 0) sb.append(";");
            sb.append(keyTimes[i]);
        }
        setWriteOnlyAttribute("keyTimes", sb.toString());
        return this;
    }

    /**
     * Sets the bezier control points for spline interpolation.
     * <p>
     * Each set of control points is four numbers (x1 y1 x2 y2) for a cubic bezier.
     * Sets are separated by semicolons. Number of sets = number of values - 1.
     * </p>
     *
     * @param keySplines the key splines (e.g., "0.5 0 0.5 1; 0.5 0 0.5 1")
     * @return this element for method chaining
     */
    public AnimateElement keySplines(String keySplines) {
        setWriteOnlyAttribute("keySplines", keySplines);
        return this;
    }

    /**
     * Sets the bezier control points for spline interpolation (single spline).
     * <p>
     * The four values define a cubic bezier curve: (x1, y1) is the first control point
     * and (x2, y2) is the second control point. Values should be between 0 and 1.
     * </p>
     * <p>
     * Common easing curves:
     * </p>
     * <ul>
     *   <li>Ease: 0.25, 0.1, 0.25, 1.0</li>
     *   <li>Ease-in: 0.42, 0, 1.0, 1.0</li>
     *   <li>Ease-out: 0, 0, 0.58, 1.0</li>
     *   <li>Ease-in-out: 0.42, 0, 0.58, 1.0</li>
     * </ul>
     *
     * @param x1 x coordinate of first control point (0-1)
     * @param y1 y coordinate of first control point (0-1)
     * @param x2 x coordinate of second control point (0-1)
     * @param y2 y coordinate of second control point (0-1)
     * @return this element for method chaining
     */
    public AnimateElement keySplines(double x1, double y1, double x2, double y2) {
        setWriteOnlyAttribute("keySplines", "%s %s %s %s".formatted(x1, y1, x2, y2));
        return this;
    }

    /**
     * Sets up ease-in-out spline interpolation for smooth animations.
     * <p>
     * This is a convenience method that sets calcMode to SPLINE
     * and applies an ease-in-out bezier curve.
     * </p>
     *
     * @return this element for method chaining
     */
    public AnimateElement easeInOut() {
        calcMode(CalcMode.SPLINE);
        keySplines(0.42, 0, 0.58, 1);
        keyTimes(0, 1);
        return this;
    }

    /**
     * Sets up ease-out spline interpolation for smooth deceleration.
     * <p>
     * This is a convenience method that sets calcMode to SPLINE
     * and applies an ease-out bezier curve (starts fast, ends slow).
     * </p>
     *
     * @return this element for method chaining
     */
    public AnimateElement easeOut() {
        calcMode(CalcMode.SPLINE);
        keySplines(0, 0, 0.58, 1);
        keyTimes(0, 1);
        return this;
    }

    /**
     * Sets up ease-in spline interpolation for smooth acceleration.
     * <p>
     * This is a convenience method that sets calcMode to SPLINE
     * and applies an ease-in bezier curve (starts slow, ends fast).
     * </p>
     *
     * @return this element for method chaining
     */
    public AnimateElement easeIn() {
        calcMode(CalcMode.SPLINE);
        keySplines(0.42, 0, 1, 1);
        keyTimes(0, 1);
        return this;
    }

    // ========== Additive and Accumulate ==========

    /**
     * Sets how the animation combines with the base value.
     *
     * @param additive the additive mode
     * @return this element for method chaining
     */
    public AnimateElement additive(Additive additive) {
        setWriteOnlyAttribute("additive", additive.toString());
        return this;
    }

    /**
     * Sets how repeated animations accumulate.
     *
     * @param accumulate the accumulate mode
     * @return this element for method chaining
     */
    public AnimateElement accumulate(Accumulate accumulate) {
        setWriteOnlyAttribute("accumulate", accumulate.toString());
        return this;
    }

    // ========== Restart ==========

    /**
     * Restart behavior options for animations.
     */
    public enum Restart {
        /** Animation can restart at any time (default) */
        ALWAYS("always"),
        /** Animation can only restart when not active */
        WHEN_NOT_ACTIVE("whenNotActive"),
        /** Animation cannot be restarted */
        NEVER("never");

        private final String value;

        Restart(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the restart behavior for the animation.
     *
     * @param restart the restart mode
     * @return this element for method chaining
     */
    public AnimateElement restart(Restart restart) {
        setWriteOnlyAttribute("restart", restart.toString());
        return this;
    }

    // ========== Animation Control Methods ==========

    /**
     * Starts the animation.
     * <p>
     * This is particularly useful for dynamically added animations, which don't
     * auto-start in browsers. Call this method after appending the animation
     * element to its target element.
     * </p>
     *
     * @return this element for method chaining
     */
    public AnimateElement beginElement() {
        executeJs("this.beginElement()");
        return this;
    }

    /**
     * Ends the animation.
     * <p>
     * Causes the animation to end immediately, as if the end time had been reached.
     * </p>
     *
     * @return this element for method chaining
     */
    public AnimateElement endElement() {
        executeJs("this.endElement()");
        return this;
    }
}