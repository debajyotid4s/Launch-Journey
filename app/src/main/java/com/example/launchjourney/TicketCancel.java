package com.example.launchjourney;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TicketCancel extends AppCompatActivity {
    private static final String TAG = "TicketCancel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_cancel);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String docId = getIntent().getStringExtra("docID");
        String trxID = getIntent().getStringExtra("trxID");

        MaterialButton btnYes = findViewById(R.id.btnCancel);
        EditText edttxtYes = findViewById(R.id.editTextYes);

        Log.d(TAG, "Document ID: " + docId);
        Log.d(TAG, "Transaction ID: " + trxID);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Please sign in to cancel your ticket", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (docId == null || trxID == null) {
            Toast.makeText(this, "Invalid ticket data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnYes.setOnClickListener(view -> {
            String input = edttxtYes.getText().toString().trim();

            if (input.equals("YES")) {
                String uid = user.getUid();
                fetchTicketAndDelete(uid, docId);
            } else {
                edttxtYes.setError("Please type YES to confirm cancellation");
            }
        });
    }

    private void fetchTicketAndDelete(String uid, String docId) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        // Fetch ticket data
        firebaseFirestore.collection("users")
                .document(uid)
                .collection("tickets")
                .document(docId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String launchName = documentSnapshot.getString("launchName");
                        String route = documentSnapshot.getString("route");
                        String ticketType = documentSnapshot.getString("ticketType");
                        Long ticketQuantity = documentSnapshot.getLong("ticketQuantity");
                        String date = documentSnapshot.getString("date");

                        // Proceed with deletion
                        deleteTicketAndNavigate(uid, docId, launchName, route, ticketType, ticketQuantity, date);
                    } else {
                        Log.e(TAG, "Document does not exist");
                        Toast.makeText(this, "Ticket not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching ticket data: " + e.getMessage());
                    Toast.makeText(this, "Error fetching ticket data", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteTicketAndNavigate(String uid, String docId, String launchName, String route, String ticketType, Long ticketQuantity, String date) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        // Delete ticket
        firebaseFirestore.collection("users")
                .document(uid)
                .collection("tickets")
                .document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Ticket deleted successfully");

                    // Navigate to TicketCanceled activity
//                    Intent intent = new Intent(TicketCancel.this, TicketCanceled.class);
//                    intent.putExtra("launchName", launchName);
//                    intent.putExtra("route", route);
//                    intent.putExtra("ticketType", ticketType);
//                    intent.putExtra("ticketQuantity", ticketQuantity);
//                    intent.putExtra("date", date);
//                    startActivity(intent);
//                    finish();

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                    DocumentReference docRef = firestore.collection("routes")
                            .document(route)
                            .collection(date)
                            .document(launchName);

                    docRef.get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if(documentSnapshot.exists()) {
                                    Long currentAvailable = documentSnapshot.getLong(ticketType);
                                    if (currentAvailable == null) currentAvailable = 0L;

                                    int updatedSeat = (int) (currentAvailable.intValue() + ticketQuantity);

                                    docRef.update(ticketType, updatedSeat)
                                            .addOnSuccessListener(aVoid1 -> {
                                                Log.d(TAG, "Ticket count updated successfully for " + ticketType);
                                                Toast.makeText(this, "Ticket count updated!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e(TAG, "Error updating ticket count: " + e.getMessage());
                                                Toast.makeText(this, "Failed to update ticket count.", Toast.LENGTH_SHORT).show();
                                            });
                                    Intent intent = new Intent(TicketCancel.this, TicketCanceled.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Log.e(TAG, "Document does not exist");
                                    Toast.makeText(this, "No matching document found.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error fetching document: " + e.getMessage());
                                Toast.makeText(this, "Error fetching document.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting ticket: " + e.getMessage());
                    Toast.makeText(this, "Error deleting ticket", Toast.LENGTH_SHORT).show();
                });
    }
}