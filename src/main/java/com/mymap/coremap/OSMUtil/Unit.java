package com.mymap.coremap.OSMUtil;

/**
 * author: Lining Pan
 */
public enum Unit {

    KILO(1.609344), NM(0.8684), MILE(1.0);

    private double convertFactor;

    Unit(double v) {
        convertFactor = v;
    }

    public static double convert(double v, com.mymap.coremap.OSMUtil.Unit from, com.mymap.coremap.OSMUtil.Unit to) {
        return v / from.convertFactor * to.convertFactor;
    }
}
