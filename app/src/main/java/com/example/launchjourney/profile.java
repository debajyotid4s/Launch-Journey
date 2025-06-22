package com.example.launchjourney;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {

    TextView txtName, txtEmail, txtPhone, txtAddress;
    Button btnchange;
    ImageView profileImage;
    Uri imageUri;

    FirebaseUser currentUser;
    DatabaseReference userRef;
    StorageReference storageRef;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference("ProfileImages");

        // UI Elements
        txtName = findViewById(R.id.textName);
        txtEmail = findViewById(R.id.textView5);
        txtPhone = findViewById(R.id.textPhone);
        txtAddress = findViewById(R.id.textAdress);
        profileImage = findViewById(R.id.img_profile);
        btnchange = findViewById(R.id.btn_change);

        db = FirebaseFirestore.getInstance();

        // Load User Data from Firebase
        loadUserProfile();

        btnchange.setOnClickListener(view -> {
            Intent intent = new Intent(profile.this, contact.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

    }



    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = document.getString("name");
                    String email = document.getString("email");
                    String contact = document.getString("contact");
                    String address = document.getString("address");

                    // Set the values to TextViews
                    txtName.setText(name);
                    txtEmail.setText(email);
                    txtPhone.setText(contact);
                    txtAddress.setText(address);
                } else {
                    Toast.makeText(this, "User data does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}