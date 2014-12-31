package com.eobr;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment {

	//private Button startButton;
	private Button stopButton;
    private static TextView mStartButtonText;
	
	public LoginFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_login, container,
				false);
		
		//startButton = (Button) rootView.findViewById(R.id.start_button);

		stopButton = (Button) rootView.findViewById(R.id.stop_button);

        mStartButtonText = (TextView) rootView.findViewById(R.id.start_button_text);
        if(MainActivity.isRunning) {
            mStartButtonText.setText("To Status");
        }
		
		mStartButtonText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i = new Intent(getActivity().getApplicationContext(), NewTripActivity.class);
				//getActivity().startActivity(i);
                if(!MainActivity.isRunning) {
                    FragmentManager fragmentManager2 = getFragmentManager();
                    FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    NewTripFragment fragment2 = new NewTripFragment();

                    //What is happening with replace is that
                    //remove(fragment1).add(fragment2)
                    fragmentTransaction2.replace(R.id.container, fragment2);
                    fragmentTransaction2.addToBackStack(null);
                    fragmentTransaction2.commit();
                } else {
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction frt = fm.beginTransaction();
                    StatusFragment sfm = new StatusFragment();
                    frt.replace(R.id.container, sfm);
                    frt.addToBackStack(null);
                    frt.commit();
                }

            }
		});
		
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                if(MainActivity.isRunning) {
                    Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
                    i.putExtra("type", "stop");
                    getActivity().startService(i);
                    mStartButtonText.setText("Start Trip");
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "There is no running trip.", Toast.LENGTH_SHORT).show();
                }
			}
		});

		return rootView;
	}

    public static void setStartButton(boolean flag) {
        if(flag) {
            mStartButtonText.setText("START TRIP");
        } else {
            mStartButtonText.setText("RESUME");
        }
    }
}



