package com.mymap.coremap.GeoLookUpMap;

import com.mymap.coremap.OSMUtil.Axis;
import com.mymap.coremap.OSMUtil.GeoLocation;
import com.mymap.coremap.OSMUtil.OSMMathUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

//region
public class LocationKDTree<T extends GeoLocation> {
    private static int MIN_NUM_NODE = 100;
    private static double LOOKUP_REGION_OFFSET = 0.01;
    private AbstractKDNode root;

    LocationKDTree(Set<T> g_set) {
        if (g_set == null)
            throw new NullPointerException();
        double[] bound = getBound(g_set);
        if (g_set.size() < MIN_NUM_NODE) {
            root = new KDLeafNode(g_set, bound);
        } else {
            root = new KDInternalNode(g_set, bound);
        }
    }

    public static void setLookupRegionOffset(double o) {
        LOOKUP_REGION_OFFSET = o;
    }


    Set<T> nearByLocations(GeoLocation loc) {
        double[] qBound = new double[4];
        qBound[0] = loc.getLatitude() + LOOKUP_REGION_OFFSET;
        qBound[1] = loc.getLatitude() - LOOKUP_REGION_OFFSET;
        qBound[2] = loc.getLongitude() + LOOKUP_REGION_OFFSET;
        qBound[3] = loc.getLongitude() - LOOKUP_REGION_OFFSET;

        Set<T> target = new HashSet<>();

        if (root.getClass() == KDLeafNode.class) {
            target.addAll(((KDLeafNode) root).getGeoLocSet());
        } else {
            ((KDInternalNode) root).findAndAddTo(qBound, target);
        }

        return target;
    }

    public double[] getRootBound() {
        double[] bound = new double[4];
        bound[0] = root.getLatitude_N();
        bound[1] = root.getLatitude_S();
        bound[2] = root.getLongitude_E();
        bound[3] = root.getLongitude_W();
        return bound;
    }

    private double[] getBound(Set<T> s) {
        //only true for a small area, or do not across 0 degree lon.
        double min_lat = Double.POSITIVE_INFINITY;
        double max_lat = Double.NEGATIVE_INFINITY;
        double min_lon = Double.POSITIVE_INFINITY;
        double max_lon = Double.NEGATIVE_INFINITY;

        for (GeoLocation g : s) {
            if (g.getLatitude() < min_lat)
                min_lat = g.getLatitude();
            if (g.getLatitude() > max_lat)
                max_lat = g.getLatitude();
            if (g.getLongitude() < min_lon)
                min_lon = g.getLongitude();
            if (g.getLongitude() > max_lon)
                max_lon = g.getLongitude();
        }
        double[] bound = new double[4];
        bound[0] = max_lat;
        bound[1] = min_lat;
        bound[2] = max_lon;
        bound[3] = min_lon;
        return bound;
    }

    private void splitSetWith(Set<T> source,
                              Set<T> targetUpper,
                              Set<T> targetLower,
                              double div,
                              Axis axis) {
        for (T v : source) {
            if (v.getValueAxis(axis) < div)
                targetLower.add(v);
            else
                targetUpper.add(v);
        }
    }

    private double averageAlongAxis(Collection<T> c, Axis axis) {
        if (c == null || c.size() == 0)
            return Double.NaN;
        double sum = 0.0;
        switch (axis) {
            case LAT:
                for (GeoLocation g : c) {
                    sum += g.getLatitude();
                }
                break;
            case LON:
                for (GeoLocation g : c) {
                    sum += g.getLongitude();
                }
                break;
        }
        return sum / c.size();
    }

    private abstract class AbstractKDNode {
        private double latitude_N;
        private double latitude_S;
        private double longitude_E;
        private double longitude_W;

        AbstractKDNode(double latN, double latS, double lonE, double lonW) {
            this.latitude_N = latN;
            this.latitude_S = latS;
            this.longitude_E = lonE;
            this.longitude_W = lonW;
        }

        AbstractKDNode(double[] bound) {
            if (bound.length != 4)
                throw new IllegalArgumentException();
            this.latitude_N = bound[0];
            this.latitude_S = bound[1];
            this.longitude_E = bound[2];
            this.longitude_W = bound[3];
        }

        double getLatitude_N() {
            return latitude_N;
        }

        double getLatitude_S() {
            return latitude_S;
        }

