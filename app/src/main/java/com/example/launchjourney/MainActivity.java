package com.example.launchjourney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private Runnable redirectRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imgWelcome = findViewById(R.id.imgWelcome);
        ImageView imageView1 = findViewById(R.id.enterImg);
        Button enterButton = findViewById(R.id.enterButton);

        imageView1.setImageResource(R.drawable.go);
        imgWelcome.setImageResource(R.drawable.welcome_icon);

        // ✅ Auto-redirect after 5 seconds
        redirectRunnable = () -> {
            Intent intent = new Intent(MainActivity.this, sign_in_activity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        };

        handler.postDelayed(redirectRunnable, 2000); // Schedule redirect after 5 sec

        // ✅ If user clicks button, cancel auto-redirect
        enterButton.setOnClickListener(v -> {
            cancelAutoRedirect();
            Intent intent = new Intent(MainActivity.this, sign_in_activity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // ✅ If user touches anywhere, cancel auto-redirect
        findViewById(R.id.main).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                cancelAutoRedirect();
                Intent intent = new Intent(MainActivity.this, sign_in_activity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                return true;
            }
            return false;
        });
    }

    // ✅ Cancel auto-redirect if user interacts
    private void cancelAutoRedirect() {
        handler.removeCallbacks(redirectRunnable);
    }
}
