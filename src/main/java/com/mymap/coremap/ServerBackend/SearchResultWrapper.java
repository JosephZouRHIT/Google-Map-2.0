package com.mymap.coremap.ServerBackend;

import com.mymap.coremap.OSMGraph.RoadEdge;
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

    public double getTime() {
        return time;
    }

    public LinkedList<GeoLocation> getRoute() {
        return route;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
