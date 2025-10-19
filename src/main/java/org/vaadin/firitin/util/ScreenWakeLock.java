package org.vaadin.firitin.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomListenerRegistration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A Vaadin wrapper for the browser's Screen Wake Lock API.
 * <p>
 * The Screen Wake Lock API prevents devices from dimming or locking the screen when an application
 * needs to keep running and remain visible to the user. This is particularly useful for applications
 * like e-book readers, navigation apps, recipe viewers, presentation software, QR/barcode scanners,
 * or any voice or gesture-controlled applications.
 * </p>
 * <p>
 * <strong>Important Notes:</strong>
 * <ul>
 *   <li>Only active documents can acquire screen wake locks. Previously acquired locks are automatically
 *       released when the document becomes inactive (e.g., when the page is hidden or minimized).</li>
 *   <li>Applications should listen for visibility changes and reacquire the wake lock when the page
 *       becomes visible again if continuous lock is required.</li>
 *   <li>The API requires a secure context (HTTPS) and browser support may vary.</li>
 *   <li>Access is controlled through the Permissions Policy directive {@code screen-wake-lock}.</li>
 * </ul>
 * </p>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Screen_Wake_Lock_API">MDN Web Docs - Screen Wake Lock API</a>
 */
public class ScreenWakeLock {

    /**
     * Represents the state of the screen wake lock.
     */
    public enum State {
        /** The wake lock state is unknown (not yet requested or checked) */
        UNKNOWN,
        /** The wake lock is currently active and preventing screen dimming */
        ACTIVE,
        /** The wake lock has been released */
        RELEASED,
        /** An error occurred while requesting the wake lock */
        ERROR
    }

    private UI ui;

    private ScreenWakeLock(UI ui) {
        this.ui = ui;
    }

    /**
     * Requests a screen wake lock for the specified UI instance.
     * <p>
     * This method acquires a {@code WakeLockSentinel} by calling {@code navigator.wakeLock.request("screen")}
     * in the browser, which prevents the device screen from dimming or locking while the lock is active.
     * </p>
     * <p>
     * <strong>Important:</strong> If the document becomes inactive (e.g., when the user switches tabs or
     * minimizes the browser), the wake lock is automatically released by the browser. Applications that require
     * continuous wake lock should monitor page visibility changes and reacquire the lock when the page becomes
     * visible again.
     * </p>
     *
     * @param ui the Vaadin UI instance for which to request the wake lock
     * @param onReleased an optional callback that will be invoked when the wake lock is released
     *                   (either manually via {@link #release(UI)} or automatically by the browser).
     *                   Can be {@code null} if no callback is needed.
     * @return a CompletableFuture that completes with the wake lock state. Returns {@link State#ACTIVE} if
     *         the lock was successfully acquired, or {@link State#ERROR} if an error occurred (e.g., browser
     *         doesn't support the API, permissions denied, or not in a secure context).
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/WakeLock/request">MDN Web Docs - WakeLock.request()</a>
     */
    public static CompletableFuture<State> request(UI ui, Runnable onReleased) {
        boolean hasReleaseListener = onReleased != null;
        if (hasReleaseListener) {
            AtomicReference<DomListenerRegistration> registrationRef = new AtomicReference<>();
            DomListenerRegistration domListenerRegistration = ui.getElement().addEventListener("wake-lock-released", e -> {
                registrationRef.get().remove();
                onReleased.run();
            });
            registrationRef.set(domListenerRegistration);
        }
        return ui.getElement().executeJs("""
                            const hasReleaseListener = $0;
                            const uiElement = this;
                            // create an async function to request a wake lock
                            try {
                              window._viritin_wakeLock = await navigator.wakeLock.request("screen");
                                if(hasReleaseListener) {
                                    window._viritin_wakeLock.addEventListener('release', () => {
                                        const evt = new CustomEvent('wake-lock-released');
                                        uiElement.dispatchEvent(evt);
                                    });
                                }
                              return "active";
                            } catch (err) {
                                return `${err.name}, ${err.message}`;
                            }
                        """, hasReleaseListener)
                .toCompletableFuture(String.class).thenApply(str -> {
                    if (str.equals("active")) {
                        return State.ACTIVE;
                    } else {
                        return State.ERROR;
                    }
                });

    }

