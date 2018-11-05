package com.mymap.coremap.GeoLookUpMap;

import com.mymap.coremap.OSMUtil.GeoLocation;
import com.mymap.coremap.OSMUtil.OSMMathUtil;
import com.mymap.coremap.OSMUtil.OSMNode;

import java.util.Set;

public class NearestRoadFinder {
    private LocationKDTree<OSMNode> locTree;

    public NearestRoadFinder(Set<OSMNode> s){
        this.locTree = new LocationKDTree<>(s);
    }

    public OSMNode findNearsetRoad(GeoLocation loc){
        Set<OSMNode> s = locTree.nearByLocations(loc);
        OSMNode nearest = null;
        double dist = Double.POSITIVE_INFINITY;
        for(OSMNode n : s){
            double cur_dist = OSMMathUtil.distance(loc, n);
            if(cur_dist < dist){
                dist = cur_dist;
                nearest = n;
            }
        }
        return nearest;
    }

    public double[] getBound(){
        return locTree.getRootBound();
    }

}
