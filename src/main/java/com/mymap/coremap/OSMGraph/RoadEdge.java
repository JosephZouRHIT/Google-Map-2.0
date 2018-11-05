package com.mymap.coremap.OSMGraph;

import com.mymap.coremap.OSMUtil.OSMWay;

import java.util.LinkedList;


/**
 * author: Lining Pan
 */
public class RoadEdge extends AbstractEdge {

    private OSMWay o_way;
    private LinkedList<Long> nodeList;
    private double speed;
	private double distance;

	RoadEdge(OSMWay o, LinkedList<Long> nodeList, AbstractVertex f, AbstractVertex t, double spd, double dis) {
		super(f, t);
		o_way = o;
		this.nodeList = nodeList;
        speed = spd;
		distance = dis;
	}

	@Override
	protected double getCostByTime() {
	    double cost = distance/speed;
		if(Double.isNaN(cost) || cost < 0){
		    return 0;
        }
        return cost;
	}

	@Override
	protected double getCostByDistance() {
		return distance;
	}

	//test speed read
	// TO BE REMOVED

	public double getSpeed(){
		return speed;
	}
	public double getDistance(){
	    return distance;
    }

    public final LinkedList<Long> getPath(){
	    return nodeList;
    }

    public String toString(){
	    return String.format("[Road edge: Name: %s, length %f, from: %s, to: %s]\n",
				o_way.getName(),
				distance,
				getFromNode().getLocString(),
				getToNode().getLocString()
		);
    }

    public final OSMWay getOSMWay(){
	    return o_way;
    }

}
