package org.vaadin.firitin;

import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.util.WebAudio;

@Route
public class WebAudioView extends VerticalLayout {

    public WebAudioView() {
        WebAudio audio = WebAudio.get();

        add(new Markdown("""
                # WebAudio Demo

                Synthesizes simple attention sounds via the browser's Web Audio API,
                no audio files required. Browsers usually require a user gesture
                before audio can play, so press one of the buttons below.
                """));

        add(new VButton("Ok beep", e -> audio.playOkBeep()));

        add(new VButton("Alarm (dramatic warning)", e -> audio.playAlarm()));

        add(new VButton("Long low beep (500 ms, 400 Hz)",
                e -> audio.playBeep(500, 400)));

        add(new VButton("Square wave beep (300 ms, 500 Hz)",
                e -> audio.playBeep(300, 500, WebAudio.Waveform.SQUARE)));

        add(new VButton("Sawtooth beep (300 ms, 500 Hz)",
                e -> audio.playBeep(300, 500, WebAudio.Waveform.SAWTOOTH)));

        add(new VButton("3 beeps, 100 ms each, 200 ms gap",
                e -> audio.playBeeps(3, 100, 200)));

        add(new VButton("3 square-wave warning beeps (200 ms, 500 Hz)",
                e -> audio.playBeeps(3, 200, 150, 500, WebAudio.Waveform.SQUARE)));

        add(new VButton("Start sequence (5 + 1 beeps)", e ->
                audio.playStartSequence().thenRun(() ->
                        e.getSource().getUI().ifPresent(ui ->
                                ui.access(() -> Notification.show("Sequence finished"))))));
    }
}
