package com.mymap.coremap.SearchEngine;

import com.mymap.coremap.OSMGraph.*;

import java.util.*;

public class AlecDijkstraSearchEngine implements AbstractSearchEngine {
    // default to time
    private Graph graph;

    public AlecDijkstraSearchEngine(Graph graph) {
        this.graph = graph;
    }

    public static void main(String[] args) {
    }

    @Override
    public List<RoadEdge> getVertexRoute(AbstractVertex from, AbstractVertex to) {
        return this.getVertexRoute(from, to, CostType.TIME);
    }

    // using dijkstra
    @Override
    public List<RoadEdge> getVertexRoute(AbstractVertex from, AbstractVertex to, CostType costType) {
        HashMap<AbstractVertex, RoadEdge> path = new HashMap<AbstractVertex, RoadEdge>();
        HashMap<AbstractVertex, Double> cost = new HashMap<AbstractVertex, Double>();
        HashSet<AbstractVertex> finished = new HashSet<AbstractVertex>();
        PriorityQueue<NodeWrapper> p = new PriorityQueue<NodeWrapper>();
        NodeWrapper currentV = null;
        boolean foundDestination = false;
        if (!this.graph.isVertex(from.getID())) {
            for (AbstractEdge e : graph.getEdgesWithInternalNode(from.getID())) {
                if (!finished.contains(e.getToNode())) {
                    RoadEdge tempE = graph.getPartialEdge((RoadEdge) e, from, Graph.Direction.FORWARD);
                    if (tempE.getCost(costType) < this.getCost(tempE.getToNode(), cost, costType)) {
                        cost.put(tempE.getToNode(), tempE.getCost(costType));
                        p.add(new NodeWrapper(tempE.getToNode(), tempE.getCost(costType)));
                        finished.add(tempE.getToNode());
                        path.put(e.getToNode(), tempE);
                    }
                }
            }
        } else {
            cost.put(from, 0.0);
            p.add(new NodeWrapper(from, 0.0));
        }
        currentV = p.poll();
        Iterator<AbstractEdge> i = this.graph.getEdgeIterator(currentV.vertex);
        while (true) {
            while (i.hasNext()) {
                AbstractEdge edge = i.next();
                if (this.getCost(edge.getToNode(), cost, costType) > currentV.cost + edge.getCost(costType)) {
                    finished.add(edge.getToNode());
                    path.put(edge.getToNode(), (RoadEdge) edge);
                    cost.put(edge.getToNode(), currentV.cost + edge.getCost(costType));
                    p.add(new NodeWrapper(edge.getToNode(), this.getCost(edge.getToNode(), cost, costType)));
                }
            }
            if (!p.isEmpty()) {
                currentV = p.poll();
            } else {
                break;
            }
            i = this.graph.getEdgeIterator(currentV.getVertex());
        }
        LinkedList<RoadEdge> returnPath = new LinkedList<RoadEdge>();
        if (this.graph.isVertex(to.getID())) {
            returnPath.add(path.get(to));
        } else {
            Set<AbstractEdge> edges = graph.getEdgesWithInternalNode(to.getID());
            RoadEdge fastestEdge = null;
            double lowestCost = Double.POSITIVE_INFINITY;
            for (AbstractEdge e : edges) {
                RoadEdge testEdge = graph.getPartialEdge((RoadEdge) e, to, Graph.Direction.BACK);
                double testCost = this.getCost(e.getFromNode(), cost, costType) + testEdge.getCost();
                if (testCost < lowestCost) {
                    lowestCost = testCost;
                    fastestEdge = testEdge;
                }
            }
            returnPath.add(fastestEdge);
        }
        while (!returnPath.getFirst().getFromNode().equals(from)) {
            RoadEdge e = returnPath.get(0);
            returnPath.add(0, path.get(e.getFromNode()));
        }
        return returnPath;
    }

    private double getCost(AbstractVertex v, HashMap<AbstractVertex, Double> cost, CostType costType) {
        //map has getOrDefault
        double tcost = Double.POSITIVE_INFINITY;
        if (cost.get(v) != null) {
            tcost = cost.get(v);
        }
        return tcost;
    }


    private class NodeWrapper implements Comparable<NodeWrapper> {

        private AbstractVertex vertex;
        private double cost;

        NodeWrapper(AbstractVertex v, double cost) {
            this.vertex = v;
            this.cost = cost;
        }

        AbstractVertex getVertex() {
            return this.vertex;
        }

        @Override
        public int compareTo(NodeWrapper o) {
            return Double.compare(this.cost, o.cost);
        }

    }

}
