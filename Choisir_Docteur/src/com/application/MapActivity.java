package com.application;

import org.json.JSONException;
import org.json.JSONObject;
import com.drapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import appconfig.ConstValue;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback  {
	JSONObject clinic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		clinic = ConstValue.selected_clinic;
		MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);
        
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
        common.menuItemClick(MapActivity.this, id);
        return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMapReady(GoogleMap map) {
		// TODO Auto-generated method stub
		 LatLng sydney = null;
		try {
			sydney = new LatLng(Double.parseDouble(clinic.getString("cl_latitude")),Double.parseDouble(clinic.getString("cl_longitude")));
			
			if(sydney!=null){
		        map.setMyLocationEnabled(true);
		        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

		        map.addMarker(new MarkerOptions()
		                .title(clinic.getString("cl_name"))
		                .snippet(clinic.getString("cl_address"))
		                .position(sydney));
			}
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
