package org.vaadin.firitin.element.svg;

import java.time.Duration;

/**
 * A typed Java API for the SVG {@code <animateTransform>} element.
 * <p>
 * The {@code <animateTransform>} element animates a transformation attribute
 * on a target element, allowing for animated translations, rotations, scaling, and skewing.
 * </p>
 * <p>
 * To use, create an AnimateTransformElement and append it as a child of the element you want to animate.
 * </p>
 * <p><b>Example - Rotating animation:</b></p>
 * <pre>{@code
 * GElement arrow = new GElement();
 * arrow.add(new PathElement().d("M0,-40 L5,0 L-5,0 Z"));
 * arrow.appendChild(new AnimateTransformElement()
 *     .rotateFromTo(0, 360, 50, 50)  // rotate around center (50,50)
 *     .dur(Duration.ofSeconds(2))
 *     .repeatIndefinitely());
 * }</pre>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/animateTransform">MDN: animateTransform element</a>
 */
public class AnimateTransformElement extends AnimateElement {

    /**
     * Transform types that can be animated.
     */
    public enum Type {
        /** Animated translation (movement) */
        TRANSLATE("translate"),
        /** Animated scaling */
        SCALE("scale"),
        /** Animated rotation */
        ROTATE("rotate"),
        /** Animated skewing along the X axis */
        SKEW_X("skewX"),
        /** Animated skewing along the Y axis */
        SKEW_Y("skewY");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public AnimateTransformElement() {
        super("animateTransform");
        // animateTransform always targets the transform attribute
        setWriteOnlyAttribute("attributeName", "transform");
    }

    /**
     * Sets the type of transformation to animate.
     *
     * @param type the transform type
     * @return this element for method chaining
     */
    public AnimateTransformElement type(Type type) {
        setWriteOnlyAttribute("type", type.toString());
        return this;
    }

    // Override methods to return AnimateTransformElement for fluent chaining

    @Override
    public AnimateTransformElement from(String from) {
        super.from(from);
        return this;
    }

    @Override
    public AnimateTransformElement from(double from) {
        super.from(from);
        return this;
    }

    @Override
    public AnimateTransformElement to(String to) {
        super.to(to);
        return this;
    }

    @Override
    public AnimateTransformElement to(double to) {
        super.to(to);
        return this;
    }

    @Override
    public AnimateTransformElement by(String by) {
        super.by(by);
        return this;
    }

    @Override
    public AnimateTransformElement by(double by) {
        super.by(by);
        return this;
    }

    @Override
    public AnimateTransformElement values(String values) {
        super.values(values);
        return this;
    }

    @Override
    public AnimateTransformElement values(double... values) {
        super.values(values);
        return this;
    }

    @Override
    public AnimateTransformElement dur(String dur) {
        super.dur(dur);
        return this;
    }

    @Override
    public AnimateTransformElement dur(Duration dur) {
        super.dur(dur);
        return this;
    }

    @Override
    public AnimateTransformElement begin(String begin) {
        super.begin(begin);
        return this;
    }

    @Override
    public AnimateTransformElement end(String end) {
        super.end(end);
        return this;
    }

    @Override
    public AnimateTransformElement repeatCount(String repeatCount) {
        super.repeatCount(repeatCount);
        return this;
    }

    @Override
    public AnimateTransformElement repeatCount(int repeatCount) {
        super.repeatCount(repeatCount);
        return this;
    }

    @Override
    public AnimateTransformElement repeatIndefinitely() {
        super.repeatIndefinitely();
        return this;
    }

    @Override
    public AnimateTransformElement repeatDur(String repeatDur) {
        super.repeatDur(repeatDur);
        return this;
    }

    @Override
    public AnimateTransformElement fill(FillMode fill) {
        super.fill(fill);
        return this;
    }

    @Override
    public AnimateTransformElement freeze() {
        super.freeze();
        return this;
    }

    @Override
    public AnimateTransformElement calcMode(CalcMode calcMode) {
        super.calcMode(calcMode);
        return this;
    }

    @Override
    public AnimateTransformElement keyTimes(String keyTimes) {
        super.keyTimes(keyTimes);
        return this;
    }

    @Override
    public AnimateTransformElement keyTimes(double... keyTimes) {
        super.keyTimes(keyTimes);
        return this;
    }

