package org.intalio.tempo.workflow.task;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.openjpa.persistence.Persistent;

@Entity
@Table(name = "tempo_sita_inspections")
public class Inspection {
	@Persistent
	@Column(name = "InspectionType")
	private String _InspectionType;

	@Persistent
	@Column(name = "startDate")
	private Date _startDate;

	@Persistent
	@Column(name = "endDate")
	private Date _endDate;

	// @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	// Collection<org.intalio.tempo.workflow.task.RTR> _RTR;

	@Persistent
	@Column(name = "remarks")
	private String _remarks;

	public String get_InspectionType() {
		return _InspectionType;
	}

	public void set_InspectionType(String inspectionType) {
		_InspectionType = inspectionType;
	}

	public Date get_startDate() {
		return _startDate;
	}

	public void set_startDate(Date date) {
		_startDate = date;
	}

	public Date get_endDate() {
		return _endDate;
	}

	public void set_endDate(Date date) {
		_endDate = date;
	}

	public String get_remarks() {
		return _remarks;
	}

	public void set_remarks(String _remarks) {
		this._remarks = _remarks;
	}

	// public Collection<org.intalio.tempo.workflow.task.RTR> get_RTR() {
	// return _RTR;
	// }
	//
	// public void set_RTR(Collection<org.intalio.tempo.workflow.task.RTR> _rtr)
	// {
	// _RTR = _rtr;
	// }
}
