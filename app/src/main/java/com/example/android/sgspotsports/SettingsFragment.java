package com.example.android.sgspotsports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private View view;

    // Android layout
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    private Button mStatusBtn;
    private Button mImageBtn;

    private static final int GALLERY_PICK = 1;
    private static final int MAX_LENGTH = 10;

    // Storage Firebase
    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDisplayImage = (CircleImageView) view.findViewById(R.id.settings_profile_photo);
        mName = (TextView) view.findViewById(R.id.settings_profile_name);
        mStatus = (TextView) view.findViewById(R.id.settings_profile_status);

        mStatusBtn = (Button) view.findViewById(R.id.button_change_status);
        mImageBtn = (Button) view.findViewById(R.id.button_change_image);

        // Retrieving user uid

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                Picasso.get().load(image).into(mDisplayImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* using intent to display the status
                String status_value = mStatus.getText().toString();
                status_intent.putExtra("status_value", status_value);
                */

                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.fragment_container,
                        new StatusFragment()).commit();

            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent galleyIntent = new Intent();
                galleyIntent.setType("image/*");
                galleyIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleyIntent, "SELECT_IMAGE"), GALLERY_PICK);


                /* start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity());
                */

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            // Testing
            Toast.makeText(getActivity(), "Cropping started", Toast.LENGTH_LONG).show();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(getActivity());

        }

        Toast.makeText(getActivity(), "Cropping...1", Toast.LENGTH_LONG).show();

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            // Testing
            Toast.makeText(getActivity(), "Cropping...2", Toast.LENGTH_LONG).show();

            if (resultCode == RESULT_OK) {

                // Testing
                Toast.makeText(getActivity(), "Cropping...3", Toast.LENGTH_LONG).show();

                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                final String current_user_id = mCurrentUser.getUid();

                // Testing
                Toast.makeText(getActivity(), current_user_id, Toast.LENGTH_LONG).show();

                StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");


                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {


                            Toast.makeText(getActivity(), "Working", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();

                            /*

                            // Test and check the .child("image.jpg")
                            mImageStorage.child("profile_images").child(current_user_id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String downloadUrl = uri.toString();

                                    mUserDatabase.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(getActivity(), "Successfully uploaded", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                            //Toast.makeText(getActivity(), "Working", Toast.LENGTH_LONG).show();

                            */

                        } else {

                            Toast.makeText(getActivity(), "Error in uploading", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();

                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(getActivity(), "Error in uploading", Toast.LENGTH_LONG).show();

            }
        }

        // Testing
        Toast.makeText(getActivity(), "Cropping...4", Toast.LENGTH_LONG).show();

    }



    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
