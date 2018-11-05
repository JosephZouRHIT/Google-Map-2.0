package com.mymap.coremap.SearchEngine;

import com.mymap.coremap.OSMGraph.*;
import com.mymap.coremap.OSMUtil.OSMMathUtil;

import java.util.*;

public class ZouAStarSearchEngine implements AbstractSearchEngine {

    private Graph g;

    public ZouAStarSearchEngine(Graph g) {
        this.g = g;
    }

    @Override
    public List<RoadEdge> getVertexRoute(AbstractVertex from, AbstractVertex to) {
        Set<AbstractVertex> closedSet = new HashSet<>();
        Set<AbstractVertex> openSet = new HashSet<>();
        openSet.add(from);
        Map<AbstractVertex, RoadEdge> cameFrom = new HashMap<>();
        Map<AbstractVertex, Double> gscore = new HashMap<>();
        gscore.put(from, 0.0);
        Comparator<FscoreVertex> fscorecomp = new VertexFScoreComparator();
        PriorityQueue<FscoreVertex> fscore = new PriorityQueue<>(10, fscorecomp);
        double sfscore = OSMMathUtil.distance(from, to);
        fscore.add(new FscoreVertex(sfscore, from));
        while (!openSet.isEmpty()) {
            AbstractVertex current = fscore.poll().v;
            if (current.equals(to)) {
                List<RoadEdge> result = new ArrayList<>();
                AbstractVertex temp = to;
                while (!temp.equals(from)) {
                    RoadEdge tempRoad = cameFrom.get(temp);
                    result.add(tempRoad);
                    temp = tempRoad.getFromNode();
                }
                return result;
            }
            openSet.remove(current);
            closedSet.add(current);
            Iterator<AbstractEdge> edges = g.getEdgeIterator(current);
            while (edges.hasNext()) {
                AbstractEdge temp = edges.next();
                AbstractVertex neighbor = temp.getToNode();
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                double ngscore = gscore.get(current) + OSMMathUtil.distance(current, neighbor);
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                } else if (ngscore >= gscore.get(neighbor)) {
                    continue;
                }
                cameFrom.put(neighbor, (RoadEdge) temp);
                gscore.put(neighbor, ngscore);
                double nfscore = gscore.get(neighbor) + OSMMathUtil.distance(neighbor, to);
                fscore.add(new FscoreVertex(nfscore, neighbor));
            }
        }
        return null;
    }

    @Override
    public List<RoadEdge> getVertexRoute(AbstractVertex from, AbstractVertex to, CostType costType) {
        return getVertexRoute(from, to);
    }

    private class FscoreVertex {

        private double fscore;
        private AbstractVertex v;

        public FscoreVertex(double fscore, AbstractVertex vertex) {
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
