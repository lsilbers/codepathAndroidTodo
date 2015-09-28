package com.lsilberstein.todoapp;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    private static final String DETAILS_KEY = "details";
    public static final int DETAILS_CLOSE_ACTION = 1;
    private static final String IMAGE_KEY = "imageFile";
    private static final String TAG = "details_fragment";

    private MainActivity mainActivity;
    private String details;
    private String imageFile;
    private TextView tvDetailsWindow;
    private ImageView ivDetailsClose;

    private OnFragmentInteractionListener mListener;

    public static DetailsFragment newInstance(String details, String imageFile) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(DETAILS_KEY, details);
        args.putString(IMAGE_KEY, imageFile);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            details = getArguments().getString(DETAILS_KEY);
            imageFile = getArguments().getString(IMAGE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_details, container, false);
        tvDetailsWindow = (TextView) view.findViewById(R.id.tvDetailsWindow);
        if (details != null) {
            tvDetailsWindow.setText(details);
        } else {
            tvDetailsWindow.setText(R.string.default_details_text);
        }
        tvDetailsWindow.setMovementMethod(new ScrollingMovementMethod());
        ivDetailsClose = (ImageView) view.findViewById(R.id.ivCloseDetails);
        ivDetailsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDetailsFragmentInteraction(DETAILS_CLOSE_ACTION);
            }
        });
        if (imageFile != null) {
            //set the background of the details to the image
            Uri imageUri = mainActivity.getPhotoFileUri(imageFile);
            Log.d(TAG, "Got uri for image " + imageFile + ". Uri: " + imageUri.getPath());
            // by this point we have the camera photo on disk
            Bitmap image = BitmapFactory.decodeFile(imageUri.getPath());
            tvDetailsWindow.setBackground(new BitmapDrawable(image));
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mainActivity = (MainActivity) activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        public void onDetailsFragmentInteraction(int action);
    }

}
