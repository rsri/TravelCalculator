package suntime.swindroid.travelcalculator.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import suntime.swindroid.travelcalculator.R;
import suntime.swindroid.travelcalculator.model.Location;

/**
 * Created by srikaram on 15-Oct-16.
 */
public class LocationsListAdapter extends RecyclerView.Adapter<LocationsListAdapter.LocationViewHolder> {

    private List<Location> locations;

    private LocationListener listener;

    public LocationsListAdapter(List<Location> locations, LocationListener listener) {
        this.locations = locations;
        this.listener = listener;
    }

    public LocationsListAdapter setLocations(List<Location> locations) {
        this.locations = locations;
        return this;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.locations_item, parent, false);
        return new LocationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LocationViewHolder holder, final int position) {
        Location location = locations.get(position);
        holder.location.setText(location.getLocationName());
        holder.latitude.setText(String.valueOf(location.getLatitude()));
        holder.longitude.setText(String.valueOf(location.getLongitude()));
        holder.removeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLocationRemoved(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        private TextView location, latitude, longitude;
        private ImageView removeIV;

        public LocationViewHolder(View itemView) {
            super(itemView);
            location = (TextView) itemView.findViewById(R.id.location_name_tv);
            latitude = (TextView) itemView.findViewById(R.id.latitude_tv);
            longitude = (TextView) itemView.findViewById(R.id.longitude_tv);
            removeIV = (ImageView) itemView.findViewById(R.id.remove_location_iv);
        }

    }

    public interface LocationListener {
        void onLocationRemoved(int pos);
    }
}
