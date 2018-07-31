package com.example.android.sgspotsports;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment {

    private EditText editEmail;
    private Button btnResetPassword;
    private FirebaseAuth mAuth;
    private View view;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(getContext());

        editEmail = (EditText) view.findViewById(R.id.editTextResetEmail);
        btnResetPassword = (Button) view.findViewById(R.id.buttonReset);

        btnResetPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                }

                mProgressDialog.setTitle("Password Reset Email");
                mProgressDialog.setMessage("Sending password reset email..");
                mProgressDialog.show();

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mProgressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Please check email to reset your password", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Fail to send email to reset password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}