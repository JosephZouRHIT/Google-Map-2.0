package com.mymap.coremap;

import com.mymap.coremap.GeoLookUpMap.NearestRoadFinder;
import com.mymap.coremap.OSMGraph.*;
import com.mymap.coremap.OSMUtil.*;
import com.mymap.coremap.SearchEngine.AStarSearchEngine;
import com.mymap.coremap.SearchEngine.AbstractSearchEngine;
import com.mymap.coremap.SearchEngine.DijkstraSearchEngine;
import com.mymap.coremap.ServerBackend.CoreMap;
import com.mymap.coremap.ServerBackend.SearchResultWrapper;
import com.mymap.coremap.ServerBackend.TempLocation;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * author: Lining Pan
 */
public class OSMTesting {
    private static final double DELTA_LOC_CONV = 1e-2;
    private static final double REG_DELTA = 1e-9;
    private static long id = 1;

    private static void runTestWithFileAndName(String file, String name) {
        System.out.println(String.format("Start running test %s", name));
        long startTime = System.currentTimeMillis();

        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile(file);

        long endTime = System.currentTimeMillis();

        assert dm != null;
//        System.out.println(dm.getAllNodeId().size());
//		System.out.println(dm.getAllWayId().size());
        long count = 0;//Node included in more than one way;
        long empty_count = 0;
        for (Long n : dm.getAllNodeId()) {
            Set<Long> s = dm.getAllWayContainsNode(n);
            if (s == null)
                empty_count++;
            else if (s.size() > 1) {
                count++;
            }
        }
//		System.out.println(String.format("%d node included by more than one way.",count));
//		System.out.println(String.format("%d node not included by any way.",empty_count));
        System.out.println(String.format("XML: %s: %d milliseconds", name, endTime - startTime));
        System.out.println();
    }
/*
    @Test
    public void testPartialEdge() {
        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile("test_partial_edge.osm");
        assert dm != null;
        OSMGraphConstructor gc = new OSMGraphConstructor(dm);
        Graph g = gc.getGraph();
        String[] s_vert = {"181882039", "181882045"};
        long[] l_vert = new long[s_vert.length];
        for (int i = 0; i < s_vert.length; i++) {
            l_vert[i] = Long.parseLong(s_vert[i]);
        }
        String[] s_node = {"181882042", "1123406159", "1123406195", "1123406215", "1123406213", "1123406178", "1123406278"};
        long[] l_node = new long[s_node.length];
        for (int i = 0; i < s_node.length; i++) {
            l_node[i] = Long.parseLong(s_node[i]);
        }
        for (long aL_vert : l_vert) {
            assertTrue(g.isVertex(aL_vert));
            assertTrue(g.isOnRoad(aL_vert));
        }
        for (long aL_node : l_node) {
            assertFalse(g.isVertex(aL_node));
            assertTrue(g.isOnRoad(aL_node));
        }
        RoadVertex v1 = new RoadVertex(dm.getNodeById(Long.parseLong("181882039")));
        RoadVertex v2 = new RoadVertex(dm.getNodeById(Long.parseLong("181882045")));
        RoadVertex vi = new RoadVertex(dm.getNodeById(Long.parseLong("1123406213")));
        Iterator<AbstractEdge> it = g.getEdgeIterator(v1);
        RoadEdge e1 = (RoadEdge) it.next();

        System.out.println(e1.getCost(CostType.DISTANCE));
        System.out.println(e1.getCost(CostType.TIME));
        RoadEdge r1 = g.getPartialEdge(e1, vi, Graph.Direction.FORWARD);
        String[] s_path1 = {"1123406213", "1123406178", "1123406278", "181882045"};
        long[] l_path1 = new long[s_path1.length];
        for (int i = 0; i < s_path1.length; i++) {
            l_path1[i] = Long.parseLong(s_path1[i]);
        }

        for (int i = 0; i < l_path1.length; i++) {
            assertEquals(l_path1[i], (long) r1.getPath().get(i));
        }
        System.out.println(r1.getCost(CostType.DISTANCE));
        System.out.println(r1.getCost(CostType.TIME));

        RoadEdge r2 = g.getPartialEdge(e1, vi, Graph.Direction.BACK);
        String[] s_path2 = {"181882039", "181882042", "1123406159", "1123406195", "1123406215", "1123406213"};
        long[] l_path2 = new long[s_path2.length];
        for (int i = 0; i < s_path2.length; i++) {
            l_path2[i] = Long.parseLong(s_path2[i]);
        }

        for (int i = 0; i < l_path2.length; i++) {
            assertEquals(l_path2[i], (long) r2.getPath().get(i));
        }


        System.out.println(r2.getCost(CostType.DISTANCE));
        System.out.println(r2.getCost(CostType.TIME));
    }
    */

