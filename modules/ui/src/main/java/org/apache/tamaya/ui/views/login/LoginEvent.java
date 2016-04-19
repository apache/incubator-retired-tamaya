package org.apache.tamaya.ui.views.login;

import org.apache.tamaya.ui.User;

public class LoginEvent {
    private User user;

    public LoginEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}