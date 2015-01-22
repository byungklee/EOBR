package com.eobr;

import android.app.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class NewTripFragment extends Fragment implements GPSListener {

    /**
     * Variables
     */
    private Button mStartButton;
    private TextView mOriginTextView;
    private GPSReceiver mGpsReceiver;
    private RadioButton mPickUpEmpty;
    private RadioGroup mTripTypeRadio;
    private Context mContext;
    private MyLocation ml;

    /**
     * Factory method for this class.
     * @return A new instance of fragment NewTripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewTripFragment newInstance() {
        NewTripFragment fragment = new NewTripFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NewTripFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_trip, container, false);
        Log.i("NEWTRIP", "INIT");

        mContext = getActivity().getApplicationContext();
        mGpsReceiver = new GPSReceiver(this);
        /**
         * Load views from the view
         */
        mOriginTextView = (TextView) v.findViewById(R.id.origin_text);
        mPickUpEmpty = (RadioButton) v.findViewById(R.id.pickup_empty);
        //PickupEmpty is set as default
        mPickUpEmpty.setChecked(true);
        mTripTypeRadio = (RadioGroup) v.findViewById(R.id.trip_type_radio);


        /**
         * Call GPSIntentSerivce to get the current location
         */
        Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
        getActivity().startService(i);
        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROAD_CAST_LOCATION_ONCE);
        /**
         * register receiver from broadcast
         */
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
                mGpsReceiver,
                mStatusIntentFilter);

        mStartButton = (Button) v.findViewById(R.id.start_button);
        		mStartButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                MainActivity.GPSIntent = new Intent(getActivity().getApplicationContext(), GPSService.class);
                getActivity().startService(MainActivity.GPSIntent);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction frt = fm.beginTransaction();
                StatusFragment sfm = new StatusFragment();
                frt.replace(R.id.container, sfm);
                frt.commit();
                MainActivity.isRunning = true;
                NoteFragment.noteNumber = 1;
                int checkedRadioButtonId = mTripTypeRadio.getCheckedRadioButtonId();

                switch (checkedRadioButtonId) {
                    case R.id.pickup_empty:
                        MainActivity.tripType = "pickup_empty";
                        break;
                    case R.id.pickup_full:
                        MainActivity.tripType = "pickup_full";
                        break;
                    case R.id.deliver_empty:
                        MainActivity.tripType = "deliver_empty";
                        break;
                    case R.id.deliver_full:
                        MainActivity.tripType = "deliver_full";
                        break;
                    default:
               }
            }
		});

        return v;
    }

    @Override
    public void execute(MyLocation location) {

    }


    /**
     * Given latitude and longitude, it's getting the address of the location using Geocoder.
     * @param str
     * @param latitude
     * @param longitude
     * @param note
     */
    @Override
    public void executeForSingle(String str, double latitude, double longitude, String note) {
        ml = new MyLocation(str, latitude, longitude);
        try{
            Log.i("NewTrip", latitude + " " + longitude);
            Geocoder geo = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                mOriginTextView.setText("Waiting for Location");
            }
            else {
                mOriginTextView.setText(addresses.get(0).getAddressLine(0) + ", " +
                        addresses.get(0).getLocality() +", " +
                        addresses.get(0).getAdminArea() + ", " +
                        addresses.get(0).getCountryName());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
