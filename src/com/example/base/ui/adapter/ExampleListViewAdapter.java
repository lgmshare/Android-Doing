package com.example.base.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.base.R;

public class ExampleListViewAdapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<String> mList;
	private Holder holder;
	
	public ExampleListViewAdapter(Context context, ArrayList<String> list){
		this.mContext = context;
		this.mList = list;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int i) {
		return mList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		if (view == null) {
			holder = new Holder();
			view = LayoutInflater.from(mContext).inflate(R.layout.activity_main, null);
			view.setTag(holder);
		}else {
			holder = (Holder) view.getTag();
		}
		
		
		return view;
	}

	private class Holder {
	}
}
