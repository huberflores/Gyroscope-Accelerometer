package mcm.client.rest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.io.HttpResponseParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Proxy;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class HttpManager {
	private static HttpManager instance;
	protected String EMPTY = "";
	protected final static String URL_AUTHENTICATE = "http://172.17.60.66:8080/DragonFly/mcm";
	protected final static String HTML_URL = "http://172.17.60.66:8080/DragonFly/index.html";
	public final static String URL_UPLOAD_FILE = "http://ec2-50-16-5-222.compute-1.amazonaws.com:8080/Uploader/uimage";
	//protected final static String URL_UPLOAD_FILE = "http://172.17.60.66:8080/DragonFly/uimage";
	protected final static String URL_EUCALYPTUS = "http://172.17.60.66:8080/DragonFly/euca";
	protected final static String PARAM_KEY = "stkey";
	protected final static String PARAM_ID = "idkey";
	protected final static String PARAM_CLOUD = "cloudProvider";
	protected final static String PROXY = "193.40.5.245";
	protected final static int PORT = 3128;

	private HttpManager() {

	}

	public static HttpManager getInstance() {
		if (instance == null)
			instance = new HttpManager();
		return instance;
	}

	public void getHttp() {
		HttpURLConnection con = null;
		URL url;
		InputStream is = null;
		try {
			url = new URL(HTML_URL);
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(100000 /* milliseconds */);
			con.setConnectTimeout(150000 /* milliseconds */);
			con.setRequestMethod("GET");
			con.setDoInput(true);
			// con.addRequestProperty("Referer", "http://blog.dahanne.net");
			// Start the query
			con.connect();
			is = con.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			String value = reader.readLine();
			value = value + "";

			String myProxy = Proxy.getDefaultHost();
			int myPort = Proxy.getDefaultPort();
			HttpHost proxy = new HttpHost(myProxy, myPort, "http");

		} catch (IOException e) {
			// handle the exception !
			e.printStackTrace();
		}

	}

	public boolean postGetString(String URL, String[] keys, String[] values)
			throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(URL);

		HttpHost proxy = new HttpHost(PROXY, PORT, "http");
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);

		try {

			HttpEntity entity = null;
			entity = new UrlEncodedFormEntity(buildParams(keys, values));

			httppost.addHeader(entity.getContentType());
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);

			InputStream is = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			String value;
			while ((value = reader.readLine()) != null) {
				value = value + "";
			}
			return true;
		} catch (ClientProtocolException e) {
			String es = e.toString();
			return false;
		} catch (IOException e) {
			String es = e.toString();
			return false;
		}
	}
	
	/*protected HttpClient setProxy(HttpClient httpClient){
		HttpHost proxy = new HttpHost(PROXY, PORT, "http");
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
			
		return httpClient;
	}*/
	
	protected HttpClient setProxy(DefaultHttpClient httpClient){
		HttpHost proxy = new HttpHost(PROXY, PORT, "http");
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
			
		return httpClient;
	}
	
	
    //decomentar
	/*public boolean postGetString(String URL, ArrayList<NameValuePair> parameters)
			throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(URL);
		httpClient = setProxy(httpClient);
		
		try {

			HttpEntity entity = null;
			entity = new UrlEncodedFormEntity(parameters);

			httppost.addHeader(entity.getContentType());
			httppost.setEntity(entity);
			HttpResponse response = httpClient.execute(httppost);

			InputStream is = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			String value;
			while ((value = reader.readLine()) != null) {
				value = value + "";
			}
			return true;
		} catch (ClientProtocolException e) {
			String es = e.toString();
			return false;
		} catch (IOException e) {
			String es = e.toString();
			return false;
		}
	}*/

	protected ArrayList buildParams(String[] keys, String[] values) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		for (int i = 0; i < keys.length; i++)
			params.add(new BasicNameValuePair(keys[i], values[i]));
		return params;
	}

	public boolean UploadFile(File file, String URL) throws IOException {
		HttpClient client = new DefaultHttpClient();
		// String postURL = "http://172.17.60.66:8080/DragonFly/uimage";
		HttpPost post = new HttpPost(URL);

		//HttpHost proxy = new HttpHost(PROXY, PORT, "http");
		//client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		FileBody bin = new FileBody(file);
		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		reqEntity.addPart("file", bin);
		post.setEntity(reqEntity);
		HttpResponse response = client.execute(post);
		// HttpEntity resEntity = response.getEntity();
		
		int status = response.getStatusLine().getStatusCode();
		HttpEntity resEntity = response.getEntity();
		
		if (resEntity != null) {    
             Log.i("RESPONSE",EntityUtils.toString(resEntity));
        }
		return true;
	}

	protected String getSTring(String URL) {
		HttpURLConnection con = null;
		URL url;
		InputStream is = null;
		try {
			url = new URL(URL);
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(100000 /* milliseconds */);
			con.setConnectTimeout(150000 /* milliseconds */);
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.connect();
			is = con.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			String value = reader.readLine();
			value = value + "";
			return value;
		} catch (IOException e) {
			// handle the exception !
			e.printStackTrace();
		}
		return EMPTY;
	}

	protected InputStream getInputStream(String URL) {
		HttpURLConnection con = null;
		URL url;
		InputStream is = null;
		try {
			url = new URL(URL);
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(100000 /* milliseconds */);
			con.setConnectTimeout(150000 /* milliseconds */);
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.connect();
			return con.getInputStream();

		} catch (IOException e) {
			// handle the exception !
			e.printStackTrace();
		}
		return null;
	}
	
	public StringBuilder postRequest(String URL, ArrayList<NameValuePair> parameters)
	throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
	
	
		HttpPost httppost = new HttpPost(URL);
		httpClient = (DefaultHttpClient) setProxy(httpClient);
		
	    /*httpClient.getCredentialsProvider().setCredentials(
				    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				    new UsernamePasswordCredentials("huber", "alquimia2003"));*/ 
		

		try {

			HttpEntity entity = null;
			entity = new UrlEncodedFormEntity(parameters);
			

			httppost.addHeader(entity.getContentType());
			httppost.setEntity(entity);
			HttpResponse response = httpClient.execute(httppost);

			InputStream is = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder total = new StringBuilder();
			String value;
			while ((value = reader.readLine()) != null) {
				total.append(value);
			}
			return total;
		} catch (ClientProtocolException e) {
			String es = e.toString();
			return null;
		} catch (IOException e) {
			String es = e.toString();
			return null;
		}
}

	
	/*
	 public StringBuilder postRequest(String URL, ArrayList<NameValuePair> parameters)
	throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
	
	
		HttpPost httppost = new HttpPost(URL);
		httpClient = setProxy(httpClient);
		

		try {

			HttpEntity entity = null;
			entity = new UrlEncodedFormEntity(parameters);
			

			httppost.addHeader(entity.getContentType());
			httppost.setEntity(entity);
			HttpResponse response = httpClient.execute(httppost);

			InputStream is = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder total = new StringBuilder();
			String value;
			while ((value = reader.readLine()) != null) {
				total.append(value);
			}
			return total;
		} catch (ClientProtocolException e) {
			String es = e.toString();
			return null;
		} catch (IOException e) {
			String es = e.toString();
			return null;
		}
}
	 */
	
	
}
