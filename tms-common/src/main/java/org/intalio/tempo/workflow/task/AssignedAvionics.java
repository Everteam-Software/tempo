package org.intalio.tempo.workflow.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.openjpa.persistence.Persistent;

@Entity
@Table(name = "tempo_sita_assignedMechanis")
public class AssignedAvionics {
	@Persistent
	@Column(name = "name")
	String _name;
//	@Persistent
//	@Column(name = "mechanicID")
//	String _mechanicID;

	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		this._name = _name;
	}

//	public String getMechanicID() {
//		return _mechanicID;
//	}
//
//	public void setMechanicID(String _mechanicid) {
//		_mechanicID = _mechanicid;
//	}
}
