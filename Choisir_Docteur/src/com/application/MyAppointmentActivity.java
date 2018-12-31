package com.application;

import imgLoader.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import util.ConnectionDetector;
import util.ObjectSerializer;
import adapters.MyAppointmentAdapter;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import appconfig.ConstValue;
import com.drapp.R;

public class MyAppointmentActivity extends ActionBarActivity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	ArrayList<HashMap<String, String>> newsArray;
	MyAppointmentAdapter adapter;
	ProgressDialog dialog;
	HashMap<String, String> selected_app;
	int selected_index;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_appointment);
		
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		 cd=new ConnectionDetector(this);
		 
		 newsArray = new ArrayList<HashMap<String,String>>();
	        try {
	        	newsArray = (ArrayList<HashMap<String,String>>) ObjectSerializer.deserialize(settings.getString("myappointment", ObjectSerializer.serialize(new ArrayList<HashMap<String,String>>())));		
			}catch (IOException e) {
				    e.printStackTrace();
			}
	    
	        ListView listview = (ListView)findViewById(R.id.listView1);
	        adapter = new MyAppointmentAdapter(getApplicationContext(), newsArray);
	        listview.setAdapter(adapter);
	        listview.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, final int position, long id) {
					// TODO Auto-generated method stub
					 AlertDialog.Builder builder = new AlertDialog.Builder(MyAppointmentActivity.this);
				        builder.setMessage("Etes-vous sûre d'annuler le rendez-vous:")
				               .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
				                   public void onClick(DialogInterface dialog, int id) {
				                      
				                	   selected_app = newsArray.get(position);
				                	   selected_index = position;
				                	   new cancleAppointment().execute(true);
				                   }
				               })
				               .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
				                   public void onClick(DialogInterface dialog, int id) {
				                      
				                   }
				               });
				        
				        AlertDialog dialog = builder.create();
				    dialog.show();
					return false;
				}
			});
	        
	        new loadNewsTask().execute(true);
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
        common.menuItemClick(MyAppointmentActivity.this, id);
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
				
			}	
			try {
				settings.edit().putString("myappointment",ObjectSerializer.serialize(newsArray)).commit();
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
					json = jParser.getJSONFromUrl(ConstValue.JSON_MY_APPOINTMENT);
					if (json.has("data")) {
						
					
					if(json.get("data") instanceof JSONArray){
						
					JSONArray menus = json.getJSONArray("data");
					if(menus!=null)
					{
						newsArray.clear();
						for (int i = 0; i < menus.length(); i++) {
							JSONObject d = menus.getJSONObject(i);
							HashMap<String, String> map2 = new HashMap<String, String>();
	  							map2.put("id", d.getString("id"));
	  							map2.put("dr_id", d.getString("dr_id"));
	  							map2.put("cl_id", d.getString("cl_id"));
	  							map2.put("day", d.getString("day"));
	  							
	  							map2.put("time", d.getString("time"));
	  							map2.put("app_date", d.getString("app_date"));
	  							map2.put("user_id", d.getString("user_id"));
	  							map2.put("phone", d.getString("phone"));
	  							map2.put("description", d.getString("description"));
	  							map2.put("confirm", d.getString("confirm"));
	  							map2.put("dr_name", d.getString("dr_name"));
	  							map2.put("dr_degree", d.getString("dr_degree"));
	  							
							newsArray.add(map2);
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
	
	
	class cancleAppointment extends AsyncTask<Boolean, Void, String> {
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(MyAppointmentActivity.this, "", 
                    "Chargement. S'il vous plaît, attendez...", true);
			super.onPreExecute();

		}
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);

		}
		@Override
		protected String doInBackground(Boolean... params) {
			// TODO Auto-generated method stub
		
			String responseString = null;
			 
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ConstValue.JSON_CANCLE_APPOINTMENT);

            try {
            
            	
            	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    	        nameValuePairs.add(new BasicNameValuePair("user_id", settings.getString("user_id","")));
	    	        nameValuePairs.add(new BasicNameValuePair("dr_id",selected_app.get("dr_id")));
	    	        nameValuePairs.add(new BasicNameValuePair("app_id",selected_app.get("id")));
	    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

 
                // appel le serveur
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
               
                	InputStream is = r_entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
        					is, "iso-8859-1"), 8);
        			StringBuilder sb = new StringBuilder();
        			String line = null;
        			while ((line = reader.readLine()) != null) {
        				sb.append(line + "\n");
        			}
        			is.close();
        			String json = sb.toString();
        			JSONObject jObj = new JSONObject(json);
        			if (jObj.getString("responce").equalsIgnoreCase("success")) {
	        			
        				
	        			
        			}else{
        				responseString = jObj.getString("data"); 
        			}
                } else {
                    responseString = "Erreur est survenue! Http Code : "
                            + statusCode;
                }
 
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }catch (JSONException e) {
    			Log.e("JSON Parser", "Erreur d'analyse les données " + e.toString());
    		}
 
            return responseString;
			
			
		}
		@Override
		protected void onPostExecute(String result) {
			if(result!=null){
				Toast.makeText(getApplicationContext(), "Error :"+result, Toast.LENGTH_LONG).show();
				
			}else{
				newsArray.remove(selected_index);
				adapter.notifyDataSetChanged();
			}
			dialog.dismiss();
		}
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
		}
	}
	
}
