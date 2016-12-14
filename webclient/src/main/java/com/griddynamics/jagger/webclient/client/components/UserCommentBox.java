package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.SessionDataService;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;


public class UserCommentBox extends AbstractWindow {

    private int maxlength = 250;

    private VerticalPanel vp;
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
        defaultButtonInitialization();
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
        vp.add(textArea);
        DockPanel dp = new DockPanel();
        dp.setPixelSize(width,60);
        dp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        dp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        dp.add(getDefaultButtonBar(), DockPanel.EAST);
        dp.add(new Label(""), DockPanel.CENTER);
        dp.add(remainCharsPanel, DockPanel.WEST);

        vp.add(dp);
        setAutoHideEnabled(true);

        add(vp);
    }

    @Override
    protected void onCancelButtonClick(){
        hide();
    }

    @Override
    protected void onApplyButtonClick() {}

    @Override
    protected void onSaveButtonClick(){
        final String resultComment = textArea.getText().trim();
        SessionDataService.Async.getInstance().saveUserComment(currentSessionDataDto.getId(), resultComment, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                new ExceptionPanel("Fail to save data : " + caught.getMessage());
                hide();
            }

            @Override
            public void onSuccess(Void result) {
                currentSessionDataDto.setUserComment(resultComment);
                currentTreeItem.put(getText(), resultComment);
                treeGrid.getTreeView().refresh(false);
                hide();
            }
        });
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
    private SessionDataDto currentSessionDataDto;

    public void popUp(SessionDataDto sessionDataDto, String userComment, SessionComparisonPanel.TreeItem item) {
        if (sessionDataDto==null){
            new ExceptionPanel("The session data has a value null. The session's id is wrong.");
        }
        getApplyButton().removeFromParent();
        currentTreeItem = item;
        currentSessionDataDto = sessionDataDto;
        setText("Session " + sessionDataDto.getSessionId());
        textArea.setText(userComment);
        onTextAreaContentChanged();
        show();
        textArea.setFocus(true);
    }
}
