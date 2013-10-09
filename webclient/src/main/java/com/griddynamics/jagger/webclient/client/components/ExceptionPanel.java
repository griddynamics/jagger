package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

public class ExceptionPanel extends Dialog {

    public ExceptionPanel(String massage) {
        super();
        setAllowTextSelection(true);
        setClosable(true);
        setPredefinedButtons();
        setHeadingText("Exception");
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(new Image(AlertMessageBox.ICONS.error()));
        HTMLPanel panel = new HTMLPanel(massage.replace("\n", "<br>"));
        hp.add(panel);
        setWidget(hp);
        setBodyStyle("max-width: 500px");
        setShadow(false);
        setHideOnButtonClick(true);
        show();
    }
}
