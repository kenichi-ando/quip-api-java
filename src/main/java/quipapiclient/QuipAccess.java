package quipapiclient;

import java.io.IOException;
import java.time.Instant;
import java.util.stream.StreamSupport;

import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

class QuipAccess {

	// ============================================
	// Protected
	// ============================================
	
	protected static JsonObject _getToJsonObject(String uri) throws ClientProtocolException, IOException {
		return _toJsonObject(_requestGet(uri));
	}

	protected static JsonObject _getToJsonObject(String uri, String param) throws ClientProtocolException, IOException {
		return _toJsonObject(_requestGet(uri, param));
	}

	protected static JsonArray _getToJsonArray(String uri) throws ClientProtocolException, IOException {
		return _toJsonArray(_requestGet(uri));
	}

	protected static JsonArray _getToJsonArray(String uri, String param) throws ClientProtocolException, IOException {
		return _toJsonArray(_requestGet(uri, param));
	}

	protected static byte[] _getToByteArray(String uri) throws ClientProtocolException, IOException {
		HttpResponse res = _requestGet(uri);
		if (res.getStatusLine().getStatusCode() == 200) {
			byte[] buff = new byte[(int) res.getEntity().getContentLength()];
			res.getEntity().getContent().read(buff);
			return buff;
		} else {
			_hasError(_toJsonObject(res));
			return null;
		}
	}

	protected static JsonObject _postToJsonObject(String uri, Form form) throws ClientProtocolException, IOException {
		return _toJsonObject(_requestPost(uri, form));
	}

	protected static JsonObject _postToJsonObject(String uri, MultipartEntityBuilder multi) throws ClientProtocolException, IOException {
		return _toJsonObject(_requestPost(uri, multi));
	}

	protected static JsonArray _postToJsonArray(String uri, Form form) throws ClientProtocolException, IOException {
		return _toJsonArray(_requestPost(uri, form));
	}

	protected static HttpResponse _requestGet(String uri) throws ClientProtocolException, IOException {
		return _sendRequest(Request.Get(uri));
	}

	protected static HttpResponse _requestGet(String uri, String param) throws ClientProtocolException, IOException {
		return _sendRequest(Request.Get(uri + "?" + param));
	}

	protected static HttpResponse _requestPost(String uri, Form form) throws ClientProtocolException, IOException {
		return _sendRequest(Request.Post(uri)
				.body(new UrlEncodedFormEntity(form.build(), Consts.UTF_8)));
	}

	protected static HttpResponse _requestPost(String uri, MultipartEntityBuilder multi) throws ClientProtocolException, IOException {
		return _sendRequest(Request.Post(uri).body(multi.build()));
	}

	protected static HttpResponse _sendRequest(Request req) throws ClientProtocolException, IOException {
		if (QuipClient._isDebugEnabled())
			System.out.println(System.lineSeparator() + "Request> " + req.toString());
		HttpResponse response = req.addHeader(HttpHeaders.AUTHORIZATION, QuipClient._getBearerToken()).execute().returnResponse();
		if (QuipClient._isDebugEnabled())
			System.out.println("Response> " + response.getStatusLine().toString() + " " + response.getEntity().toString());
		return response;
	}

	protected static JsonObject _toJsonObject(HttpResponse response) throws JsonSyntaxException, ParseException, IOException {
		JsonObject json = new Gson().fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
		if (QuipClient._isDebugEnabled())
			System.out.println("Json> " + json.toString());
		if (_hasError(json))
			return null;
		return json;
	}

	protected static JsonArray _toJsonArray(HttpResponse response) throws JsonSyntaxException, ParseException, IOException {
		JsonArray json = new Gson().fromJson(EntityUtils.toString(response.getEntity()), JsonArray.class);
		if (QuipClient._isDebugEnabled())
			System.out.println("Json> " + json.toString());
		return json;
	}

	protected static Instant _toInstant(JsonObject json, String key) {
		long usec = json.get(key).getAsLong();
		return Instant.ofEpochSecond(usec / 1000000, (usec % 1000000) * 1000);
	}

	protected static String[] _toStringArray(JsonObject json, String key) {
		JsonArray arr = json.get(key).getAsJsonArray();
		return StreamSupport.stream(arr.spliterator(), false)
				.map(e -> e.getAsString())
				.toArray(String[]::new);
	}

	protected static boolean _hasError(JsonObject json) {
		if (json.get("error") == null)
			return false;
		System.out.println("Error> "
				+ json.get("error_code").getAsString() + " "
				+ json.get("error").getAsString() + " ("
				+ json.get("error_description").getAsString() + ")");
		return true;
	}
}