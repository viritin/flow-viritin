package org.vaadin.firitin.element.svg;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * A typed Java API for the SVG {@code <animateMotion>} element.
 * <p>
 * The {@code <animateMotion>} element causes a referenced element to move
 * along a motion path.
 * </p>
 * <p>
 * To use, create an AnimateMotionElement and append it as a child of the element you want to animate.
 * </p>
 * <h3>Example - Linear motion:</h3>
 * <pre>{@code
 * CircleElement circle = new CircleElement().center(0, 50).r(15);
 * circle.appendChild(new AnimateMotionElement()
 *     .path("M 0 0 H 300 Z")
 *     .dur(Duration.ofSeconds(3))
 *     .repeatIndefinitely());
 * }</pre>
 * <h3>Example - Curved motion with auto-rotation:</h3>
 * <pre>{@code
 * RectElement rect = new RectElement().size(20, 20);
 * rect.appendChild(new AnimateMotionElement()
 *     .path("M 50,50 Q 100,0 150,50 T 250,50")
 *     .dur(Duration.ofSeconds(3))
 *     .rotateAuto()
 *     .repeatIndefinitely());
 * }</pre>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Element/animateMotion">MDN: animateMotion element</a>
 */
public class AnimateMotionElement extends SvgElement {

    /**
     * Rotation mode options for motion path animation.
     */
    public enum RotateMode {
        /** Element maintains its original orientation */
        NONE("0"),
        /** Element rotates to follow the path tangent */
        AUTO("auto"),
        /** Element rotates opposite to the path tangent */
        AUTO_REVERSE("auto-reverse");

        private final String value;