    @Test
    public void testInBound() {
        TempLocation loc = new TempLocation("32.9", "-90.8");
        assertTrue(OSMMathUtil.inBoundary(loc, new double[]{33.0, 30.0, -90.0, -91.0}));

        assertFalse(OSMMathUtil.inBoundary(loc, new double[]{35.0, 33.0, -90.0, -91.0}));

        assertFalse(OSMMathUtil.inBoundary(loc, new double[]{33.0, 30.0, -80.0, -88.0}));
    }

    @Test
    public void testDistanceCalculation() {
        OSMNode n1 = new OSMNode(id++, null, 32.9697, -96.80322);
        OSMNode n2 = new OSMNode(id++, null, 29.46786, -98.53506);
        assertEquals(OSMMathUtil.distance(n1, n2), 262.68, DELTA_LOC_CONV);
        assertEquals(262.68, OSMMathUtil.distance(n1, n2, Unit.MILE), DELTA_LOC_CONV);
        assertEquals(422.74, OSMMathUtil.distance(n1, n2, Unit.KILO), DELTA_LOC_CONV);
        assertEquals(228.11, OSMMathUtil.distance(n1, n2, Unit.NM), DELTA_LOC_CONV);
        OSMNode n3 = new OSMNode(id++, null, 32.9697, -96.80);
        OSMNode n4 = new OSMNode(id++, null, 32.9697, -96.8011);
        System.out.println(OSMMathUtil.distance(n3, n4, Unit.KILO));
    }

    @Test
    public void testZoomeLevelToRadius() {
        System.out.println(OSMMathUtil.getRadiusAtZoomLevel(16));
    }

    @Test
    public void testIntersection() {
        double[] area1 = {11.5, 10.5, -30.7, -30.8};
        double[] area2 = {11.3, 10.7, -30.6, -30.76};
        double[] ans_1_2 = {11.3, 10.7, -30.7, -30.76};
        double[] testAns1 = OSMMathUtil.intersection(area1, area2);
        for (int i = 0; i < 4; i++) {
            assertEquals(ans_1_2[i], testAns1[i], REG_DELTA);
        }

        double[] area3 = {11.3, 10.7, -30.73, -30.76};
        double[] ans_1_3 = {11.3, 10.7, -30.73, -30.76};
        double[] testAns2 = OSMMathUtil.intersection(area1, area3);
        for (int i = 0; i < 4; i++) {
            assertEquals(ans_1_3[i], testAns2[i], REG_DELTA);
        }

        double[] area4 = {11.6, 10.7, -30.6, -30.76};
        double[] ans_1_4 = {11.5, 10.7, -30.7, -30.76};
        double[] testAns3 = OSMMathUtil.intersection(area1, area4);
        for (int i = 0; i < 4; i++) {
            assertEquals(ans_1_4[i], testAns3[i], REG_DELTA);
        }
        double[] area5 = {11.9, 11.6, -30.6, -30.76};
        double[] testAns4 = OSMMathUtil.intersection(area1, area5);
        for (int i = 0; i < 4; i++) {
            assertEquals(Double.NaN, testAns4[i], REG_DELTA);
        }
    }
//	@Test
//	public void testXMLIterpreterIndiana() {
//		runTestWithFileAndName("resources/indiana-latest.osm", "Indiana");
//	}

