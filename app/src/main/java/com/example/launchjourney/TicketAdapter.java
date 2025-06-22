package com.example.launchjourney;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private List<ticket> ticketList;

    public TicketAdapter(List<ticket> ticketList){
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketAdapter.TicketViewHolder holder, int position) {
        ticket ticket = ticketList.get(position);
        holder.tvLaunchName.setText("Name of Vessel: " + ticket.getLaunchName());
        holder.tvLaunchRoute.setText("Route: " + ticket.getRoute());
        holder.tvLaunchDepartureTime.setText("Deperture Time: "+ ticket.getTime());
        holder.tvLaunchDate.setText("Date of Journey: " + ticket.getDate());
        holder.tvLaunchTicketType.setText("Ticket Class: " + ticket.getTicketType());
        holder.tvLaunchTicketQuantity.setText("Ticket Quantity: " + ticket.getTicketQuantity());
        holder.tvLaunchPaymentID.setText("Payment ID: " + ticket.getPaymentID());

        // Check if journey date is past
        boolean isExpired = isDatePast(ticket.getDate(), ticket.getTime());

        if (isExpired) {
            // Show expired image and disable button functionality
            holder.ticketExpired.setVisibility(View.VISIBLE);
            holder.linearLayoutButton.setClickable(false);
            holder.linearLayoutButton.setEnabled(false);
            holder.linearLayoutButton.setAlpha(0.5f); // Visual indication that it's disabled
        } else {
            // Hide expired image and enable button functionality
            holder.ticketExpired.setVisibility(View.GONE);
            holder.linearLayoutButton.setClickable(true);
            holder.linearLayoutButton.setEnabled(true);
            holder.linearLayoutButton.setAlpha(1.0f);

            // Set click listener only for non-expired tickets
            holder.linearLayoutButton.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "Date and Name of launch: " + ticket.getDate() + "\n" + ticket.getLaunchName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), TicketCancel.class);
                String trxID = ticket.getPaymentID();
                String docID = ticket.getDocID();
                intent.putExtra("trxID", trxID);
                intent.putExtra("docID", docID);
                v.getContext().startActivity(intent);
                Log.d("recycler", docID);
            });
        }
    }

    /**
     * Checks if the given date and time have already passed compared to current UTC date and time
     * @param dateString The date string in format "YYYY-MM-DD"
     * @param timeString The time string from the ticket
     * @return true if the date/time is in the past, false otherwise
     */
    private boolean isDatePast(String dateString, String timeString) {
        try {
            // Create a full datetime string by combining date and time
            String dateTimeString = dateString + " " + timeString;

            // Use UTC timezone for comparison
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Parse the journey date and time string to a Date object
            Date journeyDateTime;
            try {
                journeyDateTime = dateFormat.parse(dateTimeString);
            } catch (ParseException e) {
                // If we can't parse with the time, try just the date
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                journeyDateTime = dateFormat.parse(dateString);
            }

            // Get current UTC date and time
            Date currentDateTime = new Date();

            // Compare the two dates
            return journeyDateTime != null && journeyDateTime.before(currentDateTime);

        } catch (ParseException e) {
            Log.e("TicketAdapter", "Error parsing date: " + e.getMessage());
            return false; // If we can't parse the date, assume it's not expired
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder{
        TextView tvLaunchName, tvLaunchRoute, tvLaunchDepartureTime, tvLaunchDate,tvLaunchTicketType, tvLaunchTicketQuantity, tvLaunchPaymentID;
        LinearLayout linearLayoutButton;
        ImageView ticketExpired;

        public TicketViewHolder(@NonNull View itemView){
            super(itemView);
            tvLaunchName = itemView.findViewById(R.id.tvLaunchName);
            tvLaunchRoute = itemView.findViewById(R.id.tvLaunchRoute);
            tvLaunchDepartureTime = itemView.findViewById(R.id.tvLaunchDepartureTime);
            tvLaunchDate = itemView.findViewById(R.id.tvLaunchDate);
            tvLaunchTicketType = itemView.findViewById(R.id.tvLaunchTicketType);
            tvLaunchTicketQuantity = itemView.findViewById(R.id.tvLaunchTicketQuantity);
            tvLaunchPaymentID = itemView.findViewById(R.id.tvLaunchPaymentID);
            linearLayoutButton = itemView.findViewById(R.id.linearLayoutButton);
            ticketExpired = itemView.findViewById(R.id.imgExpired);

            // Initially hide the expired image
            ticketExpired.setVisibility(View.GONE);
        }
    }
}