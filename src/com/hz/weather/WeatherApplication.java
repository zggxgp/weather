package com.hz.weather;

import com.thinkland.juheapi.common.CommonFun;

import android.app.Application;

public class WeatherApplication extends Application{

	@Override
	public void onCreate() {
		
		super.onCreate();
		CommonFun.initialize(getApplicationContext());
	}
	
	
}
