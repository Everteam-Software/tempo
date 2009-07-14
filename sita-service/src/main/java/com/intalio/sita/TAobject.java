package com.intalio.sita;

import java.util.Calendar;

import org.apache.axiom.om.OMElement;

public class TAobject {

	private OMElement FormModel;
	private Calendar date;
	private int id;

	public TAobject(OMElement FormModel, Calendar date, int id) {
		this.FormModel = FormModel;
		this.date = date;
		this.id = id;
	}

	public OMElement getFormModel() {
		return FormModel;
	}

	public Calendar getDate() {
		return date;
	}

	public int getId() {
		return id;
	}

}
