package org.vaadin.firitin.util;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;

/**
 * A helper that provides access to browser cookies.
 *
 * @author Matti Tahvonen
 */
public class BrowserCookie {
    @Tag("cookie-reader")
    @HtmlImport("org/vaadin/firitin/cookie-reader.html")
    public static class CookieReader extends PolymerTemplate<TemplateModel> {
        
        private Callback callback;

        @ClientCallable
        public void call(String arguments) {
            callback.onValueDetected(arguments);
        }
        
        public void execute(String expression, Callback callback) {
            this.callback = callback;
            getElement().callFunction("readCookie", expression);
        }
    }
    
    public interface Callback {

        void onValueDetected(String value);
    }
    
    public static void setCookie(String key, String value) {
        setCookie(key, value, "/");
    }

    public static void setCookie(String key, String value, LocalDateTime expirationTime) {

        String expires = toCookieGMTDate(expirationTime);

        getPage().executeJavaScript(
                String.format("document.cookie = \"%s=%s; expires=%s\";", key, value, expires));
        
    }

    private static Page getPage() {
        return getUI().getPage();
    }

    private static UI getUI() {
        UI current = UI.getCurrent();
        Objects.requireNonNull(current, "UI.getCurrent() shouldn't return null when executing this method!");
        return current;
    }

    private static String toCookieGMTDate(LocalDateTime expirationTime) {
        ZonedDateTime zdt = ZonedDateTime.of(expirationTime, ZoneOffset.UTC);
        String expires = zdt.format(DateTimeFormatter.RFC_1123_DATE_TIME);
        return expires;
    }

    public static void setCookie(String key, String value, String path, LocalDateTime expirationTime) {

        String expires = toCookieGMTDate(expirationTime);

        getPage().executeJavaScript(String.format(
                "document.cookie = \"%s=%s; path=%s\"; Expires=%s\";", key, value, path, expires
        ));
    }

    public static void setCookie(String key, String value, String path) {
        getPage().executeJavaScript(String.format(
                "document.cookie = \"%s=%s; path=%s\";", key, value, path
        ));
    }

    public static void detectCookieValue(String key, final Callback callback) {
        CookieReader cookieReader = new CookieReader();
        cookieReader.getElement().getNode().runWhenAttached(ui -> {
            cookieReader.execute(key, cookieValue -> {
                callback.onValueDetected(cookieValue);
                ui.remove(cookieReader);
            }); 
        });
        
        UI.getCurrent().add(cookieReader);
    }
    
        /**
     *
     * Binds a HasValue&lt;V&gt; to a cookie that lives for a month. The cookies value is updated via a
     * ValueChangeListener.
     *
     * @param <V> The value-type of the HasValue&lt;&gt;
     * @param field The HasValue&lt;V&gt; that gets bound.
     * @param name The name of the cookie
     * @param cb A BrowserCookie.Callback that gets called with the actual value of the cookie. The value is guaranteed to be not null.
     *
     * @throws IllegalArgumentException if field or name are null or if name is empty.
     */
    public static <E extends ValueChangeEvent<V>, V> void bindValueToCookie(HasValue<E, V> field, String name, Callback cb) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }
        if (Objects.isNull(field)) {
            throw new IllegalArgumentException("Field must not be null");
        }

        detectCookieValue(name, (v) -> {
                                    if (v != null) {
                                        cb.onValueDetected(v);
                                    }
                                });

        field.addValueChangeListener((event) -> {
            setCookie(name, event.getValue().toString(), LocalDateTime.now().plusMonths(1l));
        });
    }

    /**
     * Binds a HasValue&lt;String&gt; to a cookie that lives for a month. The cookies value is updated via a
     * ValueChangeListener. Its crrent value is copied into the HasValue&lt;String&gt;.
     *
     * @param field The HasValue&lt;String&gt; that gets bound.
     * @param name The name of the cookie
     *
     * @throws IllegalArgumentException if field or name are null or if name is empty.
     */
    public static void bindValueToCookie(HasValue<? extends ValueChangeEvent<String>, String> field, String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }
        if (Objects.isNull(field)) {
            throw new IllegalArgumentException("Field must not be null");
        }

        detectCookieValue(name, (v) -> {
                                    if (v != null) {
                                        field.setValue(v);
                                    }
                                });

        field.addValueChangeListener((event) -> {
            setCookie(name, event.getValue(), LocalDateTime.now().plusMonths(1l));
        });
    }
}
