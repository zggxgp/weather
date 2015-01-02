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
	
	private TextView tv;//������
	
	private PullToRefreshScrollView mPullToRefreshScrollView;
	private ScrollView mScrollView;
	
	private TextView tv_city,// ����
			tv_release,// ����ʱ��
			tv_now_weather,// ����
			tv_today_temp,// �¶�
			tv_now_temp,// ��ǰ�¶�
			tv_aqi,// ��������ָ��
			tv_quality,// ��������
			tv_next_three,// 3Сʱ
			tv_next_six,// 6Сʱ
			tv_next_nine,// 9Сʱ
			tv_next_twelve,// 12Сʱ
			tv_next_fifteen,// 15Сʱ
			tv_next_three_temp,// 3Сʱ�¶�
			tv_next_six_temp,// 6Сʱ�¶�
			tv_next_nine_temp,// 9Сʱ�¶�
			tv_next_twelve_temp,// 12Сʱ�¶�
			tv_next_fifteen_temp,// 15Сʱ�¶�
			tv_today_temp_a,// �����¶�a
			tv_today_temp_b,// �����¶�b
			tv_tommorrow,// ����
			tv_tommorrow_temp_a,// �����¶�a
			tv_tommorrow_temp_b,// �����¶�b
			tv_thirdday,// ������
			tv_thirdday_temp_a,// �������¶�a
			tv_thirdday_temp_b,// �������¶�b
			tv_fourthday,// ������
			tv_fourthday_temp_a,// �������¶�a
			tv_fourthday_temp_b,// �������¶�b
			tv_humidity,// ʪ��
			tv_wind, tv_uv_index,// ������ָ��
			tv_dressing_index;// ����ָ��

	private ImageView iv_now_weather,// ����
			iv_next_three,// 3Сʱ
			iv_next_six,// 6Сʱ
			iv_next_nine,// 9Сʱ
			iv_next_twelve,// 12Сʱ
			iv_next_fifteen,// 15Сʱ
			iv_today_weather,// ����
			iv_tommorrow_weather,// ����
			iv_thirdday_weather,// ������
			iv_fourthday_weather;// ������
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		getCityWeather();
		init();
		
		
	}
	
	//������ȡ��������
	private WeatherBean parseWeather(JSONObject json){
		
		WeatherBean weatherBean = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		
		try{
			int code = json.getInt("resultcode");
			int errorcode = json.getInt("error_code");
			if(errorcode==0&&code==200){
				JSONObject resultJson = json.getJSONObject("result");
				 weatherBean = new WeatherBean();
				
				//��ȡ����������json
				JSONObject todayJson = resultJson.getJSONObject("today");
				weatherBean.setUv_index(todayJson.getString("uv_index"));
				weatherBean.setTemp(todayJson.getString("temperature"));
				weatherBean.setWeather_str(todayJson.getString("weather"));
				weatherBean.setWeather_id(todayJson.getJSONObject("weather_id").getString("fa"));
				weatherBean.setDressing_index(todayJson.getString("dressing_index"));
				
				//����sk json
				JSONObject skJson = resultJson.getJSONObject("sk");
				weatherBean.setWind(skJson.getString("wind_direction")+"  "+skJson.getString("wind_strength"));
				weatherBean.setNow_temp(skJson.getString("temp"));
				weatherBean.setRelease(skJson.getString("time"));
				weatherBean.setHumidity(skJson.getString("humidity"));
				
				//����δ����������JSON
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
	
	
	//�����л�ȡ��������
	private void getCityWeather(){
		
		WeatherData date = WeatherData.getInstance();

		date.getByCitys("�Ϻ�", 2, new JsonCallBack() {

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
