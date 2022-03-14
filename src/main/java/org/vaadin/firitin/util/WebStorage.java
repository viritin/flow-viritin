package org.vaadin.firitin.util;

import com.vaadin.flow.component.UI;

public class WebStorage {

    enum Storage {
        localStorage, sessionStorage
    }

    public interface Callback {
        void onValueDetected(String value);
    }

    public static void setItem(String key, String value) {
        setItem(Storage.localStorage, key, value);
    }

    public static void setItem(Storage storage, String key, String value) {
        setItem(UI.getCurrent(), storage, key, value);
    }

    public static void setItem(UI ui, Storage storage, String key, String value) {
        ui.getPage().executeJs("window[$0].setItem($1,$2)", storage.toString(), key, value);
    }

    public static void getItem(String key, Callback callback) {
        getItem(Storage.localStorage, key, callback);
    }

    public static void getItem(Storage storage, String key, Callback callback) {
        getItem(UI.getCurrent(), storage, key, callback);
    }

    public static void getItem(UI ui, Storage storage, String key, Callback callback) {
        UI.getCurrent().getPage()
                .executeJs("return window[$0].getItem($1);", storage.toString(), key)
                .then(String.class, callback::onValueDetected);
    }

}