    /**
     * Requests a screen wake lock for the current UI instance with a release callback.
     * <p>
     * This is a convenience method that calls {@link #request(UI, Runnable)} with the current UI.
     * </p>
     *
     * @param onReleased a callback that will be invoked when the wake lock is released
     *                   (either manually via {@link #release()} or automatically by the browser).
     *                   Can be {@code null} if no callback is needed.
     * @return a CompletableFuture that completes with the wake lock state
     * @see #request(UI, Runnable)
     */
    public static CompletableFuture<State> request(Runnable onReleased) {
        return request(UI.getCurrent(), onReleased);
    }

    /**
     * Requests a screen wake lock for the current UI instance without a release callback.
     * <p>
     * This is a convenience method that calls {@link #request(UI, Runnable)} with the current UI
     * and no release callback.
     * </p>
     *
     * @return a CompletableFuture that completes with the wake lock state
     * @see #request(UI, Runnable)
     */
    public static CompletableFuture<State> request() {
        return request(UI.getCurrent(), null);
    }

    /**
     * Releases the screen wake lock for the specified UI instance.
     * <p>
     * This method manually releases the wake lock by calling {@code release()} on the {@code WakeLockSentinel}.
     * Once released, if a new wake lock is needed, a new request must be made via {@link #request(UI, Runnable)}.
     * </p>
     * <p>
     * If no wake lock is currently active, this method has no effect.
     * </p>
     *
     * @param ui the Vaadin UI instance for which to release the wake lock
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/WakeLockSentinel/release">MDN Web Docs - WakeLockSentinel.release()</a>
     */
    public static void release(UI ui) {
        ui.getElement().executeJs("""
                    if(window._viritin_wakeLock) {
                        await window._viritin_wakeLock.release();
                        window._viritin_wakeLock = null;
                    }
                """);
    }

    /**
     * Releases the screen wake lock for the current UI instance.
     * <p>
     * This is a convenience method that calls {@link #release(UI)} with the current UI.
     * </p>
     *
     * @see #release(UI)
     */
    public static void release() {
        release(UI.getCurrent());
    }

    /**
     * Checks the current state of the screen wake lock for the specified UI instance.
     * <p>
     * This method queries the {@code WakeLockSentinel.released} property to determine the current
     * state of the wake lock. Note that this only checks the state of the wake lock that was
     * requested through this wrapper class.
     * </p>
     *
     * @param ui the Vaadin UI instance for which to check the wake lock state
     * @return a CompletableFuture that completes with one of the following states:
     *         <ul>
     *           <li>{@link State#UNKNOWN} - No wake lock has been requested yet</li>
     *           <li>{@link State#ACTIVE} - The wake lock is currently active</li>
     *           <li>{@link State#RELEASED} - The wake lock has been released</li>
     *         </ul>
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/WakeLockSentinel/released">MDN Web Docs - WakeLockSentinel.released</a>
     */
    public static CompletableFuture<State> checkState(UI ui) {
        return ui.getElement().executeJs("""
                    if (!window._viritin_wakeLock) {
                        return "unknown";
                    } else if(window._viritin_wakeLock && window._viritin_wakeLock.released === false) {
                        return "active";
                    } else {
                        return "released";
                    }
                """).toCompletableFuture(String.class).thenApply(str -> {
            if (str.equals("active")) {
                return State.ACTIVE;
            } else if ("unknown".equals(str)) {
                return State.UNKNOWN;
            } else {
                return State.RELEASED;
            }
        });
    }

    /**
     * Checks the current state of the screen wake lock for the current UI instance.
     * <p>
     * This is a convenience method that calls {@link #checkState(UI)} with the current UI.
     * </p>
     *
     * @return a CompletionStage that completes with the wake lock state
     * @see #checkState(UI)
     */
    public static CompletionStage<State> checkState() {
        return checkState(UI.getCurrent());
    }

}
