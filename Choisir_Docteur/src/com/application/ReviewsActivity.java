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
import adapters.ReviewAdapter;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import appconfig.ConstValue;
import com.drapp.R;

public class ReviewsActivity extends ActionBarActivity {
	ProgressDialog dialog;
	JSONObject j_clinic;
	public SharedPreferences settings;
	public ConnectionDetector cd;
	RatingBar ratingbar;
	EditText txtComment;
	ArrayList<HashMap<String, String>> newsArray;
	ReviewAdapter adapter;
	LinearLayout reviewlayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reviews);
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		
		cd=new ConnectionDetector(this);
		j_clinic = ConstValue.selected_clinic;
		
		TextView txtname = (TextView)findViewById(R.id.textView2);
		TextView txtdegree = (TextView)findViewById(R.id.textView3);
		HashMap<String, String> map = ConstValue.selected_doctor;
		txtname.setText(map.get("dr_name"));
		txtdegree.setText(map.get("dr_degree"));
		
		ratingbar = (RatingBar)findViewById(R.id.ratingBar1);
		txtComment = (EditText)findViewById(R.id.editText1);
		
		newsArray = new ArrayList<HashMap<String,String>>();
        try {
        	newsArray = (ArrayList<HashMap<String,String>>) ObjectSerializer.deserialize(settings.getString("reviews"+j_clinic.getString("cl_id")+j_clinic.getString("dr_id"), ObjectSerializer.serialize(new ArrayList<HashMap<String,String>>())));		
		}catch (IOException e) {
			    e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        ListView listview = (ListView)findViewById(R.id.listView1);
        adapter = new ReviewAdapter(getApplicationContext(), newsArray);
        listview.setAdapter(adapter);
        
		reviewlayout = (LinearLayout)findViewById(R.id.reviewLayout);
		reviewlayout.setVisibility(View.GONE);
		
		Button btnReview = (Button)findViewById(R.id.button1);
		btnReview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(reviewlayout.getVisibility() == View.GONE)
					reviewlayout.setVisibility(View.VISIBLE);
				else
					reviewlayout.setVisibility(View.GONE);
			}
		});
		
		Button btnSubmit = (Button)findViewById(R.id.buttonSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new registerTask().execute(true);
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
        common.menuItemClick(ReviewsActivity.this, id);
        return super.onOptionsItemSelected(item);
	}
	
	class registerTask extends AsyncTask<Boolean, Void, String> {
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ReviewsActivity.this, "", 
                    "Loading. Please wait...", true);
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
            HttpPost httppost = new HttpPost(ConstValue.JSON_ADD_REVIEW);
           

            try {
            
            	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            	 nameValuePairs.add(new BasicNameValuePair("cl_id", j_clinic.getString("cl_id")));
            	 nameValuePairs.add(new BasicNameValuePair("dr_id", j_clinic.getString("dr_id")));
            	 nameValuePairs.add(new BasicNameValuePair("user_id", settings.getString("user_id","")));
            	 nameValuePairs.add(new BasicNameValuePair("rating", String.valueOf(ratingbar.getRating())));
            	 nameValuePairs.add(new BasicNameValuePair("review", txtComment.getText().toString()));
            	 
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
	        			JSONObject d = jObj.getJSONObject("data");
	        			
		                
	        				HashMap<String, String> map2 = new HashMap<String, String>();
							map2.put("id", d.getString("id"));
							map2.put("user_id", d.getString("user_id"));
							map2.put("dr_id", d.getString("dr_id"));
							map2.put("cl_id", d.getString("cl_id"));
							
							map2.put("rating", d.getString("rating"));
							map2.put("review", d.getString("review"));
							map2.put("on_date", d.getString("on_date"));
							map2.put("approved", d.getString("approved"));
							map2.put("name", d.getString("name"));
							map2.put("email", d.getString("email"));
							
							newsArray.add(map2);
	        			
				
	        			
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
    			Log.e("JSON Parser", "Erreur d'analyse les données : " + e.toString());
    		}
 
            return responseString;
			
			
		}
		

		@Override
		protected void onPostExecute(String result) {
			if(result!=null){
				Toast.makeText(getApplicationContext(), "Erreur :"+result, Toast.LENGTH_LONG).show();
			
			}else{
				
				txtComment.setText("");
				ratingbar.setRating(0);
				reviewlayout.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
			}
			dialog.dismiss();
		}
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
		}
		
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
				settings.edit().putString("reviews"+j_clinic.getString("cl_id")+j_clinic.getString("dr_id"),ObjectSerializer.serialize(newsArray)).commit();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
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
					json = jParser.getJSONFromUrl(ConstValue.JSON_GET_REVIEW+"&cl_id"+j_clinic.getString("cl_id")+"&dr_id"+j_clinic.getString("dr_id"));
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
	  							map2.put("user_id", d.getString("user_id"));
	  							map2.put("dr_id", d.getString("dr_id"));
	  							map2.put("cl_id", d.getString("cl_id"));
	  							
	  							map2.put("rating", d.getString("rating"));
	  							map2.put("review", d.getString("review"));
	  							map2.put("on_date", d.getString("on_date"));
	  							map2.put("approved", d.getString("approved"));
	  							map2.put("name", d.getString("name"));
	  							map2.put("email", d.getString("email"));
	  							
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

	
}
