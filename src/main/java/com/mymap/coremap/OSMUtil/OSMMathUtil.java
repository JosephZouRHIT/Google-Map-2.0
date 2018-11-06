package com.mymap.coremap.OSMUtil;


import com.mymap.coremap.OSMGraph.CostType;
import com.mymap.coremap.ServerBackend.TempLocation;

/**
 * author: Lining Pan
 */
public class OSMMathUtil {
    // Retrieved from https://dzone.com/articles/distance-calculation-using-3
    // calculate distance by two geo-location(longitude & latitude)
    public static double distance(GeoLocation a, GeoLocation b) {
        return distance(a, b, Unit.MILE);
    }

    public static double calculateEstimateCost(GeoLocation a, GeoLocation b, CostType costType) {
        double estmCost = 1e100;
        switch (costType) {
            case TIME:
                estmCost = distance(a, b) / 70;
                break;
            case DISTANCE:
                estmCost = distance(a, b);
                break;
        }
        return estmCost;
    }

    public static double[] intersection(double[] a, double[] b) {
        if (a.length != 4 || b.length != 4)
            throw new IllegalArgumentException();
        if (a[0] < a[1] || a[2] < a[3])
            throw new IllegalArgumentException();
        if (b[0] < b[1] || b[2] < b[3])
            throw new IllegalArgumentException();
        double[] ans = new double[4];

        ans[0] = Math.min(a[0], b[0]);
        ans[1] = Math.max(a[1], b[1]);
        ans[2] = Math.min(a[2], b[2]);
        ans[3] = Math.max(a[3], b[3]);

        if (ans[0] <= ans[1] || ans[2] <= ans[3])
            ans[0] = ans[1] = ans[2] = ans[3] = Double.NaN;

        return ans;
    }

    public static double distance(GeoLocation a, GeoLocation b, Unit u) {
        double theta = a.getLongitude() - b.getLongitude();
        double dist = Math.sin(deg2rad(a.getLatitude())) * Math.sin(deg2rad(b.getLatitude()))
                + Math.cos(deg2rad(a.getLatitude())) * Math.cos(deg2rad(b.getLatitude())) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = Unit.convert(dist, Unit.MILE, u);

        return dist;
    }

    public static boolean inBoundary(GeoLocation loc, double[] bound) {
        if (Double.compare(loc.getLatitude(), bound[0]) > 0)
            return false;
        if (Double.compare(loc.getLatitude(), bound[1]) < 0)
            return false;
        if (Double.compare(loc.getLongitude(), bound[2]) > 0)
            return false;
        if (Double.compare(loc.getLongitude(), bound[3]) < 0)
            return false;
        return true;
    }

    public static int getZoomLevel(double[] bound) {
        if (bound.length != 4) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < 4; i++) {
            if (!Double.isFinite(bound[i]))
                throw new IllegalArgumentException();
        }
        TempLocation locSE = new TempLocation(bound[1], bound[2]);
        TempLocation locNE = new TempLocation(bound[0], bound[2]);
        TempLocation locSW = new TempLocation(bound[1], bound[3]);
        double h_span = distance(locSE, locSW);
        double v_span = distance(locNE, locSE);
        double span = Math.max(h_span, v_span);
        return (int) Math.ceil(Math.log(24901 / span) / Math.log(2));
    }

    public static int getRadiusAtZoomLevel(int z) {//in meters
        double radius = 24901 / Math.pow(2, z) / 2;
        radius = Unit.convert(radius, Unit.MILE, Unit.KILO) * 1000;
        return Math.min((int) Math.ceil(radius), 50000);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
