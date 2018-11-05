package com.mymap.coremap.SearchEngine;

import com.mymap.coremap.OSMGraph.AbstractVertex;
import com.mymap.coremap.OSMGraph.CostType;
import com.mymap.coremap.OSMGraph.RoadEdge;

import java.util.List;

/**
 * author: Lining Pan
 */
public interface AbstractSearchEngine {
    List<RoadEdge> getVertexRoute(AbstractVertex from, AbstractVertex to);

    List<RoadEdge> getVertexRoute(AbstractVertex from, AbstractVertex to, CostType costType);
}
