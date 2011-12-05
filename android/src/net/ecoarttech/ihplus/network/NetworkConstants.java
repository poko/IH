package net.ecoarttech.ihplus.network;

public class NetworkConstants {
	//general 
	public final static String SERVER_URL = "http://10.22.97.38:8888/IHServer/";
	public final static int FAILURE = -1;
	public final static int SUCCESS = 1;
	public final static String SERVER_RESULT = "result";
	
	// urls
	public final static String GET_VISTA_URL = SERVER_URL + "getVistaAction.php";
	public final static String UPLOAD_HIKE_URL = SERVER_URL + "createHike.php";
	public final static String SEARCH_HIKES_URL = SERVER_URL + "getHikes.php";
	public final static String GET_HIKE_URL = SERVER_URL + "getHike.php";
	
	//request keys
	public final static String REQUEST_JSON_ = "";
	public final static String REQUEST_JSON_VISTAS_AMOUNT = "amount";
	public final static String REQUEST_JSON_HIKE_NAME = "hike_name";
	public final static String REQUEST_JSON_HIKE_USER = "username";
	public final static String REQUEST_JSON_HIKE_DESC = "description";
	public final static String REQUEST_JSON_HIKE_ORIG = "original";
	public final static String REQUEST_JSON_HIKE_VISTAS = "vistas";
	public final static String REQUEST_JSON_HIKE_LAT = "start_lat";
	public final static String REQUEST_JSON_HIKE_LNG = "start_lng";
	public final static String REQUEST_JSON_HIKE_POINTS = "points";
	public final static String REQUEST_JSON_HIKE_PHOTOS = "photos";
	public final static String REQUEST_JSON_HIKES_LAT = "latitude";
	public final static String REQUEST_JSON_HIKES_LNG = "longitude";
	public final static String REQUEST_JSON_HIKE_ID = "hike_id";
	
	//response keys
	public final static String RESPONSE_JSON_ = "";
	public final static String RESPONSE_JSON_VISTAS_ACTIONS = "vista_actions";
	public final static String RESPONSE_JSON_VISTAS_ID = "vista_id";
	public final static String RESPONSE_JSON_VISTAS_VERBIAGE = "verbiage";
	public final static String RESPONSE_JSON_VISTAS_TYPE = "action_type";
	public final static String RESPONSE_JSON_HIKES = "hikes";
	public final static String RESPONSE_JSON_HIKE = "hike";
	
	// bundle keys
	public final static String HIKES_JSON_KEY = "hikes_json_key";
	public final static String HIKE_JSON_KEY = "hike_json_key";

}
