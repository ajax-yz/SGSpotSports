package com.example.android.sgspotsports;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class SetupFragment extends Fragment {

    private static final int CHOOSE_IMAGE = 101;
    private static Context applicationContext = MainActivity.getContextOfApplication();

    private View view;
    private TextView textView;
    private ImageView imageView;
    private EditText editText;

    private Uri uriProfileImage;
    private ProgressBar progressBar;

    private String profileImageUrl;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_setup, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Decoding
        setHasOptionsMenu(true);

        mProgressDialog = new ProgressDialog(getContext());

        mAuth = FirebaseAuth.getInstance();


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        editText = (EditText) view.findViewById(R.id.editTextDisplayName);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        textView = (TextView) view.findViewById(R.id.textViewVerified);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        loadUserInformation();

        view.findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
    }

    // OR put under onCreateView
    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            //finish();
            LogInFragment logInFragment = new LogInFragment();
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.fragment_container,
                    new LogInFragment()).commit();
        }
    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(applicationContext)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }

            if (user.getDisplayName() != null) {
                editText.setText(user.getDisplayName());
            }

            if (user.isEmailVerified()) {
                textView.setText(R.string.verified);
            } else {
                textView.setText(R.string.verify_now);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProgressDialog.setTitle("Verification email");
                        mProgressDialog.setMessage("Sending verification email..");
                        mProgressDialog.show();

                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(), "Verification Email Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }


    private void saveUserInformation() {


        String displayName = editText.getText().toString();

        if (displayName.isEmpty()) {
            editText.setError("Name required");
            editText.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profileImageUrl != null) {
            // progressBar.setVisibility(View.VISIBLE);
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();

                                //Temporary code to launch user profile page
                                ProfileFragment profileFragment = new ProfileFragment();
                                FragmentManager manager = getFragmentManager();
                                manager.beginTransaction().replace(R.id.fragment_container,
                                        new ProfileFragment()).commit();

                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), uriProfileImage);

                /*
                InputStream inputStream = PartAuthority.getAttachmentStream(context, masterSecret, uri);
                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(myStream, false);
                Bitmap region = decoder.decodeRegion(new Rect(10, 10, 50, 50), null);
                */

                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        mStorageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            mStorageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

}
