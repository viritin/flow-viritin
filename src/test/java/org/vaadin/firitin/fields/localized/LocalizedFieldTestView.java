package org.vaadin.firitin.fields.localized;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Locale;

/**
 * A minimal route used by {@link LocalizedFieldTest} to exercise the component
 * through the browserless test API (which, at this version, navigates to a view
 * rather than attaching a standalone component). It exposes the fields so the
 * test can drive and assert on them directly.
 */
@Route("localized-field-test")
public class LocalizedFieldTestView extends VerticalLayout {

    static final Locale EN = Locale.ENGLISH;
    static final Locale FI = Locale.of("fi");
    static final Locale SV = Locale.of("sv");

    /** Tab mode. Deliberately unsorted input; labeled via the (String, …) ctor. */
    public final LocalizedTextField text = new LocalizedTextField("Texts", SV, EN, FI);

    /** Same languages but a threshold of 2, so it uses a combo box selector. */
    public final LocalizedTextField combo = new LocalizedTextField(List.of(SV, EN, FI), 2);

    public LocalizedFieldTestView() {
        add(text, combo);
    }
}
