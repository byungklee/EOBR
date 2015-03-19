package com.eobr;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.eobr.model.LocationList;
import com.eobr.model.MyLocation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment implements GPSListener {

    //For Debuggif Purpose
    private final String TAG = "StatusFragment";

    //Buttons
    private Button mNoteButton;
    private Button mGateIn;
    private Button mHookUnhook;
    private Button mWaitingForDock;
    private Button mDockIn;
    private Button mDockOut;
    private Button mAvailable;
    private Button mNotAvailable;
    private Button mPickUp;
    private Button mDeliver;

    private TextView mDetailStatusTextView;
    private GPSReceiver gpsReceiver;
    private LocationList locationList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationList = LocationList.getInstance();
        gpsReceiver = new GPSReceiver(this);
    }

    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of LoginFragment");
        super.onResume();

        /**
         * Attach GPS Receivers so that Status Fragment can listen and update.
         */
        gpsReceiver = new GPSReceiver(this);
        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROAD_CAST_LOCATION);
        IntentFilter mLocationOnceFilter = new IntentFilter(Constants.BROAD_CAST_LOCATION_ONCE);

        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
                gpsReceiver,
                mStatusIntentFilter);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
                gpsReceiver,
                mLocationOnceFilter);
        if(!locationList.isEmpty())
            mDetailStatusTextView.setText(getListString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status, container, false);

        /**
         * Note Button
         */
        mNoteButton = (Button) v.findViewById(R.id.note);
        setOnClickListenerForOpeningFragment(mNoteButton,  NoteFragment.newInstance());

        /**
         * For Detail Status View
         */
        mDetailStatusTextView = (TextView) v.findViewById(R.id.scroll_view);
        setOnClickListenerForOpeningFragment(mDetailStatusTextView, DetailStatusFragment.newInstance());

        /**
         * Terminal Buttons
         */
        mGateIn = (Button) v.findViewById(R.id.gate_in);
        setOnClickListenerForGPS(mGateIn);
        mHookUnhook = (Button) v.findViewById(R.id.hook_unhook);
        setOnClickListenerForGPS(mHookUnhook);

        /**
         * Warehouse Buttons
         */
        mWaitingForDock = (Button) v.findViewById(R.id.waiting_for_dock);
        setOnClickListenerForGPS(mWaitingForDock);
        mDockIn = (Button) v.findViewById(R.id.dock_in);
        setOnClickListenerForGPS(mDockIn);
        mDockOut = (Button) v.findViewById(R.id.dock_out);
        setOnClickListenerForGPS(mDockOut);

        /**
         * Container Status Buttons
         */
        mAvailable = (Button) v.findViewById(R.id.available);
        setOnClickListenerForGPS(mAvailable);

        mNotAvailable = (Button) v.findViewById(R.id.not_available);
        setOnClickListenerForGPS(mNotAvailable);

        mPickUp = (Button) v.findViewById(R.id.pickup);
        setOnClickListenerForGPS(mPickUp);

        mDeliver = (Button) v.findViewById(R.id.deliver);
        setOnClickListenerForGPS(mDeliver);

//        IntentFilter mStatusIntentFilter = new IntentFilter(
//                Constants.BROAD_CAST_LOCATION);
//        IntentFilter mLocationOnceFilter = new IntentFilter(Constants.BROAD_CAST_LOCATION_ONCE);
//
//        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
//                gpsReceiver,
//                mStatusIntentFilter);
//        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
//                gpsReceiver,
//                mLocationOnceFilter);


//        if(!locationList.isEmpty())
//            mDetailStatusTextView.setText(getListString());

        return v;
    }

    private void openNewFragment(Fragment f) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, f);
        ft.addToBackStack("status");
        ft.commit();
    }

    private void setOnClickListenerForOpeningFragment(View b, final Fragment f) {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewFragment(f);
            }
        });
    }

    private void setOnClickListenerForGPS(final View b) {
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //System.out.println("Clicked");
                Log.d(TAG, "Clicked Button");
                getDataFromGps(b);
            }
        });
    }

    private void getDataFromGps(View b) {
        Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
        i.putExtra("type", extractTextFromButton((Button) b));
        getActivity().startService(i);
    }

    private String extractTextFromButton(Button b) {
        return b.getText().toString().toLowerCase().replaceAll(" ","_");
    }

    public String getListString() {
        StringBuilder sb = new StringBuilder();
        int size = locationList.size();
        if(size > 6) {
            for(int i=size - 6;i<size;i++) {
                MyLocation ml = locationList.get(i);
                sb.append(ml.getType() + "  ").append(ml.getLatitude()).append("  ").append(ml.getLongitude()).append("  ").append(ml.getTimeString()).append("\n");
            }
        } else {
            for(MyLocation ml :locationList.getList()) {
            sb.append(ml.getType() + "  ").append(ml.getLatitude()).append("  ").append(ml.getLongitude()).append("  ").append(ml.getTimeString()).append("\n");
         }
        }
        return sb.toString();
    }

    @Override
    public void execute() {
        mDetailStatusTextView.setText(getListString());
    }

    @Override
    public void executeForSingle(MyLocation myLocation) {
        mDetailStatusTextView.setText(getListString());
    }

    @Override
    public void onStop() {
        super.onStop();
        /**
         * Detach gps receiver if this fragment is onStop.
         */
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(gpsReceiver);
    }
}
