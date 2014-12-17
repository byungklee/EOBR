package com.eobr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailStatusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView textview;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailStatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailStatusFragment newInstance(String param1, String param2) {
        DetailStatusFragment fragment = new DetailStatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailStatusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.detail_status, container, false);
        textview = (TextView) v.findViewById(R.id.log_textview);
        textview.setMovementMethod(new ScrollingMovementMethod());
        StringBuilder sb = new StringBuilder();
        for(MyLocation ml : MainActivity.myLocationList) {
            sb.append(ml.getType() + " ").append(ml.getLatitude()).append(" ").append(ml.getLongitude()).append(" ").append(ml.getTimeString()).append("\n");
        }
        textview.setText(sb.toString());

        return v;
    }


}
