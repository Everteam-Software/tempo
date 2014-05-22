package org.intalio.tempo.workflow.task;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name="tempo_custom_column")
@NamedQueries({
    @NamedQuery(name = CustomColumn.FIND_BY_PROCESS_NAME, query = "select distinct t from CustomColumn t where t._projectName=(?1)"),
    @NamedQuery(name=CustomColumn.FIND_ALL_CUSTOM_COLUMNS, query="select distinct t.customColumnName from CustomColumn t" )}
)
public class CustomColumn {
    public static final String FIND_BY_PROCESS_NAME = "find_by_process_name";
    public static final String FIND_ALL_CUSTOM_COLUMNS= "find_all_custom_columns";
    
    @Column(name = "custom_column_name")
    private String customColumnName;
    
    @Column(name = "project_name")
    private String _projectName;
    
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

    @Column(name = "ID")
    @Basic
    @Id
    @TableGenerator(name="customcol" , table="OPENJPA_SEQUENCE_TABLE", pkColumnName="ID" , valueColumnName="SEQUENCE_VALUE" , pkColumnValue = "0", allocationSize=10)
    @GeneratedValue(strategy=GenerationType.TABLE , generator="customcol")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
