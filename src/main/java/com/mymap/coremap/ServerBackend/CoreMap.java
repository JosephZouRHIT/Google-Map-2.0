package com.mymap.coremap.ServerBackend;

import com.mymap.coremap.GeoLookUpMap.NearestRoadFinder;
import com.mymap.coremap.OSMGraph.*;
import com.mymap.coremap.OSMUtil.GeoLocation;
import com.mymap.coremap.OSMUtil.OSMAbstractDataModel;
import com.mymap.coremap.OSMUtil.OSMMathUtil;
import com.mymap.coremap.OSMUtil.OSMXMLInterpreter;
import com.mymap.coremap.SearchEngine.AbstractSearchEngine;
import com.mymap.coremap.SearchEngine.SearchEngineFactory;

import java.util.LinkedList;
import java.util.List;

public class CoreMap {
    private OSMAbstractDataModel dm;
    private NearestRoadFinder nrf;
    private Graph graph;

    //    public CoreMap(){
//        loadWithFile("Terre Haute.osm");
//    }
    public CoreMap(String osmfile) {
        loadWithFile(osmfile);
    }

    private void loadWithFile(String osmfile) {
        dm = OSMXMLInterpreter.loadFromFile(osmfile);
        assert dm != null;
        OSMGraphConstructor gc = new OSMGraphConstructor(dm);
        graph = gc.getGraph();
        nrf = new NearestRoadFinder(dm.getNodeByIdSet(graph.getNodeOnRoad()));
    }

    public GeoLocation getCenter() {
        double[] area = getMapBound();
        return new TempLocation((area[0] + area[1]) / 2, (area[2] + area[3]) / 2);
    }

    public double[] getMapBound() {
        //System.out.println(dm.getXMLbound());
        return dm.getXMLbound();
    }

    public int getZoomLevel() {
        return OSMMathUtil.getZoomLevel(getMapBound());

    }

    private boolean inMap(GeoLocation loc){
        return OSMMathUtil.inBoundary(loc, dm.getXMLbound());
    }

    public final SearchResultWrapper findRoute(GeoLocation st, GeoLocation ed, String se, String cost) {
        if (!SearchEngineFactory.isSupportedSearchEngine(se)) {
            //System.err.println(String.format("Search engine %s is not supported",se));
            throw new IllegalArgumentException();
        }
        SearchResultWrapper resultWrapper = new SearchResultWrapper();
        if(inMap(st) && inMap(ed)) {
            FindRouteQuery query = new FindRouteQuery(st, ed, resultWrapper, se, cost);
            query.run();
        }
        return resultWrapper;
    }

    private class FindRouteQuery implements Runnable {

        private GeoLocation startLocation;
        private GeoLocation endLocation;
        private LinkedList<GeoLocation> fullList;
        private SearchResultWrapper resultWrapper;
        private String searchEngine;
        private CostType costType;

        public FindRouteQuery(GeoLocation st, GeoLocation ed, SearchResultWrapper resultWrapper, String se, String cost) {
            startLocation = st;
            endLocation = ed;
            fullList = resultWrapper.getRoute();
            this.resultWrapper = resultWrapper;
            this.searchEngine = se;
            if (cost.equals("Distance")) {
                costType = CostType.DISTANCE;
            } else {
                costType = CostType.TIME;
            }
        }

        @Override
        public void run() {
            RoadVertex stVet = new RoadVertex(nrf.findNearsetRoad(startLocation));
            RoadVertex edVet = new RoadVertex(nrf.findNearsetRoad(endLocation));
//            DijkstraSearchEngine.partialEdge = new LinkedList<>();
//            System.out.println(stVet.getID());
//            System.out.println(edVet.getID());
//            System.out.println();
            assert (graph.isOnRoad(stVet.getID()));
            assert (graph.isOnRoad(edVet.getID()));
            AbstractSearchEngine engine = SearchEngineFactory.getSearchEngine(searchEngine, graph);
            List<RoadEdge> mainroute = engine.getVertexRoute(stVet, edVet, costType);
//            getAllPartialEdge();
            if (mainroute == null)
                return;

            double total_distance = 0;
            double total_estimate_time = 0;

            fullList.addLast(startLocation);
            fullList.addLast(stVet);
            //walk to nearest road
            double walk_dist = OSMMathUtil.distance(startLocation, stVet);
            total_distance += walk_dist;
            total_estimate_time += walk_dist / 5;

            for (RoadEdge i : mainroute) {
                for (Long n : i.getPath()) {
                    fullList.addLast(dm.getNodeById(n));
                }
                total_distance += i.getCost(CostType.DISTANCE);
                total_estimate_time += i.getCost(CostType.TIME);
            }

            fullList.addLast(edVet);
            fullList.addLast(endLocation);
            //walk to final dest
            walk_dist = OSMMathUtil.distance(edVet, endLocation);
            total_distance += walk_dist;
            total_estimate_time += walk_dist / 5;
            resultWrapper.setDistance(total_distance);
            resultWrapper.setTime(total_estimate_time);
        }
        //Debug
//        public LinkedList<LinkedList<GeoLocation>> getAllPartialEdge(){
//            LinkedList<LinkedList<GeoLocation>> result = new LinkedList<>();
//            for(RoadEdge e : DijkstraSearchEngine.partialEdge){
//                LinkedList<GeoLocation> tmp = new LinkedList<>();
//                for(Long g:e.getPath()){
//                    tmp.addLast(dm.getNodeById(g));
//                }
//                result.add(tmp);
//            }
//            for(List<GeoLocation> e: result){
//                System.out.println("[");
//                for(GeoLocation l: e) {
//                    System.out.println(l.getLocString());
//                }System.out.println("]");
//            }
//            return result;
//        }
    }
}
