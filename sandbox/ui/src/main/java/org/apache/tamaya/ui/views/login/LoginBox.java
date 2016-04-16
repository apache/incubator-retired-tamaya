package org.apache.tamaya.ui.views.login;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.internal.ResourceBundleMessageProvider;
import org.apache.tamaya.ui.UIConstants;
import org.apache.tamaya.ui.User;
import org.apache.tamaya.ui.event.EventBus;
import org.apache.tamaya.ui.services.MessageProvider;
import org.apache.tamaya.ui.services.UserService;


public class LoginBox extends VerticalLayout {

    private UserService userService = new UserService() {
        @Override
        public User login(String userId, String credentials) {
            if("admin".equals(userId)){
                return new User("admin", "Administrator");
            }
            return null;
        }
    }; // TODO Load
    private TextField username;
    private PasswordField password;

    public LoginBox() {
        setWidth("400px");
        addStyleName(UIConstants.LOGIN_BOX);
        setSpacing(true);
        setMargin(true);

        addCaption();
        addForm();
        addButtons();
    }

    private void addCaption() {
        Label caption = new Label("Login to system");
        addComponent(caption);

        caption.addStyleName(UIConstants.LABEL_H1);
    }

    private void addForm() {
        FormLayout loginForm = new FormLayout();
        MessageProvider mp = ServiceContextManager.getServiceContext().getService(MessageProvider.class);
        username = new TextField(mp.getMessage("default.label.username"));
        password = new PasswordField(mp.getMessage("default.label.password"));
        loginForm.addComponents(username, password);
        addComponent(loginForm);
        loginForm.setSpacing(true);
        for(Component component:loginForm){
            component.setWidth("100%");
        }
        username.focus();
    }

    private void addButtons() {
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button forgotButton = new Button("Forgot", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Notification.show("Not implemented", Notification.Type.TRAY_NOTIFICATION);
            }
        });
        Button loginButton = new Button("Login", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                login();
            }
        });
        buttonsLayout.addComponents(forgotButton, loginButton);
        addComponent(buttonsLayout);
        buttonsLayout.setSpacing(true);
        forgotButton.addStyleName(UIConstants.BUTTON_LINK);
        loginButton.addStyleName(UIConstants.BUTTON_PRIMARY);
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        setComponentAlignment(buttonsLayout, Alignment.BOTTOM_RIGHT);
    }

    private void login() {
        User user = userService.login(username.getValue(), password.getValue());
        if(user!=null){
            EventBus.post(new LoginEvent(user));
        }else{
            Notification.show("Login failed.", "Hint: use any non-empty strings", Notification.Type.WARNING_MESSAGE);
            username.focus();
        }
    }
}