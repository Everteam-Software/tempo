package org.intalio.tempo.workflow.task;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.intalio.tempo.workflow.task.traits.ITaskWithOutput;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.w3c.dom.Document;

@Entity
@Table(name="tempo_pipa_output")
//@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
@NamedQuery(name = PIPATaskOutput.FIND_BY_TASK_ID_AND_USER, query = "select m from PIPATaskOutput m where m.pipaTaskId=? and m.userOwner=?", hints = { @QueryHint(name = "org.hibernate.fetchSize", value = "1") })
                   
public class PIPATaskOutput implements ITaskWithOutput{
	
	public static final String FIND_BY_TASK_ID_AND_USER = "find_by_task_id_and_user";
	
	@Column(name="user_owner")
	private String userOwner;
	
	@Column(name="output_xml")
	private String output;
	
	@Column(name="task_id")
	private String pipaTaskId;
	
	public PIPATaskOutput(){
		
	}
	
//	public PIPATaskWithOutput(PIPATask task) {
//		this.setCreationDate(task.getCreationDate());
//		this.setDescription(task.getDescription());
//		this.setFormURL(task.getFormURL());
//		this.setInitMessageNamespaceURI(task.getInitMessageNamespaceURI());
//		this.setInitOperationSOAPAction(task.getInitOperationSOAPAction());
//		this.setInternalID(task.getInternalId());
//		this.setProcessEndpoint(task.getProcessEndpoint());
//		this.setRoleOwners(task.getRoleOwners());
//		this.setUserOwners(task.getUserOwners());
//		
//	}

	public String getUserOwner() {
		return userOwner;
	}
	
	public void setUserOwner(String userOwner) {
		this.userOwner = userOwner;
	}
	
	@Override
	public void setOutput(String outputXml) {
		this.output = outputXml;
	}
	
	@Override
    public void setOutput(Document outputDocument) {
        if (outputDocument == null) {
            throw new RequiredArgumentException("outputDocument");
        }
        output = XmlTooling.serializeDocument(outputDocument);
    }
	
	public String getPipaTaskId() {
		return pipaTaskId;
	}
	
	public void setPipaTaskId(String pipaTaskId) {
		this.pipaTaskId = pipaTaskId;
	}
	
	
	
    public String getOutputAsXmlString() {
        return output;
    }

	@Override
	public Document getOutput() {
		return XmlTooling.deserializeDocument(output);
	}
	
    @Column(name = "ID")
    @Basic
    @Id
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
