package project.rest.client.response;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class MiningResponse {

    private List<TableInfo> tables;
    private List<InfoMessage> warnings;

    public MiningResponse() {
    }

    public MiningResponse(List<TableInfo> tables, List<InfoMessage> warnings) {
        this.tables = tables;
        this.warnings = warnings;
    }

    public List<TableInfo> getTables() {
        return tables;
    }

    public void setTables(List<TableInfo> tables) {
        this.tables = tables;
    }

    public List<InfoMessage> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<InfoMessage> warnings) {
        this.warnings = warnings;
    }
}