        double getLongitude_E() {
            return longitude_E;
        }

        double getLongitude_W() {
            return longitude_W;
        }
    }

    private class KDInternalNode extends AbstractKDNode {
        private double div;
        private Axis axis;

        private AbstractKDNode leftChild;
        private AbstractKDNode rightChild;

        KDInternalNode(Set<T> s, double[] bound) {
            super(bound);
            constructTree(s);
        }

        void findAndAddTo(double[] qBound, Set<T> target) {
            double[] upper = this.getUpperBound();
            double[] lower = this.getLowerBound();
            double[] upIn = OSMMathUtil.intersection(upper, qBound);
            double[] lowIn = OSMMathUtil.intersection(lower, qBound);
//            for (int i = 0; i < 4; i++) {
//                System.out.println(qBound[i]);
//            }
//            for (int i = 0; i < 4; i++) {
//                System.out.println(lower[i]);
//            }
            if (!Double.isNaN(upIn[0])) {
                if (rightChild.getClass() == KDInternalNode.class) {
                    ((KDInternalNode) rightChild).findAndAddTo(upIn, target);
                } else {
                    target.addAll(((KDLeafNode) rightChild).getGeoLocSet());
                }
            }
            if (!Double.isNaN(lowIn[0])) {
                if (leftChild.getClass() == KDInternalNode.class) {
                    ((KDInternalNode) leftChild).findAndAddTo(lowIn, target);
                } else {
                    target.addAll(((KDLeafNode) leftChild).getGeoLocSet());
                }
            }
        }

        void constructTree(Set<T> s) {
            Set<T> low = new HashSet<>();
            Set<T> up = new HashSet<>();
            if (Math.abs(getLatitude_N() - getLatitude_S())
                    > Math.abs(getLongitude_E() - getLongitude_W())) {//lat span > lon span
                axis = Axis.LAT;
            } else {
                axis = Axis.LON;
            }
            div = averageAlongAxis(s, axis);
            splitSetWith(s, up, low, div, axis);
//            System.out.println(String.format(
//                    "Construct: %d points, LAT: from %f to %f ,LON: from %f to %f, divide: %s by %f",
//                    s.size(),
//                    getLatitude_N(),getLatitude_S(),
//                    getLongitude_E(),getLongitude_W(),
//                    axis.toString(),div));
            if (up.size() < MIN_NUM_NODE * 2) {
                rightChild = new KDLeafNode(up, getBound(up));
            } else {
                rightChild = new KDInternalNode(up, getBound(up));
            }

            if (low.size() < MIN_NUM_NODE * 2) {
                leftChild = new KDLeafNode(low, getBound(low));
            } else {
                leftChild = new KDInternalNode(low, getBound(low));
            }
        }

        private double[] getUpperBound() {
            double[] upBound = new double[4];
            switch (this.axis) {
                case LON:
                    upBound[0] = getLatitude_N();
                    upBound[1] = getLatitude_S();
                    upBound[2] = getLongitude_E();
                    upBound[3] = getDiv();
                    break;
                case LAT:
                    upBound[0] = getLatitude_N();
                    upBound[1] = getDiv();
                    upBound[2] = getLongitude_E();
                    upBound[3] = getLongitude_W();
                    break;
            }
            return upBound;
        }

        private double[] getLowerBound() {
            double[] lower = new double[4];
            switch (this.axis) {
                case LON:
                    lower[0] = getLatitude_N();
                    lower[1] = getLatitude_S();
                    lower[2] = getDiv();
                    lower[3] = getLongitude_W();
                    break;
                case LAT:
                    lower[0] = getDiv();
                    lower[1] = getLatitude_S();
                    lower[2] = getLongitude_E();
                    lower[3] = getLongitude_W();
                    break;
            }
            return lower;
        }

        public double getDiv() {
            return div;
        }

        public Axis getAxis() {
            return axis;
        }

        public AbstractKDNode getLeftChild() {
            return leftChild;
        }

        public AbstractKDNode getRightChild() {
            return rightChild;
        }

    }

    class KDLeafNode extends AbstractKDNode {

        private Set<T> geoLocSet;

        KDLeafNode(Set<T> g_set, double[] bound) {
            super(bound);
            geoLocSet = g_set;

        }

        final Set<T> getGeoLocSet() {
            return geoLocSet;
        }

    }
}
