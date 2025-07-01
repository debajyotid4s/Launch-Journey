package com.example.launchjourney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class creating_account_page extends AppCompatActivity {

    EditText edtname, edtadress, edtpass, edtemail, edtcontact;
    ProgressBar next_progress_bar;
    MaterialButton btnRegister, btnNext;

    private FirebaseFirestore firestore;
    private DatabaseReference realtimeDb;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creating_account_page);

        // Initialize Firebase components
        firestore = FirebaseFirestore.getInstance();
        realtimeDb = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        // Initialize UI components
        edtname = findViewById(R.id.edtname);
        edtadress = findViewById(R.id.edtadress);
        edtpass = findViewById(R.id.edtpass);
        edtemail = findViewById(R.id.edtemail);
        edtcontact = findViewById(R.id.edtcontact);
        btnRegister = findViewById(R.id.btnRegData);
        btnNext = findViewById(R.id.btnNext);
        next_progress_bar = findViewById(R.id.next_progress_bar);
        next_progress_bar.setVisibility(View.GONE);

        btnRegister.setOnClickListener(view -> {
            storeUserDataToDB();
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(creating_account_page.this, otp_verification_page.class);
                startActivity(intent);
            }
        });
    }

    private void storeUserDataToDB() {
        String name = edtname.getText().toString().trim();
        String address = edtadress.getText().toString().trim();
        String password = edtpass.getText().toString().trim();
        String email = edtemail.getText().toString().trim();
        String contact = edtcontact.getText().toString().trim();

        // Validate input fields
        if (name.isEmpty() || address.isEmpty() || password.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.contains("@")) {
            Toast.makeText(this, "Invalid email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        next_progress_bar.setVisibility(View.VISIBLE);

        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                        if (!isNewUser) {
                            next_progress_bar.setVisibility(View.GONE);
                            Toast.makeText(creating_account_page.this,
                                    "Account already registered. Please log in.",
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(creating_account_page.this, sign_in_activity.class);
                            startActivity(intent);
                        } else {
                            registerNewUser(name, address, email, contact, password);
                        }
                    } else {
                        next_progress_bar.setVisibility(View.GONE);
                        Toast.makeText(creating_account_page.this,
                                "Error checking email: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerNewUser(String name, String address, String email,
                                 String contact, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", name);
                        userData.put("address", address);
                        userData.put("email", email);
                        userData.put("contact", contact);
                        userData.put("password", hashedPassword);
                        userData.put("userId", userId);

                        firestore.collection("users").document(userId)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    next_progress_bar.setVisibility(View.GONE);
                                    realtimeDb.child("users").child(userId).setValue(true)
                                            .addOnSuccessListener(aVoid2 -> {
                                                Toast.makeText(this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                                                // **Sign out after registration so user is NOT logged in**
                                                auth.signOut();
                                                // Stay on this page
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Failed to save UID in Realtime Database", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    next_progress_bar.setVisibility(View.GONE);
                                    Toast.makeText(this, "Failed to register User in Firestore", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        next_progress_bar.setVisibility(View.GONE);
                        Toast.makeText(this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Ensure back goes to signup page
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(creating_account_page.this, sign_in_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}