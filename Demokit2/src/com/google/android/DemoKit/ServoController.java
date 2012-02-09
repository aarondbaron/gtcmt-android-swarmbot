package com.google.android.DemoKit;

import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ServoController implements Slider.SliderPositionListener {
	private final int mServoNumber;
	private final byte mCommandTarget;
	private TextView mLabel;
	private Slider mSlider;
	private DemoKitActivity mActivity;
	private Handler handler;
	
	public String t = "";
	
	//BoeBotController bbc;

	public ServoController(DemoKitActivity activity, int servoNumber) {
		mActivity = activity;
		mServoNumber = servoNumber;
		mCommandTarget = (byte) (servoNumber - 1 + 0x10);
		t="";
		handler = new Handler();
	}

	public void attachToView(ViewGroup targetView) {
		mLabel = (TextView) targetView.getChildAt(0);
		SpannableStringBuilder ssb = new SpannableStringBuilder("Servo");
		ssb.append(String.valueOf(mServoNumber));
		ssb.setSpan(new SubscriptSpan(), 5, 6, 0);
		ssb.setSpan(new RelativeSizeSpan(0.7f), 5, 6, 0);
		mLabel.setText(ssb);
		mSlider = (Slider) targetView.getChildAt(1);
		//targetView.
		mSlider.setPositionListener(this);
	}

	public void onPositionChange(double value) {
		int rng=25;
		
		
		float f= mActivity.map((float)value, 0, 1, 128-rng, 128+rng);
		t="" +f;
		
		byte v= (byte) f;
		
		
		
		
		
		//byte v = (byte) (value * 255);
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget, v);
		
		if(mCommandTarget==mActivity.bbc.mCommandTarget1)
		{
			mActivity.bbc.rbyte=(int)f;
		}
		if(mCommandTarget==mActivity.bbc.mCommandTarget2)
		{
			mActivity.bbc.lbyte=(int)f;
		}
		
		//mActivity.bbc.mHandler.postDelayed(mActivity.bbc.mUpdateUITimerTask, 500);
		
	}

}
