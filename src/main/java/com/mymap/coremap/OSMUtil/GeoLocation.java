package com.mymap.coremap.OSMUtil;

/**
 * author: Lining Pan
 */
public interface GeoLocation {
	double getValueAxis(Axis axis);
	double getLongitude();
	double getLatitude();
	String getLocString();
}
