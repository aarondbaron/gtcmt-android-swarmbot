package aaron.tests;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class DrawingExampleActivity extends Activity {
    DrawView drawView;
    
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set full screen view
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        drawView = new DrawView(this);
        setContentView(drawView);
        drawView.requestFocus();
        //drawView.thread.start();
        //Display display = getWindowManager().getDefaultDisplay(); 
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Random Sound");
		menu.add("Coin");
		menu.add("Lazer");
		menu.add("Explosion");
		menu.add("Powerup");
		menu.add("Hit");
		menu.add("Jump");
		menu.add("Blip");
		menu.add("UseSample");
		menu.add("DrawPoints");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		

		if (item.getTitle().equals("Random Sound")) {

			drawView.atest.randomSound();
		}

		else if (item.getTitle().equals("Coin")) {

			drawView.atest.soundType(0);

		}

		else if (item.getTitle().equals("Lazer")) {

			drawView.atest.soundType(1);

		}
		
		else if (item.getTitle().equals("Explosion")) {
			drawView.atest.soundType(2);

		}
		
		else if (item.getTitle().equals("Powerup")) {
			drawView.atest.soundType(3);

		}
		
		else if (item.getTitle().equals("Hit")) {
			drawView.atest.soundType(4);

		}

		else if (item.getTitle().equals("Jump")) {
			drawView.atest.soundType(5);

		}
		
		else if (item.getTitle().equals("Blip")) {
			drawView.atest.soundType(6);

		}
		
		else if (item.getTitle().equals("UseSample")) {
			drawView.useSample=!drawView.useSample;

		}
		
		else if (item.getTitle().equals("DrawPoints")) {
			drawView.drawPoints=!drawView.drawPoints;

		}


		
		return true;
	}
}