package com.example.saegeullee.myapplication.Dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.saegeullee.myapplication.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ChangePhotoDialog extends DialogFragment {

    private static final String TAG = "ChangePhotoDialog";

    private TextView choosePhoto, takePhoto;

    public static final int CAMERA_REQUEST_CODE = 5476;
    public static final int PICKFILE_REQUEST_CODE = 8352;

    public interface OnPhotoReceivedListener {
        void getImagePath(Uri ImagePath);
        void getImageBitmap(Bitmap bitmap);
    }

    OnPhotoReceivedListener mOnPhotoReceived;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_changephoto, container, false);
        choosePhoto = view.findViewById(R.id.dialogChoosePhoto);
        takePhoto = view.findViewById(R.id.dialogOpenCamera);

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: choosePhoto clicked");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: photo selected");
            Uri selectedImageUri = data.getData();

            CropImage.activity(selectedImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .setBorderLineColor(Color.RED)
                    .setGuidelinesColor(Color.GREEN)
                    .setBorderLineThickness(getResources().getDimensionPixelSize(R.dimen.thickness))
                    .start(getActivity());

            getDialog().dismiss();

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult: cropped image success");
                Uri mProfileResultUri = result.getUri();

                mOnPhotoReceived.getImagePath(mProfileResultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");
            mOnPhotoReceived.getImageBitmap(bitmap);
            getDialog().dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        try {
            mOnPhotoReceived = (OnPhotoReceivedListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException ", e.getCause() );
        }
        super.onAttach(context);
    }
}
