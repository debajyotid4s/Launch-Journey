package com.example.launchjourney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.launchjourney.databinding.HomePageActivityBinding;

public class TicketCanceled extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_canceled);

        // Handle edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Redirect to Home screen after 3 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(TicketCanceled.this, home_page_activity.class);
            startActivity(intent);
            finish(); // Close this activity
        }, 3000); // 3000 milliseconds = 3 seconds
    }
}