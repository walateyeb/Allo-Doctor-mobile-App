package com.application;

import java.util.Arrays;

//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.UiLifecycleHelper;
//import com.facebook.model.GraphUser;
//import com.facebook.widget.LoginButton;
//import com.facebook.widget.LoginButton.UserInfoChangedCallback;

import util.ConnectionDetector;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import appconfig.ConstValue;
import com.drapp.R;

public class LoginActivity extends ActionBarActivity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	Button btnLogin, btnRegister;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		
		 settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		 cd=new ConnectionDetector(this);
		 
		 btnLogin = (Button)findViewById(R.id.buttonLogin);
		
		 btnRegister = (Button)findViewById(R.id.buttonRegister);
		 
		 btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,Login2Activity.class);
				startActivity(intent);
				
			}
		});
		 btnRegister.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
					startActivity(intent);
					
				}
			});
		 
		

	}


}
