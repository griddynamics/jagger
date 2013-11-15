package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.view.client.*;
import com.griddynamics.jagger.webclient.client.data.MetricProvider;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link TreeViewModel} used to organize contacts into a hierarchy.
 */
public class MetricModel implements TreeViewModel {

    private final JaggerResources resources = JaggerResources.INSTANCE;

    public static final TaskDataDto NO_METRIC_TO_SHOW = new TaskDataDto(0, "Select at least one sessions and one test", "");

    private final Cell<MetricNameDto> metricDataCell;

    private static class TestCell extends AbstractCell<TaskDataDto> {

        private final String imageHtml;

        public TestCell(ImageResource imageResource) {
            imageHtml = AbstractImagePrototype.create(imageResource).getHTML();
        }

        @Override
        public void render(Context context, TaskDataDto value, SafeHtmlBuilder sb) {
            if (value == null) {
                return;
            }
            sb.appendHtmlConstant("<table><tr><td>");
            sb.appendHtmlConstant(imageHtml);
            sb.appendHtmlConstant("</td><td>");
            sb.appendEscaped(value.getTaskName());
            sb.appendHtmlConstant("</td></tr></table>");
        }
    }

    private static class MetricCell extends AbstractCell<MetricNameDto> {

        private final String imageHtml;

        public MetricCell(ImageResource imageResource) {
            imageHtml = AbstractImagePrototype.create(imageResource).getHTML();
        }

        @Override
        public void render(Context context, MetricNameDto value, SafeHtmlBuilder sb) {
            if (value == null) {
                return;
            }

            sb.appendHtmlConstant("<table><tr><td>");
            sb.appendHtmlConstant(imageHtml);
            sb.appendHtmlConstant("</td><td>");
            sb.appendEscaped(value.getName());
            sb.appendHtmlConstant("</td></tr></table>");
        }
    }

    private final DefaultSelectionEventManager<MetricNameDto> selectionManager =
            DefaultSelectionEventManager.createCheckboxManager();

    private final SelectionModel<MetricNameDto> selectionModel;
    private final AbstractDataProvider<TaskDataDto> provider;

    public MetricModel(final SelectionModel selectionModel, AbstractDataProvider<TaskDataDto> provider){
        this.selectionModel = selectionModel;
        this.provider = provider;

        List<HasCell<MetricNameDto, ?>> hasCells = new ArrayList<HasCell<MetricNameDto, ?>>();
        hasCells.add(new HasCell<MetricNameDto, Boolean>() {
            private CheckboxCell cell = new CheckboxCell(true, false);

            @Override
            public Cell<Boolean> getCell() {
                return cell;
            }

            @Override
            public FieldUpdater<MetricNameDto, Boolean> getFieldUpdater() {
                return null;
            }

            @Override
            public Boolean getValue(MetricNameDto object) {
                return selectionModel.isSelected(object);
            }
        });
        hasCells.add(new HasCell<MetricNameDto, MetricNameDto>() {
            private MetricCell cell = new MetricCell(resources.getRenderPlots());

            @Override
            public Cell<MetricNameDto> getCell() {
                return cell;
            }

            @Override
            public FieldUpdater<MetricNameDto, MetricNameDto> getFieldUpdater() {
                return null;
            }

            @Override
            public MetricNameDto getValue(MetricNameDto object) {
                return object;
            }
        });


        metricDataCell = new CompositeCell<MetricNameDto>(hasCells) {
            @Override
            public void render(Context context, MetricNameDto value, SafeHtmlBuilder sb) {
                sb.appendHtmlConstant("<table><tbody><tr>");
                super.render(context, value, sb);
                sb.appendHtmlConstant("</tr></tbody></table>");
            }

            @Override
            protected Element getContainerElement(Element parent) {
                // Return the first TR element in the table.
                return parent.getFirstChildElement().getFirstChildElement().getFirstChildElement();
            }

            @Override
            protected <X> void render(Context context, MetricNameDto value,
                                      SafeHtmlBuilder sb, HasCell<MetricNameDto, X> hasCell) {
                Cell<X> cell = hasCell.getCell();
                sb.appendHtmlConstant("<td>");
                cell.render(context, hasCell.getValue(value), sb);
                sb.appendHtmlConstant("</td>");
            }
        };

    }

    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {
        if (value == null){
            return new DefaultNodeInfo<TaskDataDto>(provider, new TestCell(resources.getTaskImage()));
        }else{
            if (value instanceof TaskDataDto){
                if (value == NO_METRIC_TO_SHOW){
                    return null;
                }
                return new DefaultNodeInfo<MetricNameDto>(new MetricProvider((TaskDataDto)value),
                                                              metricDataCell,
                                                              selectionModel,
                                                              selectionManager,
                                                              null);
            }
        }
        String type = value.getClass().getName();
        throw new IllegalArgumentException("Unsupported object type: " + type);
    }

    @Override
    public boolean isLeaf(Object value) {
        return value instanceof MetricNameDto;
    }
}