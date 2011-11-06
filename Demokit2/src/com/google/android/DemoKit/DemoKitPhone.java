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
	TextView mFaceLabel;
	
	LinearLayout mInputContainer;
	LinearLayout mOutputContainer;
	LinearLayout mCameraContainer;
	LinearLayout mRobotFaceContainer;
	
	Drawable mFocusedTabImage;
	Drawable mNormalTabImage;
	OutputController mOutputController;
	FaceController mfc;

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
		//client = new ClientCode(this);
	}

	protected void showControls() {
		super.showControls();

		mOutputController = new OutputController(this, false);
		mOutputController.accessoryAttached();
		mInputLabel = (TextView) findViewById(R.id.inputLabel);
		mOutputLabel = (TextView) findViewById(R.id.outputLabel);
		mCamLabel = (TextView) findViewById(R.id.cameraLabel);
		mFaceLabel = (TextView) findViewById(R.id.robotFaceLabel);
		//
		
		mInputContainer = (LinearLayout) findViewById(R.id.inputContainer);
		mOutputContainer = (LinearLayout) findViewById(R.id.outputContainer);
		mCameraContainer = (LinearLayout) findViewById(R.id.cameraContainer);
		mRobotFaceContainer = (LinearLayout) findViewById(R.id.rFaceContainer);
		
		mInputLabel.setOnClickListener(this);
		mOutputLabel.setOnClickListener(this);
		mCamLabel.setOnClickListener(this);
		mFaceLabel.setOnClickListener(this);

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
			
			mRobotFaceContainer.setVisibility(View.GONE);
			mFaceLabel.setBackgroundDrawable(mNormalTabImage);
		} else {
			mInputContainer.setVisibility(View.GONE);
			mInputLabel.setBackgroundDrawable(mNormalTabImage);
			mOutputContainer.setVisibility(View.VISIBLE);
			mOutputLabel.setBackgroundDrawable(mFocusedTabImage);
			
			mCameraContainer.setVisibility(View.GONE);
			mCamLabel.setBackgroundDrawable(mNormalTabImage);
			
			mRobotFaceContainer.setVisibility(View.GONE);
			mFaceLabel.setBackgroundDrawable(mNormalTabImage);
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
			
			mRobotFaceContainer.setVisibility(View.GONE);
			mFaceLabel.setBackgroundDrawable(mNormalTabImage);
			
			return;
		} 
		if(selection==R.id.outputLabel) {
			mInputContainer.setVisibility(View.GONE);
			mInputLabel.setBackgroundDrawable(mNormalTabImage);
			mOutputContainer.setVisibility(View.VISIBLE);
			mOutputLabel.setBackgroundDrawable(mFocusedTabImage);
			
			mCameraContainer.setVisibility(View.GONE);
			mCamLabel.setBackgroundDrawable(mNormalTabImage);
			
			mRobotFaceContainer.setVisibility(View.GONE);
			mFaceLabel.setBackgroundDrawable(mNormalTabImage);
			
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
			
			mRobotFaceContainer.setVisibility(View.GONE);
			mFaceLabel.setBackgroundDrawable(mNormalTabImage);
			
			//mOutputController.
			
			return;
			
		}
		
		if(selection ==R.id.robotFaceLabel)
		{
			
			mRobotFaceContainer.setVisibility(View.VISIBLE);
			mFaceLabel.setBackgroundDrawable(mFocusedTabImage);
			
			mCameraContainer.setVisibility(View.GONE);
			mCamLabel.setBackgroundDrawable(mNormalTabImage);			
			mInputContainer.setVisibility(View.GONE);
			mInputLabel.setBackgroundDrawable(mNormalTabImage);
			mOutputContainer.setVisibility(View.GONE);
			mOutputLabel.setBackgroundDrawable(mNormalTabImage);
			
			//mOutputController.
			
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