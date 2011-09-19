package com.google.android.DemoKit;



import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DemoKitPhone extends BaseActivity implements OnClickListener {
	static final String TAG = "DemoKitPhone";
	/** Called when the activity is first created. */
	TextView mInputLabel;
	TextView mOutputLabel;
	TextView mCamLabel;
	LinearLayout mInputContainer;
	LinearLayout mOutputContainer;
	LinearLayout mCameraContainer;
	
	Drawable mFocusedTabImage;
	Drawable mNormalTabImage;
	OutputController mOutputController;

	@Override
	protected void hideControls() {
		super.hideControls();
		mOutputController = null;
	}

	public void onCreate(Bundle savedInstanceState) {
		mFocusedTabImage = getResources().getDrawable(
				R.drawable.tab_focused_holo_dark);
		mNormalTabImage = getResources().getDrawable(
				R.drawable.tab_normal_holo_dark);
		super.onCreate(savedInstanceState);
		
		
		//setContentView(new FdView(this));
	}

	protected void showControls() {
		super.showControls();

		mOutputController = new OutputController(this, false);
		mOutputController.accessoryAttached();
		mInputLabel = (TextView) findViewById(R.id.inputLabel);
		mOutputLabel = (TextView) findViewById(R.id.outputLabel);
		mCamLabel = (TextView) findViewById(R.id.cameraLabel);
		
		mInputContainer = (LinearLayout) findViewById(R.id.inputContainer);
		mOutputContainer = (LinearLayout) findViewById(R.id.outputContainer);
		mCameraContainer = (LinearLayout) findViewById(R.id.cameraContainer);
		
		mInputLabel.setOnClickListener(this);
		mOutputLabel.setOnClickListener(this);
		mCamLabel.setOnClickListener(this);

		showTabContents(false);
	}

	void showTabContents(Boolean showInput) {
		if (showInput) {
			mInputContainer.setVisibility(View.VISIBLE);
			mInputLabel.setBackgroundDrawable(mFocusedTabImage);
			mOutputContainer.setVisibility(View.GONE);
			mOutputLabel.setBackgroundDrawable(mNormalTabImage);
			
			mCameraContainer.setVisibility(View.GONE);
			mCamLabel.setBackgroundDrawable(mNormalTabImage);
		} else {
			mInputContainer.setVisibility(View.GONE);
			mInputLabel.setBackgroundDrawable(mNormalTabImage);
			mOutputContainer.setVisibility(View.VISIBLE);
			mOutputLabel.setBackgroundDrawable(mFocusedTabImage);
			
			mCameraContainer.setVisibility(View.GONE);
			mCamLabel.setBackgroundDrawable(mNormalTabImage);
		}
	}
	
	void showTabContents(int selection) {
		if (selection==R.id.inputLabel) {
			mInputContainer.setVisibility(View.VISIBLE);
			mInputLabel.setBackgroundDrawable(mFocusedTabImage);
			mOutputContainer.setVisibility(View.GONE);
			mOutputLabel.setBackgroundDrawable(mNormalTabImage);
			
			mCameraContainer.setVisibility(View.GONE);
			mCamLabel.setBackgroundDrawable(mNormalTabImage);
			
			return;
		} 
		if(selection==R.id.outputLabel) {
			mInputContainer.setVisibility(View.GONE);
			mInputLabel.setBackgroundDrawable(mNormalTabImage);
			mOutputContainer.setVisibility(View.VISIBLE);
			mOutputLabel.setBackgroundDrawable(mFocusedTabImage);
			
			mCameraContainer.setVisibility(View.GONE);
			mCamLabel.setBackgroundDrawable(mNormalTabImage);
			
			return;
		}
		if(selection ==R.id.cameraLabel)
		{
			mCameraContainer.setVisibility(View.VISIBLE);
			mCamLabel.setBackgroundDrawable(mFocusedTabImage);
			
			mInputContainer.setVisibility(View.GONE);
			mInputLabel.setBackgroundDrawable(mNormalTabImage);
			mOutputContainer.setVisibility(View.GONE);
			mOutputLabel.setBackgroundDrawable(mNormalTabImage);
			
			return;
			
		}
		
		mCameraContainer.setVisibility(View.GONE);
		mCamLabel.setBackgroundDrawable(mNormalTabImage);
		mInputContainer.setVisibility(View.GONE);
		mInputLabel.setBackgroundDrawable(mNormalTabImage);
		mOutputContainer.setVisibility(View.VISIBLE);
		mOutputLabel.setBackgroundDrawable(mFocusedTabImage);
		return;
		
		
		
		
	}

	public void onClick(View v) {
		
		/*
		int vId = v.getId();
		switch (vId) {
		case R.id.inputLabel:
			showTabContents(true);
			break;

		case R.id.outputLabel:
			showTabContents(false);
			break;
			
		case R.id.cameraLabel:
			showTabContents(false);
			break;
		}
		*/
		int vId = v.getId();
		showTabContents(vId);
		
		
		
	}

}