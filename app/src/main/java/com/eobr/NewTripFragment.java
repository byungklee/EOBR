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

    private Button mStartButton;
    private TextView mOriginTextView;
    private GPSReceiver mGpsReceiver;
    private RadioButton mPickUpEmpty;
    private RadioGroup mTripTypeRadio;
    private Context mContext;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

  //  private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewTripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewTripFragment newInstance(String param1, String param2) {
        NewTripFragment fragment = new NewTripFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NewTripFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_trip, container, false);
        mContext = getActivity().getApplicationContext();
        mOriginTextView = (TextView) v.findViewById(R.id.origin_text);
        mGpsReceiver = new GPSReceiver(this);
        mPickUpEmpty = (RadioButton) v.findViewById(R.id.pickup_empty);
        //PickupEmpty is set as default
        mPickUpEmpty.setChecked(true);

        mTripTypeRadio = (RadioGroup) v.findViewById(R.id.trip_type_radio);


        Log.i("NEWTRIP", "INIT");

        Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
        getActivity().startService(i);

        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROAD_CAST_LOCATION_ONCE);


        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
                mGpsReceiver,
                mStatusIntentFilter);

        //GPSIntentService.startAction(getActivity().getApplicationContext());


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
    private MyLocation ml;
    @Override
    public void executeForSingle(String str, double latitude, double longitude) {
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

        //mOriginTextView.setText(latitude + " " + longitude);

    }


    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

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
