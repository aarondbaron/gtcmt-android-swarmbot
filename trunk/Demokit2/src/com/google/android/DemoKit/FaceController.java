package com.google.android.DemoKit;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.content.Context;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;


public class FaceController {
	
	BoeBotController bbc;
	InputController ic;
	OutputController oc;
	private DemoKitActivity mActivity;
	
	RobotFaceView rfv;
	
	
	public FaceController(BoeBotController b, InputController i, OutputController o,DemoKitActivity m)
	{
		bbc=b;
		ic=i;
		oc=o;
		
		mActivity=m;
	}
	
	
	public FaceController(DemoKitActivity mAct)
	{
		mActivity=mAct;
		
		rfv=(RobotFaceView) mActivity.findViewById(R.id.robotFaceView);
	}
	

}
