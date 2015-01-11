package com.hz.weather.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.hz.weather.bean.FutureWeatherBean;
import com.hz.weather.bean.HoursWeatherBean;
import com.hz.weather.bean.PMBean;
import com.hz.weather.bean.WeatherBean;
import com.thinkland.juheapi.common.JsonCallBack;
import com.thinkland.juheapi.data.air.AirData;
import com.thinkland.juheapi.data.weather.WeatherData;

public class WeatherService extends Service {
	
	private boolean isRunning = false;
	private int count = 0;
	private WeatherServiceBinder binder = new WeatherServiceBinder();
	private HoursWeatherBean hoursBean;
	private List<HoursWeatherBean> list;
	private PMBean pmBean;
	private WeatherBean weatherBean; 
	private OnParserCallBack callBack;
	private String  city = null;
	
	private final int REPEAT_MSG = 0x01;
	private final int CALLBACK_OK = 0x02;
	private final int CALLBACK_ERROR = 0x04;
	
	public interface OnParserCallBack{
		public void OnParserComplete(List<HoursWeatherBean> list, PMBean pmBean, WeatherBean weatherBean);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {

		return binder;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		city = "上海";
		mHandler.sendEmptyMessage(REPEAT_MSG);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.v("service", "ondestroy");
	}

	public void test() {
		Log.v("TEST", "TEST");
	}
	
	//用于切换城市
	public void getCityWeather(String city) {
		this.city = city;
		getCityWeather();
	}
	
