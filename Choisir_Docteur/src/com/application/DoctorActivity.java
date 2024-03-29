package com.application;

import java.io.File;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import util.ConnectionDetector;
import imgLoader.AnimateFirstDisplayListener;
import com.drapp.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.pkmmte.circularimageview.CircularImageView;

import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import appconfig.ConstValue;

public class DoctorActivity extends ActionBarActivity {
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	DisplayImageOptions options;
	ImageLoaderConfiguration imgconfig; 
	
	HashMap<String, String> j_doctor;
	public SharedPreferences settings;
	public ConnectionDetector cd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor);
	
		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);
		cd=new ConnectionDetector(this);
		
		File cacheDir = StorageUtils.getCacheDirectory(this);
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new SimpleBitmapDisplayer())
		.imageScaleType(ImageScaleType.NONE)
		.build();
		
		imgconfig = new ImageLoaderConfiguration.Builder(this)
		.build();
		ImageLoader.getInstance().init(imgconfig);
		
		j_doctor = ConstValue.selected_doctor;
		
		TextView txtName = (TextView)findViewById(R.id.textDrName);
		TextView txtDegree = (TextView)findViewById(R.id.textDrDegree);
		TextView txtExpr = (TextView)findViewById(R.id.textDrExp);
		TextView txtFees = (TextView)findViewById(R.id.textDrFee);
		TextView txtDesignation = (TextView)findViewById(R.id.textDrDesc);
		TextView txtSpeciality = (TextView)findViewById(R.id.textDrSpeciality);
		RatingBar ratingbar = (RatingBar)findViewById(R.id.ratingBar1);
		ratingbar.setRating(0);
		ImageView imageBanner = (ImageView)findViewById(R.id.imageBanner);
		CircularImageView imageCover = (CircularImageView)findViewById(R.id.imageView1);
		try {
			ImageLoader.getInstance().displayImage(j_doctor.get("banner_path"), imageBanner, options, animateFirstListener);
			ImageLoader.getInstance().displayImage(j_doctor.get("cover_path"), imageCover, options, animateFirstListener);
			txtName.setText(j_doctor.get("dr_name"));
			txtDegree.setText(j_doctor.get("dr_degree"));
			txtDesignation.setText(j_doctor.get("dr_designation"));
			if(!j_doctor.get("avg").equalsIgnoreCase("") && !j_doctor.get("avg").equalsIgnoreCase("null") ){
			ratingbar.setRating(Float.parseFloat((j_doctor.get("avg"))));
			}
			txtExpr.setText(j_doctor.get("dr_experiance")+" Ann�e. Exp.");
			txtFees.setText(j_doctor.get("dr_fees")+" DT.");
			txtSpeciality.setText(j_doctor.get("dr_speciality"));
			
			LinearLayout clinics_list_view = (LinearLayout)findViewById(R.id.list_clinics);
			
			if(!j_doctor.get("clinic").toString().equalsIgnoreCase("")){
				final JSONArray clinics = new JSONArray(j_doctor.get("clinic").toString());
				if(clinics.length() > 0){
					for (int i = 0; i < clinics.length(); i++) {
						JSONObject clinic = clinics.getJSONObject(i);
						LayoutInflater inflator = LayoutInflater.from(this);
				        View v = inflator.inflate(R.layout.row_clinics_list, null);
				        
				        TextView textClinic = (TextView)v.findViewById(R.id.textClinicName);
				        textClinic.setText(clinic.getString("cl_name"));
				        TextView textAddress = (TextView)v.findViewById(R.id.textClinicAddress);
				        textAddress.setText(clinic.getString("cl_address"));
				        TextView textLocation = (TextView)v.findViewById(R.id.textClinicLocation);
				        textLocation.setText(clinic.getString("cl_location"));
				        	        
				        LinearLayout galleryView = (LinearLayout)v.findViewById(R.id.layoutPhotos);
						if(clinics.getJSONObject(0).get("photos") instanceof JSONArray){
							JSONArray photos = clinic.getJSONArray("photos");
							if(photos.length() > 0){
								int lenth = photos.length();
								for (int j = 0; j < lenth; j++) {
									JSONObject jo = photos.getJSONObject(j);
									ImageView imgGal = new ImageView(this);
									ImageLoader.getInstance().displayImage(jo.getString("image_path"), imgGal, options, animateFirstListener);
									LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(45, 45);
									imgGal.setLayoutParams(layoutParams);
									imgGal.setBackgroundResource(R.drawable.xml_frame_border);
									galleryView.addView(imgGal);
								}
								
							}
						}
				        
						Button btnReview = (Button)v.findViewById(R.id.buttonReview);
						btnReview.setContentDescription(i+"");
						btnReview.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String contentid = (String) v.getContentDescription();
								try {
									ConstValue.selected_clinic = clinics.getJSONObject(Integer.parseInt(contentid));
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Intent intent = new Intent(DoctorActivity.this,ReviewsActivity.class);
								startActivity(intent);
							}
						});
						
						Button btnmap = (Button)v.findViewById(R.id.buttonLocation);
						btnmap.setContentDescription(i+"");
						btnmap.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String contentid = (String) v.getContentDescription();
								try {
									ConstValue.selected_clinic = clinics.getJSONObject(Integer.parseInt(contentid));
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Intent intent = new Intent(DoctorActivity.this,MapActivity.class);
								startActivity(intent);
							}
						});
						
						Button btnBook = (Button)v.findViewById(R.id.buttonBook);
						btnBook.setContentDescription(i+"");
						btnBook.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(settings.getString("user_id", "").equalsIgnoreCase("")){
									Intent intent = new Intent(DoctorActivity.this,LoginActivity.class);
									startActivity(intent);
								}else{
									if(!settings.getBoolean("mobile_confirm", false)){
										Intent intent = new Intent(DoctorActivity.this,ProfileActivity.class);
										startActivity(intent);
									}else{
										String contentid = (String) v.getContentDescription();
										try {
											ConstValue.selected_clinic = clinics.getJSONObject(Integer.parseInt(contentid));
										} catch (NumberFormatException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										Intent intent = new Intent(DoctorActivity.this,AppointmentActivity.class);
										startActivity(intent);
									}
								}
							}
						});
						
				        clinics_list_view.addView(v);
					}
				}
			}
			
	        
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
        common.menuItemClick(DoctorActivity.this, id);
        return super.onOptionsItemSelected(item);
	}
}
