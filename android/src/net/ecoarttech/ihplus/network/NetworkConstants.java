package net.ecoarttech.ihplus.network;

public class NetworkConstants {

	public final static String SERVER_URL = "http://10.22.97.38:8888/IHServer/";
	public final static int FAILURE = -1;
	public final static int SUCCESS = 1;
	public final static String SERVER_RESULT = "result";
	public final static String GET_VISTA_URL = SERVER_URL + "getVistaAction.php";
	public final static String UPLOAD_HIKE_URL = SERVER_URL + "createHike.php";
	public final static String SEARCH_HIKES_URL = SERVER_URL + "getHikes.php";
	public final static String GET_HIKE_URL = SERVER_URL + "getHike.php";
	// bundle keys
	public final static String HIKES_JSON_KEY = "hikes_json_key";
	public final static String HIKE_JSON_KEY = "hike_json_key";

}
