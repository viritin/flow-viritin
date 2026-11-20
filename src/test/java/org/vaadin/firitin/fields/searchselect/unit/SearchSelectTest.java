package org.vaadin.firitin.fields.searchselect.unit;

import org.junit.jupiter.api.Test;
import org.vaadin.firitin.fields.searchselect.LargeListView;
import org.vaadin.firitin.fields.searchselect.SearchMatching;
import org.vaadin.firitin.fields.searchselect.SearchSelect;
import org.vaadin.firitin.fields.searchselect.TrainingType;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SearchSelectTest {

    @Test
    void matches_all_words_in_any_order_ignoring_case_and_accents() {
        assertThat(SearchMatching.matchesAllWords("Juoksu – (PK1) kevyt", "pk juo")).isTrue();
        assertThat(SearchMatching.matchesAllWords("Juoksu – (PK1) kevyt", "  KEVYT  juoksu ")).isTrue();
        assertThat(SearchMatching.matchesAllWords("Päivä – hieman sairas", "paiva")).isTrue();
        assertThat(SearchMatching.matchesAllWords("Juoksu – (PK1) kevyt", "pk hiihto")).isFalse();
        assertThat(SearchMatching.matchesAllWords("Juoksu – (PK1) kevyt", "")).isTrue();
    }

    @Test
    void filters_the_training_types_like_the_browser_does() {
        SearchSelect<TrainingType> select = select();

        assertThat(select.filter("pk juo")).extracting(TrainingType::label).containsExactlyInAnyOrder(
                "Juoksu – (PK) pitkä lenkki", "Juoksu – (PK1) kevyt", "Juoksu – (PK2) rauhallinen");
    }

    @Test
    void the_value_is_an_item_and_survives_resetting_the_same_items() {
        SearchSelect<TrainingType> select = select();
        TrainingType skiing = new TrainingType("Hiihto", "Vaellus");

        select.setValue(skiing);
        select.setItems(TrainingType.all());

        assertThat(select.getValue()).isEqualTo(skiing);
    }

    @Test
    void a_value_no_longer_among_the_items_is_cleared() {
        SearchSelect<TrainingType> select = select();
        select.setValue(new TrainingType("Hiihto", "Vaellus"));

        select.setItems(List.of(new TrainingType("Juoksu", "Verryttely")));

        assertThat(select.getValue()).isNull();
    }

    @Test
    void the_search_text_can_differ_from_the_label() {
        SearchSelect<TrainingType> select = select();
        // Skiing types can also be found by "sukset" (skis), which isn't in their labels.
        select.setSearchTextGenerator(type -> type.sport().equals("Hiihto") ? type.label() + " sukset" : type.label());

        assertThat(select.filter("vaellus")).hasSize(2);
        assertThat(select.filter("sukset vaellus")).extracting(TrainingType::label)
                .containsExactly("Hiihto – Vaellus");
    }

    @Test
    void small_lists_are_filtered_in_the_browser_large_ones_on_the_server() {
        assertThat(select().isFilteredOnServer()).isFalse();

        SearchSelect<String> large = new SearchSelect<>("Laji", LargeListView.SEASON_TYPES);

        assertThat(large.isFilteredOnServer()).isTrue();
        assertThat(large.filter("")).hasSize(100);
        assertThat(large.filter("pk1 juo 2026")).containsExactly("Juoksu – (PK1) kevyt, kausi 2026");
    }

    @Test
    void a_backend_query_gets_the_typed_text_and_the_result_limit() {
        SearchSelect<String> select = new SearchSelect<>();
        select.setServerResultLimit(20);
        List<String> filters = new ArrayList<>();
        select.setItems(query -> {
            filters.add(query.getFilter().orElseThrow() + "/" + query.getLimit());
            return LargeListView.SEASON_TYPES.stream()
                    .filter(label -> label.contains(query.getFilter().orElseThrow()))
                    .limit(query.getLimit());
        });

        assertThat(select.isFilteredOnServer()).isTrue();
        assertThat(select.filter("kausi 1999")).hasSize(20).allMatch(label -> label.endsWith("1999"));
        assertThat(filters).contains("kausi 1999/20");
    }

    @Test
    void a_value_stays_when_switching_to_a_backend_query() {
        SearchSelect<String> select = new SearchSelect<>("Laji", LargeListView.SEASON_TYPES);
        select.setValue("Juoksu – (PK1) kevyt, kausi 2026");

        select.setItems(query -> LargeListView.SEASON_TYPES.stream().limit(query.getLimit()));

        assertThat(select.getValue()).isEqualTo("Juoksu – (PK1) kevyt, kausi 2026");
    }

    private static SearchSelect<TrainingType> select() {
        SearchSelect<TrainingType> select = new SearchSelect<>("Laji", TrainingType.all());
        select.setItemLabelGenerator(TrainingType::label);
        return select;
    }

    @Test
    void required_aria_label_and_tooltip_as_in_vaadins_own_fields() {
        SearchSelect<TrainingType> select = select();

        select.setRequired(true);
        select.setAriaLabel("Laji");
        select.setTooltipText("Harjoituksen laji");

        assertThat(select.isRequired()).isTrue();
        assertThat(select.isRequiredIndicatorVisible()).isTrue();
        assertThat(select.getAriaLabel()).contains("Laji");
        assertThat(select.getElement().getProperty("accessibleName")).isEqualTo("Laji");
        assertThat(select.getTooltip().getText()).isEqualTo("Harjoituksen laji");
    }
}
