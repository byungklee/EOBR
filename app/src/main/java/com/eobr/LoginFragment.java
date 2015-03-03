package com.eobr;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eobr.model.MyLocation;

import java.io.File;

/**
 * LoginFragment shows two main buttons: Start and Stop. This interface is to start a new trip or to stop a trip.
 */
public class LoginFragment extends Fragment {

    /**
     * Fields
     */
	private TextView mStopButtonText;
    private static TextView mStartButtonText;

    /**
     * Constructor
     */
    public LoginFragment() {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_login, container,
				false);

		mStopButtonText = (TextView) rootView.findViewById(R.id.stop_button_text);

        mStartButtonText = (TextView) rootView.findViewById(R.id.start_button_text);
        if(MainActivity.state == MainActivity.ServiceState.RUNNING) {
            mStartButtonText.setText("To Status");
        }

        //Implementation of start button: it opens a new trip view
        //TO DO: Refactor
		mStartButtonText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(MainActivity.state == MainActivity.ServiceState.READY) {
                    NewTripFragment fragment2 = new NewTripFragment();

                    //What is happening with transaction.replace is that
                    //remove(fragment1).add(fragment2)
                    fragmentTransaction.replace(R.id.container, fragment2);
                    fragmentTransaction.addToBackStack("main");
                    fragmentTransaction.commit();
                } else if(MainActivity.state == MainActivity.ServiceState.RUNNING) {
                    StatusFragment sfm = new StatusFragment();
                    fragmentTransaction.replace(R.id.container, sfm);
                    fragmentTransaction.addToBackStack("main");
                    fragmentTransaction.commit();
                }
            }
		});

        //Implementation of stop button: it stops the currently running trip.
		mStopButtonText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                if(MainActivity.state == MainActivity.ServiceState.RUNNING) {
                    MainActivity.state = MainActivity.ServiceState.WAIT;
                    Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
                    i.putExtra("type", "stop");
                    getActivity().startService(i);
                    mStartButtonText.setText("Start Trip");
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "There is no running trip.", Toast.LENGTH_SHORT).show();
                }
			}
		});

        /**
         * Debug Purpose
         */
        ((Button) rootView.findViewById(R.id.testButton)).setVisibility(Button.INVISIBLE);
        ((Button) rootView.findViewById(R.id.testButton2)).setVisibility(Button.INVISIBLE);
        ((Button) rootView.findViewById(R.id.testButton3)).setVisibility(Button.INVISIBLE);
        ((Button) rootView.findViewById(R.id.testButton)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Test Button has pressed " + MainActivity.state);
                //ResourceManager rm = new ResourceManager(getActivity().getApplicationContext());
                //rm.printFiles();

                //Create a test case:
                //MainActivity.rm.execute();

                //Clear all
                DbAdapter db = new DbAdapter(getActivity().getApplicationContext());
                SQLiteDatabase dbs = db.getWritableDatabase();
                dbs.execSQL("delete from notsent where trip_id > 0");
                File[] files = getActivity().getApplicationContext().getFilesDir().listFiles();
                //System.out.println("Cleaning File\n number of files is " + files.length);
                for(File f: files) {
                    f.delete();
                }
                System.out.println("Cleaned ALL");

            }
        });
        ((Button) rootView.findViewById(R.id.testButton2)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Test2 Button has pressed " + MainActivity.state);
                //ResourceManager rm = new ResourceManager(getActivity().getApplicationContext());
                //rm.printFiles();
                //Create a test case:
                testcaseForResourceManager();
            }
        });
        ((Button) rootView.findViewById(R.id.testButton3)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Test3 Button has pressed " + MainActivity.state);
                //ResourceManager rm = new ResourceManager(getActivity().getApplicationContext());
                //rm.printFiles();
                //Create a test case:
                testcaseTest();
            }
        });

		return rootView;
	}

    public void testcaseTest() {
        DbAdapter db = new DbAdapter(getActivity().getApplicationContext());
        SQLiteDatabase sqlDb = db.getReadableDatabase();
        Cursor cur  = sqlDb.rawQuery("select * from trips where trip_id=\"888\"", null);
        Log.i("Login", "" + cur.getCount());

        cur  = sqlDb.rawQuery("select * from notsent", null);
        Log.i("Login", "" + cur.getCount());
        db.close();
        sqlDb.close();
    }

    public void testcaseForResourceManager() {
        DbAdapter db = new DbAdapter(getActivity().getApplicationContext());
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        SQLiteDatabase readDb = db.getReadableDatabase();
        MyLocation ml = new MyLocation("start" , 1, 1);
        Cursor cur;

        sqlDb.execSQL("insert into trips (trip_id, time, trip_type,longitude, latitude, type, truck_id)" +
                " values (888, \'" + ml.getJsonTime() + "\', \"start\", 1, 1, \"pick_up_empty\", \"64:89:9a:8f:22:91\")");
        cur = readDb.rawQuery("select * from trips",null);
        cur.moveToFirst();
        System.out.println(cur.getCount());
        sqlDb.execSQL("insert into trips (trip_id, time, trip_type,longitude, latitude, type, truck_id)" +
                " values (888, \'" + ml.getJsonTime() + "\', \"running\", 2, 2, \"pick_up_empty\", \"64:89:9a:8f:22:91\")");
        cur = readDb.rawQuery("select * from trips",null);
        System.out.println(cur.getCount());
        sqlDb.execSQL("insert into notsent (trip_id) values (888)");
        cur = readDb.rawQuery("select * from notsent",null);
        System.out.println(cur.getCount());
        db.close();
        sqlDb.close();
        readDb.close();
    }

    public static void setStartButton(boolean flag) {
        if(flag) {
            mStartButtonText.setText("START TRIP");
        } else {
            mStartButtonText.setText("RESUME");
        }
    }
}



