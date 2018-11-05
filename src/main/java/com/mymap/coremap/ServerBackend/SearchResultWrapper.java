package com.mymap.coremap.ServerBackend;

import com.mymap.coremap.OSMUtil.GeoLocation;

import java.util.LinkedList;

public class SearchResultWrapper {
    private double distance;
    private double time;
    private LinkedList<GeoLocation> route;

    public SearchResultWrapper() {
        route = new LinkedList<>();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public LinkedList<GeoLocation> getRoute() {
        return route;
    }
}
