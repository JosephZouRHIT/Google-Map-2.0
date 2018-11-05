package com.mymap.coremap.OSMGraph;

import com.mymap.coremap.OSMUtil.Axis;
import com.mymap.coremap.OSMUtil.GeoLocation;
import com.mymap.coremap.OSMUtil.OSMAbstractType;

/**
 * author: Lining Pan
 */
public abstract class AbstractVertex implements GeoLocation {
	private OSMAbstractType osm_ver;
	private double center_lat;
	private double center_lon;
	
	public AbstractVertex(OSMAbstractType o, double c_lat, double c_lon) {
		osm_ver = o;
		center_lat = c_lat;
		center_lon = c_lon;
	}
	
	public long getID() {
		return osm_ver.getID();
	}

	@Override
	public double getValueAxis(Axis axis){
		if(axis == Axis.LAT)
			return getLatitude();
		else
			return getLongitude();
	}

	@Override
	public double getLongitude() {
		return center_lon;
	}

	@Override
	public double getLatitude() {
		return center_lat;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((osm_ver == null) ? 0 : osm_ver.hashCode());
		return result;
	}

	public String toString(){
		return String.format("[AbstractVertex: id: %d, at: %f,%f]",getID(),center_lat,center_lon);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractVertex other = (AbstractVertex) obj;
		if (osm_ver == null) {
			return other.osm_ver == null;
		} else return osm_ver.equals(other.osm_ver);
	}
	public String getLocString(){
		return String.format("%f,%f",center_lat,center_lon);
	}
}
