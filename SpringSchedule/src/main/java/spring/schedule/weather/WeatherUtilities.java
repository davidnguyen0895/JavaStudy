package spring.schedule.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WeatherUtilities {

	private static final String lat = "35.6895";
	private static final String lon = "139.6917";
	private static final String API_KEY = "254ca69e129ef9485cb3df5f70b55caa";
	private static final String API_BASE = "https://api.openweathermap.org/data/2.5/onecall";
	private static final String UNITS = "metric";

	/**
	 * APIリクエストリンクを取得
	 * 
	 * @return APIリクエストリンク
	 */ 
	public static String getApiRequest() {
		StringBuilder sb = new StringBuilder(API_BASE);
		sb.append(String.format("?lat=%s&lon=%s&exclude=current,minutely,hourly,alerts&appid=%s&units=%s&lang=ja", lat,
				lon, API_KEY, UNITS));
		return sb.toString();
	}

	/**
	 * UNIX日付を普通の日付に変換
	 * 
	 * @param unixTimeStamp
	 * @return 普通の日付
	 */
	public static String convertUnixTimeToDate(long unixTimeStamp) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		date.setTime((long) unixTimeStamp * 1000);
		return dateFormat.format(date);
	}

	/**
	 * 天気アイコンリンクを取得
	 * 
	 * @param icon
	 * @return
	 */
	public static String getIconImage(String icon) {
		return String.format("http://openweathermap.org/img/wn/%s@2x.png", icon);
	}

	/**
	 * 
	 * @param urlString
	 * @return
	 */
	public static String getHTTPData(String urlString) {
		String stream = "";
		try {
			URL url = new URL(urlString);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//			Gets the status code from an HTTP response message.For example, in the case of the following status lines: 
//				 HTTP/1.0 200 OK
//				 HTTP/1.0 401 Unauthorized
//				 
//				It will return 200 and 401 respectively.Returns -1 if no code can be discernedfrom 
			// the response (i.e., the response is not valid HTTP).
			// 戻り値:the HTTP Status-Code, or -1
			if (httpURLConnection.getResponseCode() == 200) {
				BufferedReader r = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null)
					sb.append(line);
				stream = sb.toString();
				httpURLConnection.disconnect();
			}

		} catch (MalformedURLException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		return stream;
	}

	/**
	 * 
	 * @param object
	 * @return
	 * @throws JSONException
	 */
	public static Map<String, Object> convertJsonObjToMap(JSONObject object) throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();

		if (object != JSONObject.NULL) {
			Iterator<String> keysItr = object.keys();
			while (keysItr.hasNext()) {
				String key = keysItr.next();
				Object value = object.get(key);

				if (value instanceof JSONArray) {
					value = convertJsonArrayToList((JSONArray) value);
				}

				else if (value instanceof JSONObject) {
					value = convertJsonObjToMap((JSONObject) value);
				}
				map.put(key, value);
			}
		}
		return map;
	}

	/**
	 * 
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	public static List<Object> convertJsonArrayToList(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = convertJsonArrayToList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = convertJsonObjToMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}
}
