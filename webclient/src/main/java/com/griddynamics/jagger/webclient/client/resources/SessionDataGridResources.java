package com.griddynamics.jagger.webclient.client.resources;

import com.google.gwt.user.cellview.client.DataGrid;

/**
 * User: amikryukov
 * Date: 12/9/13
 */
public interface SessionDataGridResources extends DataGrid.Resources {

    @Override
    @Source("SessionDataGrid.css")
    DataGrid.Style dataGridStyle();

}
