package com.griddynamics.jagger.webclient.client;

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.*;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class WorkloadTaskDetailsTreeViewModel implements TreeViewModel {

    private ListDataProvider<TaskDataDto> taskDataProvider = new ListDataProvider<TaskDataDto>();
    private final MultiSelectionModel<PlotNameDto> selectionModel;
    private final Cell<PlotNameDto> plotNameCell;
    private final Map<TaskDataDto, AbstractDataProvider<PlotNameDto>> plotNameDataProviders = new HashMap<TaskDataDto, AbstractDataProvider<PlotNameDto>>();
    private final DefaultSelectionEventManager<PlotNameDto> selectionManager =
            DefaultSelectionEventManager.createCheckboxManager();

    private static final TaskDataDto noTasksDummyNode = new TaskDataDto(-1, "No Tasks");

    //==========Constructors

    public WorkloadTaskDetailsTreeViewModel(final MultiSelectionModel<PlotNameDto> selectionModel) {
        this.selectionModel = selectionModel;
        taskDataProvider.getList().add(noTasksDummyNode);

        // Construct a composite cell for plots that includes a checkbox.
        List<HasCell<PlotNameDto, ?>> hasCells = new ArrayList<HasCell<PlotNameDto, ?>>();
        hasCells.add(new HasCell<PlotNameDto, Boolean>() {
            private CheckboxCell cell = new CheckboxCell(true, false);

            @Override
            public Cell<Boolean> getCell() {
                return cell;
            }

            @Override
            public FieldUpdater<PlotNameDto, Boolean> getFieldUpdater() {
                return null;
            }

            @Override
            public Boolean getValue(PlotNameDto object) {
                return selectionModel.isSelected(object);
            }
        });
        hasCells.add(new HasCell<PlotNameDto, PlotNameDto>() {
            private PlotNameCell cell = new PlotNameCell();

            @Override
            public Cell<PlotNameDto> getCell() {
                return cell;
            }

            @Override
            public FieldUpdater<PlotNameDto, PlotNameDto> getFieldUpdater() {
                return null;
            }

            @Override
            public PlotNameDto getValue(PlotNameDto object) {
                return object;
            }
        });
        plotNameCell = new CompositeCell<PlotNameDto>(hasCells) {
            @Override
            public void render(Context context, PlotNameDto value, SafeHtmlBuilder sb) {
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
            protected <X> void render(Context context, PlotNameDto value,
                                      SafeHtmlBuilder sb, HasCell<PlotNameDto, X> hasCell) {
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
            return new DefaultNodeInfo<TaskDataDto>(taskDataProvider, new TaskDataCell());
        } else if (value instanceof TaskDataDto) {
            TaskDataDto taskDataDto = (TaskDataDto) value;
            if (taskDataDto.equals(noTasksDummyNode)) {
                return null;
            }

            return new DefaultNodeInfo<PlotNameDto>(
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
        return value instanceof PlotNameDto;
    }

    //==========Getters

    public ListDataProvider<TaskDataDto> getTaskDataProvider() {
        return taskDataProvider;
    }

    public AbstractDataProvider<PlotNameDto> getPlotNameDataProvider(TaskDataDto taskDataDto) {
        if (taskDataDto == null) {
            return null;
        }

        return plotNameDataProviders.get(taskDataDto);
    }

    public Map<TaskDataDto, AbstractDataProvider<PlotNameDto>> getPlotNameDataProviders() {
        return plotNameDataProviders;
    }

    public static TaskDataDto getNoTasksDummyNode() {
        return noTasksDummyNode;
    }

    public MultiSelectionModel<PlotNameDto> getSelectionModel() {
        return selectionModel;
    }

    //==========Nested Classes

    private static class TaskDataCell extends AbstractCell<TaskDataDto> {
        @Override
        public void render(Context context, TaskDataDto value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendEscaped(value.getTaskName());
            }
        }
    }

    private static class PlotNameCell extends AbstractCell<PlotNameDto> {
        @Override
        public void render(Context context, PlotNameDto value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendEscaped(value.getPlotName());
            }
        }
    }

}
