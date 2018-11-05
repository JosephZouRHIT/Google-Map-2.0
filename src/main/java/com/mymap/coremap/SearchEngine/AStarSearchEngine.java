package com.mymap.coremap.SearchEngine;


import com.mymap.coremap.OSMGraph.*;
import com.mymap.coremap.OSMUtil.OSMMathUtil;

import java.util.*;

/**
 * author: Lining Pan
 */
public class AStarSearchEngine implements AbstractSearchEngine {

    private Graph graph;
    public AStarSearchEngine(Graph g){
        graph = g;
    }

    private class SearchState implements Comparable{
        AbstractVertex v;
        double costFromSrc;
        double AstarCost;

        SearchState(AbstractVertex v, double cost, double estimateCost){
            this.v = v;
            this.costFromSrc = cost;
            this.AstarCost = cost + estimateCost;
        }

        @Override
        public int compareTo(Object o) {
            if(getClass() != o.getClass()){
                throw new ClassCastException();
            }
            return Double.compare(AstarCost, ((SearchState) o).AstarCost);
        }
        public String toString(){
            return String.format("[SearchState: vertex: %s, cost: %f, a* cost: %f]", v.toString(), costFromSrc, AstarCost);
        }
    }

    @Override
    public List<RoadEdge> getVertexRoute(AbstractVertex from, AbstractVertex to) {
        return getVertexRoute(from,to, CostType.TIME);
    }

    @Override
    public List<RoadEdge> getVertexRoute(AbstractVertex from, AbstractVertex to, CostType costType) {
        if(!graph.isOnRoad(from.getID()) || !graph.isOnRoad(to.getID())){
            //Should use GeoLocation API
            //This function only navigate from road to road
            //System.err.println("Node not on road");
            return null;
        }
        HashMap<AbstractVertex, RoadEdge> backtrace = new HashMap<>();
        HashMap<AbstractVertex, Double> costFromSource = new HashMap<>();
        HashSet<AbstractVertex> visited = new HashSet<>();
        PriorityQueue<SearchState> pq = new PriorityQueue<>();

        if(!graph.isVertex(from.getID())){
            Set<AbstractEdge> se = graph.getEdgesWithInternalNode(from.getID());
            for(AbstractEdge e : se){
                if(!visited.contains(e.getToNode())){
                    RoadEdge new_e = graph.getPartialEdge((RoadEdge)e, from, Graph.Direction.FORWARD);
                    if(new_e.getCost(costType) < costFromSource.getOrDefault(new_e.getToNode(), Double.MAX_VALUE)) {
                        costFromSource.put(new_e.getToNode(), new_e.getCost(costType));
                        pq.offer(new SearchState(new_e.getToNode(), new_e.getCost(costType), OSMMathUtil.calculateEstimateCost(new_e.getToNode(), to, costType)));
                        visited.add(new_e.getToNode());
                        backtrace.put(e.getToNode(),new_e);
                    }
                }
            }
        } else {
            costFromSource.put(from, 0.0);
            pq.offer(new SearchState(from, 0.0, OSMMathUtil.calculateEstimateCost(from,to,costType)));
        }

        Set<Long> targetFinalDest = new HashSet<>();
        if(graph.isVertex(to.getID())){
            targetFinalDest.add(to.getID());
        }else{
            for(AbstractEdge e : graph.getEdgesWithInternalNode(to.getID())){
                targetFinalDest.add(e.getFromNodeID());
            }

        }

        boolean found = false;
        while(!pq.isEmpty() && !found){
            //System.out.println(pq);
            SearchState cur = pq.poll();

            Iterator<AbstractEdge> iter = graph.getEdgeIterator(cur.v);

            while(iter.hasNext()){
                AbstractEdge e = iter.next();
                double next_cost = cur.costFromSrc + e.getCost(costType);
                if(costFromSource.getOrDefault(e.getToNode(),Double.MAX_VALUE).compareTo(next_cost) > 0){
                    costFromSource.put(e.getToNode(), next_cost);
                    pq.offer(new SearchState(e.getToNode(), next_cost, OSMMathUtil.calculateEstimateCost(e.getToNode(),to,costType)));
                    visited.add(e.getToNode());
                    backtrace.put(e.getToNode(),(RoadEdge)e);
                    if(targetFinalDest.contains(e.getToNodeID())){
                        found = true;
                    }
                }
            }
        }

        //Backtrace
        LinkedList<RoadEdge> re = new LinkedList<>();
        if(graph.isVertex(to.getID())){
            //System.out.println(costFromSource.getOrDefault(to,Double.MAX_VALUE));
            re.addFirst(backtrace.get(to));
        } else {
            double best_res = Double.MAX_VALUE;
            RoadEdge te = null;
            Set<AbstractEdge> se = graph.getEdgesWithInternalNode(to.getID());
            for(AbstractEdge e : se){
                double dis_by_e = costFromSource.getOrDefault(e.getFromNode(),Double.MAX_VALUE);
                RoadEdge new_e = graph.getPartialEdge((RoadEdge)e, to, Graph.Direction.BACK);
                if(dis_by_e + new_e.getCost(costType) < best_res){
                    best_res = dis_by_e + new_e.getCost(costType);
                    te = new_e;
                }
            }
            if(te == null){
                //System.err.println("Cannot find route");
                return null;
            }
            re.addFirst(te);
        }
        while(!re.getFirst().getFromNode().equals(from)){
            RoadEdge e = re.getFirst();
            re.addFirst(backtrace.get(e.getFromNode()));
        }
        return re;
    }
}
