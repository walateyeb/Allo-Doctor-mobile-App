package com.application;

import imgLoader.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import util.ConnectionDetector;
import util.GPSTracker;
import util.ObjectSerializer;
import adapters.GetCityAdapter;
import adapters.MyAppointmentAdapter;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import appconfig.ConstValue;
import com.drapp.R;

public class GetCityActivity extends ActionBarActivity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	ArrayList<String> newsArray,searchArray;
	GetCityAdapter adapter;
	EditText txtSearch;
	GPSTracker gps;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_city);
		
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		 cd=new ConnectionDetector(this);
		 
		 searchArray = new ArrayList<String>();
		 
		 newsArray = new ArrayList<String>();
	        try {
	        	newsArray = (ArrayList<String>) ObjectSerializer.deserialize(settings.getString("cities", ObjectSerializer.serialize(new ArrayList<String>())));		
			}catch (IOException e) {
				    e.printStackTrace();
			}
	        
	        txtSearch = (EditText)findViewById(R.id.editText1);
			gps = new GPSTracker(GetCityActivity.this);
			 
	        // vérifier si le GPS activé    
	        if(gps.canGetLocation()){
	             
	            double latitude = gps.getLatitude();
	            double longitude = gps.getLongitude();
	             
	            String locality = gps.getLocality(getApplicationContext());
	            txtSearch.setText(locality);
	               
	        }else{
	            // ne peut pas obtenir l'emplacement
	            // GPS ou Réseau est pas activé
	            // Demander à l'utilisateur d'activer le GPS / réseau dans les paramètres
	            gps.showSettingsAlert();
	        }
	        txtSearch.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					searchArray.clear();
					for (String str : newsArray) {
						Pattern p = Pattern.compile(txtSearch.getText().toString().toLowerCase()+"(.*)");
						 Matcher m = p.matcher(str.toLowerCase());
						 if (m.find()) {
							 searchArray.add(str);
						 }
						
					}
					
					adapter.notifyDataSetChanged();
				}
			});
	        
	        ListView listview = (ListView)findViewById(R.id.listView1);
	        adapter = new GetCityAdapter(getApplicationContext(), searchArray);
	        listview.setAdapter(adapter);
	        listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Intent intent=new Intent();  
                    intent.putExtra("CITYNAME", newsArray.get(position));  
                    setResult(-1,intent);  
					finish();
				}
			});
	        
	        new loadNewsTask().execute(true);
	}

	
	public class loadNewsTask extends AsyncTask<Boolean, Void, ArrayList<String>> {

		JSONParser jParser;
		JSONObject json;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
			if (result!=null) {
				
			}	
			try {
				settings.edit().putString("cities",ObjectSerializer.serialize(newsArray)).commit();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@SuppressLint("NewApi") @Override
		protected void onCancelled(ArrayList<String> result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
		}

	
		@Override
		protected ArrayList<String> doInBackground(
				Boolean... params) {
			// TODO Auto-generated method stub
			
			try {
				jParser = new JSONParser();
				
				if(cd.isConnectingToInternet())
				{
					json = jParser.getJSONFromUrl(ConstValue.JSON_GETCITY);
					if (json.has("data")) {
						
					
					if(json.get("data") instanceof JSONArray){
						
					JSONArray menus = json.getJSONArray("data");
					if(menus!=null)
					{
						newsArray.clear();
						for (int i = 0; i < menus.length(); i++) {
							JSONObject d = menus.getJSONObject(i);
							
	  							
							newsArray.add(d.getString("dr_city"));
						}
					}	
					
					}
					
					}
				}else
				{
					Toast.makeText(getApplicationContext(), "S'il vous plaît connecter mobile avec Internet", Toast.LENGTH_LONG).show();
				}
					
			jParser = null;
			json = null;
			
				} catch (Exception e) {
					// TODO: handle exception
					
					return null;
				}
			return newsArray;
		}

	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
        CommonFunctions common = new CommonFunctions();
        common.menuItemClick(GetCityActivity.this, id);
        return super.onOptionsItemSelected(item);
	}
}
