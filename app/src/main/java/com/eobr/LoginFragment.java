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
import android.widget.Toast;

public class LoginFragment extends Fragment {

	private Button startButton;
	private Button stopButton;
	
	public LoginFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_login, container,
				false);
		
		startButton = (Button) rootView.findViewById(R.id.start_button);
		stopButton = (Button) rootView.findViewById(R.id.stop_button);
		
		startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent i = new Intent(getActivity().getApplicationContext(), NewTripActivity.class);
				
				//getActivity().startActivity(i);
                FragmentManager fragmentManager2 = getFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                NewTripFragment fragment2 = new NewTripFragment();

                fragmentTransaction2.addToBackStack("xyz");
                fragmentTransaction2.hide(LoginFragment.this);
                fragmentTransaction2.add(android.R.id.content, fragment2);

                fragmentTransaction2.commit();

            }
		});
		
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity().getApplicationContext(), "df", Toast.LENGTH_SHORT).show();
			}
		});
		
		return rootView;
	}
}



