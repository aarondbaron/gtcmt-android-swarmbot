package gtcmtswarmbot.mobilecontrol;

 

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;


import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

public class DemokitMobileControlActivity extends Activity {
	

	ArenaView arenaView;
	
	Client client;
	
	SomeController sc;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);  
    	super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        sc = new SomeController();
        arenaView = new ArenaView(this,sc);
        setContentView(arenaView);
        arenaView.requestFocus(); 
        
        
        client = new Client(this, sc);
        
         
                 
            
         

    }
    
    @Override
    public void onDestroy()
    {
    	arenaView.thread.setRunning(false);
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
    	arenaView.thread.setRunning(false);
    	//finish();
		System.exit(0);
    }
    
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	SubMenu sub = menu.addSubMenu(0,1,0, "Music Mappings");
    	sub.add(0,11,0,"no Mapping");
    	sub.add(0,12,0,"showSequencer");
    	sub.add(0,13,0,"angle");
    	sub.add(0,14,0,"neighbor");
    	sub.add(0,15,0,"speed");
    	
    	
    	SubMenu sub2 = menu.addSubMenu(0,1,0, "Movement Behaviors");
    	sub2.add("ViewCursor");
    	sub2.add("Move");
    	sub2.add("Orbit");
    	sub2.add("Breath1");
    	sub2.add("Breath2");
    	sub2.add("OrbitLine");
    	sub2.add("FollowLine");
    	sub2.add("Wander");
    	sub2.add("Path");
    	sub2.add("Separation");
    	sub2.add("Alignment");
    	sub2.add("Cohesion");
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
		menu.add("Quit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle() == "ViewCursor") {
			//showControls();
			this.arenaView.thread.arena.cursor.active=!this.arenaView.thread.arena.cursor.active;
		} else if (item.getTitle() == "Quit") {
			finish();
			System.exit(0);
		}else if (item.getTitle() == "StopAll") {
			//mActivity.client.sendMessage("controller,"+ 999 + "," + xx + "," + yy) ;
			arenaView.mode="StopAll";
			this.client.sendMessage("controller,"+ 998);
		}else if (item.getTitle() == "Connect") {
			this.client.doStuff();
		}else if (item.getTitle() == "Move") {
			arenaView.mode="Move";
			Log.d("item selected",""+item.getTitle());
			 
		}else if (item.getTitle() == "Orbit") {
			arenaView.mode="Orbit";
			Log.d("item selected",""+item.getTitle());
			 
		}else if (item.getTitle() == "Breath1") {
			this.client.sendMessage("");
			 
		}else if (item.getTitle() == "Breath2") {
			this.client.sendMessage("");
			 
		}else if (item.getTitle() == "OrbitLine") {
			 
		}else if (item.getTitle() == "FollowLine") {
			 
		}else if (item.getTitle() == "Wander") {
			 
		}else if (item.getTitle() == "Path") {
			arenaView.mode="Path";
			Log.d("item selected",""+item.getTitle());
			 
		}else if (item.getTitle() == "Separation") {
			 
		}else if (item.getTitle() == "Alignment") {
			 
		}else if (item.getTitle() == "Cohesion") {
			 
		}else if (item.getTitle() == "FollowLine") {
			 
		}else if (item.getTitle() == "circle") {
			 
		}else if (item.getTitle() == "square") {
			 
		}else if (item.getTitle() == "horizontal") {
			 
		}else if (item.getTitle() == "vertical") {
			 
		}else if (item.getTitle() == "diagonal") {
			 
		}else if (item.getTitle() == "drawn") {
			arenaView.mode="drawn";
			Log.d("item selected",""+item.getTitle());
		}else if (item.getTitle() == "showSequencer") {
			arenaView.thread.showSequencer=!arenaView.thread.showSequencer;
		}
		
		
		
		return true;
	}
    
    
    
}