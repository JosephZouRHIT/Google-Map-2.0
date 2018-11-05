package com.mymap.mapserver.responsetype;

import com.mymap.coremap.OSMUtil.GeoLocation;
import com.mymap.coremap.ServerBackend.SearchResultWrapper;

import java.util.LinkedList;

public class MapRoute {
    private final LinkedList<Location> route;
    private final double distance;
    private final double time;
    private final boolean have_route;

    public MapRoute(SearchResultWrapper resultWrapper) {
        have_route = resultWrapper.getRoute() != null;
        this.route = new LinkedList<>();
        if (resultWrapper.getRoute() != null) {
            for (GeoLocation gloc : resultWrapper.getRoute()) {
                this.route.addLast(new Location(gloc));
            }
        }
        this.distance = resultWrapper.getDistance();
        this.time = resultWrapper.getTime();

    }

    public boolean isHave_route() {
        return have_route;
    }

    public LinkedList<Location> getRoute() {
        return route;
    }


    public double getDistance() {
        return distance;
    }

    public double getTime() {
        return time;
    }
}
