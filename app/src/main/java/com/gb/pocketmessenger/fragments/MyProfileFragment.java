package com.gb.pocketmessenger.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewAnimator;
import android.widget.Button;

import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.utils.CircleImageView;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment {

    private static final String CROPPED_AVATAR_FILE_NAME = "avatar_crop.jpg";
    private static final String AVATAR_FILE_NAME = "avatar.jpg";
    private static final String USER_AVATAR_URI = "user_avatar_uri";
    private static final int CAMERA_REQUEST_CODE = 1001;


    private Uri avatarImageUri;
    private SharedPreferences myProfileSettings;
    private CircleImageView profilePhoto;
    private Uri filePhotoPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        Button loadImageFromGalleryButton = v.findViewById(R.id.loadFromGalleryButton);
        Button loadImageFromCamera = v.findViewById(R.id.loadFromCameraButton);
        profilePhoto = v.findViewById(R.id.my_profile_avatar);
        loadImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(getActivity().getApplicationContext(), MyProfileFragment.this);
            }
        });
        loadImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPhotoFromCamera();
            }
        });
        if (!readSettings(USER_AVATAR_URI).equals("null")) {
            avatarImageUri = Uri.parse(readSettings(USER_AVATAR_URI));
            profilePhoto.setImageURI(avatarImageUri);
        }
        return v;
    }

    private void getPhotoFromCamera() {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photoIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            File imagePath = new File(getActivity().getCacheDir(), "images");
            File filePhoto = new File(imagePath, AVATAR_FILE_NAME);
            filePhotoPath = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                    "com.pocketmessenger.android.fileprovider", filePhoto);

            if (filePhoto != null) {
                photoIntent.putExtra("TEST", filePhotoPath.toString());
                startActivityForResult(photoIntent, CAMERA_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (filePhotoPath != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                saveSettings(USER_AVATAR_URI, filePhotoPath.toString());
                profilePhoto.setImageBitmap(imageBitmap);
            }
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void beginCrop(Uri data) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), CROPPED_AVATAR_FILE_NAME));
        Crop.of(data, destination).asSquare().start(getActivity(), MyProfileFragment.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            profilePhoto.setImageResource(0);
            avatarImageUri = Crop.getOutput(result);
            saveSettings(USER_AVATAR_URI, avatarImageUri.toString());
            profilePhoto.setImageURI(avatarImageUri);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Log.d("ERROR", "ERROR ON CROP IMAGE");
        }
    }

    private void saveSettings(String tag, String value) {
        myProfileSettings = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myProfileSettings.edit();
        editor.putString(tag, value);
        editor.apply();
    }

    private String readSettings(String key) {
        myProfileSettings = getActivity().getPreferences(Context.MODE_PRIVATE);
        return myProfileSettings.getString(key, "null");

    }
}
