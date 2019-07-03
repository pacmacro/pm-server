package com.pm.server.response;

import com.pm.server.datatype.Coordinate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocationResponse {

	private double latitude;
	private double longitude;

	private final static Logger log =
			LogManager.getLogger(LocationResponse.class.getName());

	public LocationResponse() {
		latitude = 0;
		longitude = 0;
	}

	public LocationResponse(Coordinate location) {
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
	}

	public void setLatitude(double latitude) {

		log.trace("Setting latitude to {}", latitude);

		this.latitude = latitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLongitude(double longitude) {

		log.trace("Setting longitude to {}", longitude);

		this.longitude = longitude;
	}

	public double getLongitude() {
		return longitude;
	}

}
