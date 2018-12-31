package com.application;

import imgLoader.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import util.ConnectionDetector;
import util.ObjectSerializer;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;
import appconfig.ConstValue;
import com.drapp.R;

public class AboutusActivity extends ActionBarActivity {
	WebView webview;
	public SharedPreferences settings;
	public ConnectionDetector cd;
	String page_content;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutus);
		/*settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		cd=new ConnectionDetector(this);
		webview = (WebView)findViewById(R.id.webView1);
		page_content = settings.getString("page_aboutus", "");
		webview.loadData(page_content, "text/html", "UTF-8");
		new loadNewsTask().execute(true);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
        CommonFunctions common = new CommonFunctions();
        common.menuItemClick(AboutusActivity.this, id);
        return super.onOptionsItemSelected(item);
	}
	
	
	public class loadNewsTask extends AsyncTask<Boolean, Void, ArrayList<HashMap<String, String>>> {

		JSONParser jParser;
		JSONObject json;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			// TODO Auto-generated method stub
			if (result!=null) {
				//adapter.notifyDataSetChanged();
				
			}	
		
			settings.edit().putString("page_aboutus",page_content).commit();
			webview.loadData(page_content, "text/html", "UTF-8");
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@SuppressLint("NewApi") @Override
		protected void onCancelled(ArrayList<HashMap<String, String>> result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
		}

	
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				Boolean... params) {
			// TODO Auto-generated method stub
			
			try {
				jParser = new JSONParser();
				
				if(cd.isConnectingToInternet())
				{
					json = jParser.getJSONFromUrl(ConstValue.JSON_PAGE);
				
						
					JSONObject page = json.getJSONObject("page");
					page_content = page.getString("content").toString();
					
				}else
				{
					Toast.makeText(getApplicationContext(), "Please connect mobile with working Internet", Toast.LENGTH_LONG).show();
				}
					
			jParser = null;
			json = null;
			
				} catch (Exception e) {
					// TODO: handle exception
					
					return null;
				}
			return null;
		}

	}
}
