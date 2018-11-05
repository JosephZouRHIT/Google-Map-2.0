package com.mymap.coremap.OSMGraph;

import com.mymap.coremap.OSMUtil.OSMAbstractDataModel;
import com.mymap.coremap.OSMUtil.OSMMathUtil;
import com.mymap.coremap.OSMUtil.OSMNode;

import java.util.*;

/**
 * author: Lining Pan
 */
public class Graph {
    private Map<Long, AbstractVertex> verList;
    private Set<Long> nodeOnRoad;
    private Map<AbstractEdge, Set<Long>> nodeInEdge;
    private Map<AbstractVertex, LinkedList<AbstractEdge>> adjList;
    private Map<Long, Set<AbstractEdge>> way2edgeList;
    private OSMAbstractDataModel dm;

    public Graph(Map<Long, AbstractVertex> v,
                 Map<AbstractVertex, LinkedList<AbstractEdge>> a,
                 Set<Long> nr,
                 Map<AbstractEdge, Set<Long>> nie,
                 Map<Long, Set<AbstractEdge>> wl,
                 OSMAbstractDataModel d) {
        verList = v;
        nodeOnRoad = nr;
        adjList = a;
        nodeInEdge = nie;
        way2edgeList = wl;
        dm = d;
    }

    public Iterator<AbstractEdge> getEdgeIterator(AbstractVertex v) {
        if (adjList.containsKey(v)) {
            return adjList.get(v).iterator();
        }
        return null;
    }

    public AbstractVertex getVertexById(long id) {
        return verList.getOrDefault(id, null);
    }

    public boolean isVertex(long id) {
        return verList.containsKey(id);
    }

    public Set<AbstractEdge> getEdgesContainedByWay(long id) {
        if (this.way2edgeList.containsKey(id)) {
            return this.way2edgeList.get(id);
        }
        return null;
    }

    public Set<AbstractEdge> getEdgesWithInternalNode(long id) {
        Set<Long> ways = dm.getAllWayContainsNode(id);
        Set<AbstractEdge> re = new HashSet<>();
        for (Long i : ways) {
            Set<AbstractEdge> se = way2edgeList.getOrDefault(i, null);
            if (se != null) {
                for (AbstractEdge e : se) {
                    if (nodeInEdge.get(e).contains(id)) {
                        re.add(e);
                    }
                }
            }
        }
        return re;
    }

    public boolean isOnRoad(long id) {
        return nodeOnRoad.contains(id);
    }

    public final Set<Long> getNodeOnRoad() {
        return nodeOnRoad;
    }

    private double getDistanceOfInternalNode(RoadEdge e,
                                             AbstractVertex v,
                                             Direction d,
                                             LinkedList<Long> wayListDest) {
        if (!nodeInEdge.containsKey(e)) {
            System.err.println("Edge do bot exist");
            return Double.NaN;
        }
        if (!nodeInEdge.get(e).contains(v.getID())) {
            System.err.println("Vertex is not on this Edge");
            return Double.NaN;
        }
        double dis = 0;
        OSMNode last = null;
        switch (d) {
            case BACK:
                for (Long i : e.getPath()) {
                    if (last != null) {
                        dis += OSMMathUtil.distance(dm.getNodeById(i), last);
                    }
                    wayListDest.add(i);
                    last = dm.getNodeById(i);
                    if (i == v.getID()) {
                        break;
                    }
                }
                break;
            case FORWARD:
                Iterator<Long> it = e.getPath().iterator();
                while (it.next() != v.getID()) ;
                last = dm.getNodeById(v.getID());
                wayListDest.add(v.getID());
                while (it.hasNext()) {
                    Long cur = it.next();
                    dis += OSMMathUtil.distance(last, dm.getNodeById(cur));
                    wayListDest.add(cur);
                }
                break;
        }
        return dis;
    }

    ;

    public RoadEdge getPartialEdge(RoadEdge e, AbstractVertex v, Direction d) {
        if (!nodeInEdge.containsKey(e)) {
            //System.err.println("Edge do bot exist");
            throw new IllegalArgumentException();
        }
        if (!nodeInEdge.get(e).contains(v.getID())) {
//            System.err.println(e);
//            System.err.println(v);
//            System.err.println("Vertex is not on this Edge");
//            return null;
            throw new IllegalArgumentException();
        }

        double dis;
        LinkedList<Long> wayList = new LinkedList<>();
        RoadEdge new_e = null;
        switch (d) {
            case BACK:
                dis = getDistanceOfInternalNode(e, v, d, wayList);
                new_e = new RoadEdge(e.getOSMWay(), wayList, e.getFromNode(), v, e.getSpeed(), dis);
                break;
            case FORWARD:
                dis = getDistanceOfInternalNode(e, v, d, wayList);
                new_e = new RoadEdge(e.getOSMWay(), wayList, v, e.getToNode(), e.getSpeed(), dis);
                break;
        }
        return new_e;
    }

    public enum Direction {FORWARD, BACK}
}
