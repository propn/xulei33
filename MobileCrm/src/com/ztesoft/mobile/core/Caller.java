package com.ztesoft.mobile.core;

import java.io.InputStream;
import java.lang.reflect.Type;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.google.gson.reflect.TypeToken;

/**
 * @author Thunder.xu
 * 
 */
public class Caller {

	private static String serverUrl = App.serverUrl;
	private static DefaultHttpClient httpclient = null;
	private static String userName = App.userName;
	private static String passWord = App.passWord;

	/**
	 * Singleton HttpClient
	 * 
	 * @return
	 */
	private static DefaultHttpClient getHttpClient() {

		if (null == httpclient) {
			int timeoutConnection = 30000;// millisecond
			int timeoutSocket = 60000;

			HttpParams httpParameters = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			HttpClientParams.setRedirecting(httpParameters, true);
			HttpClientParams.setCookiePolicy(httpParameters,
					CookiePolicy.BROWSER_COMPATIBILITY);

			HttpProtocolParams.setUserAgent(httpParameters,
					"apache.http.client");

			httpclient = new DefaultHttpClient(httpParameters);
		}
		return httpclient;
	}

	/**
	 * 
	 * @param <T>
	 * @param url
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T> T getJson(String url, Class<T> clazz) throws Exception {

		url = serverUrl + url;
		HttpGet httpget = new HttpGet(url);
		httpget.addHeader("userName", userName);
		httpget.addHeader("passWord", passWord);
		httpget.setHeader("Accept", "application/json");

		HttpResponse response;
		response = getHttpClient().execute(httpget);

		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception("HttpStatusCode:" + statusCode);
		}

		T obj = null;

		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			String result = StringUtil.convertStreamToString(instream);
			instream.close();

			obj = JsonUtil.fromJson(result, clazz);
		}
		httpget.abort();
		return obj;
	}

	/**
	 * 
	 * @param <T>
	 * @param url
	 * @param token
	 *            Type targetType = new TypeToken<List<Contact>>(){}.getType();
	 * @return
	 * @throws Exception
	 */
	public static <T> T getJson(String url, TypeToken<T> token)
			throws Exception {

		url = serverUrl + url;
		HttpGet httpget = new HttpGet(url);
		httpget.addHeader("userName", userName);
		httpget.addHeader("passWord", passWord);
		httpget.setHeader("Accept", "application/json");

		HttpResponse response;
		response = getHttpClient().execute(httpget);

		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception("HttpStatusCode:" + statusCode);
		}

		T obj = null;

		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			String result = StringUtil.convertStreamToString(instream);
			instream.close();

			obj = JsonUtil.fromJson(result, token);
		}
		httpget.abort();
		return obj;
	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String getText(String url) throws Exception {
		url = serverUrl + url;
		String result = "";
		HttpGet httpget = new HttpGet(url);

		httpget.addHeader("userName", "userName");
		httpget.addHeader("passWord", "passWord");
		httpget.setHeader("Accept", "text/plain");

		HttpResponse response;
		response = getHttpClient().execute(httpget);

		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception("HttpStatusCode:" + statusCode);
		}

		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			result = StringUtil.convertStreamToString(instream);
			instream.close();
			httpget.abort();
		}
		return result;
	}

	/**
	 * 
	 * @param url
	 * @param obj
	 * @param targetType
	 *            Type targetType = new TypeToken<List<Contact>>() {
	 *            }.getType();
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse putJson(String url, Object obj, Type targetType)
			throws Exception {

		HttpPut httpPut = new HttpPut(serverUrl + url);

		httpPut.addHeader("userName", userName);
		httpPut.addHeader("passWord", passWord);
		httpPut.addHeader("content-type", "application/json");
		httpPut.setHeader("Accept", "application/json");

		HttpEntity entity = new StringEntity(JsonUtil.toJson(obj, targetType));
		httpPut.setEntity(entity);

		HttpResponse response;
		response = httpclient.execute(httpPut);

		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception("HttpStatusCode:" + statusCode);
		}
		return response;
	}

	/**
	 * HttpPut
	 * 
	 * @param url
	 * @param data
	 * @return
	 * @throws Exception
	 * @throws ClientProtocolException
	 */
	public static HttpResponse putJson(String url, Object obj) throws Exception {

		HttpPut httpPut = new HttpPut(serverUrl + url);

		httpPut.addHeader("userName", userName);
		httpPut.addHeader("passWord", passWord);
		httpPut.addHeader("content-type", "application/json");
		httpPut.setHeader("Accept", "application/json");

		HttpEntity entity = new StringEntity(JsonUtil.toJson(obj));
		httpPut.setEntity(entity);

		HttpResponse response;
		response = httpclient.execute(httpPut);

		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception("HttpStatusCode:" + statusCode);
		}
		return response;
	}

	/**
	 * 
	 * @param url
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public HttpResponse postJosn(String url, Object obj) throws Exception {

		HttpPost httpPost = new HttpPost(serverUrl + url);

		httpPost.addHeader("userName", userName);
		httpPost.addHeader("passWord", passWord);
		httpPost.addHeader("content-type", "application/json");
		httpPost.setHeader("Accept", "application/json");

		HttpEntity entity = new StringEntity(JsonUtil.toJson(obj));
		httpPost.setEntity(entity);

		HttpResponse response = getHttpClient().execute(httpPost);

		return response;
	}

	/**
	 * 
	 * @param url
	 * @param obj
	 * @param targetType
	 *            Type targetType = new TypeToken<List<Contact>>() {
	 *            }.getType();
	 * @return
	 * @throws Exception
	 */
	public HttpResponse postJosn(String url, Object obj, Type targetType)
			throws Exception {

		HttpPost httpPost = new HttpPost(serverUrl + url);

		httpPost.addHeader("userName", userName);
		httpPost.addHeader("passWord", passWord);
		httpPost.addHeader("content-type", "application/json");
		httpPost.setHeader("Accept", "application/json");

		HttpEntity entity = new StringEntity(JsonUtil.toJson(obj, targetType));
		httpPost.setEntity(entity);

		HttpResponse response = getHttpClient().execute(httpPost);

		return response;
	}

}
