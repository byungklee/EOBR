package com.eobr;



import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment implements GPSListener {

    private final String TAG = "StatusFragment";

    private Button mNoteButton;
    private Button mOriginWaitingAtTheGate;
    private Button mOriginTerminalGateClosed;
    private Button mOriginFillingOutPaperWork;

    private Button mDestinWaitingAtTheGate;
    private Button mDestinTerminalGateClosed;
    private Button mDestinFillingOutPaperWork;

    private Button mRoadTraffic;
    private Button mRoadRefueling;
    private Button mRoadEquipmentProblem;

    private TextView mDetailStatusTextView;
    private GPSReceiver gpsReceiver;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


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
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StatusFragment() {
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
        View v = inflater.inflate(R.layout.fragment_status, container, false);

        mNoteButton = (Button) v.findViewById(R.id.note);
        mNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new NoteFragment());
                ft.addToBackStack("status");

                ft.commit();
            }
        });
        mDetailStatusTextView = (TextView) v.findViewById(R.id.scroll_view);
        mDetailStatusTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new DetailStatusFragment());
                ft.addToBackStack("status");

                ft.commit();
            }
        });
        mOriginWaitingAtTheGate = (Button) v.findViewById(R.id.origin_waiting_at_the_gate);
        mOriginWaitingAtTheGate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
                i.putExtra("type", "origin_waiting_at_the_gate");
                getActivity().startService(i);
            }
        });
        mOriginTerminalGateClosed = (Button) v.findViewById(R.id.origin_terminal_gate_closed);
        mOriginTerminalGateClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
                i.putExtra("type", "origin_terminal_gate_closed");
                getActivity().startService(i);
            }
        });
        mOriginFillingOutPaperWork = (Button) v.findViewById(R.id.origin_filling_out_paperwork);
        mOriginFillingOutPaperWork.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
                i.putExtra("type", "origin_filling_out_paperwork");
                getActivity().startService(i);
            }
        });
        mDestinWaitingAtTheGate = (Button) v.findViewById(R.id.destin_waiting_at_the_gate);
        mDestinWaitingAtTheGate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
                i.putExtra("type", "destin_waiting_at_the_gate");
                getActivity().startService(i);
            }
        });
        mDestinTerminalGateClosed = (Button) v.findViewById(R.id.destin_terminal_gate_closed);
        mDestinTerminalGateClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
                i.putExtra("type", "destin_terminal_gate_closed");
                getActivity().startService(i);
            }
        });
        mDestinFillingOutPaperWork = (Button) v.findViewById(R.id.destin_filling_out_paperwork);
        mDestinFillingOutPaperWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
                i.putExtra("type", "destin_filling_out_paperwork");
                getActivity().startService(i);
            }
        });
        mRoadTraffic = (Button) v.findViewById(R.id.road_traffic);
        mRoadTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
                i.putExtra("type", "road_traffic");
                getActivity().startService(i);
            }
        });
        mRoadRefueling = (Button) v.findViewById(R.id.road_refueling);
        mRoadRefueling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
                i.putExtra("type", "road_refueling");
                getActivity().startService(i);
            }
        });
        mRoadEquipmentProblem = (Button) v.findViewById(R.id.road_equipment_problem);
        mRoadEquipmentProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
                i.putExtra("type", "road_equipment_problem");
                getActivity().startService(i);
            }
        });



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

        if(!MainActivity.myLocationList.isEmpty())
            mDetailStatusTextView.setText(getListString());

        return v;
    }
    public String getListString() {
        StringBuilder sb = new StringBuilder();
        int size = MainActivity.myLocationList.size();
        if(size > 3) {
            for(int i=size - 3;i<size;i++) {
                MyLocation ml = MainActivity.myLocationList.get(i);
                sb.append(ml.getType() + " ").append(ml.getLatitude()).append(" ").append(ml.getLongitude()).append(" ").append(ml.getTimeString()).append("\n");
            }
        } else {
            for(MyLocation ml : MainActivity.myLocationList) {
            sb.append(ml.getType() + " ").append(ml.getLatitude()).append(" ").append(ml.getLongitude()).append(" ").append(ml.getTimeString()).append("\n");
         }
        }
        return sb.toString();
    }

    @Override
    public void execute(MyLocation location) {
        Log.i(TAG, location.getType() + " " + location.getLatitude() + " " + location.getLongitude());
        mDetailStatusTextView.setText(getListString());
        DbAdapter db = new DbAdapter(getActivity().getApplicationContext());


        if(MainActivity.CURRENT_TRIP_ID == -1) {
            SQLiteDatabase sqlDb = db.getReadableDatabase();
            String query="select trip_id from trips order by trip_id desc";
            Cursor curs=sqlDb.rawQuery(query, null);
            curs.moveToFirst();
            if(curs.getCount() != 0) {
                MainActivity.CURRENT_TRIP_ID = curs.getInt(0);
            } else
                MainActivity.CURRENT_TRIP_ID = 1;
            curs.close();
        }

        SQLiteDatabase sqlDb = db.getWritableDatabase();
        sqlDb.execSQL("insert into trips (trip_id, truck_id, trip_type, type, latitude, longitude, time) " +
                "values (" + MainActivity.CURRENT_TRIP_ID + ", \"" +MainActivity.TRUCK_ID + "\", \"" + MainActivity.tripType + "\", \"" + location.getType() + "\", " +
                location.getLatitude() + ", " + location.getLongitude() + ", \"" + location.getTimeString() + "\")");
        sqlDb.close();
        db.close();
    }

    @Override
    public void executeForSingle(String str, double latitude, double longitude, String note) {
        MyLocation location = new MyLocation(str,
                latitude,
                longitude);
        if(note != null) {
            location.setNote(note);
        }
        MainActivity.myLocationList.add(location);
        DbAdapter db = new DbAdapter(getActivity().getApplicationContext());
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        Log.i(TAG, "Checking time string " + location.getTimeString());
        sqlDb.execSQL("insert into trips (trip_id, truck_id, trip_type, type, latitude, longitude, time, note) " +
                "values (" + MainActivity.CURRENT_TRIP_ID + ", \"" +MainActivity.TRUCK_ID + "\", \"" + MainActivity.tripType + "\", \"" +
                location.getType() + "\", " +
                location.getLatitude() + ", " + location.getLongitude() + ", \"" + location.getTimeString() + "\", \"" + location.getNote() + "\")");
        Log.i(TAG, str + " " + latitude + " " + longitude );
        sqlDb.close();
        db.close();
        mDetailStatusTextView.setText(getListString());
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(gpsReceiver);
    }
}
