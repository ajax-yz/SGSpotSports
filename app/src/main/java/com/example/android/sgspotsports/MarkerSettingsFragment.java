package com.example.android.sgspotsports;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MarkerSettingsFragment extends Fragment {


    private View view;
    private ArrayList<MarkerItems> mMarkerList;
    private DropDownListAdapter mAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView mChooseImage;
    private Button mButtonSave;
    private EditText mFacilityNameText, mDescriptionText;
    private ProgressBar mProgressBar;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserDatabase;

    private StorageReference fileReference;

    private UploadTask uploadTask;

    private FirebaseAuth mAuth;

    // Variables:
    private Uri mImageUri;
    private String selectedSportsType;
    private String mCurrent_user_id;
    private String mFacilityName, mDescription;
    private LatLng latLng;
    private Double lat, lng;
    private String address;

    public MarkerSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_marker_settings, container, false);

        // Getting data from the map picker
        Bundle bundle = getArguments();
        if (bundle != null) {
            address = bundle.getString("Address");
            lat = bundle.getDouble("Lat");
            lng = bundle.getDouble("Lng");

            //latLng = bundle.getParcelable("LatLng");

            /*
            Toast.makeText(getContext(), address, Toast.LENGTH_LONG).show();
            Log.d("ADDRESS : ", address);
            Toast.makeText(getContext(), String.valueOf(latLng), Toast.LENGTH_LONG).show();
            Log.d("LatLng : ", String.valueOf(latLng));
            */

        } else {
            Toast.makeText(getContext(), "Unable to retrieve location data", Toast.LENGTH_LONG).show();
        }

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialise list first
        initList();

        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mChooseImage = (ImageView) view.findViewById(R.id.upload_image);
        mButtonSave = (Button) view.findViewById(R.id.marker_save);
        mFacilityNameText = (EditText) view.findViewById(R.id.editTextFacilityName);
        mDescriptionText = (EditText) view.findViewById(R.id.editTextDescription);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        mStorageRef = FirebaseStorage.getInstance().getReference("Markers");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Markers");
        mUserDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mCurrent_user_id).child("Markers");

        // ------------------ Spinner method ------------------------
        Spinner spinnerMarkers = view.findViewById(R.id.sports_type_drop_down_list);
        mAdapter = new DropDownListAdapter(getContext(), mMarkerList);
        spinnerMarkers.setAdapter(mAdapter);

        spinnerMarkers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                MarkerItems selectedItem = (MarkerItems) parent.getItemAtPosition(position);

                selectedSportsType = selectedItem.getmSportsType();

                // Store type into firebase (to decide which marker image to use)
                // Toast.makeText(getContext(), selectedSportsType + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // No item selected
                // String selectedSportsType = "Default";
                // Toast.makeText(getContext(), selectedSportsType + " selected", Toast.LENGTH_SHORT).show();

            }

        });


        // ------------------ CHOOSE IMAGE ------------------------
        mChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        // ------------------ Upload to Firebase ------------------------
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }

            }
        });
    }

    private void uploadFile() {
        if (mImageUri != null) {
            fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask
                    (new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) { // throw task.getException();
                                throw Objects.requireNonNull(task.getException());
                            }
                            // Continue with the task to get the download URL
                            return fileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setProgress(0);
                            }
                        }, 500);

                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();

                        Uri downloadUri = task.getResult();
                        String image_download_url = downloadUri.toString();

                        // Derive latLng, address, price rating************************************************
                        mFacilityName = mFacilityNameText.getText().toString().trim();
                        mDescription = mDescriptionText.getText().toString().trim();

                        verifyEmptyEntries(mFacilityName, mDescription);

                        // Make Markers object and tie to the uploadId
                        Markers upload = new Markers(
                                lat, lng, mCurrent_user_id, mFacilityName, mDescription, image_download_url, address, selectedSportsType);

                        // Creates a unique id
                        String uploadId = mDatabaseRef.push().getKey();

                        // Add to database
                        mDatabaseRef.child(uploadId).setValue(upload);
                        mUserDatabase.child(uploadId).setValue(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    getFragmentManager().beginTransaction()
                                            .addToBackStack(null)
                                            .replace(R.id.fragment_container,
                                                    new ManageMarkersFragment()).commit();

                                } else {
                                    Toast.makeText(getContext(), "Failed to update database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressBar.setProgress((int) progress);
                }
            });

        }else{
            Toast.makeText(getActivity(), "Please upload an image of the facility", Toast.LENGTH_SHORT).show();
        }
    }


    private void verifyEmptyEntries(String mFacilityName, String mDescription) {

        if (mFacilityName.isEmpty()) {

            mFacilityNameText.setError("Facility name is required");
            mFacilityNameText.requestFocus();
        }

        if (mDescription.isEmpty()) {

            mDescriptionText.setError("Description is required");
            mDescriptionText.requestFocus();
        }

    }

    private void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(mChooseImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void initList() {

        mMarkerList = new ArrayList<>();
        mMarkerList.add(new MarkerItems("Select type...", R.drawable.ic_default_pin));
        mMarkerList.add(new MarkerItems("Basketball", R.drawable.ic_basketball_pin));
        mMarkerList.add(new MarkerItems("Badminton", R.drawable.ic_badminton_pin));
        mMarkerList.add(new MarkerItems("Stadium", R.drawable.ic_stadium_pin));
        mMarkerList.add(new MarkerItems("Soccer", R.drawable.ic_basketball_pin));
        mMarkerList.add(new MarkerItems("Swimming", R.drawable.ic_swim_pin));
        mMarkerList.add(new MarkerItems("Fitness Corner", R.drawable.ic_fitness_pin));

    }
}
