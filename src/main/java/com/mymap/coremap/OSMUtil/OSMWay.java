package com.mymap.coremap.OSMUtil;

import java.util.List;
import java.util.Map;

/**
 * author: Lining Pan
 */
public class OSMWay extends OSMAbstractType {

    private List<Long> nodeIdList;

    OSMWay(long _id, Map<String, String> map, List<Long> nList) {
        super(_id, map);
        nodeIdList = nList;
    }

    public final List<Long> getNodeIdList() {
        return nodeIdList;
    }

    public boolean isRoad() {
        if(this.hasTag("highway")){
            return !this.getTagValue("highway").equals("footway");
        }
        return false;
    }

    public double getSpeedLimit() {
        double speed = 30;
        if (this.hasTag("maxspeed")) {
            String str = this.getTagValue("maxspeed");
            String[] sl = str.split("\\s+");
            if(sl.length <= 1){
                try{
                    speed = Unit.convert(Integer.parseInt(sl[0]), Unit.KILO, Unit.MILE);
                } catch (NumberFormatException ignored){}
            } else if(sl[1].equals("mph")){
                try{
                    speed = Integer.parseInt(sl[0]);
                } catch (NumberFormatException ignored){}
            } else {//might have several speed, try to get the first one
                try{
                    speed = Integer.parseInt(sl[0]);
                } catch (NumberFormatException ignored){}
            }
        } else if(this.hasTag("highway")){
            String str = this.getTagValue("highway");
            switch (str){
                case "residential":
                    speed = 25;
                    break;
                case "unclassified":
                    speed = 35;
                    break;
                case "tertiary":
                    speed = 30;
                    break;
                case "secondary":
                    speed = 35;
                    break;
                case "primary":
                    speed = 55;
                    break;
                case "motorway":
                    speed = 70;
                    break;
            }
        }
        return (double) speed;
    }

    public boolean isOneway() {
        if (this.hasTag("oneway")) {
            return this.getTagValue("oneway").equals("yes");
        }
        return false;
    }

    public String getName(){
        if(this.hasTag("name")){
            return this.getTagValue("name");
        }
        return "Unknown";
    }
    @Override
    public String toString() {
        return super.toStringHelper(String.format("node list: %s", this.nodeIdList.toString()));
    }
}
