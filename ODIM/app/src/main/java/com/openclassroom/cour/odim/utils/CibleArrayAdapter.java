package com.openclassroom.cour.odim.utils;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.openclassroom.cour.odim.Cible;
import com.openclassroom.cour.odim.R;

public class CibleArrayAdapter extends ArrayAdapter<Cible>{
	private ArrayList<Cible> cibleList;
	private final Context ctx;

	public CibleArrayAdapter(Context context, int textViewResourceId,ArrayList<Cible> cibleList) {
		super(context, textViewResourceId,cibleList);
		this.ctx = context;
		this.cibleList = new ArrayList<Cible>();
		this.cibleList = cibleList;	
	}
	
	private class ViewHolder {
		TextView phonenumber;
		TextView coords;
		CheckBox name;
	}	
	
	public ArrayList<Cible> getCibleList() {
		return cibleList;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

 		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.cible_info, null);

			holder = new ViewHolder();
			holder.phonenumber = convertView
					.findViewById(R.id.phoneNumber);
			holder.name = convertView
					.findViewById(R.id.checkBox1);
			holder.coords = convertView
					.findViewById(R.id.tvCoords);			
			convertView.setTag(holder);

			holder.name.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					Cible cible = (Cible) cb.getTag();
					cible.setSelected(cb.isChecked());
				}
			});
		} else {
			holder = (ViewHolder) convertView.getTag();
		}		
		
 		Cible cible = cibleList.get(position);
		holder.phonenumber.setText(" (" + Html.fromHtml(cible.getMsg()) + ")");
		holder.name.setText(cible.getPhoneNumber());
		holder.name.setChecked(cible.isSelected());
		holder.name.setTag(cible);
		if (cible.getLocation()!=null) {
			holder.coords.setText(cible.longLat());
		}else {
			holder.coords.setText(cible.getDateStr());
		}
		return convertView;		
	}

}
