package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import java.util.*;

/**
 * Class that holds menu bar for SummaryPanel
 */
public class SummaryButtonsPanel extends HorizontalPanel {
    private List<String> sessionNames;
    private final String NAME = "name";
    private final String TEST_DESCRIPTION = "testDescription";
    private final String TEST_INFO = "Test Info";
    private List<List<String>> summaryTableAsList;

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
                doSomth();
                FileDownLoader.downloadSummaryTableInCsv(summaryTableAsList);
//                System.out.println(summaryTableAsList);
            }
        });
        return downloadTxtMenuItem;
    }

    private List<String> getSessionNames() {
        if (sessionNames == null) {
            sessionNames = new ArrayList<String>();
            SortedSet<SessionDataDto> sortedSet = new TreeSet<SessionDataDto>(new Comparator<SessionDataDto>() {
                @Override
                public int compare(SessionDataDto o, SessionDataDto o2) {
                    return (Long.parseLong(o.getSessionId()) - Long.parseLong(o2.getSessionId())) > 0 ? 1 : -1;
                }
            });
            sortedSet.addAll(summaryPanel.getSessionComparisonPanel().getChosenSessions());
            for (SessionDataDto sdDto : sortedSet)
                sessionNames.add(sdDto.getName());
        }
        return sessionNames;
    }

    private void doSomth() {
//        List<List<String>> fullList
        TreeStore<SessionComparisonPanel.TreeItem> store;
        try {
            store = summaryPanel.getSessionComparisonPanel().getTreeStore();
        }
        catch (NullPointerException e) {
            return;
        }
        summaryTableAsList = new ArrayList<List<String>>();
        List<String> sessions = new ArrayList<String>(getSessionNames());
        sessions.add(0, "");
        //Add sessions names as header
        summaryTableAsList.add(sessions);
        for (SessionComparisonPanel.TreeItem rootItem : store.getRootItems()) {
            doSomth(store, rootItem);
        }
        sessionNames = null;
    }

    private void doSomth(TreeStore<SessionComparisonPanel.TreeItem> store, SessionComparisonPanel.TreeItem aChild) {
        parseTableRow(aChild);
        if (store.hasChildren(aChild)) {
            for (SessionComparisonPanel.TreeItem treeItem: store.getChildren(aChild))
                doSomth(store, treeItem);
        }
    }

    private void parseTableRow(SessionComparisonPanel.TreeItem aRow) {
        List<String> result = new ArrayList<String>();
        String aName = aRow.get(NAME);
        // test results
        if (aRow.containsKey(TEST_DESCRIPTION)) {
            result.addAll(getTestInfo(aRow, aName));
        }
        //Session Info results OR test description value
        else {
            result.addAll(getSessionInfo(aRow, aName));
        }
        summaryTableAsList.add(result);
    }

    private List<String> fillTestLabels(String testLabel, String aName) {
        List<String> result = new ArrayList<String>();
        result.add(testLabel);
        for (int i = 0; i < getSessionNames().size(); i++) {
            result.add(aName);
        }
        return result;
    }

    private List<String> getSessionInfo(SessionComparisonPanel.TreeItem aRow, String aName) {
        if (aName.equals("Session Info")) {
            return new ArrayList<String>(Arrays.asList(new String[]{aName}));
        }
        else {
            if (aRow.size() == 1) {
                summaryTableAsList.add(Arrays.asList(new String[]{""}));
                return fillTestLabels("Test description", aName);
            }
            else {
                List<String> result = new ArrayList<String>();
                result.add(aName);
                for (String sessionName : getSessionNames()) {
                    result.add(clearHtmlTags(aRow.get(sessionName)));
                }
                return result;
            }
        }
    }

    private List<String> getTestInfo(SessionComparisonPanel.TreeItem aRow, String aName) {
        if (aName.equals(TEST_INFO)) {
            return new ArrayList<String>(Arrays.asList(new String[]{aName}));
        }
        List<String> result = new ArrayList<String>();
        if (aRow.size() == 2) {
            return fillTestLabels("Test name", aName);
        }
        result.add(aName);
        for (String sessionName : getSessionNames()) {
            result.add(clearHtmlTags(aRow.get(sessionName)));
        }
        return result;
    }

    private String clearHtmlTags(String input) {
        if (input == null) {
            return "";
        }
        String result = input.replaceAll("<br>", " ");
        if (result.contains("<p title=")) {
            result = result.substring(result.indexOf("\">") + 2, result.indexOf("</"));
        }
        return result;
    }

}