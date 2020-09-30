package project.rest.client.mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {

    private List<String> columns = new ArrayList<>();
    private List<List<String>> rows = new ArrayList<>();
    private String name;
    private Map<Integer, String> columnTypeMap = new HashMap<>();

    public Table() {
    }

    public Table(String name, List<String> columns, List<List<String>> rows, Map<Integer, String> columnTypeMap) {
        this.columns = columns;
        this.rows = rows;
        this.name = name;
        this.columnTypeMap = columnTypeMap;
    }

    public Map<Integer, String> getColumnTypeMap() {
        return columnTypeMap;
    }

    public void setColumnTypeMap(Map<Integer, String> columnTypeMap) {
        this.columnTypeMap = columnTypeMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }

}
