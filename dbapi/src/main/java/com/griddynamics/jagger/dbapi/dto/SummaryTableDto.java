package com.griddynamics.jagger.dbapi.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by aovodov on 08.07.14.
 */

public class SummaryTableDto implements Serializable {
    private List<List<String>> tableData;
    private Map<Integer, String> tableNames;

    public SummaryTableDto() {
        tableData = new ArrayList<List<String>>();
        tableNames = new TreeMap<Integer, String>();
    }

    public void insertRow(List<String> input) {
        this.tableData.add(input);
    }

    public void insertTableName(String tableName) {
        tableNames.put(tableData.size(), tableName);
    }

    public List<List<String>> getTableData() {
        return tableData;
    }

}
