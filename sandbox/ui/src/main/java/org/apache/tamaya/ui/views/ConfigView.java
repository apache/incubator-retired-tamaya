package org.apache.tamaya.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.ui.CurrentUser;
import org.apache.tamaya.ui.UIConstants;
import org.apache.tamaya.ui.ViewConfig;
import org.apache.tamaya.ui.components.VerticalSpacedLayout;

import java.util.Map;


@ViewConfig(uri = "/config", displayName = "Configuration")
public class ConfigView extends VerticalSpacedLayout implements View {

    private TextField keyFilter = new TextField("Key filter");
    private TextField valueFilter = new TextField("Value filter");
    private Tree tree = new Tree("Current Configuration");

    public ConfigView() {
        Label caption = new Label("Raw Configuration");
        Label description = new Label(
                "This view shows the overall <b>raw</b> configuration tree. Dependening on your access rights you" +
                        "may see partial or masked data. Similarly configuration can be <i>read-only</i> or <i>mutable</i>.",
                ContentMode.HTML);
        HorizontalLayout filters = new HorizontalLayout();

        Button filterButton = new Button("Filter", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                fillTree();
            }
        });
        filters.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        filters.addComponents(keyFilter, valueFilter, filterButton);

        fillTree();

        addComponents(caption, description, filters, tree);

        caption.addStyleName(UIConstants.LABEL_HUGE);
        description.addStyleName(UIConstants.LABEL_LARGE);

    }

    private void fillTree() {
        String keyFilterExp = this.keyFilter.getValue();
        if(keyFilterExp.isEmpty()){
            keyFilterExp = null;
        }
        String valueFilterExp = this.valueFilter.getValue();
        if(valueFilterExp.isEmpty()){
            valueFilterExp = null;
        }
        tree.removeAllItems();
        for(Map.Entry<String,String> entry: ConfigurationProvider.getConfiguration().getProperties().entrySet()){
            String key = entry.getKey();
            if(keyFilterExp!=null && !key.matches(keyFilterExp)){
                continue;
            }
            if(valueFilterExp!=null && !entry.getValue().matches(valueFilterExp)){
                continue;
            }
            tree.addItem(key);
            tree.setItemCaption(key, getCaption(key, entry.getValue()));
            tree.setChildrenAllowed(key, false);
            String parent = null;
            int start = 0;
            int index = key.indexOf('.', start);
            while(index>0){
                String subItem = key.substring(0,index);
                String caption = key.substring(start, index);
                tree.addItem(subItem);
                tree.setItemCaption(subItem, caption);
                if(parent!=null){
                    tree.setParent(subItem, parent);
                }
                parent = subItem;
                start = index+1;
                index = key.indexOf('.', start);
            }
            String lastItem = key.substring(start);
            if(!lastItem.equals(key)){
                if(parent!=null){
                    tree.setParent(key, parent);
                }else{
                    // should not happen
                }
            }else{ // singl root entry
                if(parent!=null) {
                    tree.setParent(key, parent);
                }
            }
        }
    }

    private String getCaption(String key, String value) {
        int index = key.lastIndexOf('.');
        if(index<0){
            return key + " = " + value;
        }else{
            return key.substring(index+1) + " = " + value;
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}