package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;

public class SignupFragment extends Fragment {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FragmentManager fragmentManager;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    final String passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        fragmentManager = getFragmentManager();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        btnSignIn = view.findViewById(R.id.sign_in_button);
        btnSignUp = view.findViewById(R.id.sign_up_button);
        inputEmail = view.findViewById(R.id.email);
        inputPassword = view.findViewById(R.id.password);
        progressBar = view.findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new LoginFragment()).addToBackStack("login").commit();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    inputEmail.setError(getString(R.string.enter_email));
                }
                else if (password.isEmpty()) {
                    inputPassword.setError(getString(R.string.enter_password));
                }
                else if (password.length() < 8) {
                    Toast.makeText(getContext(), getString(R.string.password_length_toast), Toast.LENGTH_SHORT).show();
                }
                else if(email.matches(emailPattern)) {
                    if (password.matches(passwordPattern)) {
                        progressBar.setVisibility(View.VISIBLE);
                        //create user
                        if (getActivity() != null)
                            auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NotNull Task<AuthResult> task) {
                                            Toast.makeText(getContext(),getString(R.string.account_created), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            // If sign in fails, display a message to the user. If sign in succeeds
                                            // the auth state listener will be notified and logic to handle the
                                            // signed in user can be handled in the listener.
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getContext(),getString(R.string.authentication_failed) + task.getException(),
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                fragmentManager.beginTransaction().replace(R.id.content_frame, new MainPage()).addToBackStack("login").commit();
                                            }
                                        }
                                    });
                    } else {
                        inputPassword.setError(getString(R.string.password_msg));
                    }
                }else {
                        inputEmail.setError(getString(R.string.invalid_email));
                    }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}