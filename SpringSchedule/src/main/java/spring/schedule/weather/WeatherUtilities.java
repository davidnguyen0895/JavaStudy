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
import java.util.List;

import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WeatherUtilities {

	private static final String API_BASE = "https://api.openweathermap.org/data/2.5/onecall";
	private static final String UNITS = "metric";

	/**
	 * 天気Hashmapデータを作成。
	 * 
	 * @param lat    地理座標(緯度)経度
	 * @param lon    地理座標(経度)
	 * @param apiKey APIキー
	 * @return 天気Hashmapデータ
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public static HashMap<org.joda.time.LocalDate, String> createWeatherHashMap(String lat, String lon, String apiKey)
			throws JsonMappingException, JsonProcessingException {

		// 天気情報取得対象の日付リスト
		List<org.joda.time.LocalDate> targetWeatherDateList = new ArrayList<org.joda.time.LocalDate>();
		// 天気アイコンの文字リスト
		List<String> iconList = new ArrayList<String>();

		// dailyリストを取得
		List<Daily> daily = new ObjectMapper()
				.readValue(getHTTPData(getApiRequest(lat, lon, apiKey)), MainJsonData.class).getDaily();

		// dailyリストから日付を取得
		for (Daily dailyData : daily) {

			targetWeatherDateList.add(org.joda.time.LocalDate.parse(convertUnixTimeToDate(dailyData.getDt()),
					DateTimeFormat.forPattern("yyyy/MM/dd")));

			// dailyDataから天気情報を取得
			List<Weather> weather = dailyData.getWeather();

			// 天気アイコンを取得
			for (Weather weatherData : weather) {
				iconList.add(weatherData.getIcon());
			}
		}

		// 日付とアイコン情報をHashmapに格納
		HashMap<org.joda.time.LocalDate, String> weatherMap = new HashMap<>();

		for (int i = 0; i < targetWeatherDateList.size(); i++) {
			weatherMap.put(targetWeatherDateList.get(i), iconList.get(i));
		}
		return weatherMap;
	}

	/**
	 * APIリクエストリンクを取得
	 * 
	 * @return APIリクエストリンク
	 */
	private static String getApiRequest(String lat, String lon, String apiKey) {
		StringBuilder sb = new StringBuilder(API_BASE);
		sb.append(String.format("?lat=%s&lon=%s&exclude=current,minutely,hourly,alerts&appid=%s&units=%s&lang=ja", lat,
				lon, apiKey, UNITS));
		return sb.toString();
	}

	/**
	 * UNIX日付を普通の日付に変換
	 * 
	 * @param unixTimeStamp
	 * @return 普通の日付
	 */
	private static String convertUnixTimeToDate(long unixTimeStamp) {
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
	 * URL情報を取得 HTTP/1.0 200 OK HTTP/1.0 401 Unauthorized It will return 200 and 401
	 * respectively.Returns -1 if no code can be discernedfrom the response (i.e.,
	 * the response is not valid HTTP). 戻り値:the HTTP Status-Code, or -1
	 * 
	 * @param urlString
	 * @return URL情報
	 */
	private static String getHTTPData(String urlString) {
		String stream = "";
		try {
			URL url = new URL(urlString);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

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
}
