package pl.jakd.tg_project;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication
{

	Sender sender;
	
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);

		sender = new Sender ();
		
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration ();
		cfg.useAccelerometer = true;
		cfg.useCompass = true;
		cfg.useImmersiveMode = true;
		
		ApplicationListener listener = new GameSnake(this, sender);
		initialize(listener,cfg);
		//initialize (new TGProject (this), cfg);
	}
	
	@Override
	protected void onPause ()
	{
		super.onPause ();
		sender.close ();
	}
}