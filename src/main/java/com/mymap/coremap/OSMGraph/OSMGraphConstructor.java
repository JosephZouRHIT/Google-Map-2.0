package com.mymap.coremap.OSMGraph;

import com.mymap.coremap.OSMUtil.OSMAbstractDataModel;
import com.mymap.coremap.OSMUtil.OSMMathUtil;
import com.mymap.coremap.OSMUtil.OSMNode;
import com.mymap.coremap.OSMUtil.OSMWay;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: Lining Pan
 */
public class OSMGraphConstructor {
    //private Set<Long> nodeSet;
    private Set<Long> waySet;
    private Set<Long> nodeOnRoad;

    private Map<Long, AbstractVertex> verList;
    private Map<AbstractVertex, LinkedList<AbstractEdge>> adjList;
    private Map<Long, Set<AbstractEdge>> way2edgeList;
    private OSMAbstractDataModel dm;
    private Map<AbstractEdge, Set<Long>> nodeInEdge;

    private Graph graph;

    public OSMGraphConstructor(OSMAbstractDataModel dm) {
        this.dm = dm;
        //nodeSet = dm.getAllNodeId();
        this.waySet = dm.getAllWayId();
        this.verList = new ConcurrentHashMap<>();
        this.nodeOnRoad = new HashSet<>();
        this.adjList = new ConcurrentHashMap<>();
        this.way2edgeList = new ConcurrentHashMap<>();
        this.nodeInEdge = new ConcurrentHashMap<>();

        this.graph = this.constructGraph();
    }

    public Graph getGraph() {
        return this.graph;
    }

    private Graph constructGraph() {

        for (Long i_w : this.waySet) {
            OSMWay way = this.dm.getWayByID(i_w);

            if (!way.isRoad()) continue;
            double speed = way.getSpeedLimit();

            List<Long> wayList = way.getNodeIdList();
            Iterator<Long> it = wayList.iterator();
            OSMNode from = this.dm.getNodeById(it.next());
            OSMNode last = from;
            Set<Long> inEdge = new HashSet<>();
            this.nodeOnRoad.add(from.getID());
            LinkedList<Long> path = new LinkedList<>();
            path.addLast(from.getID());
            inEdge.add(from.getID());
            double length = 0.0;
            while (it.hasNext()) {
                OSMNode node = this.dm.getNodeById(it.next());
                length += OSMMathUtil.distance(last, node);

                this.nodeOnRoad.add(node.getID());
                path.addLast(node.getID());
                inEdge.add(node.getID());

                if (!it.hasNext() || this.dm.isVertexNode(node.getID())) {
                    //construct new edge(s)
                    RoadVertex f = new RoadVertex(from);
                    RoadVertex t = new RoadVertex(node);

                    this.verList.put(f.getID(), f);
                    this.verList.put(t.getID(), t);

                    if (!this.adjList.containsKey(f))
                        this.adjList.put(f, new LinkedList<AbstractEdge>());
                    if (!this.adjList.containsKey(t))
                        this.adjList.put(t, new LinkedList<AbstractEdge>());
                    if (!this.way2edgeList.containsKey(way.getID()))
                        this.way2edgeList.put(way.getID(), new HashSet<AbstractEdge>());

                    RoadEdge e = new RoadEdge(way, path, f, t, speed, length);
                    this.adjList.get(f).add(e);
                    this.way2edgeList.get(way.getID()).add(e);
                    this.nodeInEdge.put(e, inEdge);

                    if (!way.isOneway()) {
                        LinkedList<Long> rev = new LinkedList<>();
                        Iterator<Long> pathit = path.descendingIterator();
                        while (pathit.hasNext())
                            rev.addLast(pathit.next());
                        RoadEdge r = new RoadEdge(way, rev, t, f, speed, length);
                        this.adjList.get(t).add(r);
                        this.way2edgeList.get(way.getID()).add(r);
                        this.nodeInEdge.put(r, inEdge);
                    }
                    length = 0.0;
                    from = node;
                    inEdge = new HashSet<>();
                    path = new LinkedList<>();
                    path.addLast(node.getID());
                    inEdge.add(node.getID());
                } else {
                    inEdge.add(node.getID());
                }
                last = node;
            }
        }

        return new Graph(this.verList, this.adjList, this.nodeOnRoad, this.nodeInEdge, this.way2edgeList, this.dm);
    }
}
