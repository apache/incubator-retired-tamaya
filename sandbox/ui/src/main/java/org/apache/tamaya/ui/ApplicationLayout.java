package org.apache.tamaya.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import org.apache.tamaya.ui.components.LazyProvider;
import org.apache.tamaya.ui.components.PageTitleUpdater;
import org.apache.tamaya.ui.views.ComponentView;
import org.apache.tamaya.ui.views.ConfigView;
import org.apache.tamaya.ui.views.ErrorView;
import org.apache.tamaya.ui.views.HomeView;

public class ApplicationLayout extends HorizontalLayout {

    private NavBar navBar;
    private Panel content;
    private Navigator navigator;

    public ApplicationLayout() {
        addStyleName(UIConstants.MAIN_LAYOUT);

        setSizeFull();

        initLayouts();
        setupNavigator();
    }

    private void initLayouts() {
        navBar = new NavBar();
        // Use panel as main content container to allow it's content to scroll
        content = new Panel();
        content.setSizeFull();
        content.addStyleName(UIConstants.PANEL_BORDERLESS);

        addComponents(navBar, content);
        setExpandRatio(content, 1);
    }

    private void setupNavigator() {
        navigator = new Navigator(MyUI.getCurrent(), content);

        registerViews();

        // Add view change listeners so we can do things like select the correct menu item and update the page title
        navigator.addViewChangeListener(navBar);
        navigator.addViewChangeListener(new PageTitleUpdater());

        navigator.navigateTo(navigator.getState());
    }

    private void registerViews() {
        addView(HomeView.class);
        addView(ConfigView.class);
        addView(ComponentView.class);
        navigator.setErrorView(ErrorView.class);
    }

    /**
     * Registers av given view to the navigator and adds it to the NavBar
     */
    private void addView(Class<? extends View> viewClass) {
        ViewConfig viewConfig = viewClass.getAnnotation(ViewConfig.class);

        switch (viewConfig.createMode()) {
            case CREATE:
                navigator.addView(viewConfig.uri(), viewClass);
                break;
            case LAZY:
                navigator.addProvider(new LazyProvider(viewConfig.uri(), viewClass));
                break;
            case EAGER:
                try {
                    navigator.addView(viewConfig.uri(), viewClass.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        navBar.addView(viewConfig.uri(), viewConfig.displayName());
    }
}