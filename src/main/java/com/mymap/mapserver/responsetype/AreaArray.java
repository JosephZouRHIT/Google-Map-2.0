package com.mymap.mapserver.responsetype;

public class AreaArray {
    private final double[] bound;

    public AreaArray(double[] bound) {
        this.bound = bound;
    }

    public double[] getBound() {
        return bound;
    }
}
