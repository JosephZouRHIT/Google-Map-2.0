package com.mymap.coremap.SearchEngine;

import com.mymap.coremap.OSMGraph.Graph;

public class SearchEngineFactory {
    public static AbstractSearchEngine getSearchEngine(String type, Graph g) {
        if (type.equals("Dijkstra")) {
            return new DijkstraSearchEngine(g);
        }
        if (type.equals("AStar")) {
            return new AStarSearchEngine(g);
        }
        if (type.equals("AStarZou")) {
            return new ZouAStarSearchEngine(g);
        }
        if (type.equals("DijkstraAlec")) {
            return new AlecDijkstraSearchEngine(g);
        }
        return new AStarSearchEngine(g);
    }

    public static boolean isSupportedSearchEngine(String se) {
        return se.equals("Dijkstra") || se.equals("AStar") || se.equals("AStarZou") || se.equals("DijkstraAlec");
    }
}
