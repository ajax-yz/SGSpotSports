package com.example.android.sgspotsports;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private EditText editTextEmail, editTextPassword, editTextDisplayName;
    //private ProgressBar progressBar;

    //
    private ProgressDialog mRegProgress;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Button signUp;
    private View view;
    private TextView logIn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        editTextDisplayName = (EditText) view.findViewById(R.id.editTextDisplayName);

        //progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        mRegProgress = new ProgressDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();

        signUp = (Button) view.findViewById(R.id.buttonSignUp);
        signUp.setOnClickListener(this);

        logIn = (TextView) view.findViewById(R.id.textViewLogin);
        logIn.setOnClickListener(this);

        // Set title of toolbar (doesn't work)
        // getSupportActionBar().setTitle("Sign Up A New Account");
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Creates a back button at top left

    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String display_name = editTextDisplayName.getText().toString();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (display_name.isEmpty()) {
            editTextDisplayName.setError("Display name is required");
            editTextDisplayName.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum length of password should contain at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        // progressBar.setVisibility(View.VISIBLE);

        mRegProgress.setTitle("Registering User");
        mRegProgress.setMessage("Please wait while we create your account");
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                //progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    final String uid = current_user.getUid();

                    // getToken is deprecated
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String deviceToken = instanceIdResult.getToken();

                            // Root & child directory
                            mDatabase = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(uid);

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", display_name);
                            userMap.put("status", "You may update your status here");
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");
                            userMap.put("device_token", deviceToken);

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        mRegProgress.dismiss();
                                        //finish();
                                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                new SettingsFragment()).commit();

                                    }
                                }
                            });
                        }
                    });
                } else {
                    mRegProgress.hide();
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getActivity(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignUp:
                registerUser();
                break;

            case R.id.textViewLogin:
                //finish();
                LogInFragment logInFragment = new LogInFragment();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,
                        new LogInFragment()).commit();
                break;
        }
    }
}
