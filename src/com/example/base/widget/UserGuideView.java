package com.example.base.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.base.R;
import com.example.base.utils.UiUtils;

/**
 * 用户向导view
 * @author xujh
 *
 */
public class UserGuideView extends RelativeLayout implements OnPageChangeListener, View.OnClickListener{

	private ViewPager mPager;
	private LinearLayout mNodes;
	private int mlastP;
	private View mClickView; //接收用户事件的view
	private int res[] = { R.drawable.user_guide_2, R.drawable.user_guide_3 };
	
	public UserGuideView(Context context) {
		super(context);
	}
	
	public UserGuideView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public UserGuideView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public static UserGuideView createGuideView(Context context){
		LayoutInflater inflater = LayoutInflater.from(context);
		UserGuideView mUserGuideView = (UserGuideView) inflater.inflate(R.layout.user_guide, null);
		mUserGuideView.init(context);
		return mUserGuideView;
	}
	
	private void init(Context context){
		mPager = (ViewPager) findViewById(R.id.viewpager);
		List<View> data = getData(context);
		mPager.setAdapter(new UserGuideAdapter(data));
		mPager.setCurrentItem(0, false);
		mPager.setOnPageChangeListener(this);
		
		mNodes = (LinearLayout) findViewById(R.id.userguide_nodes);
		mlastP = 0;
		for(int i=0; i< data.size(); i++){
			ImageView image = new ImageView(context);
			if( 0 == i){
				image.setBackgroundResource(R.drawable.spot_);
			}else{
				image.setBackgroundResource(R.drawable.spot);
			}
			mNodes.addView(image);
			LinearLayout.LayoutParams parmas = (android.widget.LinearLayout.LayoutParams) image.getLayoutParams();
			parmas.setMargins(4, 0, 4, 0);
			image.setLayoutParams(parmas);
		}
	}
	
	private List<View> getData(Context context) {
		List<View> ret = new ArrayList<View>();
		
		int i = 0;
		for (; i < (res.length - 1); i++) {
			ImageView image = new ImageView(context);
			image.setBackgroundResource(res[i]);
			ret.add(image);
			image.setOnClickListener(this);
		}
		//在此添加最后一个view，
		RelativeLayout lastView = new RelativeLayout(context);
		ImageView lastImage = new ImageView(context);
		lastView.addView(lastImage, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		lastView.setBackgroundResource(res[i]);
		int px = UiUtils.dp2px(getContext(), 80.0f);
		lastView.setPadding(0, 0, 0, px);
		mClickView = new ImageButton(context);
		mClickView.setBackgroundResource(R.drawable.start);
		RelativeLayout.LayoutParams buttonLayoutParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		buttonLayoutParam.alignWithParent = true;
		buttonLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		buttonLayoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
		buttonLayoutParam.bottomMargin = UiUtils.dp2px(context, 50.0f);
		lastView.addView(mClickView, buttonLayoutParam);
		ret.add(lastView);

		return ret;
	}

	public void setOnClickListener(OnClickListener listener){
		mClickView.setOnClickListener(listener);
	}
	
	@Override
	public void onPageSelected(int position) {
		View v = mNodes.getChildAt(position);
		v.setBackgroundResource(R.drawable.spot_);
		v = mNodes.getChildAt(mlastP );
		v.setBackgroundResource(R.drawable.spot);
		mlastP = position;
	}
	
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}
	
	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onClick(View v) {
		int index = mPager.getCurrentItem();
		int nextIndex = index+1;
		nextIndex = nextIndex < res.length ? nextIndex : res.length - 1;
		mPager.setCurrentItem(nextIndex);
	}
}
