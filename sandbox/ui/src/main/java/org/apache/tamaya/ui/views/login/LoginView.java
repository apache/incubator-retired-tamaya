package org.apache.tamaya.ui.views.login;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        addComponent(new LoginBox());
    }
}