    @Test
    public void testNearByPoints() {
        double lat = Double.parseDouble("39.4819864");
        double lon = Double.parseDouble("-87.3209536");
        TempLocation loc = new TempLocation(lat, lon);
        System.out.println(loc.getLocString());
        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile("Terre Haute.osm");
        assert dm != null;
        OSMGraphConstructor gc = new OSMGraphConstructor(dm);
        //LocationKDTree<OSMNode> kt = new LocationKDTree<>(dm.getAllNodeInstance());
        //System.out.println(kt.nearByLocations(loc).size());
        Graph g = gc.getGraph();
        NearestRoadFinder nrdF = new NearestRoadFinder(dm.getNodeByIdSet(g.getNodeOnRoad()));
        GeoLocation roadLoc = nrdF.findNearsetRoad(loc);
        System.out.println(roadLoc);
        System.out.println(OSMMathUtil.distance(roadLoc, loc));
    }

    @Test
    public void testBuildingWithName() {
        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile("Terre Haute.osm");
        assert dm != null;
        Set<OSMAbstractType> targ = new HashSet<>();
        for (OSMNode n : dm.getAllNodeInstance()) {
            if (n.hasTag("building") || n.hasTag("landuse") || n.hasTag("amenity") || n.hasTag("place") || n.hasTag("shop")) {
                targ.add(n);
            }
        }
        for (OSMWay n : dm.getAllWayInstance()) {
            if (n.hasTag("building") || n.hasTag("landuse")) {
                targ.add(n);
            }
        }
        System.out.println(targ.size());
    }

    @Test
    public void testXMLIterpreter() {
        runTestWithFileAndName("test.osm", "small test");
    }

    @Test
    public void testXMLIterpreterTerreHaute() {
        runTestWithFileAndName("Terre Haute.osm", "Terre Haute");
    }

    @Test
    public void testEqualAndHashCode() {
        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile("Terre Haute.osm");
        assert dm != null;
        Set<Long> id = dm.getAllNodeId();
        for (Long i : id) {
            OSMNode tmp = new OSMNode(i, null, 0, 0);
            assertNotSame(dm.getNodeById(i), tmp);
            //System.out.println(tmp.hashCode());
            assertEquals(tmp, dm.getNodeById(i));
        }

    }

    @Test
    public void testGraphConstructSmall() {
        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile("test.osm");
        assert dm != null;
        OSMGraphConstructor gc = new OSMGraphConstructor(dm);
        Graph g = gc.getGraph();
        long vids[] = {153357350, 153421657, 153497538, 153532013};
        long notV[] = {153380829, 153516590};

        for (long i : vids) {
            //System.out.println(i);
            assertTrue(g.isVertex(i));
        }
        for (long i : notV) {
            assertFalse(g.isVertex(i));
        }

        Iterator<AbstractEdge> e_it = g.getEdgeIterator(new RoadVertex(dm.getNodeById(153357350)));
        RoadEdge my_e = (RoadEdge) e_it.next();
        assertEquals(Long.parseLong("153421657"), my_e.getToNodeID());
        RoadEdge tmp_e = g.getPartialEdge(my_e,
                new RoadVertex(dm.getNodeById(Long.parseLong("153380829"))),
                Graph.Direction.BACK);
        System.out.println(tmp_e.getPath());
        for (AbstractEdge e : g.getEdgesContainedByWay(17571013)) {
            assertEquals(50.0, ((RoadEdge) e).getSpeed(), DELTA_LOC_CONV);
        }
        for (AbstractEdge e : g.getEdgesContainedByWay(17570942)) {
            assertEquals(37.28, ((RoadEdge) e).getSpeed(), DELTA_LOC_CONV);
        }
    }

