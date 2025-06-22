package com.example.launchjourney;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class home_page_activity extends AppCompatActivity {

    Spinner from_spinner, destination_spinner;
    Button date_picker_button, search_trip_button, logout_button;
    ArrayAdapter<CharSequence> fromAdapter, destinationAdapter;
    String[] locations;  // To store locations array

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_activity);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize spinners and buttons
        from_spinner = findViewById(R.id.from_spinner);
        destination_spinner = findViewById(R.id.destination_spinner);
        date_picker_button = findViewById(R.id.date_picker_button);
        search_trip_button = findViewById(R.id.search_trip_button);

        // Initialize the title TextView
        TextView homepageTitle = findViewById(R.id.homepage_title);

        // Load the animation
        Animation fadeInScale = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);

        // Apply the animation to the title
        homepageTitle.startAnimation(fadeInScale);

        // Load locations array
        locations = getResources().getStringArray(R.array.from_locations_array);

        // Set up "From" spinner with adapter
        fromAdapter = ArrayAdapter.createFromResource(this,
                R.array.from_locations_array, android.R.layout.simple_spinner_item);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        from_spinner.setAdapter(fromAdapter);

        // Set up "Destination" spinner with adapter
        destinationAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, locations);
        destinationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destination_spinner.setAdapter(destinationAdapter);

        // Set listener for "From" spinner to update "Destination" spinner
        from_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get selected item from "From" spinner
                String selectedFromLocation = (String) parentView.getItemAtPosition(position);
                // Update "Destination" spinner by filtering out the selected "From" location
                updateDestinationSpinner(selectedFromLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing when no item is selected
            }
        });

        // Handle date picker button click
        date_picker_button.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(home_page_activity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Check if the selected date is in the past
                        Calendar currentDate = Calendar.getInstance();
                        currentDate.set(Calendar.HOUR_OF_DAY, 0);
                        currentDate.set(Calendar.MINUTE, 0);
                        currentDate.set(Calendar.SECOND, 0);
                        currentDate.set(Calendar.MILLISECOND, 0);

                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);

                        if (selectedDate.before(currentDate)) {
                            new AlertDialog.Builder(home_page_activity.this)
                                    .setTitle("Invalid Date")
                                    .setMessage("The selected date is in the past. Please choose a valid date.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        } else {
                            String selectedDateString = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            date_picker_button.setText(selectedDateString);
                        }
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Handle search trip button click
        search_trip_button.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Get user-selected locations and convert them to lowercase
            String fromLocation = from_spinner.getSelectedItem().toString().toLowerCase().trim();
            String destinationLocation = destination_spinner.getSelectedItem().toString().toLowerCase().trim();

            // Create Firestore path for the subcollection
            String routePath = fromLocation + destinationLocation;
            String selectedDate = date_picker_button.getText().toString();

            Intent intent = new Intent(home_page_activity.this, AvailableTripsActivity.class);
            intent.putExtra("route", routePath);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        // Handle logout button click
//        logout_button.setOnClickListener(view -> {
//            FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
//            Toast.makeText(home_page_activity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(home_page_activity.this, MainActivity.class);
//            startActivity(intent);
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            finish();
//        });
    }

    // Method to update the "Destination" spinner after selecting an item in the "From" spinner
    // Method to update the "Destination" spinner after selecting an item in the "From" spinner
    private void updateDestinationSpinner(String selectedFromLocation) {
        // Create a list of CharSequence to store the filtered locations
        ArrayList<CharSequence> filteredLocations = new ArrayList<>();

        // Filter out the selected "From" location
        for (String location : locations) {
            if (!location.equals(selectedFromLocation)) {
                filteredLocations.add(location);
            }
        }

        // Create a new ArrayAdapter<CharSequence> with the filtered locations
        destinationAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, filteredLocations);
        destinationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destination_spinner.setAdapter(destinationAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            Intent intent = new Intent(home_page_activity.this, profile.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        } else if (id == R.id.action_trips) {
            Intent intent = new Intent(home_page_activity.this, trips.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        } else if (id == R.id.action_contact) {
            Intent intent = new Intent(home_page_activity.this, contact.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        } else if(id == R.id.action_logout){
            FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
            Toast.makeText(home_page_activity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(home_page_activity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
