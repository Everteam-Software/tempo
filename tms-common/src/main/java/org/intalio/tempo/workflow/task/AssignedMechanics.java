package org.intalio.tempo.workflow.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.openjpa.persistence.Persistent;

@Entity
@Table(name = "tempo_sita_assignedAvionics")
public class AssignedMechanics {
	@Persistent
	@Column(name = "name")
	private String _name;
//	@Persistent
//	@Column(name = "mechanicID")
//	String _mechanicID;

	@Persistent
	@Column(name = "entitledToRelease")
	private Boolean _entitledToRelease;
	
	public Boolean get_entitledToRelease() {
		return _entitledToRelease;
	}

	public void set_entitledToRelease(Boolean toRelease) {
		_entitledToRelease = toRelease;
	}

	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		this._name = _name;
	}


}
