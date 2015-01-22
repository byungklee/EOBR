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

    /**
     * Variable declaration
     */
    private TextView textview;

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
        return v;
    }


}
