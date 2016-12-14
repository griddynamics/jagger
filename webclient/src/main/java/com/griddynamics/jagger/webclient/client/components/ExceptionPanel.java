package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.griddynamics.jagger.webclient.client.trends.TrendsPlace;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

public class ExceptionPanel extends Dialog {

    public ExceptionPanel(String message) {
        super();
        init(null, message);
    }

    public ExceptionPanel(TrendsPlace place, String message) {

        super();
        init(place, message);
    }

    private void init(TrendsPlace place, String message) {

        setAllowTextSelection(true);
        setClosable(true);
        setShadow(false);
        setHideOnButtonClick(true);
        setStyleName(JaggerResources.INSTANCE.css().exceptionPanel());
        setPredefinedButtons();
        setHeadingText("Exception");
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add(new Image(AlertMessageBox.ICONS.error()));

        String htmlBody;
        if (message == null) {
            htmlBody = "no message";
        } else {
            htmlBody = message.replace("\n", "<br>");
            if (place != null) {
                htmlBody += "<br><br><i>URL: " + place.getUrl() + "</i>";
            }
        }
        hPanel.add(new HTML(htmlBody));

        setWidget(hPanel);
        show();
    }

}
