package gtcmtswarmbot.mobilecontrol;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import gtcmtswarmbot.mobilecontrol.enums.ControllerCode;
import gtcmtswarmbot.mobilecontrol.enums.Mapping;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.Window;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.WindowManager;


import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

public class DemokitMobileControlActivity extends Activity {


	DrawView drawView;

	Client client;

	BeatTimer beatTimer;

	SomeController sc;

	boolean clockwise;

	String prevMode;

	File file;
	FileOutputStream fos;

	long startTime;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);


		//setContentView(R.layout.main);

		beatTimer = new BeatTimer(this);


		sc = new SomeController(this);
		beatTimer.bbc=sc;


		drawView = new DrawView(this,sc);
		setContentView(drawView);
		drawView.requestFocus(); 


		client = new Client(this, sc);


		beatTimer.setRunning(true);
		beatTimer.start();

		Log.d("testing oncreate","oncreate blah lbha");


		/*
		try {
		    File filename = new File(Environment.getExternalStorageDirectory()+"/logfile.log"); 
		    filename.createNewFile(); 
		    String cmd = "logcat -d -f "+filename.getAbsolutePath();
		    Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		String FILENAME = "hello_file";
		String string = "hello world!";

		FileOutputStream fos = null;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_WORLD_READABLE);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			fos.write(string.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		 */
		Time now = new Time();
		now.setToNow();

		String filename = "filename_" + Math.random() + ".txt";
		file = new File(Environment.getExternalStorageDirectory(), filename);

		byte[] data = new String("data to write to file\n").getBytes();
		try {
			fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
			//fos.close();
		} catch (FileNotFoundException e) {
			// handle exception
		} catch (IOException e) {
			// handle exception
		}


		startTime=System.currentTimeMillis();


	}

	@Override
	public void onDestroy()
	{
		drawView.thread.setRunning(false);
		/*
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */



	}


	@Override
	public void finish()
	{
		byte[] data = new String("\nclosing file").getBytes();
		try {

			//fos.write(data);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// handle exception
		} catch (IOException e) {
			// handle exception
		}

		drawView.thread.setRunning(false);
		//finish();
		System.exit(0);
	}

	void writeToFile(String s)
	{


		s +=  "\ttime\t" + (System.currentTimeMillis()-startTime) + "\n" ;
		if(fos==null)
		{
			Log.d("fos is null","fos is null");
		}
		byte[] data = s.getBytes();
		try 
		{
			fos.write(data);
			fos.flush();
			//fos.close();
		} catch (FileNotFoundException e) {
			// handle exception
		} catch (IOException e) {
			// handle exception
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do something on back.

			if(this.drawView.thread.showSequencer)
			{
				this.drawView.thread.showSequencer=false;
			}
			else
			{
				finish();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu sub = menu.addSubMenu(0,1,0, "Music Mappings");
		sub.add("noMapping");
		sub.add("FightSong_Angle");
		sub.add("FightSong_Neighbor");
		sub.add("FightSong_Speed");
		sub.add("broadcastSequence");
		sub.add("angle");
		sub.add("neighbor");
		sub.add("speed");
		sub.add("angle_embellish");
		sub.add("neighbor_embellish");
		sub.add("speed_embellish");
		sub.add("proximity1");


		SubMenu sub2 = menu.addSubMenu(0,1,0, "Movement Behaviors");
		sub2.add("TugMove");
		sub2.add("AvatarMove");
		sub2.add("Orbit");
		sub2.add("Wander");

		sub2.add("ViewCursor");
		sub2.add("Move");
		sub2.add("MoveRelative");

		sub2.add("Breath1");
		sub2.add("Breath2");
		sub2.add("OrbitLine");
		sub2.add("FollowLine");

		sub2.add("Path");
		sub2.add("Separation");
		sub2.add("Alignment");
		sub2.add("Cohesion");

		sub2.add("followAvatar");
		sub2.add("followAvatarInLine");
		sub2.add("evadeAvatar");
		sub2.add("orbitAvatar");
		sub2.add("StopAll");
		//sub2.add("Formation");

		//SubMenu sub3 = sub2.addSubMenu(0,1,0, "formations");//this does not work
		SubMenu sub3 = menu.addSubMenu(0,1,0, "formations");
		sub3.add("circle");
		sub3.add("square");
		sub3.add("horizontal");
		sub3.add("vertical");
		sub3.add("diagonal");
		sub3.add("drawn");

		menu.add("StopAll");
		menu.add("Connect");
		menu.add("Sync");
		menu.add("Inspect");
		/*
		menu.add("ViewCursor");
		menu.add("Move");
		menu.add("Orbit");
		menu.add("Breath1");
		menu.add("Breath2");
		menu.add("OrbitLine");
		menu.add("FollowLine");
		menu.add("Wander");
		menu.add("Separation");
		menu.add("Alignment");
		menu.add("Cohesion");
		 */
		menu.add("Problem?");
		menu.add("ToSongMaker");
		menu.add("Quit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Log.d("item selected",""+item.getTitle());
		this.writeToFile("item\t" + item.getTitle() );

		if (item.getTitle() == "ViewCursor") {
			//showControls();
			this.drawView.thread.arena.cursor.active=!this.drawView.thread.arena.cursor.active;
		} else if (item.getTitle() == "Quit") {
			finish();
			System.exit(0);
		}else if (item.getTitle() == "StopAll") {
			//mActivity.client.sendMessage("controller,"+ 999 + "," + xx + "," + yy) ;
			drawView.mode="StopAll";
			this.client.sendMessage("controller,"+ 999);
		}else if (item.getTitle() == "Connect") {
			this.client.doStuff();
		}else if (item.getTitle() == "Sync") {
			this.client.sendMessage("controller,"+ 1000);
		}else if (item.getTitle() == "Inspect") {
			drawView.mode="Inspect";
		}else if (item.getTitle() == "Move") {
			drawView.mode="Move";
			Log.d("item selected",""+item.getTitle());

		}else if (item.getTitle() == "MoveRelative") {
			drawView.mode="MoveRelative";
			Log.d("item selected",""+item.getTitle());

		}else if (item.getTitle() == "Orbit") {
			drawView.mode="Orbit";
			int ck=0;
			if(this.clockwise)
			{
				ck=1;
			}
			else
			{
				ck=0;
			}
			this.clockwise=!this.clockwise;
			this.client.sendMessage("controller,"+ 996 + ",200," + ck );
			Log.d("item selected",""+item.getTitle());

		}else if (item.getTitle() == "Breath1") {
			//this.client.sendMessage("");

		}else if (item.getTitle() == "Breath2") {
			//this.client.sendMessage("");

		}else if (item.getTitle() == "OrbitLine") {

		}else if (item.getTitle() == "FollowLine") {

		}else if (item.getTitle() == "Wander") {
			this.client.sendMessage("controller,"+ 995 );
		}else if (item.getTitle() == "Path") {
			drawView.mode="Path";
			Log.d("item selected",""+item.getTitle());

		}else if (item.getTitle() == "Separation") {

		}else if (item.getTitle() == "Alignment") {

		}else if (item.getTitle() == "Cohesion") {

		}else if (item.getTitle() == "TugMove") {
			this.drawView.mode="TugMove";
		}else if (item.getTitle() == "AvatarMove") {
			this.drawView.mode="AvatarMove";
		}else if (item.getTitle() == "followAvatar") {
			this.client.sendMessage("controller,"+ 99879 );
			this.drawView.mode="AvatarMove";
		}else if (item.getTitle() == "followAvatarInLine") {
			this.client.sendMessage("controller,"+ 99878 );
			this.drawView.mode="AvatarMove";
		}else if (item.getTitle() == "evadeAvatar") {
			this.client.sendMessage("controller,"+ 99877 );
			this.drawView.mode="AvatarMove";
		}else if (item.getTitle() == "orbitAvatar") {
			this.client.sendMessage("controller,"+ 99876 );
			this.drawView.mode="AvatarMove";
		}else if (item.getTitle() == "circle") {
			this.drawView.mode="formation";
			this.client.sendMessage("controller,"+ 997 + ",circle," + 80);

		}else if (item.getTitle() == "square") {
			this.drawView.mode="formation";
			this.client.sendMessage("controller,"+ 997 + ",square," + 80);
		}else if (item.getTitle() == "horizontal") {
			this.drawView.mode="formation";
			this.client.sendMessage("controller,"+ 997 + ",horizontal," + 80);
		}else if (item.getTitle() == "vertical") {
			this.drawView.mode="formation";
			this.client.sendMessage("controller,"+ 997 + ",vertical," + 80);
		}else if (item.getTitle() == "diagonal") {
			this.drawView.mode="formation";
			this.client.sendMessage("controller,"+ 997 + ",diagonal," + 80);
		}else if (item.getTitle() == "drawn") {
			drawView.mode="drawn";
			Log.d("item selected",""+item.getTitle());
		}else if (item.getTitle() == "broadcastSequence") {
			drawView.thread.showSequencer=!drawView.thread.showSequencer;
			if(drawView.thread.showSequencer)
			{
				prevMode=drawView.mode;
				drawView.mode="editSequencer";
			}
			else
			{
				drawView.mode=prevMode;
			}
		}else if (item.getTitle() == "noMapping") {
			this.client.sendMessage("controller,"+ 800 + "," + Mapping.NONE.getMap());
		}else if (item.getTitle() == "angle") {
			this.client.sendMessage("controller,"+ 800 + "," + Mapping.ANGLE.getMap());
		}else if (item.getTitle() == "neighbor") {
			this.client.sendMessage("controller,"+ 800 + "," + Mapping.NEIGHBOR.getMap());
		}else if (item.getTitle() == "speed") {
			this.client.sendMessage("controller,"+ 800 + "," + Mapping.SPEED.getMap());
		}else if (item.getTitle() == "angle_embellish") {
			this.client.sendMessage("controller,"+ 800 + "," + Mapping.ANGLE_EMBELLISH.getMap());
		}else if (item.getTitle() == "neighbor_embellish") {
			this.client.sendMessage("controller,"+ 800 + "," + Mapping.NEIGHBOR_EMBELLISH.getMap());
		}else if (item.getTitle() == "speed_embellish") {
			this.client.sendMessage("controller,"+ 800 + "," + Mapping.SPEED_EMBELLISH.getMap());
		}else if (item.getTitle() == "proximity1") {
			this.client.sendMessage("controller,"+ ControllerCode.MAPPING.getCode() + "," + Mapping.PROXIMITY1.getMap());
		}else if (item.getTitle() == "FightSong_Angle") {
			this.client.sendMessage("controller,"+ ControllerCode.MAPPING.getCode() + "," + Mapping.FIGHTSONG_ANGLE.getMap());
		}else if (item.getTitle() == "FightSong_Neighbor") {
			this.client.sendMessage("controller,"+ ControllerCode.MAPPING.getCode() + "," + Mapping.FIGHTSONG_NEIGHBOR.getMap());
		}else if (item.getTitle() == "FightSong_Speed") {
			this.client.sendMessage("controller,"+ ControllerCode.MAPPING.getCode() + "," + Mapping.FIGHTSONG_SPEED.getMap());
		}else if(item.getTitle() == "ToSongMaker")		{
			this.drawView.thread.arenaSongmaker=true;
		}else if(item.getTitle() == "ToArena")		{
			this.drawView.thread.arenaSongmaker=false;
		}else if(item.getTitle() == "ClearAll")		{
			boolean[][] gridVals = this.drawView.thread.songMaker.g.gridVals;
			for (int i=0;i<gridVals.length;i++)
			{
				for (int j = 0;j<gridVals[i].length; j++)
				{
					gridVals[i][j]=false;
				}
			}
			
			HandleStuffThread h = new HandleStuffThread(this);

			h.start();
			
			
			
		}else if(item.getTitle() == "RandomEuclid")		{

			boolean[][] gridVals = this.drawView.thread.songMaker.g.gridVals;
			for (int i=0;i<gridVals.length;i++)
			{
				this.drawView.bbc.fillEuclid((int) (Math.random()*gridVals[i].length/4 +1), gridVals[i]);
			}
		}else if(item.getTitle() == "BroadcastMeasure")	{

			/*
			for(int i=0;i<this.drawView.bbc.allBots.size();i++)
			{
				Bot b= (Bot) this.drawView.bbc.allBots.get(i);
				//client.sendMessage("com,"+ client.myID + "," + b.ID + "," + "query" + "," + "nnnn");

			}*/

			HandleStuffThread h = new HandleStuffThread(this);

			h.start();

			/*

			for(int i=0;i<this.drawView.thread.songMaker.g.gridVals.length;i++)
			{
				boolean[] b= new boolean[this.drawView.bbc.SEQUENCERLENGTH];

				int factor=0;
				for(int j=0;j<this.drawView.thread.songMaker.g.gridVals[i].length;j++)
				{
					factor = b.length/this.drawView.thread.songMaker.g.gridVals[i].length;
					int index = j*factor;
					if(index<b.length)
					{

						b[index]=this.drawView.thread.songMaker.g.gridVals[i][j];
					}
				}

				Log.d("","i: " + i + ", " + this.drawView.bbc.patternToString(b));
				//
				client.sendMessage("controller,"+ ControllerCode.SETSEQUENCE.getCode() + "," + i+ "," + this.drawView.bbc.patternToString(b) );
				Log.d("sending patterns","pattern " + i);


			}

			 */


			//client.sendMessage("controller,"+ ControllerCode.SENDMEASURE.getCode() + "," + this.drawView.bbc.notesToString());
		}




		/*
		Mapping m = Mapping.NONE;

		switch(m)
		{
		case NONE:
			break;
		case ANGLE:

			default : 
				;

		}
		 */


		return true;
	}



	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		if(this.drawView.thread.arenaSongmaker)
		{
			makeSongMakerMenu( menu);
		}
		else
		{
			makeArenaMenu(menu);
		}
		return true;

	}

	public void makeSongMakerMenu(Menu menu)
	{

		menu.clear();

		menu.add("ToArena");
		menu.add("BroadcastMeasure");
		menu.add("ClearAll");
		menu.add("RandomEuclid");
		menu.add("StopAll");		
		menu.add("Connect");
		menu.add("Sync");		
		menu.add("Inspect");
		menu.add("Problem?");		
		menu.add("Quit");

	}
	public void makeArenaMenu(Menu menu)
	{
		menu.clear();
		SubMenu sub = menu.addSubMenu(0,1,0, "Music Mappings");
		sub.add("noMapping");
		sub.add("FightSong_Angle");
		sub.add("FightSong_Neighbor");
		sub.add("FightSong_Speed");
		sub.add("broadcastSequence");
		sub.add("angle");
		sub.add("neighbor");
		sub.add("speed");
		sub.add("angle_embellish");
		sub.add("neighbor_embellish");
		sub.add("speed_embellish");
		sub.add("proximity1");


		SubMenu sub2 = menu.addSubMenu(0,1,0, "Movement Behaviors");
		sub2.add("TugMove");
		sub2.add("AvatarMove");
		sub2.add("Orbit");
		sub2.add("Wander");

		sub2.add("ViewCursor");
		sub2.add("Move");
		sub2.add("MoveRelative");

		sub2.add("Breath1");
		sub2.add("Breath2");
		sub2.add("OrbitLine");
		sub2.add("FollowLine");

		sub2.add("Path");
		sub2.add("Separation");
		sub2.add("Alignment");
		sub2.add("Cohesion");

		sub2.add("followAvatar");
		sub2.add("followAvatarInLine");
		sub2.add("evadeAvatar");
		sub2.add("orbitAvatar");
		sub2.add("StopAll");
		//sub2.add("Formation");

		//SubMenu sub3 = sub2.addSubMenu(0,1,0, "formations");//this does not work
		SubMenu sub3 = menu.addSubMenu(0,1,0, "formations");
		sub3.add("circle");
		sub3.add("square");
		sub3.add("horizontal");
		sub3.add("vertical");
		sub3.add("diagonal");
		sub3.add("drawn");

		menu.add("StopAll");
		menu.add("Connect");
		menu.add("Sync");
		menu.add("Inspect");
		/*
		menu.add("ViewCursor");
		menu.add("Move");
		menu.add("Orbit");
		menu.add("Breath1");
		menu.add("Breath2");
		menu.add("OrbitLine");
		menu.add("FollowLine");
		menu.add("Wander");
		menu.add("Separation");
		menu.add("Alignment");
		menu.add("Cohesion");
		 */
		menu.add("Problem?");
		menu.add("ToSongMaker");
		menu.add("Quit");

	}










}