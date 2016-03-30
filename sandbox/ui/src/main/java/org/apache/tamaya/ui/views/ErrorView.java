package org.apache.tamaya.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.apache.tamaya.ui.UIConstants;

public class ErrorView extends VerticalLayout implements View {
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setSizeFull();
        setMargin(true);
        Label label = new Label("Could not find a view with that name. You are most likely doing it wrong.");
        label.addStyleName(UIConstants.LABEL_FAILURE);

        addComponent(label);
        setComponentAlignment(label, Alignment.MIDDLE_CENTER);
    }
}