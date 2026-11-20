package org.vaadin.firitin.fields.searchselect;

import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasAriaLabel;
import com.vaadin.flow.component.HasHelper;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasPlaceholder;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.shared.HasClearButton;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.shared.HasValidationProperties;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.KeyMapper;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.internal.JacksonUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Selects one item of a list, with search: semantically a {@code Select} (no
 * custom values), but you can type to filter.
 * <p>
 * <b>Small and medium lists</b> (up to {@link #setClientSideItemLimit(int) 1000}
 * items by default) are sent to the browser once and filtered there: typing
 * causes no server round trips at all; only the selected item is sent back. The
 * default filter matches all typed words in any order, ignoring case and accents
 * (see {@link SearchMatching}); provide your own with
 * {@link #setClientFilter(String)}. Items can be found by more than their label
 * with {@link #setSearchTextGenerator(ItemLabelGenerator)}.
 * <p>
 * <b>Large lists</b> — more in-memory items than the limit, or a backend query
 * given with {@link #setItems(CallbackDataProvider.FetchCallback)} — are filtered
 * on the server, but only when the user pauses typing
 * ({@link #setServerFilterDelay(Duration) 300 ms} by default), so a fluent typist
 * causes one request rather than one per keystroke. Meanwhile, as long as the
 * typed text refines the previous search, the previous results are narrowed down
 * in the browser at once. At most {@link #setServerResultLimit(int) 100} matches
 * are shown; typing more narrows them down.
 * <p>
 * Renders as Vaadin's {@code vaadin-combo-box} web component (look, keyboard use,
 * accessibility and theming as in the rest of a Vaadin app), driven by a small
 * connector of its own instead of Flow's {@link ComboBox} — no custom frontend
 * bundle needed.
 *
 * @param <T> the item type
 *
 * @deprecated A new component with possibly changing API
 */
@Deprecated(forRemoval = false)
@Tag("vaadin-combo-box")
@Uses(ComboBox.class) // brings in the vaadin-combo-box web component, in the platform's version
public class SearchSelect<T> extends AbstractSinglePropertyField<SearchSelect<T>, T>
        implements HasLabel, HasHelper, HasValidationProperties, HasClearButton, HasPlaceholder,
        HasSize, HasStyle, Focusable<SearchSelect<T>>, HasAriaLabel, HasTooltip {

    /**
     * The connector: keeps the items in the element and filters them on each
     * filter change, by setting {@code filteredItems} (the web component's
     * documented custom filtering hook).
     * <p>
     * $0 = items (all of them, or in server mode the results for {@code $3}),
     * $1 = custom filter function body or null, $2 = server filter delay in ms, or
     * -1 to filter only in the browser, $3 = the filter $0 are the results for,
     * $4 = the selected item or null.
     * <p>
     * In server mode, the items are the latest results. A changed filter is sent
     * to the server only after a pause of $2 ms; meanwhile, if the new filter
     * refines the one of the latest results, those are narrowed down at once.
     * Late responses for an outdated filter are ignored.
     */
    private static final String UPDATE_ITEMS = """
            const comboBox = this;
            const normalize = text => (text ?? '').toString().normalize('NFD')
                    .replace(/\\p{M}+/gu, '').toLowerCase();
            const matchesAllWords = (item, filter) => {
                const text = normalize(item.search);
                return normalize(filter).trim().split(/\\s+/).filter(Boolean)
                        .every(word => text.includes(word));
            };
            const state = comboBox.__searchSelect ??= {};
            state.items = $0;
            state.matches = $1 ? new Function('item', 'filter', 'normalize', $1) : matchesAllWords;
            state.delay = $2;
            state.resultFilter = $3;
            const narrow = filter => state.items.filter(item => state.matches(item, filter, normalize));
            state.apply = () => {
                const filter = comboBox.filter || '';
                clearTimeout(state.timer);
                if (state.delay < 0) {
                    comboBox.filteredItems = filter ? narrow(filter) : state.items;
                    return;
                }
                if (filter === state.resultFilter) {
                    comboBox.filteredItems = state.items;
                    comboBox.loading = false;
                    return;
                }
                if (normalize(filter).startsWith(normalize(state.resultFilter))) {
                    comboBox.filteredItems = narrow(filter);
                }
                comboBox.loading = true;
                state.timer = setTimeout(async () => {
                    const results = await comboBox.$server.searchSelectFetch(filter);
                    if ((comboBox.filter || '') === filter) {
                        state.items = results;
                        state.resultFilter = filter;
                        comboBox.filteredItems = results;
                        comboBox.loading = false;
                    }
                }, state.delay);
            };
            if (!state.installed) {
                state.installed = true;
                comboBox.itemLabelPath = 'label';
                comboBox.itemValuePath = 'key';
                comboBox.itemIdPath = 'key';
                comboBox.addEventListener('filter-changed', () => state.apply());
            }
            comboBox.loading = false;
            state.apply();
            comboBox.selectedItem = $4;
            """;

    /** Shows the item $0 (or none) as selected. */
    private static final String SELECT = "this.selectedItem = $0;";

    private static final int DEFAULT_CLIENT_SIDE_ITEM_LIMIT = 1000;

    private final KeyMapper<T> keyMapper = new KeyMapper<>();
    /** In-memory items; empty when {@link #fetch} queries a backend. */
    private final List<T> items = new ArrayList<>();
    private CallbackDataProvider.FetchCallback<T, String> fetch;
    private ItemLabelGenerator<T> itemLabelGenerator = String::valueOf;
    private ItemLabelGenerator<T> searchTextGenerator;
    private String clientFilter;
    private int clientSideItemLimit = DEFAULT_CLIENT_SIDE_ITEM_LIMIT;
    private Duration serverFilterDelay = Duration.ofMillis(300);
    private int serverResultLimit = 100;
    /** The latest and the previous results sent in server mode: their keys must stay valid. */
    private List<T> latestResults = List.of();
    private List<T> previousResults = List.of();

    public SearchSelect() {
        super("value", null, String.class, SearchSelect::toModel, SearchSelect::toPresentation);
        // The server decides validity (e.g. via Binder); the web component must not override it.
        getElement().setProperty("manualValidation", true);
        getElement().setProperty("allowCustomValue", false);
    }

    public SearchSelect(String label) {
        this();
        setLabel(label);
    }

    public SearchSelect(String label, Collection<T> items) {
        this(label);
        setItems(items);
    }

    /**
     * Whether a value is required: shows the required indicator, as
     * {@link #setRequiredIndicatorVisible(boolean)} (which this is an alias for,
     * as in {@code ComboBox}). Checking it is up to the server, e.g. Binder's
     * {@code asRequired()}.
     */
    public void setRequired(boolean required) {
        setRequiredIndicatorVisible(required);
    }

    /** Whether a value is required; see {@link #setRequired(boolean)}. */
    public boolean isRequired() {
        return isRequiredIndicatorVisible();
    }

    /**
     * The accessible name, for a field without a visible label (e.g. in a compact
     * toolbar). Set as the web component's {@code accessibleName}, as Vaadin's own
     * fields do, so screen readers announce it on the input.
     */
    @Override
    public void setAriaLabel(String ariaLabel) {
        getElement().setProperty("accessibleName", ariaLabel);
    }

    @Override
    public Optional<String> getAriaLabel() {
        return Optional.ofNullable(getElement().getProperty("accessibleName"));
    }

    @Override
    public void setAriaLabelledBy(String labelledBy) {
        getElement().setProperty("accessibleNameRef", labelledBy);
    }

    @Override
    public Optional<String> getAriaLabelledBy() {
        return Optional.ofNullable(getElement().getProperty("accessibleNameRef"));
    }

    /**
     * The items to choose from, in the order shown. Up to the
     * {@link #setClientSideItemLimit(int) client side item limit} they are filtered
     * in the browser, above it on the server with the default rule
     * ({@link SearchMatching#matchesAllWords} on the search text).
     */
    public void setItems(Collection<T> items) {
        T value = getValue();
        this.fetch = null;
        this.items.clear();
        this.items.addAll(items);
        if (value != null && !this.items.contains(value)) {
            clear();
        }
        updateClient();
    }

    @SafeVarargs
    public final void setItems(T... items) {
        setItems(List.of(items));
    }

    /**
     * Queries the items from a backend, e.g. a database, always filtered on the
     * server. The query's filter is the typed text (empty for "no filter"); its
     * limit is the {@link #setServerResultLimit(int) result limit}. Example:
     * {@code select.setItems(query -> repository.findByNameContaining(query.getFilter().orElse(""),
     * PageRequest.of(0, query.getLimit())).stream())}
     * <p>
     * {@link SearchMatching#matchesAllWords} implements the default rule of the
     * in-memory lists, if you want to match it.
     */
    public void setItems(CallbackDataProvider.FetchCallback<T, String> fetch) {
        T value = getValue();
        this.fetch = Objects.requireNonNull(fetch);
        this.items.clear();
        if (value != null) {
            // The value can't be checked against a backend cheaply; it stays.
            keyMapper.key(value);
        }
        updateClient();
    }

    /**
     * The in-memory items; empty when the items are queried from a backend
     * ({@link #setItems(CallbackDataProvider.FetchCallback)}).
     */
    public List<T> getItems() {
        return List.copyOf(items);
    }

    /** Whether filtering happens on the server (large list or backend query). */
    public boolean isFilteredOnServer() {
        return fetch != null || items.size() > clientSideItemLimit;
    }

    /** How an item is shown (default: {@code toString()}). */
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        this.itemLabelGenerator = Objects.requireNonNull(itemLabelGenerator);
        updateClient();
    }

    public ItemLabelGenerator<T> getItemLabelGenerator() {
        return itemLabelGenerator;
    }

    /**
     * The text searched for an item, when it should be found by more than its
     * label, e.g. synonyms or codes (default: the label). Not used with a backend
     * query, which does its own matching.
     */
    public void setSearchTextGenerator(ItemLabelGenerator<T> searchTextGenerator) {
        this.searchTextGenerator = searchTextGenerator;
        updateClient();
    }

    /** The search text of an item (see {@link #setSearchTextGenerator}). */
    public String getSearchText(T item) {
        return searchTextGenerator != null ? searchTextGenerator.apply(item) : itemLabelGenerator.apply(item);
    }

    /**
     * A custom filter, as the body of a JavaScript function
     * {@code (item, filter, normalize) => boolean}: {@code item.label} and
     * {@code item.search} are the item's label and search text, {@code filter} is
     * the typed text and {@code normalize(text)} lower-cases and removes accents.
     * Example (prefix match): {@code return normalize(item.label).startsWith(normalize(filter));}
     * <p>
     * When filtering on the server, this only narrows down the previous results
     * while waiting for the server; make the server side match the same way.
     * <p>
     * {@code null} restores the default: all words in any order, ignoring case and
     * accents ({@link SearchMatching#matchesAllWords}).
     */
    public void setClientFilter(String functionBody) {
        this.clientFilter = functionBody;
        updateClient();
    }

    /**
     * Up to how many in-memory items are filtered in the browser (default 1000);
     * longer lists are filtered on the server.
     */
    public void setClientSideItemLimit(int clientSideItemLimit) {
        this.clientSideItemLimit = clientSideItemLimit;
        updateClient();
    }

    public int getClientSideItemLimit() {
        return clientSideItemLimit;
    }

    /**
     * How long the user must pause typing before the filter is sent to the server
     * (default 300 ms), when filtering on the server.
     */
    public void setServerFilterDelay(Duration serverFilterDelay) {
        this.serverFilterDelay = Objects.requireNonNull(serverFilterDelay);
        updateClient();
    }

    public Duration getServerFilterDelay() {
        return serverFilterDelay;
    }

    /** How many matches at most are shown when filtering on the server (default 100). */
    public void setServerResultLimit(int serverResultLimit) {
        this.serverResultLimit = serverResultLimit;
        updateClient();
    }

    public int getServerResultLimit() {
        return serverResultLimit;
    }

    /**
     * The items shown for the given text: with in-memory items, those matching the
     * default rule (the same the browser applies); with a backend query, its
     * results. Handy in tests and server-side logic. Limited to the
     * {@link #setServerResultLimit(int) result limit} when filtering on the server.
     */
    public List<T> filter(String filterText) {
        String filter = filterText == null ? "" : filterText;
        if (fetch != null) {
            return fetch.fetch(new Query<>(0, serverResultLimit, List.of(), null, filter)).toList();
        }
        var matches = items.stream().filter(item -> SearchMatching.matchesAllWords(getSearchText(item), filter));
        return (isFilteredOnServer() ? matches.limit(serverResultLimit) : matches).toList();
    }

    /** Called by the connector after a typing pause, when filtering on the server. */
    @ClientCallable
    private JsonNode searchSelectFetch(String filter) {
        if (!isFilteredOnServer()) {
            return JacksonUtils.createArrayNode();
        }
        List<T> results = filter(filter);
        forgetKeysExcept(results);
        return toJson(results);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        updateClient();
    }

    @Override
    protected void setPresentationValue(T value) {
        super.setPresentationValue(value);
        getElement().executeJs(SELECT, toJson(value));
    }

    private void updateClient() {
        boolean onServer = isFilteredOnServer();
        List<T> shown = onServer ? filter("") : items;
        keyMapper.removeAll();
        latestResults = onServer ? shown : List.of();
        previousResults = List.of();
        // the value got a new key
        getElement().setProperty("value", toPresentation(this, getValue()));
        getElement().executeJs(UPDATE_ITEMS, toJson(shown), clientFilter,
                onServer ? (int) serverFilterDelay.toMillis() : -1, "", toJson(getValue()));
    }

    /**
     * Keeps the keys of the latest and previous results (the user may pick one of
     * those while the new ones are on their way) and of the value; forgets the
     * rest, so the key map doesn't grow without limit.
     */
    private void forgetKeysExcept(List<T> newResults) {
        Set<T> keep = new HashSet<>(newResults);
        keep.addAll(latestResults);
        T value = getValue();
        if (value != null) {
            keep.add(value);
        }
        previousResults.stream().filter(item -> !keep.contains(item)).forEach(keyMapper::remove);
        previousResults = latestResults;
        latestResults = newResults;
    }

    private ArrayNode toJson(List<T> list) {
        ArrayNode json = JacksonUtils.createArrayNode();
        list.forEach(item -> json.add(toJson(item)));
        return json;
    }

    private JsonNode toJson(T item) {
        if (item == null) {
            return JacksonUtils.nullNode();
        }
        ObjectNode node = JacksonUtils.createObjectNode();
        node.put("key", keyMapper.key(item));
        node.put("label", itemLabelGenerator.apply(item));
        node.put("search", getSearchText(item));
        return node;
    }

    private static <T> T toModel(SearchSelect<T> select, String key) {
        return key == null || key.isEmpty() ? null : select.keyMapper.get(key);
    }

    private static <T> String toPresentation(SearchSelect<T> select, T value) {
        return value == null ? "" : select.keyMapper.key(value);
    }
}
