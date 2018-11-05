package com.mymap.mapserver.responsetype;

import com.mymap.coremap.OSMUtil.GeoLocation;

public class InitialView {
    private final Location pos;
    private final int zoom;

    public InitialView(GeoLocation center, int zoom){
        pos = new Location(center);
        this.zoom = zoom;
    }

    public Location getPos() {
        return pos;
    }

    public int getZoom() {
        return zoom;
    }
}
