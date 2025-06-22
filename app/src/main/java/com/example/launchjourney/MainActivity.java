package com.example.launchjourney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.animation.ValueAnimator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {
    private static final int AUTO_REDIRECT_DELAY = 2000; // 2 seconds
    private Handler handler = new Handler();
    private Runnable redirectRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        ImageView imgWelcome = findViewById(R.id.imgWelcome);
        View enterButton = findViewById(R.id.enterButton);

        // Set welcome image
        imgWelcome.setImageResource(R.drawable.welcome_icon);

        // Setup animations for a more engaging experience
        setupAnimations();

        // Setup automatic redirection
        setupAutoRedirect();

        // Setup click handler for the enter button
        enterButton.setOnClickListener(v -> navigateToSignIn());

        // Allow clicking anywhere to navigate
        findViewById(R.id.main).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                navigateToSignIn();
                return true;
            }
            return false;
        });
    }

    private void setupAnimations() {
        // Find views to animate
        ImageView imgWelcome = findViewById(R.id.imgWelcome);
        TextView welcomeText = findViewById(R.id.WelcomePageText);
        View divider = findViewById(R.id.divider);
        CardView enterButtonContainer = findViewById(R.id.enterButtonContainer);
        TextView tagline = findViewById(R.id.WelcomePageText2);

        // Set initial states
        imgWelcome.setAlpha(0f);
        welcomeText.setTranslationY(50f);
        welcomeText.setAlpha(0f);
        divider.setScaleX(0f);
        enterButtonContainer.setScaleX(0f);
        enterButtonContainer.setScaleY(0f);
        tagline.setAlpha(0f);

        // Animate logo/image
        imgWelcome.animate()
                .alpha(1f)
                .setDuration(500)
                .start();

        // Animate welcome text
        welcomeText.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(300)
                .start();

        // Animate divider
        divider.animate()
                .scaleX(1f)
                .setDuration(300)
                .setStartDelay(600)
                .start();

        // Animate enter button
        enterButtonContainer.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setStartDelay(700)
                .withEndAction(() -> {
                    // Create a subtle pulse animation
                    ValueAnimator pulseAnimator = ValueAnimator.ofFloat(1f, 1.05f);
                    pulseAnimator.setDuration(800);
                    pulseAnimator.setRepeatMode(ValueAnimator.REVERSE);
                    pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
                    pulseAnimator.addUpdateListener(animation -> {
                        float value = (float) animation.getAnimatedValue();
                        enterButtonContainer.setScaleX(value);
                        enterButtonContainer.setScaleY(value);
                    });
                    pulseAnimator.start();
                })
                .start();

        // Animate tagline
        tagline.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(900)
                .start();
    }

    private void setupAutoRedirect() {
        redirectRunnable = this::navigateToSignIn;
        handler.postDelayed(redirectRunnable, AUTO_REDIRECT_DELAY);
    }

    private void navigateToSignIn() {
        // Cancel any pending auto-redirect
        cancelAutoRedirect();

        // Navigate to sign in screen
        Intent intent = new Intent(MainActivity.this, sign_in_activity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void cancelAutoRedirect() {
        handler.removeCallbacks(redirectRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelAutoRedirect();
    }
}