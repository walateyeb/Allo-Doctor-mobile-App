package adapters;

import java.util.ArrayList;

import com.application.InfoRowdata;
import com.drapp.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ServiceChargeAdapter extends BaseAdapter implements ImageGetter {
	
	
	Activity activity;
	private ArrayList<InfoRowdata> postItems;
	public SharedPreferences settings;
	public final String PREFS_NAME = "Magazine";
	Double cLat,cLog;
	DisplayImageOptions options;
	ImageLoaderConfiguration imgconfig; 
	int count = 0;
	TextView txtTotal,txtDiscount,txtNetAmount, txtFees;
	String discount;
	public ServiceChargeAdapter(Activity context, ArrayList<InfoRowdata> arraylist,String Discount){
		this.activity = context;
		discount = Discount;
		txtTotal  = (TextView)activity.findViewById(R.id.textTotal);
		txtDiscount = (TextView)activity.findViewById(R.id.textDiscount);
		txtNetAmount = (TextView)activity.findViewById(R.id.textNetAmount);
		txtFees = (TextView)activity.findViewById(R.id.textFees);
		postItems = arraylist;
		settings = context.getSharedPreferences(PREFS_NAME, 0);		
		
	}

	@Override
	public int getCount() {
		return postItems.size();
	}
	@Override
	public Object getItem(int position) {		
		return postItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
	            LayoutInflater mInflater = (LayoutInflater)
	            		activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	            convertView = mInflater.inflate(R.layout.row_service_charge, null);	
	           
	     
	        }
		
			final InfoRowdata inforow = postItems.get(position) ;
		
			TextView txtPrice = (TextView)convertView.findViewById(R.id.textView1);
			txtPrice.setText(inforow.getAmount());
			
			final CheckBox chbox = (CheckBox)convertView.findViewById(R.id.checkBox1);
			chbox.setText(inforow.getServiceName());
			chbox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					 inforow.setChecked(chbox.isChecked());
					 addTotalAmount();
				}
			});
			
        return convertView;
	}
	public void addTotalAmount(){
		double totalAmount = Double.parseDouble(txtFees.getText().toString());
		for (int i = 0; i < postItems.size(); i++) {
			InfoRowdata inforow = postItems.get(i) ;
			if(inforow.isChecked()){
				totalAmount = totalAmount + Double.parseDouble(inforow.getAmount());
			}
		}
		txtTotal.setText(""+totalAmount);
		Double disc = Double.parseDouble(discount) * totalAmount / 100;
		Double netAmount = totalAmount - disc ;
		txtNetAmount.setText(""+netAmount);
		txtDiscount.setText(""+disc);
		
	}
	@Override
	public Drawable getDrawable(String source) {
		// TODO Auto-generated method stub
		return null;
	}

}

