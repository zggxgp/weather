package com.hz.weather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hz.weather.bean.FutureWeatherBean;
import com.hz.weather.bean.WeatherBean;
import com.hz.weather.swiperefresh.PullToRefreshBase;
import com.hz.weather.swiperefresh.PullToRefreshBase.OnRefreshListener;
import com.hz.weather.swiperefresh.PullToRefreshScrollView;
import com.thinkland.juheapi.common.JsonCallBack;
import com.thinkland.juheapi.data.weather.WeatherData;

public class WeatherActivity extends Activity{
	
	private TextView tv;//测试用
	
	private PullToRefreshScrollView mPullToRefreshScrollView;
	private ScrollView mScrollView;
	
	private TextView tv_city,// 城市
			tv_release,// 发布时间
			tv_now_weather,// 天气
			tv_today_temp,// 温度
			tv_now_temp,// 当前温度
			tv_aqi,// 空气质量指数
			tv_quality,// 空气质量
			tv_next_three,// 3小时
			tv_next_six,// 6小时
			tv_next_nine,// 9小时
			tv_next_twelve,// 12小时
			tv_next_fifteen,// 15小时
			tv_next_three_temp,// 3小时温度
			tv_next_six_temp,// 6小时温度
			tv_next_nine_temp,// 9小时温度
			tv_next_twelve_temp,// 12小时温度
			tv_next_fifteen_temp,// 15小时温度
			tv_today_temp_a,// 今天温度a
			tv_today_temp_b,// 今天温度b
			tv_tommorrow,// 明天
			tv_tommorrow_temp_a,// 明天温度a
			tv_tommorrow_temp_b,// 明天温度b
			tv_thirdday,// 第三天
			tv_thirdday_temp_a,// 第三天温度a
			tv_thirdday_temp_b,// 第三天温度b
			tv_fourthday,// 第四天
			tv_fourthday_temp_a,// 第四天温度a
			tv_fourthday_temp_b,// 第四天温度b
			tv_humidity,// 湿度
			tv_wind, tv_uv_index,// 紫外线指数
			tv_dressing_index;// 穿衣指数

