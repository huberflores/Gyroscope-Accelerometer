package mcm.client.rest;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class DataManager {
	static DataManager instance = null;

	public static DataManager getInstance() {
		if (instance == null)
			instance = new DataManager();
		return instance;
	}

	private DataManager() {
	}

	public ArrayList<NameValuePair> getKeys(String profile) {
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(Utilities.SECRET_KEY_PARAM,
				Utilities.secretKey));
		parameters.add(new BasicNameValuePair(Utilities.ID_PARAM,
				Utilities.idKey));
		return parameters;
	}
}
