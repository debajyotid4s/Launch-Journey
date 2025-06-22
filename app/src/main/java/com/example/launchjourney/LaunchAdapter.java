package com.example.launchjourney;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class LaunchAdapter extends RecyclerView.Adapter<LaunchAdapter.LaunchViewHolder> {

    private List<Launch> launchList;
    private String date, time;

    public LaunchAdapter(List<Launch> launchList, String date) {
        this.launchList = launchList;
        this.date = date;
    }

    @NonNull
    @Override
    public LaunchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket_view, parent, false);
        return new LaunchViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull LaunchViewHolder holder, int position) {
        Launch launch = launchList.get(position);
        holder.tvLaunchName.setText("Name : " + launch.getName());
        holder.tvLaunchDate.setText("Date of Journey : " + date);
        holder.tvDeck.setText("Deck Available : " + String.valueOf(launch.getDeck()));
        holder.tvScabin.setText("Single Cabin Available : " + String.valueOf(launch.getScabin()));
        holder.tvVip.setText("VIP Available : " + String.valueOf(launch.getVip()));
        holder.tvTime.setText("Time of Departure : " + launch.getTime());

        holder.linearLayoutButton.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Clicked: " + launch.getName(), Toast.LENGTH_SHORT).show();

            // Example: Open a new activity
            String routeName = launch.getRoute();
            String launchName = launch.getName();
            String lowerName = launchName.toLowerCase();
            Intent intent = new Intent(v.getContext(), TicketDetailsActivity.class);
            intent.putExtra("launchName", lowerName);
            intent.putExtra("trueDate", date);
            intent.putExtra("route", routeName);
            intent.putExtra("time", launch.getTime());
            v.getContext().startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return launchList.size();
    }

    public static class LaunchViewHolder extends RecyclerView.ViewHolder {
        public TextView tvLaunchName, tvLaunchDate, tvDeck, tvScabin, tvVip, tvTime;
        public LinearLayout linearLayoutButton;
        public LaunchViewHolder(View view) {
            super(view);
            tvLaunchName = view.findViewById(R.id.nameTextView);
            tvLaunchDate = view.findViewById(R.id.dateTextView);
            tvDeck = view.findViewById(R.id.deckTextView);
            tvScabin = view.findViewById(R.id.scabinTextView);
            tvVip = view.findViewById(R.id.vipTextView);
            tvTime = view.findViewById(R.id.timeTextView);
            linearLayoutButton = view.findViewById(R.id.linearLayoutButton);

        }
    }
}
