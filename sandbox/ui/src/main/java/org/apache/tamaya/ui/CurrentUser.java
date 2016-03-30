package org.apache.tamaya.ui;

import com.vaadin.server.VaadinSession;

/**
 * Convenience wrapper for storing and retreiving a user from the VaadinSession
 */
public class CurrentUser {

    private static final String KEY = "currentser";

    public static void set(User user) {
        VaadinSession.getCurrent().setAttribute(KEY, user);
    }

    public static User get() {
        return (User) VaadinSession.getCurrent().getAttribute(KEY);
    }

    public static boolean isLoggedIn() {
        return get() != null;
    }
}