    @Test
    public void testSearchOnSmall() {
        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile("test.osm");
        assert dm != null;
        OSMGraphConstructor gc = new OSMGraphConstructor(dm);
        Graph g = gc.getGraph();
        long vids[] = {153357350, 153421657, 153497538, 153532013};
        long notV[] = {153380829, 153516590};
        AbstractVertex f = new RoadVertex(dm.getNodeById(Long.parseLong("153357350")));
        AbstractVertex t = new RoadVertex(dm.getNodeById(Long.parseLong("153532013")));
        AbstractSearchEngine engine = new DijkstraSearchEngine(g);
        engine.getVertexRoute(f, t);
    }

    @Test
    public void testSearchOnTerreHaute() {
        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile("Terre Haute.osm");
        assert dm != null;
        OSMGraphConstructor gc = new OSMGraphConstructor(dm);
        Graph g = gc.getGraph();
        AbstractVertex f = new RoadVertex(dm.getNodeById(Long.parseLong("181915798")));
        AbstractVertex t = new RoadVertex(dm.getNodeById(Long.parseLong("181903428")));

        long startTime = System.currentTimeMillis();
        AbstractSearchEngine engine = new DijkstraSearchEngine(g);
        List<RoadEdge> route = engine.getVertexRoute(f, t);
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("To Graph: %s: %d milliseconds", "indiana", endTime - startTime));

        startTime = System.currentTimeMillis();
        engine = new AStarSearchEngine(g);
        route = engine.getVertexRoute(f, t);
        endTime = System.currentTimeMillis();
        System.out.println(String.format("To Graph: %s: %d milliseconds", "indiana", endTime - startTime));


        System.out.println(route.get(0).getFromNode().getLocString());
        for (RoadEdge e : route) {
            System.out.println(String.format("%s\t\t%f\t\t%d", e.getToNode().getLocString(), e.getSpeed(), e.getOSMWay().getID()));
        }
    }

    @Test
    public void testSearchOnTerreHaute2() {
        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile("Terre Haute.osm");
        assert dm != null;
        OSMGraphConstructor gc = new OSMGraphConstructor(dm);
        Graph g = gc.getGraph();
        AbstractVertex f = new RoadVertex(dm.getNodeById(Long.parseLong("3642911770")));
        AbstractVertex t = new RoadVertex(dm.getNodeById(Long.parseLong("181895110")));

        long startTime = System.currentTimeMillis();
        AbstractSearchEngine engine = new DijkstraSearchEngine(g);
        List<RoadEdge> route = engine.getVertexRoute(f, t);
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("To Graph: %s: %d milliseconds", "indiana", endTime - startTime));

        startTime = System.currentTimeMillis();
        engine = new AStarSearchEngine(g);
        route = engine.getVertexRoute(f, t);
        endTime = System.currentTimeMillis();
        System.out.println(String.format("To Graph: %s: %d milliseconds", "indiana", endTime - startTime));

        System.out.println(route);


        System.out.println(route.get(0).getFromNode().getLocString());
        for (RoadEdge e : route) {
            System.out.println(String.format("%s\t\t%f\t\t%d", e.getToNode().getLocString(), e.getSpeed(), e.getOSMWay().getID()));
        }
    }

    @Test
    public void testGraphConstructTH1() {
        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile("Terre Haute.osm");
        assert dm != null;
        OSMGraphConstructor gc = new OSMGraphConstructor(dm);
        Graph g = gc.getGraph();
        AbstractVertex vt = g.getVertexById(181908029);
        assert vt != null;
        Iterator<AbstractEdge> it = g.getEdgeIterator(vt);
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        System.out.println();
    }

    @Test
    public void testGraphConstructTH2() {
        long startTime = System.currentTimeMillis();
        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile("Terre Haute.osm");
        assert dm != null;
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("Load: %s: %d milliseconds", "Terre Haute", endTime - startTime));

        startTime = System.currentTimeMillis();
        OSMGraphConstructor gc = new OSMGraphConstructor(dm);
        Graph g = gc.getGraph();
        endTime = System.currentTimeMillis();
        System.out.println(String.format("To Graph: %s: %d milliseconds", "Terre Haute", endTime - startTime));
        System.out.println();
    }

    @Test
    public void testCoreMap() {
        CoreMap map = new CoreMap("Terre Haute.osm");
        TempLocation loc1 = new TempLocation("39.468131", "-87.360245");
        TempLocation loc2 = new TempLocation("39.468894", "-87.398102");
        SearchResultWrapper fullRoute = map.findRoute(loc1, loc2, "AStar", "Time");
        for (GeoLocation i : fullRoute.getRoute()) {
            System.out.println(i.getLocString());
        }
    }
