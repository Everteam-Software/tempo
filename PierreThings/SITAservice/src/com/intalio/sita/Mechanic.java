package com.intalio.sita;

public class Mechanic {

	private String name;
	private String id;
	private String certified;

	public Mechanic(String name, String id, String certified) {
		this.name = name;
		this.id = id;
		this.certified = certified;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getCertified() {
		return certified;
	}

}
