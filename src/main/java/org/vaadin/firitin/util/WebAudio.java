package org.vaadin.firitin.util;

import com.vaadin.flow.component.UI;

import java.util.concurrent.CompletableFuture;

/**
 * Utility for playing simple attention sounds via the browser's Web Audio API.
 *
 * <p>Handy when a Vaadin UI needs to grab the user's attention with a short
 * beep without shipping any audio assets &ndash; the tones are synthesized
 * directly in the browser.</p>
 *
 * <p>Browsers require a user gesture (typically a click or key press) before
 * an {@code AudioContext} is allowed to produce sound. If you want to play
 * sounds triggered by something other than a direct user interaction, call
 * {@link #resume()} from inside e.g. some click handler once to unlock audio
 * playback for the rest of the session.</p>
 *
 * <pre>
 * WebAudio.get().playOkBeep();
 * WebAudio.get().playAlarm();
 * WebAudio.get().playBeep(150, 600, WebAudio.Waveform.SQUARE);
 * </pre>
 */
public class WebAudio {

    public static final double DEFAULT_FREQUENCY_HZ = 800;

    /**
     * Oscillator waveform. {@link #SINE} is soft and bell-like, {@link #TRIANGLE}
     * a touch harsher, while {@link #SQUARE} and {@link #SAWTOOTH} sound buzzy
     * and are a lot more attention-grabbing &ndash; useful for warnings.
     */
    public enum Waveform {
        SINE, SQUARE, SAWTOOTH, TRIANGLE;

        String jsType() {
            return name().toLowerCase();
        }
    }

    private static final String INIT_CTX = """
            window.__viritinAudio = window.__viritinAudio || {};
            if (!window.__viritinAudio.ctx) {
                window.__viritinAudio.ctx = new (window.AudioContext || window.webkitAudioContext)();
            }
            const ctx = window.__viritinAudio.ctx;
            """;

    private final UI ui;

    public static WebAudio get() {
        return new WebAudio(UI.getCurrent());
    }

    public WebAudio(UI ui) {
        this.ui = ui;
    }

    /**
     * Plays a short, friendly 100 ms sine beep at 800 Hz &ndash; the kind
     * of sound you'd use to confirm a successful action.
     */
    public void playOkBeep() {
        playBeep(100, DEFAULT_FREQUENCY_HZ, Waveform.SINE);
    }

    /**
     * Plays a short 100 ms beep at 800 Hz with a sine waveform.
     */
    public void playBeep() {
        playBeep(100, DEFAULT_FREQUENCY_HZ, Waveform.SINE);
    }

    /**
     * Plays a sine beep at 800 Hz with the given duration.
     *
     * @param durationMs the beep duration in milliseconds
     */
    public void playBeep(int durationMs) {
        playBeep(durationMs, DEFAULT_FREQUENCY_HZ, Waveform.SINE);
    }

    /**
     * Plays a sine beep with the given duration and frequency.
     *
     * @param durationMs  the beep duration in milliseconds
     * @param frequencyHz the tone frequency in Hertz
     */
    public void playBeep(int durationMs, double frequencyHz) {
        playBeep(durationMs, frequencyHz, Waveform.SINE);
    }

    /**
     * Plays a beep with the given duration, frequency and waveform. A short
     * exponential fade-out is applied so the tone doesn't end with an audible
     * click. Use {@link Waveform#SQUARE} or {@link Waveform#SAWTOOTH} for a
     * harsher, more attention-grabbing sound.
     *
     * @param durationMs  the beep duration in milliseconds
     * @param frequencyHz the tone frequency in Hertz
     * @param waveform    the oscillator waveform
     */
    public void playBeep(int durationMs, double frequencyHz, Waveform waveform) {
        ui.getPage().executeJs(INIT_CTX + """
                const dur = $0 / 1000;
                const osc = ctx.createOscillator();
                const gain = ctx.createGain();
                osc.connect(gain);
                gain.connect(ctx.destination);
                osc.frequency.value = $1;
                osc.type = $2;
                const t = ctx.currentTime;
                gain.gain.setValueAtTime(0.3, t);
                gain.gain.exponentialRampToValueAtTime(0.01, t + dur);
                osc.start(t);
                osc.stop(t + dur);
                """, durationMs, frequencyHz, waveform.jsType());
    }

    /**
     * Plays {@code count} sine beeps at 800 Hz, each lasting {@code durationMs}
     * milliseconds, with {@code gapMs} milliseconds of silence between them.
     *
     * @param count      the number of beeps to play
     * @param durationMs duration of each beep in milliseconds
     * @param gapMs      silence between consecutive beeps in milliseconds
     * @return a future that completes after the last beep has finished
     */
    public CompletableFuture<Void> playBeeps(int count, int durationMs, int gapMs) {
        return playBeeps(count, durationMs, gapMs, DEFAULT_FREQUENCY_HZ, Waveform.SINE);
    }

    /**
     * Plays {@code count} sine beeps with the given duration, frequency and gap.
     *
     * @param count       the number of beeps to play
     * @param durationMs  duration of each beep in milliseconds
     * @param gapMs       silence between consecutive beeps in milliseconds
     * @param frequencyHz the tone frequency in Hertz
     * @return a future that completes after the last beep has finished
     */
    public CompletableFuture<Void> playBeeps(int count, int durationMs, int gapMs, double frequencyHz) {
        return playBeeps(count, durationMs, gapMs, frequencyHz, Waveform.SINE);
    }

