package com.eobr;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
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

import com.eobr.model.MyLocation;

import java.util.List;
import java.util.Locale;

/**
 * New Trip View
 */
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


                if(MainActivity.CURRENT_TRIP_ID == -1) {
                    DbAdapter db = new DbAdapter(getActivity().getApplicationContext());
                    SQLiteDatabase sqlDb = db.getReadableDatabase();
                    String query="select trip_id from trips order by trip_id desc";
                    Cursor curs=sqlDb.rawQuery(query, null);
                    curs.moveToFirst();
                    if(curs.getCount() != 0) {
                        MainActivity.CURRENT_TRIP_ID = curs.getInt(0)+1;
                    } else
                        MainActivity.CURRENT_TRIP_ID = 1;
                    curs.close();
                }

				// TODO Auto-generated method stub
                MainActivity.GPSIntent = new Intent(getActivity().getApplicationContext(), GPSService.class);
                getActivity().startService(MainActivity.GPSIntent);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction frt = fm.beginTransaction();
                StatusFragment sfm = new StatusFragment();
                frt.replace(R.id.container, sfm);
                frt.addToBackStack("new");
                frt.commit();
               // MainActivity.isRunning = true;
                MainActivity.state = MainActivity.ServiceState.RUNNING;

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
    public void execute(){}

    /**
     * Given latitude and longitude, it's getting the address of the location using Geocoder.
     * @param ml
     */
    @Override
    public void executeForSingle(MyLocation ml) {
        try{
            Log.i("NewTrip", ml.getLatitude() + " " + ml.getLongitude() + " " + ml.getType());
            if(ml.getType() == null) {
                Geocoder geo = new Geocoder(mContext, Locale.getDefault());
                List<Address> addresses = geo.getFromLocation(ml.getLatitude(), ml.getLongitude(), 1);
                if (addresses.isEmpty()) {
                    mOriginTextView.setText("Waiting for Location");
                } else {
                    mOriginTextView.setText(addresses.get(0).getAddressLine(0) + ", " +
                            addresses.get(0).getLocality() + ", " +
                            addresses.get(0).getAdminArea() + ", " +
                            addresses.get(0).getCountryName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
