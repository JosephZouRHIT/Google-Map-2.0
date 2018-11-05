package com.mymap.coremap.OSMUtil;

import java.util.Map;

/**
 * author: Lining Pan
 */
public class OSMNode extends OSMAbstractType implements GeoLocation {
	
	private double longitude;
	private double latitude;

	public OSMNode(long _id, Map<String, String> map, double lat, double lon) {
		super(_id, map);
		this.latitude = lat;
		this.longitude = lon;
	}

	@Override
	public double getValueAxis(Axis axis) {
		if(axis == Axis.LAT)
			return getLatitude();
		else
			return getLongitude();
	}

	public double getLongitude() {
		return this.longitude;
	}
	public double getLatitude() {
		return this.latitude;
	}

	public String getLocString() {
		return String.format("%f,%f",latitude,longitude);
	}


	@Override
	public String toString() {
		return super.toStringHelper(String.format("latitude: %f, longitude:%f",this.latitude, this.longitude));
	}

}

