package com.mymap.coremap.SearchEngine;

import com.mymap.coremap.OSMGraph.*;
import com.mymap.coremap.OSMGraph.Graph.Direction;
import com.mymap.coremap.OSMUtil.OSMMathUtil;

import java.util.*;

public class ZouAStarSearchEngine implements AbstractSearchEngine {

    private Graph g;

    public ZouAStarSearchEngine(Graph g) {
        this.g = g;
    }

    @Override
    public List<RoadEdge> getVertexRoute(AbstractVertex from, AbstractVertex to, CostType cost) {
        Set<AbstractVertex> closedSet = new HashSet<>();
        Set<AbstractVertex> openSet = new HashSet<>();
        Map<AbstractVertex, RoadEdge> cameFrom = new HashMap<>();
        Map<AbstractVertex, Double> gscore = new HashMap<>();
        gscore.put(from, 0.0);
        Comparator<FscoreVertex> fscorecomp = new VertexFScoreComparator();
        PriorityQueue<FscoreVertex> fscore = new PriorityQueue<>(10, fscorecomp);
        if (!g.isVertex(from.getID())) {
            Set<AbstractEdge> startroads = g.getEdgesWithInternalNode(from.getID());
            for (AbstractEdge e : startroads) {
                AbstractVertex tempnode = e.getToNode();
                openSet.add(tempnode);
                RoadEdge temp = g.getPartialEdge((RoadEdge) e, from, Direction.FORWARD);
                double tempgscore = temp.getCost(cost);
                gscore.put(tempnode, tempgscore);
                cameFrom.put(tempnode, (RoadEdge) temp);
                fscore.add(new FscoreVertex(tempgscore +
                        OSMMathUtil.calculateEstimateCost(tempnode, to, cost), tempnode));
            }
        } else {
            openSet.add(from);
            double sfscore = OSMMathUtil.calculateEstimateCost(from, to, cost);
            fscore.add(new FscoreVertex(sfscore, from));
        }
        Set<AbstractEdge> goalRoads = new HashSet<>();
        if (!g.isVertex(to.getID())) {
            goalRoads = g.getEdgesWithInternalNode(to.getID());
        }
        while (!openSet.isEmpty()) {
            AbstractVertex current = fscore.poll().v;
            openSet.remove(current);
            closedSet.add(current);
            Iterator<AbstractEdge> edges = g.getEdgeIterator(current);
            while (edges.hasNext()) {
                AbstractEdge temp = edges.next();
                if (temp.getToNode().equals(to) || goalRoads.contains(temp)) {
                    LinkedList<RoadEdge> result = new LinkedList<>();
                    AbstractVertex tempnode = to;
                    if (!g.isVertex(to.getID())) {
                        cameFrom.put(to, g.getPartialEdge((RoadEdge) temp, to, Direction.BACK));
                    } else {
                        cameFrom.put(to, (RoadEdge) temp);
                    }//System.out.println("Backtrace");
                    while (!tempnode.equals(from)) {
                        //System.out.println(tempnode.getID());
                        RoadEdge tempRoad = cameFrom.get(tempnode);
                        result.addFirst(tempRoad);//
                        tempnode = tempRoad.getFromNode();
                    }
                    return result;
                }
                AbstractVertex neighbor = temp.getToNode();
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                double ngscore = gscore.get(current) + temp.getCost(cost);
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                } else if (ngscore >= gscore.get(neighbor)) {
                    continue;
                }
                cameFrom.put(neighbor, (RoadEdge) temp);
                gscore.put(neighbor, ngscore);
                double nfscore = gscore.get(neighbor) + OSMMathUtil.calculateEstimateCost(neighbor, to, cost);
                fscore.add(new FscoreVertex(nfscore, neighbor));
            }
        }
        return null;
    }

    @Override
    public List<RoadEdge> getVertexRoute(AbstractVertex from, AbstractVertex to) {
        return getVertexRoute(from, to, CostType.TIME);
    }

    private class FscoreVertex {

        private double fscore;
        private AbstractVertex v;

        FscoreVertex(double fscore, AbstractVertex vertex) {
            this.fscore = fscore;
            this.v = vertex;
        }
    }

    private class VertexFScoreComparator implements Comparator<FscoreVertex> {

        @Override
        public int compare(FscoreVertex arg0, FscoreVertex arg1) {
            // TODO Auto-generated method stub
            return Double.compare(arg0.fscore, arg1.fscore);
        }

    }

}
