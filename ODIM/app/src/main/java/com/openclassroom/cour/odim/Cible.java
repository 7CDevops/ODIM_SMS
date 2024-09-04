package com.openclassroom.cour.odim;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.location.Location;

import com.openclassroom.cour.odim.utils.LatLonConvert;


public class Cible {

	private String 		phoneNumber 	= null;
	private String 		msg 			= null;
	private Location 	location 		= null;
	private Date 		date 			= null;
	private String 		dateStr			= "";
	private long 		timeBetween 	= 0;
	private float 		distanceTo		= (float) 99999.0;
	private boolean 	selected 		= false;
	private boolean 	done			= false;
	//==========================================//
	private String 		transmissionStatus	= "";
	private boolean 	delivered			= false;
	//==========================================//



	public Cible(String phoneNumber, String msg, Location location) {
		super();
		this.phoneNumber = phoneNumber;
		this.msg = msg;
		this.location = location;
	}
	public Cible(String phoneNumber, String msg, Date date) {
		super();
		this.phoneNumber = phoneNumber;
		this.msg = msg;
		this.date = date;
		this.dateStr = getDateInStr();
	}
	public Cible(String phoneNumber, String msg) {
		super();
		this.phoneNumber = phoneNumber;
		this.msg = msg;
	}
	private String getDateInStr() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return df.format(date);
	}


	public void setDate(Date date) {
		this.date = date;
	}
	public Date getDate() {
		return date;
	}
	public String getDateStr() {
		return dateStr;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setDistanceTo(float distanceTo) {
		this.distanceTo = distanceTo;
	}
	public void setTimeBetween(Date pDate) {
		this.timeBetween = date.getTime()-pDate.getTime();
	}
	public long getTimeBetween() {
		return timeBetween;
	}
	public float getDistanceTo() {
		return distanceTo;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	public boolean isDone() {
		return done;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public String getMsg() {
		return msg;
	}

	public boolean isDelivered() {
		return delivered;
	}
	public String isDeliveredStatus() {
		if (delivered) {
			return "Message délivré";
		} else {
			return "Message non délivré";
		}
	}
	public String getTransmissionStatus() {
		return transmissionStatus;
	}
	public void setTransmissionStatus(String transmissionStatus) {
		this.transmissionStatus = transmissionStatus;
	}
	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}


	public String longLat(){
		LatLonConvert llc;
		llc = new LatLonConvert(location.getLongitude());
		String longitude =(int)llc.getDegree()+"°"+(int)llc.getMinute()+"'"+(int)llc.getSecond()+"''";
		llc = new LatLonConvert(location.getLatitude());
		String latitude =(int)llc.getDegree()+"°"+(int)llc.getMinute()+"'"+(int)llc.getSecond()+"''";
		return longitude+"/"+latitude;
	}

	@Override
	public String toString() {
		return this.getPhoneNumber()+"\t"+this.getMsg().trim()+"\t"+this.getTransmissionStatus()+"\t"+this.isDeliveredStatus()+"\n";
	}
	
}
