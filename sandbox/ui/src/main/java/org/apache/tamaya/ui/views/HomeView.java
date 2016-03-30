package org.apache.tamaya.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import org.apache.tamaya.ui.CurrentUser;
import org.apache.tamaya.ui.UIConstants;
import org.apache.tamaya.ui.ViewConfig;
import org.apache.tamaya.ui.components.VerticalSpacedLayout;


@ViewConfig(uri = "", displayName = "Home")
public class HomeView extends VerticalSpacedLayout implements View {

    public HomeView() {
        Label caption = new Label("Welcome, " + CurrentUser.get().getUserID());
        Label description = new Label(
                "<b>Apache Tamaya</b> is an API and extendable framework for accessing and managing configuration.<br/> \n" +
                        "Please check the project's home page <a href='http://tamaya.incubator.apache.org'>http://tamaya.incubator.apache.org</a>.",
                ContentMode.HTML);

        addComponents(caption, description);

        caption.addStyleName(UIConstants.LABEL_HUGE);
        description.addStyleName(UIConstants.LABEL_LARGE);

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}