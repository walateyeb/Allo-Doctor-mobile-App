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
import adapters.ServiceChargeAdapter;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import appconfig.ConstValue;
import com.drapp.R;

public class Appointment2Activity extends ActionBarActivity {
	TextView textDesc;
	ProgressDialog dialog;
	
	String clinic_id, dr_id, app_date, time, day, clinic_fees, clinic_discount;
	public SharedPreferences settings;
	public ConnectionDetector cd;
	
	ServiceChargeAdapter adapter;
	ArrayList<InfoRowdata> serviceArray;
	ListView listview;
	
	TextView txtDiscount, txtTotal, txtNetAmount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment2);
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		cd=new ConnectionDetector(this);
		
		Bundle b = getIntent().getExtras();
		
		
		
		clinic_id = b.getString("clinic_id");
		clinic_fees = b.getString("cl_fees");
		clinic_discount = b.getString("cl_discount");
		dr_id = b.getString("dr_id");
		app_date = b.getString("app_date");
		time = b.getString("time");
		day = b.getString("day");
		
		textDesc = (TextView)findViewById(R.id.editDescription);
		
		Button btncontinue = (Button)findViewById(R.id.button1);
		btncontinue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new registerTask().execute(true);	
			}
		});
		
		TextView txtFees = (TextView)findViewById(R.id.textFees);
	
		txtFees.setText(clinic_fees);
		
		serviceArray = new ArrayList<InfoRowdata>();
		
		txtDiscount = (TextView)findViewById(R.id.textDiscount);
		TextView txtDiscountPer = (TextView)findViewById(R.id.textDisPer);
		txtDiscountPer.setText(clinic_discount+"%");
		txtDiscount.setText((Double.parseDouble(clinic_fees) * Double.parseDouble(clinic_discount) / 100) + "");
		
		
		txtNetAmount = (TextView)findViewById(R.id.textNetAmount);
		txtTotal = (TextView)findViewById(R.id.textTotal);
		txtTotal.setText(clinic_fees);
		
		txtNetAmount.setText((Double.parseDouble(clinic_fees) - Double.parseDouble(txtDiscount.getText().toString()))+"");
		
		listview = (ListView)findViewById(R.id.listView1);
		adapter = new ServiceChargeAdapter(Appointment2Activity.this,serviceArray,clinic_discount);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
			}
		});
		new serviceLoadTask().execute(true);
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
        common.menuItemClick(Appointment2Activity.this, id);
        return super.onOptionsItemSelected(item);
	}
	
	class registerTask extends AsyncTask<Boolean, Void, String> {
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(Appointment2Activity.this, "", 
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
            HttpPost httppost = new HttpPost(ConstValue.JSON_ADD_APPOINTMENT);
          

            try {
            
            	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            	 double totalAmount = Double.parseDouble(ConstValue.selected_doctor.get("dr_fees"));
            	 for(int i=0;i<serviceArray.size();i++)
                 {
                     if (serviceArray.get(i).isclicked)
                     {
                    	 nameValuePairs.add(new BasicNameValuePair("services[]", serviceArray.get(i).getServiceId() ));
                    	 totalAmount = totalAmount + Double.parseDouble(serviceArray.get(i).getAmount());
                     }
                 }
            	 nameValuePairs.add(new BasicNameValuePair("clinic_id", clinic_id));
            	 nameValuePairs.add(new BasicNameValuePair("dr_id", dr_id));
            	 nameValuePairs.add(new BasicNameValuePair("app_date", app_date));
            	 nameValuePairs.add(new BasicNameValuePair("time", time));
            	 nameValuePairs.add(new BasicNameValuePair("day", day));
            	 nameValuePairs.add(new BasicNameValuePair("phone", settings.getString("user_phone","")));
            	 nameValuePairs.add(new BasicNameValuePair("description", textDesc.getText().toString()));
            	 nameValuePairs.add(new BasicNameValuePair("user_id", settings.getString("user_id", "")));
            	 
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
	        			JSONObject data = jObj.getJSONObject("data");
	        			if (!data.getString("app_id").equalsIgnoreCase("")) {
	        				settings.edit().putString("app_id", data.getString("app_id")).commit();
	        				settings.edit().putString("reg_id", data.getString("reg_id")).commit();
	        				settings.edit().putString("totalamount", String.valueOf(totalAmount)).commit();
	        				settings.edit().putString("app_date", app_date).commit();
	        				settings.edit().putString("app_time", time).commit();
		                   
		                    
						}
	        			
        			}else{
        				responseString = jObj.getString("data"); 
        			}
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }
 
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }catch (JSONException e) {
    			Log.e("JSON Parser", "Error parsing data " + e.toString());
    		}
 
            return responseString;
			
			
		}
		

		@Override
		protected void onPostExecute(String result) {
			if(result!=null){
				Toast.makeText(getApplicationContext(), "Error :"+result, Toast.LENGTH_LONG).show();
				
			}else{
				try {
					
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
				
				Intent intent = new Intent(Appointment2Activity.this,ConfirmActivity.class);
				
				startActivity(intent);
				
			}
			dialog.dismiss();
		}
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
		}
		
	}


	
	public class serviceLoadTask extends AsyncTask<Boolean, Void, ArrayList<HashMap<String, String>>> {

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
				settings.edit().putString(ConstValue.PREF_SERV_CHARGE+clinic_id,ObjectSerializer.serialize(serviceArray)).commit();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			adapter.notifyDataSetChanged();
			
			ViewGroup.LayoutParams params = listview.getLayoutParams();
			float d = getApplicationContext().getResources().getDisplayMetrics().density;
	        params.height = (int)(serviceArray.size() * d * 45);
	        listview.setLayoutParams(params);
	        listview.requestLayout();
	        
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
					json = jParser.getJSONFromUrl(ConstValue.JSON_GET_SERVICE_CHARGES+"&cl_id="+clinic_id);
					if (json.has("data")) {
						
					
					if(json.get("data") instanceof JSONArray){
						
					JSONArray menus = json.getJSONArray("data");
					if(menus!=null)
					{
						serviceArray.clear();
						for (int i = 0; i < menus.length(); i++) {
							JSONObject d = menus.getJSONObject(i);

							serviceArray.add(new InfoRowdata(false, i, d.getString("id"), d.getString("charge"), d.getString("service")));
						}
					}	
					
					}
					
					}
				}else
				{
					Toast.makeText(getApplicationContext(), "S'il vous plaît connecter mobile avec Internet ", Toast.LENGTH_LONG).show();
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
