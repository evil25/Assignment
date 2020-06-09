package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class LoginFragment extends Fragment {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin;
    private TextView btnReset;
    private FragmentManager fragmentManager;
    private long back_pressed;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        fragmentManager = getFragmentManager();

        //hide Toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new MainPage()).addToBackStack("login").commit();
        }

        inputEmail = view.findViewById(R.id.email);
        inputPassword = view.findViewById(R.id.password);
        progressBar = view.findViewById(R.id.progressBar);
        btnSignup = view.findViewById(R.id.btn_signup);
        btnLogin = view.findViewById(R.id.btn_login);
        btnReset = view.findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //Click on SignUp Button
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.content_frame,new SignupFragment()).addToBackStack("login").commit();

            }
        });

        //Click on Forget password
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.content_frame,new ResetPasswordFragment()).addToBackStack("login").commit();

            }
        });

        //Click on Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (email.isEmpty()) {
                    inputEmail.setError(getString(R.string.enter_email));
                } else if (password.isEmpty()) {
                    inputPassword.setError(getString(R.string.enter_password));
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    //authenticate user
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NotNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    progressBar.setVisibility(View.GONE);
                                    if (!task.isSuccessful()) {
                                        // there was an error
                                        if (password.length() < 6) {
                                            inputPassword.setError(getString(R.string.minimum_password));
                                        } else {
                                            Toast.makeText(getContext(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        fragmentManager.beginTransaction().replace(R.id.content_frame, new MainPage()).addToBackStack("login").commit();
                                    }
                                }
                            });
                }
            }
        });

        //onBackPressed
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        back_pressed = System.currentTimeMillis();// Backpressed initialized //
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (back_pressed++ > System.currentTimeMillis()) {
                        Log.d("Time backpressed..if", "" + back_pressed);
                        Log.d("Time system..if", "" + System.currentTimeMillis());
                        getActivity().finish();
                    }else {
                        back_pressed = (System.currentTimeMillis()+2000);
                        Log.d("Time backpressed..else", "" + back_pressed);
                        Log.d("Time system..else", "" + System.currentTimeMillis());
                    }
                    return true;
                }
                return false;
            }
        });
        return view;
    }
}

