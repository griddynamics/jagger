package com.griddynamics.jagger.webclient.client;

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.view.client.*;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class TaskDataTreeViewModel implements TreeViewModel {
    private static final TaskDataDto NO_TASKS_DUMMY_NODE = new TaskDataDto(-1, "Select at least one session and one test", "");

    private final ListDataProvider<TaskDataDto> taskDataProvider = new ListDataProvider<TaskDataDto>();
    private final MultiSelectionModel<MetricNameDto> selectionModel;
    private final Cell<MetricNameDto> plotNameCell;
    private final Map<TaskDataDto, AbstractDataProvider<MetricNameDto>> plotNameDataProviders = new HashMap<TaskDataDto, AbstractDataProvider<MetricNameDto>>();
    private final DefaultSelectionEventManager<MetricNameDto> selectionManager =
            DefaultSelectionEventManager.createCheckboxManager();

    private final JaggerResources resources;

    //==========Constructors

    public TaskDataTreeViewModel(final MultiSelectionModel<MetricNameDto> selectionModel, final JaggerResources resources) {
        this.selectionModel = selectionModel;
        this.resources = resources;
        clear();

        // Construct a composite cell for plots that includes a checkbox.
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
            private PlotNameCell cell = new PlotNameCell(resources.getPlotImage());

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
        plotNameCell = new CompositeCell<MetricNameDto>(hasCells) {
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

    //==========Contract Methods

    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {
        if (value == null) {
            return new DefaultNodeInfo<TaskDataDto>(taskDataProvider, new TaskDataCell(resources.getTaskImage()));
        } else if (value instanceof TaskDataDto) {
            TaskDataDto taskDataDto = (TaskDataDto) value;
            if (taskDataDto == NO_TASKS_DUMMY_NODE) {
                return null;
            }

            return new DefaultNodeInfo<MetricNameDto>(
                    getPlotNameDataProvider(taskDataDto),
                    plotNameCell,
                    selectionModel,
                    selectionManager,
                    null);
        }

        String type = value.getClass().getName();
        throw new IllegalArgumentException("Unsupported object type: " + type);
    }

    @Override
    public boolean isLeaf(Object value) {
        return value instanceof MetricNameDto;
    }

    //==========Getters
    public void clear() {
        taskDataProvider.getList().clear();
        taskDataProvider.getList().add(NO_TASKS_DUMMY_NODE);
    }

    public void populateTaskList(List<TaskDataDto> taskDataDtoList) {
        if (taskDataDtoList == null || taskDataDtoList.isEmpty()) {
            // If list is already empty
            if (taskDataProvider.getList().size() == 1 && taskDataProvider.getList().get(0) == NO_TASKS_DUMMY_NODE) {
                return;
            }
            clear();
        } else {
            taskDataProvider.getList().clear();
            taskDataProvider.getList().addAll(taskDataDtoList);
        }
    }

    public AbstractDataProvider<MetricNameDto> getPlotNameDataProvider(TaskDataDto taskDataDto) {
        if (taskDataDto == null) {
            return null;
        }

        return plotNameDataProviders.get(taskDataDto);
    }

    public Map<TaskDataDto, AbstractDataProvider<MetricNameDto>> getPlotNameDataProviders() {
        return plotNameDataProviders;
    }

    public MultiSelectionModel<MetricNameDto> getSelectionModel() {
        return selectionModel;
    }

    //==========Nested Classes

    private static class TaskDataCell extends AbstractCell<TaskDataDto> {

        private final String imageHtml;

        public TaskDataCell(ImageResource imageResource) {
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

    private static class PlotNameCell extends AbstractCell<MetricNameDto> {

        private final String imageHtml;

        public PlotNameCell(ImageResource imageResource) {
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
            sb.appendEscaped(value.getMetricName());
            sb.appendHtmlConstant("</td></tr></table>");
        }
    }

}
