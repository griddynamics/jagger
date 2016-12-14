package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 2/20/14
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractWindow extends DialogBox {
    protected int width = 600;
    protected int height = 500;
    private TextButton saveButton;
    private TextButton cancelButton;
    private TextButton applyButton;
    private final int PIXELS_BETWEEN_BUTTONS = 10;


    public AbstractWindow(String width, String height){
        setSize(width,height);
    }

    public AbstractWindow(){
        addStyleName(JaggerResources.INSTANCE.css().abstractWindow());
        setPixelSize(width,height);
        setPopupPosition(300, 100);
    }

    protected void defaultButtonInitialization(){
        saveButton = new TextButton("Save");
        saveButton.setPixelSize(60, 22);
        saveButton.getElement().setMargins(new Margins(0, 0, 0, 0));
        saveButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent selectEvent) {
                onSaveButtonClick();
            }
        });

        applyButton = new TextButton("Apply");
        applyButton.setPixelSize(60, 22);
        applyButton.getElement().setMargins(new Margins(0,0,0,0));
        applyButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent selectEvent) {
                onApplyButtonClick();
            }
        });

        cancelButton = new TextButton("Cancel");
        cancelButton.setPixelSize(60, 22);
        cancelButton.getElement().setMargins(new Margins(0, 0, 0, PIXELS_BETWEEN_BUTTONS));
        cancelButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent selectEvent) {
                onCancelButtonClick();
            }
        });

    }

    protected HorizontalPanel getDefaultButtonBar(){
        HorizontalPanel saveAndCancelButtonBar = new HorizontalPanel();
        saveAndCancelButtonBar.setSpacing(5);
        saveAndCancelButtonBar.add(applyButton);
        saveAndCancelButtonBar.add(saveButton);
        saveAndCancelButtonBar.add(cancelButton);
        return saveAndCancelButtonBar;
    }

    protected TextButton getSaveButton() {
        return saveButton;
    }

    protected TextButton getCancelButton() {
        return cancelButton;
    }

    protected TextButton getApplyButton() {
        return applyButton;
    }

    protected abstract void onSaveButtonClick();
    protected abstract void onCancelButtonClick();
    protected abstract void onApplyButtonClick();



}
