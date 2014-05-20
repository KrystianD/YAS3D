package pl.jakd.tg_project;

import android.os.Bundle;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication
{
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration ();
		cfg.useAccelerometer = true;
		cfg.useCompass = true;
		cfg.useImmersiveMode = true;

		ApplicationListener listener = new GameSnake (this);
		initialize (listener, cfg);
	}

	@Override
	protected void onPause ()
	{
		super.onPause ();
	}
}