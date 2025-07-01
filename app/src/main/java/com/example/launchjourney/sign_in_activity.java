package com.example.launchjourney;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class sign_in_activity extends AppCompatActivity {
    TextInputEditText emailEditText, passwordEditText;
    MaterialButton loginButton, signUpButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginBtn);
        signUpButton = findViewById(R.id.signUpBtn);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Redirect to home page if user is already logged in
            Intent intent = new Intent(sign_in_activity.this, home_page_activity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sign_in_activity.this, creating_account_page.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });



        // Handle login button click
        loginButton.setOnClickListener(view -> {
            String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
            String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();

            // Validate inputs
            if (email.isEmpty() || password.isEmpty()) {
                showDialog("Error", "Please fill in both email and password.");
                return;
            }


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign-in successful
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(sign_in_activity.this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();

                            // Redirect to home page
                            Intent intent = new Intent(sign_in_activity.this, home_page_activity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                            } else {
                            // Sign-in failed
                            Exception e = task.getException();
                            if (e != null) {
                                Log.e("FirebaseAuth", "Sign-in error: " + e.getMessage());
                                Toast.makeText(sign_in_activity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Log.e("FirebaseAuth", "Sign-in failed: Unknown error");
                                Toast.makeText(sign_in_activity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        });
    }



    // Function to show a dialog box
    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(sign_in_activity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
