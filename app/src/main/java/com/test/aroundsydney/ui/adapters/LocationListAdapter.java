package com.test.aroundsydney.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.aroundsydney.R;
import com.test.aroundsydney.models.entitys.Location;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {

    public interface LocationListAdapterItemClickListener {

        void onItemClick(Location location);
    }

    private List<Location> locations = new LinkedList<>();
    private LocationListAdapterItemClickListener listener;

    @NonNull
    @Override
    public LocationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_list, parent, false);
        return new ViewHolder(itemRootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Location item = locations.get(position);
        holder.name.setText(item.name);
        holder.latitude.setText(String.valueOf(item.latitude));
        holder.longitude.setText(String.valueOf(item.longitude));
        holder.distanceTitle.setVisibility(item.distance != 0 ? View.VISIBLE : View.INVISIBLE);
        holder.distance.setVisibility(item.distance != 0 ? View.VISIBLE : View.INVISIBLE);
        holder.distance.setText(item.distance != 0 ? String.format(Locale.getDefault(), "%.3f km", item.distance) : "");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void setListener(LocationListAdapterItemClickListener listener) {
        this.listener = listener;
    }

    public void addLocations(List<Location> locations) {
        this.locations.addAll(locations);
        notifyDataSetChanged();
    }

    public void clearAdapter() {
        this.locations.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView latitude;
        TextView longitude;
        TextView distance;
        TextView distanceTitle;
        CardView cardView;

        ViewHolder(View viewGroup) {
            super(viewGroup);
            name = viewGroup.findViewById(R.id.text_view_location_name);
            latitude = viewGroup.findViewById(R.id.text_view_latitude);
            longitude = viewGroup.findViewById(R.id.text_view_longitude);
            distance = viewGroup.findViewById(R.id.text_view_distance);
            distanceTitle = viewGroup.findViewById(R.id.text_view_distance_title);
            cardView = viewGroup.findViewById(R.id.card_view);
        }
    }
}
