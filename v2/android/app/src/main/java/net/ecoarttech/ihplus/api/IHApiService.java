package net.ecoarttech.ihplus.api;

import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.api.NetworkConstants;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IHApiService {

    @GET(NetworkConstants.SEARCH_HIKES_PATH)
    Call<HikesResponse> searchHikes(@Query(NetworkConstants.REQUEST_JSON_HIKES_LAT) double lat, @Query(NetworkConstants.REQUEST_JSON_HIKES_LNG) double lng);

    @GET(NetworkConstants.GET_VISTA_URL)
    Call<VistaActionsResponse> getVistaActions(@Query(NetworkConstants.REQUEST_JSON_VISTAS_AMOUNT) int amount);

    @GET(NetworkConstants.GET_HIKE_URL)
    Call<HikeDetailsResponse> getHikeDetails(@Query(NetworkConstants.REQUEST_JSON_HIKE_ID) int id);

    @GET(NetworkConstants.GET_HIKES_URL)
    Call<HikesResponse> getHikesById(@Query(NetworkConstants.REQUEST_JSON_HIKE_ID) int id);

    //	public final static String UPLOAD_HIKE_URL = SERVER_URL + "scripts/createHike.php";
    //	public final static String PHOTO_URL = SERVER_URL + "uploads/";

}
