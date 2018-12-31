package com.application;

import imgLoader.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import com.drapp.R;
import com.google.android.gcm.GCMRegistrar;

import util.ConnectionDetector;
import util.ObjectSerializer;
import util.ServerUtilities;
import util.WakeLocker;
import adapters.MainAdapter;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import appconfig.ConstValue;
import static util.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static util.CommonUtilities.EXTRA_MESSAGE;
import static util.CommonUtilities.SENDER_ID;


public class MainActivity extends ActionBarActivity {
	public SharedPreferences settings;
	public ConnectionDetector cd;
	ArrayList<HashMap<String, String>> newsArray;
	MainAdapter adapter;
	
	AsyncTask<Void, Void, Void> mRegisterTask;
	public static String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		 cd=new ConnectionDetector(this);
		 
		 newsArray = new ArrayList<HashMap<String,String>>();
	        try {
	        	newsArray = (ArrayList<HashMap<String,String>>) ObjectSerializer.deserialize(settings.getString(ConstValue.PREFS_MAIN_CAT, ObjectSerializer.serialize(new ArrayList<HashMap<String,String>>())));		
			}catch (IOException e) {
				    e.printStackTrace();
			}
	        new loadNewsTask().execute(true);
	        
	        adapter = new MainAdapter(getApplicationContext(), newsArray);
	        GridView gridview = (GridView)findViewById(R.id.gridView1);
	        gridview.setAdapter(adapter);
	        gridview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					ConstValue.selected_category = newsArray.get(position);
					Intent intent = new Intent(MainActivity.this,DoctorListActivity.class);
					startActivity(intent);
				}

				
			});
	        
	     
	        	email = settings.getString("user_email", "");
	        	registerMeForGCM();
	       
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
				settings.edit().putString(ConstValue.PREFS_MAIN_CAT,ObjectSerializer.serialize(newsArray)).commit();
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
					json = jParser.getJSONFromUrl(ConstValue.JSON_MAINCAT);
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
	  							map2.put("title", d.getString("title"));
	  							map2.put("icon", d.getString("icon"));
	  							map2.put("iconpath", d.getString("iconpath"));
	  							
							newsArray.add(map2);
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
        common.menuItemClick(MainActivity.this, id);
        return super.onOptionsItemSelected(item);
    }
    
    
    //MESSAGERIE GCM
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
           
            WakeLocker.acquire(getApplicationContext());
             
            
             
                     
            Toast.makeText(getApplicationContext(), "Nouveau Message: " + newMessage, Toast.LENGTH_LONG).show();
             
            WakeLocker.release();
        }
    };
     
    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }
    
    public void registerMeForGCM(){
    	

    	GCMRegistrar.checkManifest(this);

    	registerReceiver(mHandleMessageReceiver, new IntentFilter(
    			DISPLAY_MESSAGE_ACTION));

    	
    	final String regId = GCMRegistrar.getRegistrationId(this);

    	if (regId.equals("")) {
    		//L'inscription est pas présent, inscrivez-vous maintenant avec GCM		
    		GCMRegistrar.register(this, SENDER_ID);
    		settings.edit().putBoolean("REGISTER", true).commit();
    	} else {
    		// Le périphérique est déjà enregistré sur GCM
    		if (GCMRegistrar.isRegisteredOnServer(this)) {
    			// passer l'inscription				
    			Toast.makeText(getApplicationContext(), "Déjà inscrit avec GCM", Toast.LENGTH_LONG).show();
    		} else {
    			
    			final Context context = this;
    			mRegisterTask = new AsyncTask<Void, Void, Void>() {

    				@Override
    				protected Void doInBackground(Void... params) {
    					// inscription sur serveur
    					
    					ServerUtilities.register(context, settings.getString("user_email","").toString(), regId);
    					return null;
    				}

    				@Override
    				protected void onPostExecute(Void result) {
    					mRegisterTask = null;
    				}

    			};
    			mRegisterTask.execute(null, null, null);
    		}
    	}
    	
    	

    }
    
}