    @Override
    public AnimateTransformElement keySplines(String keySplines) {
        super.keySplines(keySplines);
        return this;
    }

    @Override
    public AnimateTransformElement keySplines(double x1, double y1, double x2, double y2) {
        super.keySplines(x1, y1, x2, y2);
        return this;
    }

    @Override
    public AnimateTransformElement easeInOut() {
        super.easeInOut();
        return this;
    }

    @Override
    public AnimateTransformElement easeOut() {
        super.easeOut();
        return this;
    }

    @Override
    public AnimateTransformElement easeIn() {
        super.easeIn();
        return this;
    }

    @Override
    public AnimateTransformElement additive(Additive additive) {
        super.additive(additive);
        return this;
    }

    @Override
    public AnimateTransformElement accumulate(Accumulate accumulate) {
        super.accumulate(accumulate);
        return this;
    }

    @Override
    public AnimateTransformElement restart(Restart restart) {
        super.restart(restart);
        return this;
    }

    @Override
    public AnimateTransformElement beginElement() {
        super.beginElement();
        return this;
    }

    @Override
    public AnimateTransformElement endElement() {
        super.endElement();
        return this;
    }

    // ========== Convenience methods for rotation animation ==========

    /**
     * Sets up a rotation animation from one angle to another, rotating around the origin.
     *
     * @param fromAngle the starting angle in degrees
     * @param toAngle the ending angle in degrees
     * @return this element for method chaining
     */
    public AnimateTransformElement rotateFromTo(double fromAngle, double toAngle) {
        type(Type.ROTATE);
        from(String.valueOf(fromAngle));
        to(String.valueOf(toAngle));
        return this;
    }

    /**
     * Sets up a rotation animation from one angle to another, rotating around a specified center.
     *
     * @param fromAngle the starting angle in degrees
     * @param toAngle the ending angle in degrees
     * @param cx the x coordinate of the rotation center
     * @param cy the y coordinate of the rotation center
     * @return this element for method chaining
     */
    public AnimateTransformElement rotateFromTo(double fromAngle, double toAngle, double cx, double cy) {
        type(Type.ROTATE);
        from("%s %s %s".formatted(fromAngle, cx, cy));
        to("%s %s %s".formatted(toAngle, cx, cy));
        return this;
    }

    /**
     * Sets up a rotation animation through multiple angles, rotating around a specified center.
     * <p>
     * This is useful for animating to a new angle when the starting angle might differ.
     * The values should include the rotation center coordinates.
     * </p>
     *
     * @param angles the angles to animate through (each as "angle cx cy")
     * @return this element for method chaining
     */
    public AnimateTransformElement rotateValues(String... angles) {
        type(Type.ROTATE);
        values(String.join(";", angles));
        return this;
    }

    // ========== Convenience methods for translation animation ==========

    /**
     * Sets up a translation animation.
     *
     * @param fromX starting X position
     * @param fromY starting Y position
     * @param toX ending X position
     * @param toY ending Y position
     * @return this element for method chaining
     */
    public AnimateTransformElement translateFromTo(double fromX, double fromY, double toX, double toY) {
        type(Type.TRANSLATE);
        from("%s %s".formatted(fromX, fromY));
        to("%s %s".formatted(toX, toY));
        return this;
    }

    // ========== Convenience methods for scale animation ==========

    /**
     * Sets up a uniform scale animation.
     *
     * @param fromScale starting scale factor
     * @param toScale ending scale factor
     * @return this element for method chaining
     */
    public AnimateTransformElement scaleFromTo(double fromScale, double toScale) {
        type(Type.SCALE);
        from(fromScale);
        to(toScale);
        return this;
    }

    /**
     * Sets up a non-uniform scale animation.
     *
     * @param fromSx starting X scale
     * @param fromSy starting Y scale
     * @param toSx ending X scale
     * @param toSy ending Y scale
     * @return this element for method chaining
     */
    public AnimateTransformElement scaleFromTo(double fromSx, double fromSy, double toSx, double toSy) {
        type(Type.SCALE);
        from("%s %s".formatted(fromSx, fromSy));
        to("%s %s".formatted(toSx, toSy));
        return this;
    }
}