	private ImageView iv_now_weather,// 现在
			iv_next_three,// 3小时
			iv_next_six,// 6小时
			iv_next_nine,// 9小时
			iv_next_twelve,// 12小时
			iv_next_fifteen,// 15小时
			iv_today_weather,// 今天
			iv_tommorrow_weather,// 明天
			iv_thirdday_weather,// 第三天
			iv_fourthday_weather;// 第四天
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		getCityWeather();
		init();
		
		
	}
	
	//解析获取到的数据
	private WeatherBean parseWeather(JSONObject json){
		
		WeatherBean weatherBean = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		
		try{
			int code = json.getInt("resultcode");
			int errorcode = json.getInt("error_code");
			if(errorcode==0&&code==200){
				JSONObject resultJson = json.getJSONObject("result");
				 weatherBean = new WeatherBean();
				
				//获取今天天气的json
				JSONObject todayJson = resultJson.getJSONObject("today");
				weatherBean.setUv_index(todayJson.getString("uv_index"));
				weatherBean.setTemp(todayJson.getString("temperature"));
				weatherBean.setWeather_str(todayJson.getString("weather"));
				weatherBean.setWeather_id(todayJson.getJSONObject("weather_id").getString("fa"));
				weatherBean.setDressing_index(todayJson.getString("dressing_index"));
				
				//解析sk json
				JSONObject skJson = resultJson.getJSONObject("sk");
				weatherBean.setWind(skJson.getString("wind_direction")+"  "+skJson.getString("wind_strength"));
				weatherBean.setNow_temp(skJson.getString("temp"));
				weatherBean.setRelease(skJson.getString("time"));
				weatherBean.setHumidity(skJson.getString("humidity"));
				
				//解析未来几天天气JSON
				JSONArray futureArray = resultJson.getJSONArray("future");
				Date date = new Date(System.currentTimeMillis());
				List<FutureWeatherBean> futureList = new ArrayList<FutureWeatherBean>();
				
				for(int i=0;i<futureArray.length();i++){
					JSONObject futureJson = futureArray.getJSONObject(i);
					FutureWeatherBean  futureBean = new FutureWeatherBean();
					
					Date datef = sdf.parse(futureJson.getString("date"));
					if(!datef.after(date)){
						continue;
					}
					futureBean.setTemp(futureJson.getString("temperature"));
					futureBean.setWeek(futureJson.getString("week"));
					futureBean.setWeather_id(futureJson.getJSONObject("weather_id").getString("fa"));
					futureList.add(futureBean);
					if(futureList.size()>=3){
						break;
					}
				}
				
				weatherBean.setFutureList(futureList);
				
				
			}
		}catch(JSONException e){
			e.printStackTrace();
		}catch(ParseException e){
			e.printStackTrace();
		}
		
		return weatherBean;
	}
	
	
	//按城市获取天气数据
	private void getCityWeather(){
		
		WeatherData date = WeatherData.getInstance();

		date.getByCitys("上海", 2, new JsonCallBack() {

			@Override
			public void jsonLoaded(JSONObject arg0) {
				
				System.out.println(arg0.toString());

			}
		});
	}
	
	public void init(){
		
		
		mPullToRefreshScrollView = (PullToRefreshScrollView)findViewById(R.id.pull_refresh_scrollview);
		mPullToRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				
				
			}
				
			
		});
			
		
		
		 tv_city = (TextView) findViewById(R.id.tv_city);
	        tv_release = (TextView) findViewById(R.id.tv_release);
	        tv_now_weather = (TextView) findViewById(R.id.tv_now_weather);
	        tv_today_temp = (TextView) findViewById(R.id.tv_today_temp);
	        tv_now_temp = (TextView) findViewById(R.id.tv_now_temp);
	        tv_aqi = (TextView) findViewById(R.id.tv_aqi);
	        tv_quality = (TextView) findViewById(R.id.tv_quality);
	        tv_next_three = (TextView) findViewById(R.id.tv_next_three);
	        tv_next_six = (TextView) findViewById(R.id.tv_next_six);
	        tv_next_nine = (TextView) findViewById(R.id.tv_next_nine);
	        tv_next_twelve = (TextView) findViewById(R.id.tv_next_twelve);
	        tv_next_fifteen = (TextView) findViewById(R.id.tv_next_fifteen);
	        tv_next_three_temp = (TextView) findViewById(R.id.tv_next_three_temp);
	        tv_next_six_temp = (TextView) findViewById(R.id.tv_next_six_temp);
	        tv_next_nine_temp = (TextView) findViewById(R.id.tv_next_nine_temp);
	        tv_next_twelve_temp = (TextView) findViewById(R.id.tv_next_twelve_temp);
	        tv_next_fifteen_temp = (TextView) findViewById(R.id.tv_next_fifteen_temp);
	        tv_today_temp_a = (TextView) findViewById(R.id.tv_today_temp_a);
	        tv_today_temp_b = (TextView) findViewById(R.id.tv_today_temp_b);
	        tv_tommorrow = (TextView) findViewById(R.id.tv_tommorrow);
	        tv_tommorrow_temp_a = (TextView) findViewById(R.id.tv_tommorrow_temp_a);
	        tv_tommorrow_temp_b = (TextView) findViewById(R.id.tv_tommorrow_temp_b);
	        tv_thirdday = (TextView) findViewById(R.id.tv_thrid);
	        tv_thirdday_temp_a = (TextView) findViewById(R.id.tv_thrid_temp_a);
	        tv_thirdday_temp_b = (TextView) findViewById(R.id.tv_thrid_temp_b);
	        tv_fourthday = (TextView) findViewById(R.id.tv_fourth);
	        tv_fourthday_temp_a = (TextView) findViewById(R.id.tv_fourth_temp_a);
	        tv_fourthday_temp_b = (TextView) findViewById(R.id.tv_fourth_temp_b);
	        tv_humidity = (TextView) findViewById(R.id.tv_humidity);
	        tv_wind = (TextView) findViewById(R.id.tv_wind);
	        tv_uv_index = (TextView) findViewById(R.id.tv_uv_index);
	        tv_dressing_index = (TextView) findViewById(R.id.tv_dressing_index);

	        iv_now_weather = (ImageView) findViewById(R.id.iv_now_weather);
	        iv_next_three = (ImageView) findViewById(R.id.iv_next_three);
	        iv_next_six = (ImageView) findViewById(R.id.iv_next_six);
	        iv_next_nine = (ImageView) findViewById(R.id.iv_next_nine);
	        iv_next_twelve = (ImageView) findViewById(R.id.iv_next_twelve);
	        iv_next_fifteen = (ImageView) findViewById(R.id.iv_next_fifteen);
	        iv_today_weather = (ImageView) findViewById(R.id.iv_today_weather);
	        iv_tommorrow_weather = (ImageView) findViewById(R.id.iv_tommorrow_weather);
	        iv_thirdday_weather = (ImageView) findViewById(R.id.iv_thrid_weather);
	        iv_fourthday_weather = (ImageView) findViewById(R.id.iv_fourth_weather);
	}
	
	
}
