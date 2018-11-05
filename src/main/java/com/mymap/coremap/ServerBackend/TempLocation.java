package com.mymap.coremap.ServerBackend;


import com.mymap.coremap.OSMUtil.Axis;
import com.mymap.coremap.OSMUtil.GeoLocation;

public class TempLocation implements GeoLocation {
    private double longitude;
    private double latitude;

    public TempLocation(String latitude, String longitude){
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }

    public TempLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public TempLocation(GeoLocation g, double offset_lat, double offset_lon){
        longitude = g.getLongitude() + offset_lon;
        latitude = g.getLatitude() + offset_lat;
    }

    @Override
    public double getValueAxis(Axis axis) {
        if(axis == Axis.LAT)
            return latitude;
        else
            return longitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public String getLocString() {
        return String.format("%f,%f",latitude,longitude);
    }
}