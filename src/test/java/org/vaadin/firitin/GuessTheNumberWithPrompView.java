package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.Random;

import static org.vaadin.firitin.util.BrowserPrompt.promptInteger;

@Route
public class GuessTheNumberWithPrompView extends VerticalLayout {

    private int secretNumber;
    private int attempts;

    public GuessTheNumberWithPrompView() {
        add(new H1("Guess a number game"));
        add(new Button("Start the game", e -> startGame()));
        setAlignItems(Alignment.CENTER);
    }

    void startGame() {
        secretNumber = new Random().nextInt(10) + 1;
        attempts = 0;
        promptInteger("Guess a number between 1-10!?").thenAccept(this::checkGuess);
    }

    void checkGuess(Integer guess) {
        attempts++;
        if (guess == secretNumber) {
            Notification.show("Congrats, you got it on %s attempt(s). The right answer is %s."
                            .formatted(attempts, guess)).setPosition(Notification.Position.MIDDLE);
        } else {
            String hint = guess < secretNumber ? "higher" : "lower";
            promptInteger("Try again. The secret number is "+hint+" than your guess.")
                    .thenAccept(this::checkGuess);
        }
    }
}