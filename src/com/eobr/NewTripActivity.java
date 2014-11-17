package com.eobr;

import java.lang.reflect.Field;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.Toast;

public class NewTripActivity extends ActionBarActivity {
	private Button mStartButton;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_trip);
		getOverflowMenu();
		mStartButton = (Button) findViewById(R.id.start_button);
		mStartButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), StatusActivity.class);				
				startActivity(i);				
			}
		});
	}
	
	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch(NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch(id) {
			case R.id.action_home:
				Toast.makeText(getApplicationContext(), "To_home", Toast.LENGTH_SHORT).show();
				break;
			case R.id.action_new_trip:
				Toast.makeText(getApplicationContext(), "New", Toast.LENGTH_SHORT).show();
				break;
			case R.id.action_view_trip:
				Toast.makeText(getApplicationContext(), "View", Toast.LENGTH_SHORT).show();
				break;
			case R.id.action_stop_trip:
				Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();
				break;
			
			
		}
		
		return false;
	}
}
