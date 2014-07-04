package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * Class that holds menu bar for SummaryPanel
 */
public class SummaryButtonsPanel extends HorizontalPanel {

    private SummaryPanel summaryPanel;
    private TextButton menuButton = new TextButton("Additional options");

    private Menu summaryMenu = new Menu();

    public void setupButtonPanel(SummaryPanel summaryPanel) {
        if (this.summaryPanel == null) {
            this.summaryPanel = summaryPanel;

            this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
            this.setVerticalAlignment(ALIGN_MIDDLE);

            summaryMenu.add(createDownloadTxtMenuItem());
            menuButton.addSelectHandler(new SelectEvent.SelectHandler() {
                @Override
                public void onSelect(SelectEvent selectEvent) {
                    summaryMenu.show(menuButton);
                }
            });
            this.add(menuButton);
        }
    }
    private MenuItem createDownloadTxtMenuItem() {
        MenuItem downloadTxtMenuItem = new MenuItem("Download in .csv");
        downloadTxtMenuItem.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> itemSelectionEvent) {
                summaryPanel.getSessionComparisonPanel().getSummaryTableAsList();
//                collectInfoFromTree();
//                FileDownLoader.downloadSummaryTableInCsv(summaryPanel.getSessionComparisonPanel().getSummaryTableAsList());
//                System.out.println(summaryTableAsList);
            }
        });
        return downloadTxtMenuItem;
    }

}