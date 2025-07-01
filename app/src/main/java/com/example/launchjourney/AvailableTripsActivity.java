package com.example.launchjourney;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AvailableTripsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String route;
    private String notdate;

    private String date;

    private RecyclerView recyclerView;
    private TextView noTripsFoundText;
    private LaunchAdapter launchAdapter;
    private List<Launch> launchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_trips);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        launchList = new ArrayList<>();

        noTripsFoundText = findViewById(R.id.noTripsFoundText);
        db = FirebaseFirestore.getInstance();
        route = getIntent().getStringExtra("route");
        notdate = getIntent().getStringExtra("selectedDate");
        date = formatDate(notdate);

        Log.i("datelog", date);

        launchAdapter = new LaunchAdapter(launchList, date);
        recyclerView.setAdapter(launchAdapter);
        fetchLaunches(route, date);
    }


    private void fetchLaunches(String route, String date) {
        db.collection("routes").document(route).collection(date)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        launchList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String launchName = document.getId();

                            String[] words = launchName.split(" ");

                            StringBuilder formattedName = new StringBuilder();
                            for (String word : words) {
                                if (!word.isEmpty()) {
                                    formattedName.append(Character.toUpperCase(word.charAt(0)))
                                            .append(word.substring(1))
                                            .append(" "); // Add space after each word
                                }
                            }
                            launchName = formattedName.toString().trim();
                            int vip = document.getLong("vip").intValue();
                            int deck = document.getLong("deck").intValue();
                            int scabin = document.getLong("scabin").intValue();
                            String time = document.getString("time");
                            Log.i("name", launchName);
                            Launch launch = new Launch(launchName, vip, deck, scabin, route, time);
                            Log.d(TAG, "Fetched Launch: " + launch.getName() + ", Deck: " + launch.getDeck() + ", S-Cabin: " + launch.getScabin() + ", VIP: " + launch.getVip());
                            launchList.add(launch);
                        }
                        if (launchList.isEmpty()) {
                            displayNoTripsFound();
                        } else {
                            // Make sure RecyclerView is visible when we have data
                            recyclerView.setVisibility(View.VISIBLE);
                            noTripsFoundText.setVisibility(View.GONE);
                            launchAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // Error or no trips found
                        Log.e(TAG, "Error fetching documents: ", task.getException());
                        displayNoTripsFound();
                    }
                });
    }
    private void displayNoTripsFound() {
        recyclerView.setVisibility(View.GONE);
        noTripsFoundText.setVisibility(View.VISIBLE);
    }

    public static String formatDate(String inputDate) {
        // Possible input formats
        String[] possibleFormats = {"d/M/yyyy", "d/MM/yyyy", "dd/M/yyyy", "dd/MM/yyyy"};

        // Desired output format
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (String format : possibleFormats) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.getDefault());
                Date date = inputFormat.parse(inputDate);
                return outputFormat.format(date); // Convert and return the formatted date
            } catch (ParseException ignored) {
                // Continue trying the next format if parsing fails
            }
        }
        return "Invalid Date"; // Return an error message if parsing fails
    }
}
