package com.example.base.widget;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 用户使用引导适配器
 * @ClassName: UserGuideAdapter 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author lim
 * @date 2013-8-13 上午03:52:58 
 *
 */
public class UserGuideAdapter extends PagerAdapter {
	
	private List<View> mListViews;
	private boolean mIsModify = false;

	public UserGuideAdapter(List<View> list){
		this.mListViews = list;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View v = mListViews.get(position);
		container.addView(v);
		return v;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View v;
		if(null == object){
			v = mListViews.get(position);
		}else{
			v = (View) object;
		}
		container.removeView(v);
	}
	
	@Override
	public int getItemPosition(Object object) {
		if(mListViews.contains(object)){
			if(mIsModify){
				int i = 0;
				for(; i<mListViews.size(); i++){
					if(object == mListViews.get(i)){
						break;
					}
				}
				return i;
			} else {
				return PagerAdapter.POSITION_UNCHANGED;
			}
		}else{
			return PagerAdapter.POSITION_NONE;
		}
	}
	
	@Override
	public void startUpdate(ViewGroup container) {
		//TODO:page切换开始的操作
		super.startUpdate(container);
	}
	
	@Override
	public void finishUpdate(ViewGroup container) {
		//TODO:page切换结束的操作
		super.finishUpdate(container);
	}
	
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		// TODO 设置主要的Item
		super.setPrimaryItem(container, position, object);
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		if (arg0 == arg1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getCount() {
		int ret = mListViews.size();
		return ret;
	}
	
}
