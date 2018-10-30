package net.ecoarttech.ihplus.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodeService {

    @GET("maps/api/geocode/json")
    Call<GeocodeResponse> geocode(@Query("address") String address, @Query("key") String apiKey);

    @GET("maps/api/directions/json?mode=walking")
    Call<DirectionsResponse> directions(@Query("origin") String origin, @Query("destination") String destination, @Query("key") String apiKey);
}
