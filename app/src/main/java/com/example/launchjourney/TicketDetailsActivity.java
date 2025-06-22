package com.example.launchjourney;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TicketDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_details_activity);

        String selectedLaunch, tourDate, routeName, timeDeparture;
        TextView textLaunch = findViewById(R.id.textLaunch);
        RadioGroup ticketRadioGroup = findViewById(R.id.ticketRadioGroup);
        EditText edtNumberofTicket = findViewById(R.id.edtNumberofTicket);
        TextView textView4 = findViewById(R.id.textView4);
        Button btnCheckout = findViewById(R.id.btnCheckout);

        // Initially hide the EditText and TextView
        edtNumberofTicket.setVisibility(View.GONE);
        textView4.setVisibility(View.GONE);

        // Get data from Intent
        selectedLaunch = getIntent().getStringExtra("launchName");
        tourDate = getIntent().getStringExtra("trueDate");
        routeName = getIntent().getStringExtra("route");
        timeDeparture = getIntent().getStringExtra("time");

        String formattedLaunch = Character.toUpperCase(selectedLaunch.charAt(0)) + selectedLaunch.substring(1);
        textLaunch.setText(formattedLaunch);
        Log.d("Fetched", "Received launchName: " + selectedLaunch);
        Log.d("Fetched", "Received trueDate: " + tourDate);
        Log.d("Fetched", "RouteName: " + routeName);
        Log.d("Fetched", "time: " + timeDeparture);

        // Set listener on RadioGroup
        ticketRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                RadioButton selectedRadioButton = findViewById(checkedId);
                String ticketType = selectedRadioButton.getText().toString();

                // Show input fields
                edtNumberofTicket.setVisibility(View.VISIBLE);
                textView4.setVisibility(View.VISIBLE);

                Toast.makeText(TicketDetailsActivity.this, "Selected Ticket: " + ticketType, Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "Proceed to Checkout" button click
        btnCheckout.setOnClickListener(v -> {
            int selectedId = ticketRadioGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(TicketDetailsActivity.this, "Please select a ticket type", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadioButton = findViewById(selectedId);
            String ticketSelected = selectedRadioButton.getText().toString().toLowerCase(); // Convert to lowercase

            // Extract first word of ticket type
            String ticketType = ticketSelected.split(" ")[0];
            if(ticketType.equals("single")) {
                ticketType = "scabin";
            }

            // Validate ticket input
            String ticketInput = edtNumberofTicket.getText().toString().trim();
            if (ticketInput.isEmpty()) {
                Toast.makeText(TicketDetailsActivity.this, "Please enter ticket quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int ticketQuantity;
            try {
                ticketQuantity = Integer.parseInt(ticketInput);
            } catch (NumberFormatException e) {
                Toast.makeText(TicketDetailsActivity.this, "Invalid number of tickets!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (ticketQuantity > 3) {
                Toast.makeText(TicketDetailsActivity.this, "You can't buy more than 3 tickets per trip!", Toast.LENGTH_LONG).show();
                return;
            }

            Log.d("Fetched", "ticket: " + ticketType);

            // Check availability in database
            checkTicketAvailability(routeName, tourDate, selectedLaunch, ticketType, ticketQuantity, timeDeparture);
        });
    }

    // Function to check ticket availability
    private void checkTicketAvailability(String route, String tourDate, String launchName, String ticketType, int ticketQuantity, String timeDeparture) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("routes").document(route)
                .collection(tourDate).document(launchName);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long availableSeats = documentSnapshot.getLong(ticketType.toLowerCase());

                if (availableSeats != null) {
                    if (availableSeats >= ticketQuantity) {
                        Intent i = new Intent(TicketDetailsActivity.this, CheckOut.class);
                        i.putExtra("launchName", launchName);
                        i.putExtra("trueDate", tourDate);
                        i.putExtra("route", route);
                        i.putExtra("ticketNumber", ticketQuantity);
                        i.putExtra("ticketType", ticketType);
                        i.putExtra("time", timeDeparture);  //

                        startActivity(i);
                        Toast.makeText(TicketDetailsActivity.this, "Tickets available! Proceeding to payment.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TicketDetailsActivity.this, "Not enough tickets available!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TicketDetailsActivity.this, "Invalid ticket type!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TicketDetailsActivity.this, "Launch data not found!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(TicketDetailsActivity.this, "Error checking availability", Toast.LENGTH_SHORT).show());
    }

}
