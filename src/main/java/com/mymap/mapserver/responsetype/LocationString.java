package com.mymap.mapserver.responsetype;

import com.mymap.coremap.OSMUtil.GeoLocation;

public class LocationString {
    private String loc;

    public LocationString(GeoLocation g) {
        this.loc = g.getLocString();
    }

    public String getLoc() {
        return loc;
    }
}
