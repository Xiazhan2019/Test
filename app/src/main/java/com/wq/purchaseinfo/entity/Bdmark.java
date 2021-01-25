package com.wq.purchaseinfo.entity;

import java.io.Serializable;

public class Bdmark implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String number;
	private String title;
	private double latitude;
	private double longitude;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private String content;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Bdmark(int id, String number, String title, double latitude,
				  double longitude, String content) {
		super();
		this.id = id;
		this.number = number;
		this.title = title;
		this.latitude = latitude;
		this.longitude = longitude;
		this.content = content;
	}

}
