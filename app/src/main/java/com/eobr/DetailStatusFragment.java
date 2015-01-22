package com.eobr;

import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailStatusFragment extends Fragment implements GPSListener {

    private static final String TAG = "DetailStatus";
    /**
     * Variable declaration
     */
    private TextView textview;
    private GPSReceiver gpsReceiver;

    /**
     * Factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailStatusFragment newInstance() {
        DetailStatusFragment fragment = new DetailStatusFragment();
        return fragment;
    }

    public DetailStatusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *
     * Initializing the view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.detail_status, container, false);
        textview = (TextView) v.findViewById(R.id.log_textview);
        textview.setMovementMethod(new ScrollingMovementMethod());
        StringBuilder sb = new StringBuilder();
        for(MyLocation ml : LocationList.getInstance().getList()) {
            sb.append(ml.getType() + " ").append(ml.getLatitude()).append(" ").append(ml.getLongitude()).append(" ").append(ml.getTimeString()).append("\n");
        }
        textview.setText(sb.toString());

        gpsReceiver = new GPSReceiver(this);
        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROAD_CAST_LOCATION);

        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(
                gpsReceiver,
                mStatusIntentFilter);

        return v;
    }

    //Listens to GPS Receiver for GPSService.
    public void execute(MyLocation location) {
        Log.i(TAG, location.getType() + " " + location.getLatitude() + " " + location.getLongitude());
        MyLocation ml = LocationList.getInstance().getList().get(LocationList.getInstance().size()-1);
        StringBuilder sb = new StringBuilder().append(ml.getType() + " ").append(ml.getLatitude()).append(" ").append(ml.getLongitude()).append(" ").append(ml.getTimeString()).append("\n");
        textview.setText(textview.getText() + sb.toString());
        DbAdapter db = new DbAdapter(getActivity().getApplicationContext());
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        sqlDb.execSQL("insert into trips (trip_id, truck_id, trip_type, type, latitude, longitude, time) " +
                "values (" + MainActivity.CURRENT_TRIP_ID + ", \"" +MainActivity.TRUCK_ID + "\", \"" + MainActivity.tripType + "\", \"" + location.getType() + "\", " +
                location.getLatitude() + ", " + location.getLongitude() + ", \"" + location.getTimeString() + "\")");
        sqlDb.close();
        db.close();
    }

    //This doesn't happen in detail status.
    @Override
    public void executeForSingle(String type, double latitude, double longitude, String note) {

    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(gpsReceiver);
    }
}
