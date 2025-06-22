package com.example.launchjourney;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CheckOut extends AppCompatActivity {
    private static final String TAG = "CheckOutActivity";

    private ImageView image1, image2;
    private boolean isImage1Selected = false, isImage2Selected = false;
    private TextView timerTextView;
    private Button done;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 10 * 60 * 300; // 10 minutes in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        image1 = findViewById(R.id.imagePay1);
        image2 = findViewById(R.id.imagePay2);
        EditText payableText = findViewById(R.id.payableEdttext);
        EditText trxid = findViewById(R.id.trxidEdttext);
        done = findViewById(R.id.btnPaydone);
        timerTextView = findViewById(R.id.txtCountdown); // Timer TextView
        done.setEnabled(false);
        startTimer(); // Start the countdown timer


        int ticketQuantity = getIntent().getIntExtra("ticketNumber", 0);
        String ticketType = getIntent().getStringExtra("ticketType");
        String launchName = getIntent().getStringExtra("launchName");
        String tourDate = getIntent().getStringExtra("trueDate");
        String route = getIntent().getStringExtra("route");
        String time = getIntent().getStringExtra("time");


        if (ticketType.equals("scabin")) {
            payableText.setText(String.valueOf(ticketQuantity * 1020));
        } else if (ticketType.equals("deck")) {
            payableText.setText(String.valueOf(ticketQuantity * 280));
        } else {
            payableText.setText(String.valueOf(ticketQuantity * 3200));
        }


        done.setOnClickListener(v -> {
            String transactionId = trxid.getText().toString().trim();

            // Check if the user is logged in and the transaction ID is not empty
            if (user == null || transactionId.isEmpty()) {
                Toast.makeText(this, "Invalid Payment!", Toast.LENGTH_LONG).show();
            } else {
                if (user != null) {
                    String userId = user.getUid();

                    // Save ticket to Firestore with the success callback
                    saveTicketToFirestore(userId, launchName, tourDate, route,
                            time, ticketType, ticketQuantity, transactionId, () -> {
                                // After successful save, update the available tickets
                                updateAvailableTickets(route, tourDate, launchName, ticketType, ticketQuantity, () -> {
                                    // After successful update, navigate to Finalize activity
                                    Log.d("CheckOut", "Starting Finalize activity");
                                    Intent i = new Intent(CheckOut.this, Finalize.class);
                                    startActivity(i);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                });
                            });
                }
            }
        });


        // Image selection handling
        image1.setOnClickListener(v -> {
            isImage1Selected = true;
            isImage2Selected = false;
            done.setEnabled(true);
            updateSelection();
            Log.d(TAG, "Image 1 Selected");
        });

        image2.setOnClickListener(v -> {
            isImage1Selected = false;
            isImage2Selected = true;
            done.setEnabled(true);
            updateSelection();
            Log.d(TAG, "Image 2 Selected");
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timerTextView.setText("Time Expired!");
                done.setEnabled(false); // Disable button after time runs out
                Log.d(TAG, "Time Expired - Checkout Disabled");
            }
        }.start();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "Time Left: %02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
    }

    private void updateSelection() {
        if (isImage1Selected) {
            image1.setBackgroundResource(R.drawable.selected_border);
            image2.setBackgroundResource(R.drawable.unselected_border);
            Log.d(TAG, "Updated UI - Image 1 selected");
        } else if (isImage2Selected) {
            image1.setBackgroundResource(R.drawable.unselected_border);
            image2.setBackgroundResource(R.drawable.selected_border);
            Log.d(TAG, "Updated UI - Image 2 selected");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void saveTicketToFirestore(String userId, String launchName, String tourDate, String route,
                                       String time, String ticketType, int ticketQuantity, String paymentId, Runnable onSuccess) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the user's ticket collection
        CollectionReference ticketCollection = db.collection("users")
                .document(userId)
                .collection("tickets");

        // Create a ticket object
        Map<String, Object> ticketDetails = new HashMap<>();
        ticketDetails.put("launchName", launchName);
        ticketDetails.put("date", tourDate);
        ticketDetails.put("route", route);
        ticketDetails.put("time", time);
        ticketDetails.put("ticketType", ticketType);
        ticketDetails.put("ticketQuantity", ticketQuantity);
        ticketDetails.put("paymentID", paymentId);

        // Add a new document with an auto-generated ID
        ticketCollection.add(ticketDetails)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Ticket saved successfully! ID: " + documentReference.getId());
                    Toast.makeText(this, "Ticket booked successfully!", Toast.LENGTH_SHORT).show();

                    // Call the callback to proceed to the next activity
                    Log.d("Firestore", "Calling onSuccess callback");
                    runOnUiThread(onSuccess); // Ensuring it runs on the main thread
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error saving ticket", e);
                    Toast.makeText(this, "Failed to book ticket. Try again!", Toast.LENGTH_SHORT).show();
                });
    }
    private void updateAvailableTickets(String route, String date, String launchName, String ticketType, int bookedQuantity, Runnable onSuccess) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the available tickets document
        DocumentReference availableTicketsRef = db.collection("routes")
                .document(route)
                .collection(date)
                .document(launchName);

        // Get the current available tickets
        availableTicketsRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long currentAvailableTickets = documentSnapshot.getLong(ticketType);

                        if (currentAvailableTickets != null && currentAvailableTickets >= bookedQuantity) {
                            // Calculate the new available tickets
                            long newAvailableTickets = currentAvailableTickets - bookedQuantity;

                            // Update the available tickets in Firestore
                            availableTicketsRef.update(ticketType, newAvailableTickets)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Available tickets updated successfully!");

                                        // Call the callback to proceed to the next activity
                                        onSuccess.run();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error updating available tickets", e);
                                        Toast.makeText(this, "Failed to update available tickets. Try again!", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.e("Firestore", "Not enough tickets available");
                            Toast.makeText(this, "Not enough tickets available.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Firestore", "Document does not exist");
                        Toast.makeText(this, "Error fetching available tickets.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching available tickets", e);
                    Toast.makeText(this, "Error fetching available tickets. Try again!", Toast.LENGTH_SHORT).show();
                });
    }

}