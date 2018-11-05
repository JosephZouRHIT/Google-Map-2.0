package com.mymap.coremap.OSMGraph;

import com.mymap.coremap.OSMUtil.OSMAbstractType;


/**
 * author: Lining Pan
 */
public class PointOfInterestVertex extends AbstractVertex {

	private String name;
	public PointOfInterestVertex(OSMAbstractType o, double c_lat, double c_lon, String n) {
		super(o, c_lat, c_lon);
		name = n;
	}
}
