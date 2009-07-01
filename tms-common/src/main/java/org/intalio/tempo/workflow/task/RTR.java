package org.intalio.tempo.workflow.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.openjpa.persistence.Persistent;

@Entity
@Table(name = "tempo_sita_RTR")
public class RTR {
	@Persistent
	@Column(name = "RTRID")
	String _RTRID;
	@Persistent
	@Column(name = "status")
	String _RTRstatus;

	public String getRTRID() {
		return _RTRID;
	}

	public void setRTRID(String id) {
		this._RTRID = id;
	}

	public String getRTRStatus() {
		return _RTRstatus;
	}

	public void setRTRStatus(String status) {
		_RTRstatus = status;
	}
}
