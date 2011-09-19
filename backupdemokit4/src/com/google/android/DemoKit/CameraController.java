package com.google.android.DemoKit;

import android.view.SurfaceView;
import android.widget.Button;

public class CameraController {

	
	public FdView mview;
	//public SurfaceView sv;
	public DemoKitActivity mActivity;
	
	public CameraController(DemoKitActivity mHostActivity)
	{
		mActivity = mHostActivity;
		//mview = new FdView(mHostActivity);
		
		
	}
	
	public void attachToView() {
		// TODO Auto-generated method stub
		
		//sv=(SurfaceView) mActivity.findViewById(R.id.surfaceView1);
		
		//mview = (FdView) mActivity.findViewById(R.id.fdview);
	}

}
