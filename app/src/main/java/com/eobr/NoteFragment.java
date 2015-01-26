package com.eobr;


import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;


/**
 * NoteFragment is for an user to write some extra information other than options provided in the
 * status view. This note is written as description and calls GPSIntentService to get the location.
 */
public class NoteFragment extends Fragment {
    private final static String TAG = "NoteFragment";
    public static int noteNumber = 1;
    private Button mCancelButton;
    private Button mSaveButton;
    private MediaRecorder mRecorder = null;
    private String mFileName;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment NoteFragment.
     */
    public static NoteFragment newInstance() {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Initialize a media recorder and start when the fragment is on.
     */
    public void initializeMediaRecorder() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note, container, false);

        //Get a new file name for a record.
        mFileName = getActivity().getFilesDir().getAbsolutePath() + "/" + MainActivity.TRUCK_ID + "_" + MainActivity.CURRENT_TRIP_ID + "_" + noteNumber +".3gp";

        //initialize the media recorder.
        initializeMediaRecorder();

        /*
            When the cancel button is clicked, just stop recording and pop backstack.
         */
        mCancelButton = (Button) v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Toast.makeText(getActivity().getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
                 stopRecording();
                 getFragmentManager().popBackStack();
             }
            }
        );

        /*
            When the save button is clicked
         */
        mSaveButton = (Button) v.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
                NoteList.getInstance().add(mFileName);
                noteNumber++;
                Intent i = new Intent(getActivity().getApplicationContext(), GPSIntentService.class);
                Toast.makeText(getActivity().getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                i.putExtra("type", "note");
                i.putExtra("note", mFileName);
                getActivity().startService(i);
                getFragmentManager().popBackStack();
            }
        });

        return v;
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

}
