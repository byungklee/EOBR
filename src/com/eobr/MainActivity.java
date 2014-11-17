package com.eobr;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
		private static final int TO_HOME = 1;
		private static final int NEW_TRIP = 2;
		private static final int VIEW_TRIP = 3;
		private static final int STOP_TRIP = 4;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {			
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new LoginFragment()).commit();
		}
		getOverflowMenu();		
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

		//Inflate the menu; this adds items to the action bar if it is present.
		
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
//	public static class PlaceholderFragment extends Fragment {
//
//		public PlaceholderFragment() {
//			
//		}
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//				Bundle savedInstanceState) {
//			View rootView = inflater.inflate(R.layout.fragment_login, container,
//					false);
//			return rootView;
//		}
//	}

}
