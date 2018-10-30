package net.ecoarttech.ihplus.api;

import android.location.Location;

import java.util.LinkedHashMap;
import java.util.List;

public class GeocodeResponse {

    private static String KEY_GEOMETRY = "geometry";
    private static String KEY_LOCATION = "location";
    private static String KEY_LAT = "lat";
    private static String KEY_LNG = "lng";

    private String status;
    private String error_message;
    private List<LinkedHashMap> results;


    public String getStatus() {
        return status;
    }

    public String getError_message() {
        return error_message;
    }

    public List<LinkedHashMap> getResults() {
        return results;
    }

    public Location getLocation(){
        if (results != null && results.get(0) != null){
            LinkedHashMap geometry = (LinkedHashMap) results.get(0).get(KEY_GEOMETRY);
            if (geometry != null){
                LinkedHashMap locRes = (LinkedHashMap) geometry.get(KEY_LOCATION);
                if (locRes != null){
                    Location location =  new Location("code");
                    location.setLatitude((Double) locRes.get(KEY_LAT));
                    location.setLongitude((Double) locRes.get(KEY_LNG));
                    return location;
                }
            }
        }
        return null;
    }
}
