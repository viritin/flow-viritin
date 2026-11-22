package org.vaadin.firitin.appframework.menuaccess;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;

/** The views and groups of {@link MenuAccessTest}'s menu. */
class Views {

    @MenuItem(title = "Home", order = 1)
    @Route(value = "home", layout = MenuAccessLayout.class)
    public static class Home extends Div {
    }

    @MenuItem(title = "Secret top", order = 2)
    @Route(value = "secret-top", layout = MenuAccessLayout.class)
    @Secret
    public static class SecretTop extends Div {
    }

    /** A group without a view of its own; one of its views is denied. */
    @MenuItem(title = "Settings", order = 3)
    public static class Settings {
    }

    @MenuItem(title = "Profile", order = 1, parent = Settings.class)
    @Route(value = "profile", layout = MenuAccessLayout.class)
    public static class Profile extends Div {
    }

    @MenuItem(title = "Users", order = 2, parent = Settings.class)
    @Route(value = "users", layout = MenuAccessLayout.class)
    @Secret
    public static class Users extends Div {
    }

    /** A group whose every view is denied. */
    @MenuItem(title = "Admin", order = 4)
    public static class Admin {
    }

    @MenuItem(title = "Audit log", parent = Admin.class)
    @Route(value = "audit", layout = MenuAccessLayout.class)
    @Secret
    public static class AuditLog extends Div {
    }
}
