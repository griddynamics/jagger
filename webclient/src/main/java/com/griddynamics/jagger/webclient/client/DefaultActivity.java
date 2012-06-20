package com.griddynamics.jagger.webclient.client;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public abstract class DefaultActivity extends AbstractActivity implements IsWidget {
    private Widget widget;
    private JaggerResources resources;

    protected DefaultActivity(JaggerResources resources) {
        this.resources = resources;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        if (widget == null) {
            widget = initializeWidget();
        }

        panel.setWidget(widget);

        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand()
        {
            @Override
            public void execute()
            {
                // trick under IE6 to force a dom reconstruction
                PopupPanel pop = new PopupPanel();
                pop.show();
                pop.hide();
            }
        } );
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @UiFactory
    public JaggerResources getResources() {
        return resources;
    }

    protected abstract Widget initializeWidget();
}