    /**
     * Plays {@code count} beeps with the given duration, frequency, gap and
     * waveform. All beeps are scheduled in a single round trip to the browser,
     * so timing is sample-accurate.
     *
     * @param count       the number of beeps to play
     * @param durationMs  duration of each beep in milliseconds
     * @param gapMs       silence between consecutive beeps in milliseconds
     * @param frequencyHz the tone frequency in Hertz
     * @param waveform    the oscillator waveform
     * @return a future that completes after the last beep has finished
     */
    public CompletableFuture<Void> playBeeps(int count, int durationMs, int gapMs, double frequencyHz, Waveform waveform) {
        CompletableFuture<Void> done = new CompletableFuture<>();
        ui.getPage().executeJs(INIT_CTX + """
                const count = $0;
                const dur = $1 / 1000;
                const gap = $2 / 1000;
                const f = $3;
                const type = $4;
                const beep = (start, dur, f) => {
                    const osc = ctx.createOscillator();
                    const gain = ctx.createGain();
                    osc.connect(gain);
                    gain.connect(ctx.destination);
                    osc.frequency.value = f;
                    osc.type = type;
                    gain.gain.setValueAtTime(0.3, start);
                    gain.gain.exponentialRampToValueAtTime(0.01, start + dur);
                    osc.start(start);
                    osc.stop(start + dur);
                };
                const start = ctx.currentTime;
                let t = start;
                for (let i = 0; i < count; i++) {
                    beep(t, dur, f);
                    t += dur + gap;
                }
                const totalMs = (t - gap - start + dur) * 1000;
                return new Promise(r => setTimeout(r, Math.max(0, totalMs)));
                """, count, durationMs, gapMs, frequencyHz, waveform.jsType()).then(
                v -> done.complete(null),
                err -> done.completeExceptionally(new RuntimeException(err)));
        return done;
    }

    /**
     * Plays a dramatic two-tone klaxon for warnings and errors: six cycles of
     * alternating 880 Hz and 600 Hz square-wave tones (200 ms each), with a
     * flat envelope so the sound stays loud throughout. Total duration is
     * roughly 2.4 seconds.
     *
     * @return a future that completes after the alarm has finished
     */
    public CompletableFuture<Void> playAlarm() {
        CompletableFuture<Void> done = new CompletableFuture<>();
        ui.getPage().executeJs(INIT_CTX + """
                const cycles = 4;
                const toneDur = 0.2;
                const f1 = 880;
                const f2 = 600;
                const vol = 0.4;
                const tone = (start, dur, f) => {
                    const osc = ctx.createOscillator();
                    const gain = ctx.createGain();
                    osc.connect(gain);
                    gain.connect(ctx.destination);
                    osc.frequency.value = f;
                    osc.type = 'square';
                    gain.gain.setValueAtTime(0.0, start);
                    gain.gain.linearRampToValueAtTime(vol, start + 0.005);
                    gain.gain.setValueAtTime(vol, start + dur - 0.005);
                    gain.gain.linearRampToValueAtTime(0.0, start + dur);
                    osc.start(start);
                    osc.stop(start + dur);
                };
                const start = ctx.currentTime;
                let t = start;
                for (let i = 0; i < cycles; i++) {
                    tone(t, toneDur, f1); t += toneDur;
                    tone(t, toneDur, f2); t += toneDur;
                }
                const totalMs = (t - start) * 1000;
                return new Promise(r => setTimeout(r, totalMs));
                """).then(
                v -> done.complete(null),
                err -> done.completeExceptionally(new RuntimeException(err)));
        return done;
    }

    /**
     * Plays a "get ready" sequence: five short 800 Hz beeps spaced one second
     * apart, followed by a longer 500 ms beep at 1000 Hz to indicate the
     * actual start. The returned future completes when the last beep has
     * finished playing on the client.
     *
     * @return a future that completes after the sequence has finished
     */
    public CompletableFuture<Void> playStartSequence() {
        CompletableFuture<Void> done = new CompletableFuture<>();
        ui.getPage().executeJs(INIT_CTX + """
                const beep = (start, dur, f) => {
                    const osc = ctx.createOscillator();
                    const gain = ctx.createGain();
                    osc.connect(gain);
                    gain.connect(ctx.destination);
                    osc.frequency.value = f;
                    osc.type = 'sine';
                    gain.gain.setValueAtTime(0.3, start);
                    gain.gain.exponentialRampToValueAtTime(0.01, start + dur);
                    osc.start(start);
                    osc.stop(start + dur);
                };
                let t = ctx.currentTime;
                for (let i = 0; i < 5; i++) { beep(t, 0.1, 800); t += 1.0; }
                beep(t, 0.5, 1000);
                const totalMs = (t + 0.5 - ctx.currentTime) * 1000;
                return new Promise(r => setTimeout(r, totalMs));
                """).then(
                v -> done.complete(null),
                err -> done.completeExceptionally(new RuntimeException(err)));
        return done;
    }

    /**
     * Resumes the underlying {@code AudioContext} if it was suspended.
     * Most browsers suspend audio contexts created outside of a user
     * gesture; calling this once from inside a click handler is usually
     * enough to unlock subsequent playback.
     *
     * @return a future that completes when the context is running again
     */
    public CompletableFuture<Void> resume() {
        CompletableFuture<Void> done = new CompletableFuture<>();
        ui.getPage().executeJs(INIT_CTX + """
                if (ctx.state === 'suspended') {
                    return ctx.resume();
                }
                """).then(
                v -> done.complete(null),
                err -> done.completeExceptionally(new RuntimeException(err)));
        return done;
    }
}
