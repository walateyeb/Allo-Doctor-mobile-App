package com.application;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import adapters.TimeTableAdapter;
import android.R.string;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import appconfig.ConstValue;
import com.drapp.R;

public class AppointmentActivity extends FragmentActivity  {
	private static final int NUM_PAGES = 10;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	JSONObject j_clinic;
	TextView textDateTitle;
	String selected_date;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment);
		
		textDateTitle = (TextView)findViewById(R.id.textDateTitle);
		
		
		j_clinic = ConstValue.selected_clinic;
		mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        
        changeDateTitle(0);
        mPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				changeDateTitle(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
       
	}
	@Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
          
            // retour au page précedente
            super.onBackPressed();
        } else {
          
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return new ScreenSlidePageFragment(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return NUM_PAGES;
		}
       
    }
	public String changeDateTitle(int arg0){
		 Calendar c = Calendar.getInstance();
		 c.add(Calendar.DATE, arg0);
		 String day_of_week =c.getDisplayName(Calendar.DAY_OF_WEEK, 0, Locale.UK);
		 day_of_week = day_of_week.substring(0,3);
		 SimpleDateFormat sdf1 = new SimpleDateFormat("MM-d-yyyy");
		 SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-d");
		 String output = sdf1.format(c.getTime());
		 selected_date = sdf2.format(c.getTime());
		 textDateTitle.setText(output +" ");
		 return day_of_week;
	}
	public class ScreenSlidePageFragment extends Fragment {
		
		ArrayList<HashMap<String, String>> time_table;
		TimeTableAdapter adaptertime;
		public ScreenSlidePageFragment(int arg0) {
			// TODO Auto-generated constructor stub
			 
			 time_table = new ArrayList<HashMap<String,String>>();
			 try {
				if(j_clinic.get("times") instanceof JSONArray){
						final JSONArray times_Array = j_clinic.getJSONArray("times");
						if(times_Array.length() > 0){
							for (int i = 0; i < times_Array.length(); i++) {
								JSONObject o = times_Array.getJSONObject(i);
								if(o.getString("day").equalsIgnoreCase(changeDateTitle(arg0))){
									HashMap<String, String> map = new HashMap<String, String>();
									map.put("day", o.getString("day"));
									map.put("during", o.getString("during"));
									time_table.add(map);
								}
							}
						}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 
			 
		}

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        ViewGroup rootView = (ViewGroup) inflater.inflate(
	                R.layout.row_timetable_slide, container, false);
	        
	        ListView listview = (ListView)rootView.findViewById(R.id.listView1);
	        adaptertime = new TimeTableAdapter(getApplicationContext(), time_table);
	        listview.setAdapter(adaptertime);
	       listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AppointmentActivity.this,Appointment2Activity.class);
				try {
					intent.putExtra("clinic_id", j_clinic.getString("cl_id"));
					intent.putExtra("dr_id", j_clinic.getString("dr_id"));
					intent.putExtra("cl_fees", j_clinic.getString("cl_fees"));
					intent.putExtra("cl_discount", j_clinic.getString("cl_discount"));
					intent.putExtra("app_date", selected_date);
					intent.putExtra("day",time_table.get(position).get("day") );
					intent.putExtra("time",time_table.get(position).get("during") );
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(intent);
			}
		});
	        return rootView;
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
        common.menuItemClick(AppointmentActivity.this, id);
        return super.onOptionsItemSelected(item);
	}
}
