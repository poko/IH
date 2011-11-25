package net.ecoarttech.ihplus.network;

public class NetworkConstants {

	public final static String SERVER_URL = "http://192.168.1.5:8888/IHServer/";
	public final static int FAILURE = -1;
	public final static int SUCCESS = 1;
	public final static String SERVER_RESULT = "result";
	public final static String GET_VISTA_URL = SERVER_URL + "getVistaAction.php";
	public final static String UPLOAD_HIKE_URL = SERVER_URL + "createHike.php";
	public final static String SEARCH_HIKES_URL = SERVER_URL + "getHikes.php";
	// bundle keys
	public final static String HIKES_JSON_KEY = "hikes_json_key";

}
