package com.mymap.mapserver.responsetype;

public class Area {
    private final double lat_N;
    private final double lat_S;
    private final double lon_E;
    private final double lon_W;

    public Area(double[] area) {
        lat_N = area[0];
        lat_S = area[1];
        lon_E = area[2];
        lon_W = area[3];
    }

    public double getLat_N() {
        return lat_N;
    }

    public double getLat_S() {
        return lat_S;
    }

    public double getLon_E() {
        return lon_E;
    }

    public double getLon_W() {
        return lon_W;
    }
}
