package adapters;

import imgLoader.AnimateFirstDisplayListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;








import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.drapp.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import appconfig.ConstValue;

public class DrListAdapter extends BaseAdapter implements ImageGetter {
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	private Context context;
	ArrayList<HashMap<String, String>> listarray;
	public SharedPreferences settings;
	Double cLat,cLog;
	DisplayImageOptions options;
	ImageLoaderConfiguration imgconfig; 
	int count = 0;
	
	public DrListAdapter(Context context,ArrayList<HashMap<String, String>> jsonarray){
		this.context = context;
		
		File cacheDir = StorageUtils.getCacheDirectory(context);
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
		
		imgconfig = new ImageLoaderConfiguration.Builder(context)
		.build();
		ImageLoader.getInstance().init(imgconfig);
			
		listarray = jsonarray;
		settings = context.getSharedPreferences(ConstValue.MAIN_PREF, 0);		
		
	}

	@Override
	public int getCount() {
		return listarray.size();
	}
	@Override
	public Object getItem(int position) {		
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
	            LayoutInflater mInflater = (LayoutInflater)
	            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	            convertView = mInflater.inflate(R.layout.row_dr_list, null);	
	           
	     
	        }
		
			try {
				HashMap<String, String> map = listarray.get(position);
				
				TextView txttitle = (TextView)convertView.findViewById(R.id.textView1);
				txttitle.setText(map.get("dr_name"));
				RatingBar ratingbar = (RatingBar)convertView.findViewById(R.id.ratingBar1);
				ratingbar.setRating(0);
				if(!map.get("avg").equalsIgnoreCase("") && map.get("avg")!=null  && !map.get("avg").equalsIgnoreCase("null")){
				ratingbar.setRating(Float.parseFloat(map.get("avg")));
				}
				
				TextView txtfee = (TextView)convertView.findViewById(R.id.TextFee);
				txtfee.setVisibility(View.GONE);
				
				TextView txtaddress = (TextView)convertView.findViewById(R.id.textAddress);
				txtaddress.setVisibility(View.GONE);
				TextView txtlocation = (TextView)convertView.findViewById(R.id.textLocation);
				txtlocation.setVisibility(View.GONE);
				
				if(!map.get("clinic").toString().equalsIgnoreCase("")){
					JSONArray clinics = new JSONArray(map.get("clinic").toString());
					if(clinics.length() > 0){
						txtaddress.setText(clinics.getJSONObject(0).getString("cl_address"));
						txtaddress.setVisibility(View.VISIBLE);
						
						if(!clinics.getJSONObject(0).getString("cl_fees").equalsIgnoreCase("")){
							txtfee.setText(clinics.getJSONObject(0).getString("cl_fees")+" DT.");
							txtfee.setVisibility(View.VISIBLE);
						}
						if(!clinics.getJSONObject(0).getString("cl_location").equalsIgnoreCase("")){
							txtlocation.setText(clinics.getJSONObject(0).getString("cl_location"));
							txtlocation.setVisibility(View.VISIBLE);
						}
						
						LinearLayout galleryView = (LinearLayout)convertView.findViewById(R.id.imagegallery);
						galleryView.removeAllViews();
						if(clinics.getJSONObject(0).get("photos") instanceof JSONArray){
							JSONArray photos = clinics.getJSONObject(0).getJSONArray("photos");
							if(photos.length() > 0){
								int lenth = photos.length();
								if(lenth > 3)
									lenth = 3;
								for (int i = 0; i < lenth; i++) {
									JSONObject jo = photos.getJSONObject(i);
									ImageView imgGal = new ImageView(context);
									ImageLoader.getInstance().displayImage(jo.getString("image_path"), imgGal, options, animateFirstListener);
									LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(45, 45);
									imgGal.setLayoutParams(layoutParams);
									imgGal.setBackgroundResource(R.drawable.xml_frame_border);
									galleryView.addView(imgGal);
								}
								
							}
						}
					}
				}
				
				
				TextView txtExp = (TextView)convertView.findViewById(R.id.textExp);
				txtExp.setText(map.get("dr_experiance")+" année exp.");
				
				ImageView imgIcon = (ImageView)convertView.findViewById(R.id.imageView1);
				ImageLoader.getInstance().displayImage(map.get("cover_path"), imgIcon, options, animateFirstListener);
		    	
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
        return convertView;
	}

	@Override
	public Drawable getDrawable(String source) {
		// TODO Auto-generated method stub
		return null;
	}

}

