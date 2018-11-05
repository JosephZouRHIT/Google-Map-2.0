package com.mymap.coremap.OSMUtil;

import java.util.Collection;
import java.util.Set;

/**
 * author: Lining Pan
 */
public interface OSMAbstractDataModel {
	OSMNode getNodeById(long id);
	Set<OSMNode> getNodeByIdSet(Collection<Long> id_set);
	Set<Long> getAllWayContainsNode(long id);
	Set<Long> getAllWayContainsNode(OSMNode n);
	Set<Long> getAllNodeId();
	Set<OSMNode> getAllNodeInstance();
	
	OSMWay getWayByID(long id);
	Set<OSMWay> getWayByIdSet(Collection<Long> id_set);
	Set<Long> getAllWayId();
	Set<OSMWay> getAllWayInstance();

	double[] getXMLbound();
	
	boolean isVertexNode(long id);
}
