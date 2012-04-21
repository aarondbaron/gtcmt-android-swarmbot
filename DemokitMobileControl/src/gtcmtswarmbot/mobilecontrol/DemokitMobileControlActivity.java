package gtcmtswarmbot.mobilecontrol;

 

import gtcmtswarmbot.mobilecontrol.enums.Mapping;
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
	
	boolean clockwise;
	
	
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
    	sub.add(0,11,0,"noMapping");
    	sub.add(0,12,0,"showSequencer");
    	sub.add(0,13,0,"angle");
    	sub.add(0,14,0,"neighbor");
    	sub.add(0,15,0,"speed");
    	sub.add(0,16,0,"angle_embellish");
    	sub.add(0,17,0,"neighbor_embellish");
    	sub.add(0,18,0,"speed_embellish");
    	
    	
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
    	sub2.add("TugMove");
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
			this.client.sendMessage("controller,"+ 999);
		}else if (item.getTitle() == "Connect") {
			this.client.doStuff();
		}else if (item.getTitle() == "Sync") {
			this.client.sendMessage("controller,"+ 1000);
		}else if (item.getTitle() == "Move") {
			arenaView.mode="Move";
			Log.d("item selected",""+item.getTitle());
			 
		}else if (item.getTitle() == "Orbit") {
			arenaView.mode="Orbit";
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
			 
		}else if (item.getTitle() == "Path") {
			arenaView.mode="Path";
			Log.d("item selected",""+item.getTitle());
			 
		}else if (item.getTitle() == "Separation") {
			 
		}else if (item.getTitle() == "Alignment") {
			 
		}else if (item.getTitle() == "Cohesion") {
			 
		}else if (item.getTitle() == "TugMove") {
			this.arenaView.mode="TugMove";
		}else if (item.getTitle() == "circle") {
			this.arenaView.mode="formation";
			this.client.sendMessage("controller,"+ 997 + ",circle," + 80);
			 
		}else if (item.getTitle() == "square") {
			this.arenaView.mode="formation";
			this.client.sendMessage("controller,"+ 997 + ",square," + 80);
		}else if (item.getTitle() == "horizontal") {
			this.arenaView.mode="formation";
			this.client.sendMessage("controller,"+ 997 + ",horizontal," + 80);
		}else if (item.getTitle() == "vertical") {
			this.arenaView.mode="formation";
			this.client.sendMessage("controller,"+ 997 + ",vertical," + 80);
		}else if (item.getTitle() == "diagonal") {
			this.arenaView.mode="formation";
			this.client.sendMessage("controller,"+ 997 + ",diagonal," + 80);
		}else if (item.getTitle() == "drawn") {
			arenaView.mode="drawn";
			Log.d("item selected",""+item.getTitle());
		}else if (item.getTitle() == "showSequencer") {
			arenaView.thread.showSequencer=!arenaView.thread.showSequencer;
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
			this.client.sendMessage("controller,"+ 800 + "," + Mapping.NEIGBHOR_EMBELLISH.getMap());
		}else if (item.getTitle() == "speed_embellish") {
			this.client.sendMessage("controller,"+ 800 + "," + Mapping.SPEED_EMBELLISH.getMap());
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
	
	
 
	
 
	
	
    
    
    
}