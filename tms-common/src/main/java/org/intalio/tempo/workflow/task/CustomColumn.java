package org.intalio.tempo.workflow.task;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Persistent;


@Entity
@Table(name="tempo_custom_column")
@NamedQueries({
    @NamedQuery(name = CustomColumn.FIND_BY_PROCESS_NAME, query = "select distinct t from CustomColumn t where t._projectName=(?1)"),
    @NamedQuery(name=CustomColumn.FIND_ALL_CUSTOM_COLUMNS, query="select distinct t.customColumnName from CustomColumn t" )}
)
public class CustomColumn {
    public static final String FIND_BY_PROCESS_NAME = "find_by_process_name";
    public static final String FIND_ALL_CUSTOM_COLUMNS= "find_all_custom_columns";
    
    @Persistent
    @Column(name = "custom_column_name")
    private String customColumnName;
    
    @Persistent
    @Column(name = "project_name")
    private String _projectName;
    
    @Persistent
    @Column(name = "project_namespace")
    private Date _projectNamespace;

    public void setCustomColumnName(String customColumnName) {
        this.customColumnName = customColumnName;
    }

    public String getCustomColumnName() {
        return customColumnName;
    }

    public void setProjectName(String _projectName) {
        this._projectName = _projectName;
    }

    public String getProjectName() {
        return _projectName;
    }

    public void setProjectNamespace(Date _projectNamespace) {
        this._projectNamespace = _projectNamespace;
    }

    public Date getProjectNamespace() {
        return _projectNamespace;
    }
}
