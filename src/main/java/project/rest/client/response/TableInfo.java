package project.rest.client.response;

import project.rest.client.mining.Table;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class TableInfo {

    private String name;
    private int rowsWritten;
    private List<String> columns;

    public TableInfo(Table table) {
        name = table.getName();
        rowsWritten = table.getRows().size();
        columns = table.getColumns();
    }

    public TableInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRowsWritten() {
        return rowsWritten;
    }

    public void setRowsWritten(int rowsWritten) {
        this.rowsWritten = rowsWritten;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}
