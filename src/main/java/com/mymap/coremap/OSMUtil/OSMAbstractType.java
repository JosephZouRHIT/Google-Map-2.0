package com.mymap.coremap.OSMUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * author: Lining Pan
 */
public abstract class OSMAbstractType {
	private long id;
	private Map<String,String> tag;
	
	OSMAbstractType(long _id, Map<String, String> map) {
		id = _id;
		if(map != null) tag = new HashMap<>(map);
	}
	
	String toStringHelper(String s) {
		return String.format("[id: %d, %s, tags: %s]\n", id, s, tag.toString());
	}
	
	public String toString() {
		return Double.toString(this.id);
	}
	
	public long getID() {
		return id;
	}
	
	public boolean hasTag(String key) {
		return tag.containsKey(key);
	}
	
	String getTagValue(String key) {
		if(!hasTag(key))
			return null;
		return tag.get(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OSMAbstractType other = (OSMAbstractType) obj;
		return id == other.id;
	}
}
