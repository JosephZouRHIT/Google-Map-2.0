package com.mymap.mapserver.responsetype;

import com.mymap.coremap.OSMUtil.GeoLocation;

public class Location {
    private final double lat;
    private final double lng;

    public Location(GeoLocation gl){
        lat = gl.getLatitude();
        lng = gl.getLongitude();
    }

    public Location(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
