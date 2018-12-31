package com.application;

import util.ConnectionDetector;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import appconfig.ConstValue;
import com.drapp.R;

public class Splash extends Activity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		//this.getActionBar();
		
		 settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		 cd=new ConnectionDetector(this);
		 
		 if(settings.getString("user_id", "").equalsIgnoreCase("")){
			 Intent intent = new Intent(Splash.this,LoginActivity.class);
			 startActivity(intent);
			 finish();
		 }else{
			 
			 Intent intent = new Intent(Splash.this,MainActivity.class);
			 startActivity(intent);
			 finish();
		 }
	}
	
}
