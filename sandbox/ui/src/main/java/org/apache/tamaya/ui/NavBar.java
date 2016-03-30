package org.apache.tamaya.ui;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import org.apache.tamaya.ui.event.EventBus;
import org.apache.tamaya.ui.event.LogoutEvent;
import org.apache.tamaya.ui.event.NavigationEvent;

import java.util.HashMap;
import java.util.Map;

public class NavBar extends CssLayout implements ViewChangeListener {

    private Map<String, Button> buttonMap = new HashMap<>();

    public NavBar() {
        setHeight("100%");
        addStyleName(UIConstants.MENU_ROOT);
        addStyleName(UIConstants.NAVBAR);

        Label logo = new Label("<strong>Apache Tamaya</strong>", ContentMode.HTML);
        logo.addStyleName(UIConstants.MENU_TITLE);
        addComponent(logo);

        addLogoutButton();
    }

    private void addLogoutButton() {
        Button logout = new Button("Log out", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                EventBus.post(new LogoutEvent());
            }
        });
        addComponent(logout);

        logout.addStyleName(UIConstants.BUTTON_LOGOUT);
        logout.addStyleName(UIConstants.BUTTON_BORDERLESS);
        logout.setIcon(FontAwesome.SIGN_OUT);
    }

    public void addView(final String uri, String displayName) {
        Button viewButton = new Button(displayName, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                EventBus.post(new NavigationEvent(uri));
            }
        });
        viewButton.addStyleName(UIConstants.MENU_ITEM);
        viewButton.addStyleName(UIConstants.BUTTON_BORDERLESS);
        buttonMap.put(uri, viewButton);

        addComponent(viewButton, components.size() - 1);
    }

    @Override
    public boolean beforeViewChange(ViewChangeEvent event) {
        return true; // false blocks navigation, always return true here
    }

    @Override
    public void afterViewChange(ViewChangeEvent event) {
        for(Button button: buttonMap.values()){
            button.removeStyleName(UIConstants.SELECTED);
        }
        Button button = buttonMap.get(event.getViewName());
        if (button != null) button.addStyleName(UIConstants.SELECTED);
    }
}