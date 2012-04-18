package gtcmtswarmbot.mobilecontrol;

 

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import android.view.Menu;
import android.view.MenuItem;

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
		menu.add("Cursor");
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
		menu.add("Quit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle() == "Cursor") {
			//showControls();
			this.arenaView.thread.arena.cursor.active=!this.arenaView.thread.arena.cursor.active;
		} else if (item.getTitle() == "Quit") {
			finish();
			System.exit(0);
		}else if (item.getTitle() == "Move") {
			arenaView.mode="Move";
			 
		}else if (item.getTitle() == "Orbit") {
			 
		}else if (item.getTitle() == "Breath1") {
			 
		}else if (item.getTitle() == "Breath2") {
			 
		}else if (item.getTitle() == "OrbitLine") {
			 
		}else if (item.getTitle() == "FollowLine") {
			 
		}else if (item.getTitle() == "Wander") {
			 
		}else if (item.getTitle() == "Separation") {
			 
		}else if (item.getTitle() == "Alignment") {
			 
		}else if (item.getTitle() == "Cohesion") {
			 
		}else if (item.getTitle() == "FollowLine") {
			 
		}
		
		return true;
	}
    
    
    
}