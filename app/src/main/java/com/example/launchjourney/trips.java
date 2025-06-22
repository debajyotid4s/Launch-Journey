package com.example.launchjourney;

import static java.lang.reflect.Array.get;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class trips extends AppCompatActivity {
    private RecyclerView ticketRecyclerView;
    private TicketAdapter ticketAdapter;
    private List<ticket> ticketList;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trips);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ticketRecyclerView = findViewById(R.id.tripsRecyclerView);
        ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ticketList = new ArrayList<>();
        ticketAdapter = new TicketAdapter(ticketList);
        ticketRecyclerView.setAdapter(ticketAdapter);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            fetchTickets(user.getUid());
        }
    }
    public String routeFixer(String route) {
        String finalRoute = route.toUpperCase();  // Default uppercase conversion

        String[] keywords = {"barishal", "pirojpur", "bhola", "potuakhali", "sundarban", "chandpur"};

        for (String keyword : keywords) {
            if (route.toLowerCase().contains(keyword)) {
                int position = route.toLowerCase().indexOf(keyword.charAt(0));  // Find the first letter
                StringBuilder sb = new StringBuilder(route);
                sb.insert(position, "-");  // Insert '-' before the matched word
                finalRoute = sb.toString().toUpperCase();  // Convert the result to uppercase
                break; // Exit loop once a match is found
            }
        }

        return finalRoute;
    }
    public void fetchTickets(String userID){
        db.collection("users").document(userID).collection("tickets")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null){
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        ticketList.clear();
                        for(DocumentSnapshot ticketDoc : queryDocumentSnapshots){
                            String launchName = ticketDoc.getString("launchName");
                            String nameFinal = Character.toUpperCase(launchName.charAt(0)) + launchName.substring(1);
                            String route = ticketDoc.getString("route");
                            String date = ticketDoc.getString("date");
                            String ticketType = ticketDoc.getString("ticketType").toUpperCase();
                            int ticketQuantity = ticketDoc.getLong("ticketQuantity").intValue();
                            String paymentID = ticketDoc.getString("paymentID");
                            String time = ticketDoc.getString("time");
                            String finalRoute = routeFixer(route);

                            Log.d("USER_UID", "launchName: " + nameFinal);
                            Log.d("USER_UID", "route: " + route);
                            Log.d("USER_UID", "date: " + date);
                            Log.d("USER_UID", "ticketType: " + ticketType);
                            Log.d("USER_UID", "ticketQuantity: " + ticketQuantity);
                            Log.d("USER_UID", "paymentID: " + paymentID);
                            Log.d("USER_UID", "time: " + time);
                            Log.d("USER_UID", "User ID: " + userID);
                            String ticketDocument = ticketDoc.getId();
                            //ticket.setDocID(ticketDocument);
                            ticket ticket = new ticket(nameFinal, finalRoute, date, time, ticketType, String.valueOf(ticketQuantity), paymentID, ticketDocument);
                            //ticket.setDocID(ticketDoc.getId());  // <== 🔥 Key line!
                            ticketList.add(ticket);
                        }
                        ticketAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Log.e("FIRESTORE_ERROR", "Error fetching tickets", e));
    }
}