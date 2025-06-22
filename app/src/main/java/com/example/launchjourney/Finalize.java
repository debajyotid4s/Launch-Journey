package com.example.launchjourney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Finalize extends AppCompatActivity {
    private String route, date, ticketType, launchName;
    private int ticketQuantity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_finalize);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView done = findViewById(R.id.imageDone);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Finalize.this, home_page_activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Close Finalize activity
        }, 5000); // 4000ms = 4 seconds
        String imageUrl = "https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExb3A2cHE2Zm5pbWN4bzdwYjNoaGo5dGtyc2tmY2loazFkdmVpaXcwNCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9cw/6PBcDjGlOJccTziZVS/giphy.gif"; // Replace with your image URL

        Glide.with(this)
                .load(imageUrl)
                .apply(new RequestOptions()
                )
                .into(done);

    }


}