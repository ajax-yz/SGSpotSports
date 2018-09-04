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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private StorageTask mUploadTask;

    private FirebaseAuth mAuth;

    // Variables:
    private Uri mImageUri;
    private String selectedSportsType;
    private String mCurrent_user_id;
    private String mFacilityName, mDescription;
    private LatLng latLng;
    private String address;

    public MarkerSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            address = bundle.getString("Address");
            latLng = bundle.getParcelable("LatLng");

            Toast.makeText(getContext(), address, Toast.LENGTH_LONG).show();
            Log.d("ADDRESS : ", address);
            Toast.makeText(getContext(), String.valueOf(latLng), Toast.LENGTH_LONG).show();
            Log.d("LatLng : ", String.valueOf(latLng));
        } else {
            Toast.makeText(getContext(), "bundle is empty",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_marker_settings, container, false);
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

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }

            }
        });
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();

                            // Derive latLng, address, price rating************************************************
                            mFacilityName = mFacilityNameText.getText().toString().trim();
                            mDescription = mDescriptionText.getText().toString().trim();

                            verifyEmptyEntries(mFacilityName, mDescription);

                            // Make Markers object and tie to the uploadId

                            // Creates a unique id
                            String uploadId = mDatabaseRef.push().getKey();

                            // Add to database
                            mDatabaseRef.child(uploadId).setValue(null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Please upload an image of the facility", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyEmptyEntries(String mFacilityName, String mDescription){

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
