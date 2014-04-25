package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.sencha.gxt.widget.core.client.info.Info;

import java.util.ArrayList;
import java.util.List;

public class DynamicLayoutPanel extends VerticalPanel{

    private Layout layout = Layout.ONE_COLUMN;

    public Layout getLayout() {
        return layout;
    }

    private final PlotContainer stub;

    {
        stub = new PlotContainer("stub", "stub", JaggerResources.INSTANCE.css().stub());
    }

    public void changeLayout(Layout layout) {

        if (this.layout == layout) // no need to change anything
            return;

        this.layout = layout;

        List<Widget> widgets = new ArrayList<Widget>();
        for (int i = 0; i < getWidgetCount(); i++) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(i);
            for (int j = 0; j < hp.getWidgetCount(); j++) {
                Widget widget = hp.getWidget(j);
                if (widget != stub)
                    widgets.add(widget);
            }
        }

        clear();

        for (Widget widget : widgets) {
            addChild(widget);
        }

        Info.display("DynamicLayoutPanel", "layout changed to " + layout);

    }

    public void addChild(Widget widget) {

        switch (layout) {
            case ONE_COLUMN:  addChildOneColumn(widget);
                              break;
            case TWO_COLUMNS: addChildTwoColumns(widget);
                              break;
            default:
                Info.display("DynamicLayoutPanel", "addChild with layout " + layout + " not yet implemented");
        }
    }

    private void addChildOneColumn(Widget widget) {
        HorizontalPanel newHp = new HorizontalPanel();
        newHp.setHorizontalAlignment(ALIGN_CENTER);
        newHp.setWidth("100%");
        newHp.setBorderWidth(2);
        newHp.add(widget);
        newHp.setCellWidth(widget, "100%");
        newHp.setSpacing(1);
        this.add(newHp);
    }

    private void addChildTwoColumns(Widget widget) {

        int totalCount = this.getWidgetCount();
        if (totalCount != 0) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(totalCount - 1); // get last horizontal Panel
            if (hp.getWidgetCount() == 2 && stub == hp.getWidget(1)) {
                hp.remove(stub);
                hp.add(widget);
                hp.setCellWidth(widget, "50%");
                return;
            }
        }

        HorizontalPanel newHp = new HorizontalPanel();
        newHp.setHorizontalAlignment(ALIGN_CENTER);
        newHp.setWidth("100%");
        newHp.setBorderWidth(2);
        newHp.add(widget);
        newHp.setCellWidth(widget, "50%");
        newHp.add(stub);
        newHp.setCellWidth(stub, "50%");
        newHp.setSpacing(1);
        this.add(newHp);
    }

    // demo method
    // remove method for widget is clear and not taking much time task
    public void removeLast() {
        int totalCount = this.getWidgetCount();
        if (totalCount != 0) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(totalCount - 1);
            if (hp.getWidgetCount() == 1 || stub == hp.getWidget(1)) {
                hp.removeFromParent();
            } else {
                hp.remove(1);
                hp.add(stub);
                hp.setCellWidth(stub, "50%");
            }
        }
    }

    public void changeChildrenHeight(String plotContainerHeight) {
        for (int i = 0; i < getWidgetCount(); i ++) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(i);
            for (int j = 0; j < hp.getWidgetCount(); j ++) {
                Widget widget = hp.getWidget(j);
                widget.setHeight(plotContainerHeight);
            }
        }
    }

    /**
     * @param id id of widget to remove (id of plot container)
     */
    public void removeChild(String id) {
        //as id there
        if (layout == Layout.ONE_COLUMN) {
            for (int i = 0; i < getWidgetCount(); i ++) {
                HorizontalPanel hp = (HorizontalPanel) getWidget(i);
                if (id.equals(((PlotContainer) hp.getWidget(0)).getId())) {
                    remove(i);
                    return;
                }
            }
        } else {
            List<PlotContainer> containers = new ArrayList<PlotContainer>();
            for (int i = 0; i < getWidgetCount(); i ++) {
                HorizontalPanel hp = (HorizontalPanel) getWidget(i);
                for (int j = 0; j < hp.getWidgetCount(); j++) {
                    PlotContainer pc = (PlotContainer) hp.getWidget(j);
                    if (!id.equals(pc.getId()) && pc != stub) {
                        containers.add(pc);
                    }
                }
            }
            clear();
            for (PlotContainer pc : containers) {
                addChild(pc);
            }
        }
    }

    /**
     * just enum of possible layouts, maybe it should be inside DynamicLayoutPanel*/
    public static enum Layout {
        ONE_COLUMN , TWO_COLUMNS
    }
}
