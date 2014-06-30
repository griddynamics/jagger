package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * Class that holds menu bar for SummaryPanel
 */
public class SummaryButtonsPanel extends HorizontalPanel {
    private SummaryPanel summaryPanel;

    Menu summaryMenu = new Menu();

    public void setupButtonPanel(SummaryPanel summaryPanel) {
        if (this.summaryPanel == null) {
            this.summaryPanel = summaryPanel;
            this.addStyleName(JaggerResources.INSTANCE.css().summaryPanelMenuLabel());

            MenuItem dummyMenuItem = new MenuItem("Dummy item.");
            dummyMenuItem.setEnabled(false);
            summaryMenu.add(dummyMenuItem);

            final Image settingsImageButton = new Image(JaggerResources.INSTANCE.getGearImage().getSafeUri());
            settingsImageButton.addStyleName(JaggerResources.INSTANCE.css().pointer());
            settingsImageButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    summaryMenu.show(settingsImageButton);
                }
            });
            settingsImageButton.addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    settingsImageButton.setUrl(JaggerResources.INSTANCE.getGearBlueImage().getSafeUri());
                }
            });
            settingsImageButton.addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    settingsImageButton.setUrl(JaggerResources.INSTANCE.getGearImage().getSafeUri());
                }
            });

            this.add(new HorizontalPanel());
            this.add(settingsImageButton);
            this.setCellWidth(settingsImageButton, "20px");
        }
    }
}
