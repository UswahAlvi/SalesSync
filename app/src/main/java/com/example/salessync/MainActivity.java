package com.example.salessync;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private FragmentManager manager;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Fragment loginFrag, signupFrag;
    private Button btnLogin, btnSignup, btnCancelL, btnCancelS;
    private TextInputEditText etEmailL, etPwdL;
    private TextInputEditText etEmailS, etPwdS, etCpwdS;
    private TextView tvLogin, tvSignup;
    private ProgressBar progress_bar;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        tvLogin.setOnClickListener(v -> switchToLoginFragment());

        tvSignup.setOnClickListener(v -> switchToSignupFragment());

        btnCancelL.setOnClickListener(v -> clearLoginFields());

        btnCancelS.setOnClickListener(v -> clearSignupFields());

        btnSignup.setOnClickListener(v -> handleSignup());

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void switchToLoginFragment() {
        manager.beginTransaction()
                .show(loginFrag)
                .hide(signupFrag)
                .addToBackStack(null)
                .commit();
    }

    private void switchToSignupFragment() {
        manager.beginTransaction()
                .show(signupFrag)
                .hide(loginFrag)
                .addToBackStack(null)
                .commit();
    }

    private void clearLoginFields() {
        etEmailL.setText("");
        etPwdL.setText("");
    }

    private void clearSignupFields() {
        etEmailS.setText("");
        etPwdS.setText("");
        etCpwdS.setText("");
    }

    private void handleSignup() {
        String email = Objects.requireNonNull(etEmailS.getText()).toString().trim();
        String pwd = Objects.requireNonNull(etPwdS.getText()).toString();
        String cPwd = Objects.requireNonNull(etCpwdS.getText()).toString();

        if (isValid(email, pwd, cPwd)) {
            ProgressDialog processing = new ProgressDialog(MainActivity.this);
            processing.setMessage("Registration in process...");
            processing.show();

            auth.createUserWithEmailAndPassword(email, pwd)
                    .addOnSuccessListener(authResult -> {
                        processing.dismiss();
                        switchToLoginFragment();
                    })
                    .addOnFailureListener(e -> {
                        processing.dismiss();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void handleLogin() {
        String email = Objects.requireNonNull(etEmailL.getText()).toString().trim();
        String pass = Objects.requireNonNull(etPwdL.getText()).toString();

        if (notEmpty(email, pass)) {
            progress_bar.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> {
                        progress_bar.setVisibility(View.GONE);
                        startActivity(new Intent(MainActivity.this, Home.class));
                    })
                    .addOnFailureListener(e -> {
                        progress_bar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private boolean notEmpty(String email, String pwd) {
        boolean flag = true;
        if (email.isEmpty()) {
            etEmailL.setError("Please fill the field");
            flag = false;
        } else if (!isValidEmail(email)) {
            etEmailL.setError("Email format incorrect");
            flag = false;
        } else if (pwd.isEmpty()) {
            etPwdL.setError("Please fill the field");
            flag = false;
        }
        return flag;
    }

    private boolean containsDigit(String pwd) {
        return pwd.matches(".*\\d.*");
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return email != null && pat.matcher(email).matches();
    }

    private boolean isValid(String email, String pwd, String cPwd) {
        boolean flag = true;

        if (!isValidEmail(email)) {
            etEmailS.setError("Email incorrect format");
            flag = false;
        } else if (pwd.isEmpty() || !containsDigit(pwd)) {
            etPwdS.setError("At least 1 digit with 8-15 characters");
            flag = false;
        } else if (cPwd.isEmpty()) {
            etCpwdS.setError("Confirm Password cannot be empty");
            flag = false;
        } else if (!pwd.equals(cPwd)) {
            Toast.makeText(MainActivity.this, "Password and confirm password do not match", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        return flag;
    }

    private void init() {
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(MainActivity.this, Home.class));
            finish();
        }
        manager = getSupportFragmentManager();
        loginFrag = manager.findFragmentById(R.id.fragLogin);
        signupFrag = manager.findFragmentById(R.id.fragSignup);

        if (signupFrag != null) {
            View signupFragView = signupFrag.requireView();
            etEmailS = signupFragView.findViewById(R.id.etEmailS);
            etPwdS = signupFragView.findViewById(R.id.etPwdS);
            etCpwdS = signupFragView.findViewById(R.id.etCpwdS);
            btnSignup = signupFragView.findViewById(R.id.btnSignup);
            btnCancelS = signupFragView.findViewById(R.id.btnCancelS);
            tvLogin = signupFragView.findViewById(R.id.tvLogin);
        }

        if (loginFrag != null) {
            View loginFragView = loginFrag.requireView();
            etEmailL = loginFragView.findViewById(R.id.etEmailL);
            etPwdL = loginFragView.findViewById(R.id.etPwdL);
            btnLogin = loginFragView.findViewById(R.id.btnLogin);
            btnCancelL = loginFragView.findViewById(R.id.btnCancelL);
            tvSignup = loginFragView.findViewById(R.id.tvSignup);
            progress_bar = loginFragView.findViewById(R.id.progress_bar);
            progress_bar.setVisibility(View.GONE);
        }

        manager.beginTransaction()
                .show(signupFrag)
                .commit();
    }
}
