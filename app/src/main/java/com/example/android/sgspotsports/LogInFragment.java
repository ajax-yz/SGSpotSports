package com.example.android.sgspotsports;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LogInFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private Button logIn;
    private TextView signUp, forgotPassword;
    //private ProgressBar progressBar;
    private ProgressDialog mLoginProgress;
    private View view;

    private DatabaseReference mUserDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);

        //progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mLoginProgress = new ProgressDialog(getActivity());

        forgotPassword = (TextView) view.findViewById(R.id.textViewForgotPassword);
        forgotPassword.setOnClickListener(this);

        signUp = (TextView) view.findViewById(R.id.textViewSignup);
        signUp.setOnClickListener(this);

        logIn = (Button) view.findViewById(R.id.buttonLogin);
        logIn.setOnClickListener(this);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    private void userLogin() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
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
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        mLoginProgress.setTitle("Logging In");
        mLoginProgress.setMessage("Please wait while we check your credentials.");
        mLoginProgress.setCanceledOnTouchOutside(false);
        mLoginProgress.show();
        // progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {

                    mLoginProgress.dismiss();


                    final String current_user_id = mAuth.getCurrentUser().getUid();

                    // getToken is deprecated
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String deviceToken = instanceIdResult.getToken();
                            Log.e("newToken",deviceToken);

                            mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                //finish();
                                                // Changed to profile fragment for videoing
                                                //SetupSetupFragment setupFragment = new SetupSetupFragment();
                                                FragmentManager manager = getFragmentManager();
                                                manager.beginTransaction().replace(R.id.fragment_container,
                                                        new ProfileFragment()).commit();
                                                //Toast.makeText(getActivity(), "Successfully uploaded token", Toast.LENGTH_SHORT).show();

                                            } else {

                                                Toast.makeText(getActivity(), "Error in storing token", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                        }
                    });

                    // Add to back stack for fragment (back button) intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                } else {
                    mLoginProgress.hide();
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Consider putting inside onCreateView method as it overrides the parent activity
    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //finish();

            // Changed accountFragment to ProfileFragment, should rename account fragment to setup
            //SetupFragment accountFragment = new SetupSetupFragment();
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewSignup:
                //finish();
                SignUpFragment signUpFragment = new SignUpFragment();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,
                        new SignUpFragment()).commit();
                break;

            case R.id.buttonLogin:
                userLogin();
                break;

            case R.id.textViewForgotPassword:
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,
                        new ForgotPasswordFragment()).commit();
                break;
        }
    }

}
