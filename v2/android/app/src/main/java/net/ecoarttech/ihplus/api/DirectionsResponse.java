package net.ecoarttech.ihplus.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.ecoarttech.ihplus.model.MapPoint;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectionsResponse {

    private static String KEY_LEGS = "legs";
    private static String KEY_STEPS = "steps";
    private static String KEY_POLYLINE = "polyline";
    private static String KEY_POINTS = "points";

    private String status;
    private String error_message;
    private List<LinkedHashMap> routes;

    public String getStatus() {
        return status;
    }

    public String getError_message() {
        return error_message;
    }

    public List<LinkedHashMap> getRoutes() {
        return routes;
    }

    public List<MapPoint> getPathPoints(){
        List<MapPoint> list = new ArrayList<>();
        //legs, steps, polyline, points
        List<LinkedHashMap> legs = (List<LinkedHashMap>) routes.get(0).get(KEY_LEGS);
        List<LinkedHashMap> steps = (List<LinkedHashMap>) legs.get(0).get(KEY_STEPS);
        for (LinkedHashMap step : steps) {
            LinkedHashMap polyline = (LinkedHashMap) step.get(KEY_POLYLINE);
            String points = (String) polyline.get(KEY_POINTS);
            list.addAll(decodePoly(points));
        }
        return list;
    }

    private List<MapPoint> decodePoly(String encoded) {
        List<MapPoint> poly = new ArrayList<MapPoint>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            MapPoint p = new MapPoint(-1, -1, -1, lat*10, lng*10);
            poly.add(p);
        }

        return poly;
    }

}
