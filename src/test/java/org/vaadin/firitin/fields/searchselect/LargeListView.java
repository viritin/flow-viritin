package org.vaadin.firitin.fields.searchselect;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Test and demo view: ~19 000 items, too many for the browser, filtered on the
 * server after a typing pause. One select has an in-memory list, the other
 * queries a "backend".
 */
@Route("searchselect-large")
public class LargeListView extends VerticalLayout {

    /** Every training type for 200 seasons, e.g. "Juoksu – (PK1) kevyt, season 2026". */
    public static final List<String> SEASON_TYPES = IntStream.rangeClosed(1901, 2100).boxed()
            .flatMap(season -> TrainingType.all().stream().map(type -> type.label() + ", season " + season))
            .toList();

    public LargeListView() {
        SearchSelect<String> inMemory = new SearchSelect<>("In memory", SEASON_TYPES);
        inMemory.setId("in-memory");
        inMemory.setWidth("26em");

        AtomicInteger queries = new AtomicInteger();
        Paragraph queryCount = new Paragraph("Queries: 0");
        queryCount.setId("queries");
        SearchSelect<String> backend = new SearchSelect<>("From backend");
        backend.setId("backend");
        backend.setWidth("26em");
        backend.setItems(query -> {
            String filter = query.getFilter().orElse("");
            queryCount.setText("Queries: " + queries.incrementAndGet() + " (latest: \"" + filter + "\")");
            return SEASON_TYPES.stream()
                    .filter(label -> SearchMatching.matchesAllWords(label, filter))
                    .skip(query.getOffset())
                    .limit(query.getLimit());
        });

        Paragraph selected = new Paragraph("Selected: –");
        selected.setId("selected");
        inMemory.addValueChangeListener(event -> selected.setText("Selected: " + event.getValue()));
        backend.addValueChangeListener(event -> selected.setText("Selected: " + event.getValue()));

        add(inMemory, backend, queryCount, selected);
    }
}