	// 解析pm
	private PMBean parserPM(JSONObject json) {
		PMBean bean = null;
		try {
			int code = json.getInt("resultcode");
			int error_code = json.getInt("error_code");
			if (error_code == 0 && code == 200) {
				bean = new PMBean();
				JSONObject pmJSON = json.getJSONArray("result")
						.getJSONObject(0).getJSONObject("citynow");
				bean.setAqi(pmJSON.getString("AQI"));
				bean.setQuality(pmJSON.getString("quality"));

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bean;

	}

	// 解析3小时预报
	private List<HoursWeatherBean> parserForecast3h(JSONObject json) {
		List<HoursWeatherBean> list = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date(System.currentTimeMillis());
		try {
			int code = json.getInt("resultcode");
			int error_code = json.getInt("error_code");
			if (error_code == 0 && code == 200) {
				list = new ArrayList<HoursWeatherBean>();
				JSONArray resultArray = json.getJSONArray("result");
				for (int i = 0; i < resultArray.length(); i++) {
					JSONObject hourJson = resultArray.getJSONObject(i);
					Date hDate = sdf.parse(hourJson.getString("sfdate"));
					if (!hDate.after(date)) {
						continue;
					}
					HoursWeatherBean bean = new HoursWeatherBean();
					bean.setWeather_id(hourJson.getString("weatherid"));
					bean.setTemp(hourJson.getString("temp1"));
					Calendar c = Calendar.getInstance();
					c.setTime(hDate);
					bean.setTime(c.get(Calendar.HOUR_OF_DAY) + "");
					list.add(bean);
					if (list.size() == 5) {
						break;
					}
				}

			} else {
				Toast.makeText(getApplicationContext(), "HOURS_ERROR",
						Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return list;

	}

	// 解析获取到今天和未来三天的天气JSON数据
	private WeatherBean parseWeather(JSONObject json) {

		WeatherBean weatherBean = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		try {
			int code = json.getInt("resultcode");
			int errorcode = json.getInt("error_code");
			if (errorcode == 0 && code == 200) {
				JSONObject resultJson = json.getJSONObject("result");
				weatherBean = new WeatherBean();
				// 获取今天天气的json
				JSONObject todayJson = resultJson.getJSONObject("today");
				weatherBean.setCity(todayJson.getString("city"));
				weatherBean.setUv_index(todayJson.getString("uv_index"));
				weatherBean.setTemp(todayJson.getString("temperature"));
				weatherBean.setWeather_str(todayJson.getString("weather"));
				weatherBean.setWeather_id(todayJson.getJSONObject("weather_id")
						.getString("fa"));
				weatherBean.setDressing_index(todayJson
						.getString("dressing_index"));

				// 解析sk json
				JSONObject skJson = resultJson.getJSONObject("sk");
				weatherBean.setWind(skJson.getString("wind_direction") + "  "
						+ skJson.getString("wind_strength"));
				weatherBean.setNow_temp(skJson.getString("temp"));
				weatherBean.setRelease(skJson.getString("time"));
				weatherBean.setHumidity(skJson.getString("humidity"));

				// 解析未来几天天气JSON
				JSONArray futureArray = resultJson.getJSONArray("future");
				Date date = new Date(System.currentTimeMillis());
				List<FutureWeatherBean> futureList = new ArrayList<FutureWeatherBean>();

				for (int i = 0; i < futureArray.length(); i++) {
					JSONObject futureJson = futureArray.getJSONObject(i);
					FutureWeatherBean futureBean = new FutureWeatherBean();

					Date datef = sdf.parse(futureJson.getString("date"));
					if (!datef.after(date)) {
						continue;
					}
					futureBean.setTemp(futureJson.getString("temperature"));
					futureBean.setWeek(futureJson.getString("week"));
					futureBean.setWeather_id(futureJson.getJSONObject(
							"weather_id").getString("fa"));
					futureList.add(futureBean);
					if (futureList.size() >= 3) {
						break;
					}
				}

				weatherBean.setFutureList(futureList);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return weatherBean;
	}
	
	public void setCallBack(OnParserCallBack callBack){
		this.callBack = callBack;
	}
	
	public void removeCallBack() {
		callBack = null;
	}
	
	//按城市获取天气数据
		public void getCityWeather(){
			
			if(isRunning){
				return;
			}
			
			isRunning = true;
			count = 0;
			System.out.println("origin--------------------------"+count);
			WeatherData date = WeatherData.getInstance();

			date.getByCitys(city, 2, new JsonCallBack() {

				@Override
				public void jsonLoaded(JSONObject arg0) {
					System.out.println("WeatherWeatherWeatherWeather");
					
					weatherBean = parseWeather(arg0);
					
					
					synchronized (this) {
						count++;
					}
					
					
					if(count==3){
						//mPullToRefreshScrollView.onRefreshComplete();
						if(callBack!=null){
							callBack.OnParserComplete(list, pmBean, weatherBean);
						}
						
						isRunning = false;
					}
				}
			});
			
			
			date.getForecast3h(city, new JsonCallBack(){
				
				@Override
				public void jsonLoaded(JSONObject arg0) {
					
					list = parserForecast3h(arg0);
					
					synchronized (this) {
						count++;
					}
					
					
					if(count==3){
						//mPullToRefreshScrollView.onRefreshComplete();
						if(callBack!=null){
							callBack.OnParserComplete(list, pmBean, weatherBean);
						}
						isRunning = false;
					}
					
				}
				
			});
			
			
			AirData airData = AirData.getInstance();
			airData.cityAir(city, new JsonCallBack() {

				@Override
				public void jsonLoaded(JSONObject arg0) {
				    pmBean = parserPM(arg0);
					if (pmBean != null) {
					 //setPMView(pmBean);
					 }
					
					synchronized (this) {
						count++;
					}
					
					
					if(count==3){
						//mPullToRefreshScrollView.onRefreshComplete();
						isRunning = false;
						if(callBack!=null){
							callBack.OnParserComplete(list, pmBean, weatherBean);
						}
						
					}
				}
			});
			
			
		}
		
		Handler mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				switch (msg.what) {
				case REPEAT_MSG:

					getCityWeather();
					sendEmptyMessageDelayed(REPEAT_MSG, 30 * 60 * 1000);
					break;
				case CALLBACK_OK:
					if (callBack != null) {
						callBack.OnParserComplete(list, pmBean, weatherBean);
					}
					isRunning = false;
					break;
				case CALLBACK_ERROR:
					Toast.makeText(getApplicationContext(), "loading error", Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
				}
			}

		};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		
		return super.onStartCommand(intent, flags, startId);

	}

	public class WeatherServiceBinder extends Binder {

		public WeatherService getService() {
			return WeatherService.this;
		}
	}
}
