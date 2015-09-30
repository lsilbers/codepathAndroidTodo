package com.lsilberstein.todoapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SettingsDialog extends DialogFragment implements View.OnClickListener {

    private MainActivity mainActivity;

    public SettingsDialog() {
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
        View view = inflater.inflate(R.layout.fragment_settings_dialog, container, false);
        view.findViewById(R.id.btnCamera).setOnClickListener(this);
        view.findViewById(R.id.btnGallery).setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mainActivity = (MainActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSettingsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    // called when one of the buttons is clicked
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCamera:
                onLaunchCamera();
                break;
            case R.id.btnGallery:
                onPickPhoto();
                break;
            default:
                dismiss();
        }
    }

    public interface OnSettingsInteractionListener {
        public void onSettingsDone(Uri uri);
    }

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;

    // Trigger gallery selection for a photo
    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Bring up gallery to select a photo
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mainActivity.getPhotoFileUri(MainActivity.BACKGROUND)); // set the image file name
        // Start the image capture intent to take photo
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri photoUri = null;
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                photoUri = mainActivity.getPhotoFileUri(MainActivity.BACKGROUND);
            }
        } else if (requestCode == PICK_PHOTO_CODE) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                photoUri = data.getData();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        mainActivity.onSettingsDone(photoUri);
        dismiss();
    }



}
