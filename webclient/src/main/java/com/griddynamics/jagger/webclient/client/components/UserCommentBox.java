package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;


public class UserCommentBox extends AbstractWindow {

    private int maxlength = 250;

    private final int PIXELS_BETWEEN_BUTTONS = 10;

    private VerticalPanel vp;
    private TextButton saveButton;
    private TextButton cancelButton;
    private TextArea textArea;

    private TreeGrid<SessionComparisonPanel.TreeItem> treeGrid;

    private Label remainingCharsLabel;


    /**
     * Extended TextArea class to customize onPaste event
     */
    private class FeaturedTextArea extends TextArea {
        public FeaturedTextArea() {
            super();
            sinkEvents(Event.ONPASTE);
        }

        @Override
        public void onBrowserEvent(Event event){
            super.onBrowserEvent(event);
            switch (event.getTypeInt()){
                case Event.ONPASTE: {
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                        @Override
                        public void execute() {
                            ValueChangeEvent.fire(FeaturedTextArea.this, getText());
                        }
                    });
                    break;
                }
            }
        }
    }

    public UserCommentBox(int maxlength) {
        super();
        this.maxlength = maxlength;
        setTitle("User Comment");

        vp = new VerticalPanel();
        vp.setPixelSize(width,height);

        textArea = new FeaturedTextArea();
        textArea.addStyleName(JaggerResources.INSTANCE.css().textAreaPanel());
        textArea.setPixelSize(width, 440);
        textArea.getElement().setAttribute("maxlength", String.valueOf(maxlength));


        remainingCharsLabel = new Label(String.valueOf(maxlength));
        remainingCharsLabel.getElement().getStyle().setFontSize(12, Style.Unit.PX);

        HorizontalPanel remainCharsPanel = new HorizontalPanel();
        remainCharsPanel.setSpacing(5);
        remainCharsPanel.setWidth("100%");
        remainCharsPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
        remainCharsPanel.add(remainingCharsLabel);


        textArea.addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event){
                onTextAreaContentChanged();
            }
        });
        textArea.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                onTextAreaContentChanged();
            }
        });
        textArea.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                onTextAreaContentChanged();
            }
        });
        textArea.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                onTextAreaContentChanged();
            }
        });
        saveButton = new TextButton("Save");
        saveButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                // ask some service to save comment
                currentTreeItem.put(getText(), textArea.getText());
                treeGrid.getTreeView().refresh(false);
                hide();
            }
        });
        saveButton.setPixelSize(60, 22);
        saveButton.getElement().setMargins(new Margins(0, 0, 0, 0));
        cancelButton = new TextButton("Cancel");
        cancelButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                // ask some service to save comment
                hide();
            }
        });
        cancelButton.setPixelSize(60, 22);
        cancelButton.getElement().setMargins(new Margins(0, 0, 0, PIXELS_BETWEEN_BUTTONS));
        vp.add(textArea);
        DockPanel dp = new DockPanel();
        dp.setPixelSize(width,60);
        dp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        dp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dp.add(buttonPanel, DockPanel.EAST);
        dp.add(new Label(""), DockPanel.CENTER);
        dp.add(remainCharsPanel, DockPanel.WEST);

        vp.add(dp);
        setAutoHideEnabled(true);

        add(vp);
    }

    public void setTreeGrid(TreeGrid<SessionComparisonPanel.TreeItem> treeGrid) {
        this.treeGrid = treeGrid;
    }

    private void onTextAreaContentChanged() {
        int counter = textArea.getText().length();

        if (GXT.isChrome()) {
            for (char c : textArea.getText().toCharArray()) {
                if (c == '\n') {
                    counter ++;
                }
            }
            if (counter > maxlength)
                counter = maxlength;
        }

        int charsRemaining = maxlength - counter;
        remainingCharsLabel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
        remainingCharsLabel.setText(Integer.toString(charsRemaining));
    }

    private SessionComparisonPanel.TreeItem currentTreeItem;

    public void popUp(String sessionId, String userComment, SessionComparisonPanel.TreeItem item) {
        currentTreeItem = item;
        setText(sessionId);
        textArea.setText(userComment);
        onTextAreaContentChanged();
        show();
        textArea.setFocus(true);
    }
}