        RotateMode(String value) {
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
     * Calc mode options for animation interpolation.
     */
    public enum CalcMode {
        /** Paced interpolation - even timing along the path */
        PACED("paced"),
        /** Linear interpolation between keyPoints */
        LINEAR("linear"),
        /** Discrete interpolation - jumps between positions */
        DISCRETE("discrete"),
        /** Spline interpolation using keySplines */
        SPLINE("spline");

        private final String value;

        CalcMode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public AnimateMotionElement() {
        super("animateMotion");
    }

    // ========== Path Definition ==========

    /**
     * Sets the motion path using SVG path syntax.
     * <p>
     * Common path commands:
     * <ul>
     *   <li>M x,y - Move to</li>
     *   <li>L x,y - Line to</li>
     *   <li>H x - Horizontal line to</li>
     *   <li>V y - Vertical line to</li>
     *   <li>Q cx,cy x,y - Quadratic Bezier curve</li>
     *   <li>C cx1,cy1 cx2,cy2 x,y - Cubic Bezier curve</li>
     *   <li>A rx,ry rotation large-arc sweep x,y - Arc</li>
     *   <li>Z - Close path</li>
     * </ul>
     *
     * @param path the SVG path definition
     * @return this element for method chaining
     */
    public AnimateMotionElement path(String path) {
        setWriteOnlyAttribute("path", path);
        return this;
    }

    /**
     * Sets the motion path using a lambda expression that configures a PathBuilder.
     * <p>
     * Example usage:
     * <pre>{@code
     * rect.animateMotion()
     *     .path(p -> p
     *         .moveTo(50, 80)
     *         .horizontalLineTo(250)
     *         .quadraticBezierTo(280, 80, 280, 60)
     *         .quadraticBezierTo(280, 40, 250, 40)
     *         .horizontalLineTo(50)
     *         .closePath())
     *     .dur(Duration.ofSeconds(3))
     *     .rotateAuto()
     *     .repeatIndefinitely();
     * }</pre>
     *
     * @param pathConfigurator a consumer that configures the path builder
     * @return this element for method chaining
     */
    public AnimateMotionElement path(Consumer<PathBuilder> pathConfigurator) {
        PathBuilder builder = new PathBuilder();
        pathConfigurator.accept(builder);
        setWriteOnlyAttribute("path", builder.build());
        return this;
    }

    /**
     * Sets the motion path using a PathElement reference.
     * <p>
     * The referenced path element must have an id attribute set.
     *
     * @param pathElement the path element to follow
     * @return this element for method chaining
     */
    public AnimateMotionElement path(PathElement pathElement) {
        // Use mpath child element for referencing external path
        MPathElement mpath = new MPathElement();
        mpath.setWriteOnlyAttribute("href", "#" + pathElement.getAttribute("id"));
        appendChild(mpath);
        return this;
    }

    // ========== Timing ==========

    /**
     * Sets the duration of the animation.
     *
     * @param dur the duration (e.g., "3s", "500ms")
     * @return this element for method chaining
     */
    public AnimateMotionElement dur(String dur) {
        setWriteOnlyAttribute("dur", dur);
        return this;
    }

    /**
     * Sets the duration of the animation using a Duration object.
     *
     * @param dur the duration
     * @return this element for method chaining
     */
    public AnimateMotionElement dur(Duration dur) {
        setWriteOnlyAttribute("dur", dur.toMillis() + "ms");
        return this;
    }

    /**
     * Sets the begin time for the animation.
     *
     * @param begin the begin time (e.g., "0s", "2s", "click")
     * @return this element for method chaining
     */
    public AnimateMotionElement begin(String begin) {
        setWriteOnlyAttribute("begin", begin);
        return this;
    }

    /**
     * Sets the end time for the animation.
     *
     * @param end the end time
     * @return this element for method chaining
     */
    public AnimateMotionElement end(String end) {
        setWriteOnlyAttribute("end", end);
        return this;
    }

    /**
     * Sets the repeat count for the animation.
     *
     * @param repeatCount the number of repeats or "indefinite"
     * @return this element for method chaining
     */
    public AnimateMotionElement repeatCount(String repeatCount) {
        setWriteOnlyAttribute("repeatCount", repeatCount);
        return this;
    }

    /**
     * Sets the repeat count for the animation.
     *
     * @param repeatCount the number of repeats
     * @return this element for method chaining
     */
    public AnimateMotionElement repeatCount(int repeatCount) {
        setWriteOnlyAttribute("repeatCount", String.valueOf(repeatCount));
        return this;
    }

    /**
     * Sets the animation to repeat indefinitely.
     *
     * @return this element for method chaining
     */
    public AnimateMotionElement repeatIndefinitely() {
        setWriteOnlyAttribute("repeatCount", "indefinite");
        return this;
    }

    /**
     * Sets the repeat duration for the animation.
     *
     * @param repeatDur the repeat duration (e.g., "10s", "indefinite")
     * @return this element for method chaining
     */
    public AnimateMotionElement repeatDur(String repeatDur) {
        setWriteOnlyAttribute("repeatDur", repeatDur);
        return this;
    }

    // ========== Fill Mode ==========

    /**
     * Sets what happens after the animation ends.
     *
     * @param fill the fill mode
     * @return this element for method chaining
     */
    public AnimateMotionElement fill(FillMode fill) {
        setWriteOnlyAttribute("fill", fill.toString());
        return this;
    }

    /**
     * Sets the animation to freeze at the end position after completion.
     *
     * @return this element for method chaining
     */
    public AnimateMotionElement freeze() {
        return fill(FillMode.FREEZE);
    }

    // ========== Rotation ==========

    /**
     * Sets the rotation mode for the animated element.
     *
     * @param rotate the rotation mode
     * @return this element for method chaining
     */
    public AnimateMotionElement rotate(RotateMode rotate) {
        setWriteOnlyAttribute("rotate", rotate.toString());
        return this;
    }

    /**
     * Sets a fixed rotation angle for the animated element.
     *
     * @param angle the rotation angle in degrees
     * @return this element for method chaining
     */
    public AnimateMotionElement rotate(double angle) {
        setWriteOnlyAttribute("rotate", String.valueOf(angle));
        return this;
    }

    /**
     * Sets the element to automatically rotate to follow the path tangent.
     *
     * @return this element for method chaining
     */
    public AnimateMotionElement rotateAuto() {
        return rotate(RotateMode.AUTO);
    }

    /**
     * Sets the element to automatically rotate opposite to the path tangent.
     *
     * @return this element for method chaining
     */
    public AnimateMotionElement rotateAutoReverse() {
        return rotate(RotateMode.AUTO_REVERSE);
    }

    // ========== Interpolation ==========

    /**
     * Sets the calculation mode for interpolation.
     *
     * @param calcMode the calculation mode
     * @return this element for method chaining
     */
    public AnimateMotionElement calcMode(CalcMode calcMode) {
        setWriteOnlyAttribute("calcMode", calcMode.toString());
        return this;
    }

    /**
     * Sets the key points along the motion path.
     * <p>
     * Values should be between 0 and 1, semicolon-separated.
     *
     * @param keyPoints the key points (e.g., "0;0.5;1")
     * @return this element for method chaining
     */
    public AnimateMotionElement keyPoints(String keyPoints) {
        setWriteOnlyAttribute("keyPoints", keyPoints);
        return this;
    }

    /**
     * Sets the key points along the motion path.
     *
     * @param keyPoints the key points (values between 0 and 1)
     * @return this element for method chaining
     */
    public AnimateMotionElement keyPoints(double... keyPoints) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyPoints.length; i++) {
            if (i > 0) sb.append(";");
            sb.append(keyPoints[i]);
        }
        setWriteOnlyAttribute("keyPoints", sb.toString());
        return this;
    }

    /**
     * Sets the key times for keyframe animations.
     * <p>
     * Values should be between 0 and 1, semicolon-separated.
     *
     * @param keyTimes the key times
     * @return this element for method chaining
     */
    public AnimateMotionElement keyTimes(String keyTimes) {
        setWriteOnlyAttribute("keyTimes", keyTimes);
        return this;
    }

    /**
     * Sets the key times for keyframe animations.
     *
     * @param keyTimes the key times (values between 0 and 1)
     * @return this element for method chaining
     */
    public AnimateMotionElement keyTimes(double... keyTimes) {
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
     *
     * @param keySplines the key splines
     * @return this element for method chaining
     */
    public AnimateMotionElement keySplines(String keySplines) {
        setWriteOnlyAttribute("keySplines", keySplines);
        return this;
    }

    // ========== Animation Control ==========

    /**
     * Starts the animation.
     *
     * @return this element for method chaining
     */
    public AnimateMotionElement beginElement() {
        executeJs("this.beginElement()");
        return this;
    }

    /**
     * Ends the animation.
     *
     * @return this element for method chaining
     */
    public AnimateMotionElement endElement() {
        executeJs("this.endElement()");
        return this;
    }

    // ========== Inner class for mpath element ==========

    /**
     * The mpath element provides the ability to reference an external path
     * as the definition of a motion path.
     */
    public static class MPathElement extends SvgElement {
        public MPathElement() {
            super("mpath");
        }
    }
}