// 331104747

//    @Test
//    public void testSearchInIndiana(){
//        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile("resources/indiana-latest.osm");
//        assert dm != null;
//        OSMGraphConstructor gc = new OSMGraphConstructor(dm);
//        Graph g = gc.getGraph();
//        AbstractVertex f = new RoadVertex(dm.getNodeById(Long.parseLong("182009650")));
//        AbstractVertex t = new RoadVertex(dm.getNodeById(Long.parseLong("179321252")));
//
//        long startTime = System.currentTimeMillis();
//        AbstractSearchEngine engine = new DijkstraSearchEngine(g);
//        List<RoadEdge> route = engine.getVertexRoute(f,t);
//
//        long endTime = System.currentTimeMillis();
//        System.out.println(String.format("Find: %s: %d milliseconds", "Indiana", endTime - startTime));
//
//
//        startTime = System.currentTimeMillis();
//        engine = new AStarSearchEngine(g);
//        route = engine.getVertexRoute(f,t);
//        endTime = System.currentTimeMillis();
//        System.out.println(String.format("To Graph: %s: %d milliseconds", "indiana", endTime - startTime));
//
//
//        if(route != null) {
//            System.out.println(route.get(0).getFromNode().getLocString());
//            for (RoadEdge e : route) {
//                System.out.println(e.getToNode().getLocString());
//            }
//        }
//    }

//    @Test
//    public void testGraphConstructIN(){
//        long startTime = System.currentTimeMillis();
//        OSMAbstractDataModel dm = OSMXMLInterpreter.loadFromFile("resources/indiana-latest.osm");
//        assert dm != null;
//        long endTime = System.currentTimeMillis();
//        System.out.println(String.format("Load: %s: %d milliseconds", "indiana", endTime - startTime));
//
//        startTime = System.currentTimeMillis();
//        OSMGraphConstructor gc = new OSMGraphConstructor(dm);
//        Graph g = gc.getGraph();
//        endTime = System.currentTimeMillis();
//        System.out.println(String.format("To Graph: %s: %d milliseconds", "indiana", endTime - startTime));
//        System.out.println();
//    }

//	@Test
//	public void testXMLSeqIterpreter() {
//		long startTime = System.currentTimeMillis();
//		
//		OSMXMLInterpreter.loadFromFileSequential("resources/test.osm");
//		
//		long endTime = System.currentTimeMillis();
//		System.out.println("XML: Seq: small:       " + (endTime - startTime) + " milliseconds");
//	}
//	
//	@Test
//	public void testXMLSeqIterpreterTerreHaute() {
//		long startTime = System.currentTimeMillis();
//		
//		OSMXMLInterpreter.loadFromFileSequential("resources/Terre Haute.osm");
//		
//		long endTime = System.currentTimeMillis();
//		System.out.println("XML: Seq: Terre Haute: " + (endTime - startTime) + " milliseconds");
//	}
// It is not possible to load the map of Indiana using the XML Interpreter
// must use database mongoDB is probably a good choice
}