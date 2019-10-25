package com.supraliminalsolutions.spamblocker.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.supraliminalsolutions.spamblocker.R;

public class SlashScreen extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen);
		new Timer().schedule(new TimerTask(){ public void run()
		{
			go();
		}}, 5000);
	}

	public void go()
	{
		Intent i =new Intent(this, MainActivity.class);
		startActivity(i);
	}
}
