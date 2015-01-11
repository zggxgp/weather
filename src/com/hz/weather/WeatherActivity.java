package com.hz.weather;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hz.weather.bean.FutureWeatherBean;
import com.hz.weather.bean.HoursWeatherBean;
import com.hz.weather.bean.PMBean;
import com.hz.weather.bean.WeatherBean;
import com.hz.weather.service.WeatherService;
import com.hz.weather.service.WeatherService.OnParserCallBack;
import com.hz.weather.service.WeatherService.WeatherServiceBinder;
import com.hz.weather.swiperefresh.PullToRefreshBase;
import com.hz.weather.swiperefresh.PullToRefreshBase.OnRefreshListener;
import com.hz.weather.swiperefresh.PullToRefreshScrollView;

public class WeatherActivity extends Activity{
	
	private TextView tv;//������
	private Context mContext;
	private PullToRefreshScrollView mPullToRefreshScrollView;
	private ScrollView mScrollView;
	private WeatherService mWeatherService;
	
	private RelativeLayout rl_city;

	
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
		
		mContext = this;
		init();
		initService();
		
	}
	
	private void initService(){
		Intent intent = new Intent(mContext, WeatherService.class);
		
		startService(intent);
		
		bindService(intent, conn , Context.BIND_AUTO_CREATE);
	}
	
	ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mWeatherService.removeCallBack();
			
		}
		
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			
			mWeatherService = ((WeatherServiceBinder)arg1).getService();
			mWeatherService.setCallBack(new OnParserCallBack() {
				
				@Override
				public void OnParserComplete(List<HoursWeatherBean> list, PMBean pmBean,
						WeatherBean weatherBean) {
					
					
					
                    if (list != null && list.size() >= 5) {
                    	
                        setHourViews(list);
                    }

                    if (pmBean != null) {
                    	
                        setPMView(pmBean);
                    }

                    if (weatherBean != null) {
                    	
                    	
                        setWeatherViews(weatherBean);
                    }
					
                    mPullToRefreshScrollView.onRefreshComplete();
				}
			});
			
			mWeatherService.getCityWeather();
		}
	};
	
	//�������
	private void setWeatherViews(WeatherBean weatherBean){
		//�����кͷ���ʱ��
      	tv_city.setText(weatherBean.getCity());
      	tv_release.setText(weatherBean.getRelease());
      	
      	//��䵱������
      	String[] tempArr = weatherBean.getTemp().split("~");
      	String temp_str_a = tempArr[1].substring(0, tempArr[1].indexOf("��"));
      	String temp_str_b = tempArr[0].substring(0, tempArr[0].indexOf("��"));
      	tv_today_temp.setText("��"+temp_str_a+"��     ��"+temp_str_b+"��");
      	tv_now_temp.setText(weatherBean.getNow_temp());
      	tv_today_temp_a.setText(temp_str_a + "��");
        tv_today_temp_b.setText(temp_str_b + "��");
        
        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.HOUR_OF_DAY);
        String prefixStr = null;
        if (time >= 6 && time < 18) {
            prefixStr = "d";
        } else {
            prefixStr = "n";
        }
        iv_now_weather.setImageResource(getResources().getIdentifier(prefixStr +weatherBean.getWeather_id(), "drawable", "com.hz.weather"));
        
        
        iv_today_weather.setImageResource(getResources().getIdentifier("d"+weatherBean.getWeather_id(), "drawable", "com.hz.weather"));
        
        //���δ������
        List<FutureWeatherBean> futureList = weatherBean.getFutureList();
        if (futureList != null && futureList.size() == 3) {
            setFutureViews(tv_tommorrow, iv_tommorrow_weather, tv_tommorrow_temp_a, tv_tommorrow_temp_b, futureList.get(0));
            setFutureViews(tv_thirdday, iv_thirdday_weather, tv_thirdday_temp_a, tv_thirdday_temp_b, futureList.get(1));
            setFutureViews(tv_fourthday, iv_fourthday_weather, tv_fourthday_temp_a, tv_fourthday_temp_b, futureList.get(2));
        } 
        
        //��ϸ��Ϣ
        tv_humidity.setText(weatherBean.getHumidity());
        tv_dressing_index.setText(weatherBean.getDressing_index());
        tv_uv_index.setText(weatherBean.getUv_index());
        tv_wind.setText(weatherBean.getWind());
         
	}
	
	
	//���δ����������
	 private void setFutureViews(TextView tv_week, ImageView iv_weather, TextView tv_temp_a, TextView tv_temp_b, FutureWeatherBean bean) {
	        tv_week.setText(bean.getWeek());
	        iv_weather.setImageResource(getResources().getIdentifier("d" + bean.getWeather_id(), "drawable", "com.hz.weather"));
	        String[] tempArr = bean.getTemp().split("~");
	        String temp_str_a = tempArr[1].substring(0, tempArr[1].indexOf("��"));
	        String temp_str_b = tempArr[0].substring(0, tempArr[0].indexOf("��"));
	        tv_temp_a.setText(temp_str_a + "��");
	        tv_temp_b.setText(temp_str_b + "��");

	    }
	
	 //���δ��3Сʱ����
	 
	 private void setHourViews(List<HoursWeatherBean> list) {
	        setHourData(tv_next_three, iv_next_three, tv_next_three_temp, list.get(0));
	        setHourData(tv_next_six, iv_next_six, tv_next_six_temp, list.get(1));
	        setHourData(tv_next_nine, iv_next_nine, tv_next_nine_temp, list.get(2));
	        setHourData(tv_next_twelve, iv_next_twelve, tv_next_twelve_temp, list.get(3));
	        setHourData(tv_next_fifteen, iv_next_fifteen, tv_next_fifteen_temp, list.get(4));
	    }

	    private void setHourData(TextView tv_hour, ImageView iv_weather, TextView tv_temp, HoursWeatherBean bean) {

	        String prefixStr = null;
	        int time = Integer.valueOf(bean.getTime());
	        if (time >= 6 && time < 18) {
	            prefixStr = "d";
	        } else {
	            prefixStr = "n";
	        }

	        tv_hour.setText(bean.getTime() + "ʱ");
	        iv_weather.setImageResource(getResources().getIdentifier(prefixStr + bean.getWeather_id(), "drawable", "com.juhe.weather"));
	        tv_temp.setText(bean.getTemp() + "��");
	    }
	 
	 //���PM����
	    private void setPMView(PMBean bean) {
	        tv_aqi.setText(bean.getAqi());
	        tv_quality.setText(bean.getQuality());
	    }

	    
	    
	
	
	
	 
	 
	
	
	public void init(){
		
		
		mPullToRefreshScrollView = (PullToRefreshScrollView)findViewById(R.id.pull_refresh_scrollview);
		mPullToRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				mWeatherService.getCityWeather();
				
			}
				
			
		});
			
		rl_city = (RelativeLayout) findViewById(R.id.rl_city);
        rl_city.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivityForResult(new Intent(mContext, CityActivity.class), 1);

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
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        // TODO Auto-generated method stub
		 	
		 	Log.i("resulttest", "onActivityResulִ��");
		 		
	        if (requestCode == 1 && resultCode == 1) {
	            String city = data.getStringExtra("city");
	            mWeatherService.getCityWeather(city);
	        }
            
	    }
	
	@Override
	protected void onDestroy() {
		
		unbindService(conn);
		//mWeatherService.stopSelf();
		super.onDestroy();
		
	}
	
	
}
