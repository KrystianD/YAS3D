package pl.jakd.tg_project;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication
{
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		Log.d ("KD", "AS1D");

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration ();
		cfg.useAccelerometer = true;
		cfg.useCompass = true;
		cfg.useImmersiveMode = true;
		Log.d ("KD", "ASD");
		initialize (new TGProject (this), cfg);
	}
}