package suntime.swindroid.travelcalculator.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import suntime.swindroid.travelcalculator.R;
import suntime.swindroid.travelcalculator.controller.LocationsController;
import suntime.swindroid.travelcalculator.controller.LocationsListAdapter;
import suntime.swindroid.travelcalculator.model.Location;
import suntime.swindroid.travelcalculator.util.Constants;
import suntime.swindroid.travelcalculator.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationsFragment extends AbsFragment {

    private RecyclerView locationsRecyclerView;
    private LocationsListAdapter adapter;
    private List<Location> locations;
    private Dialog addLocationDialog;

    public LocationsFragment() {
        // Required empty public constructor
    }

    public static LocationsFragment newInstance() {
        return new LocationsFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationsRecyclerView = (RecyclerView) view.findViewById(R.id.locations_recyclerview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocationsController controller = getController();
        locations = controller.getLocations();
        adapter = new LocationsListAdapter(locations, listener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        locationsRecyclerView.setLayoutManager(mLayoutManager);
        locationsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        locationsRecyclerView.setAdapter(adapter);
    }

    private LocationsListAdapter.LocationListener listener = new LocationsListAdapter.LocationListener() {
        @Override
        public void onLocationRemoved(int pos) {
            Location location = locations.get(pos);
            locations.remove(pos);
            getController().removeLocation(location.getLocationName());
            adapter.notifyItemRemoved(pos);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_add_location) {
            showAddLocationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddLocationDialog() {
        addLocationDialog = new Dialog(getContext());
        addLocationDialog.setContentView(R.layout.dialog_create_location);

        final EditText locationName = (EditText) addLocationDialog.findViewById(R.id.enter_loc_et);
        final EditText latitude = (EditText) addLocationDialog.findViewById(R.id.enter_lat_et);
        final EditText longitude = (EditText) addLocationDialog.findViewById(R.id.enter_long_et);
        final ImageView currLocation = (ImageView) addLocationDialog.findViewById(R.id.curr_location_iv);

        currLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrentLocation();
            }
        });

        Button saveButton = (Button) addLocationDialog.findViewById(R.id.save_loc_btn);
        Button cancelButton = (Button) addLocationDialog.findViewById(R.id.cancel_loc_btn);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocationDialog.dismiss();
                Location location = new Location();
                location.setLocationName(locationName.getText().toString());
                location.setLatitude(Double.parseDouble(latitude.getText().toString()));
                location.setLongitude(Double.parseDouble(longitude.getText().toString()));
                locations.add(location);
                getController().insertLocation(location);
                adapter.setLocations(locations).notifyItemInserted(locations.size() - 1);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocationDialog.dismiss();
            }
        });
        addLocationDialog.show();
    }

    private void updateCurrentLocation() {
        if (Util.ensureLocationPermission(getActivity(), Constants.REQ_CODE_ACCESS_LOCATION)) {
            final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getActivity(), "Location is turned off.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            } else {
                Toast.makeText(getActivity(), "Please wait while you're being located.", Toast.LENGTH_SHORT).show();
                manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(android.location.Location location) {
                        if (addLocationDialog != null && addLocationDialog.isShowing()) {
                            EditText latitude = (EditText) addLocationDialog.findViewById(R.id.enter_lat_et);
                            EditText longitude = (EditText) addLocationDialog.findViewById(R.id.enter_long_et);
                            double lat = Util.trimPrecision(location.getLatitude());
                            double lng = Util.trimPrecision(location.getLongitude());
                            latitude.setText(String.valueOf(lat));
                            longitude.setText(String.valueOf(lng));
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}
                    @Override
                    public void onProviderEnabled(String provider) {}
                    @Override
                    public void onProviderDisabled(String provider) {}
                }, null);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQ_CODE_ACCESS_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getActivity(), getString(R.string.need_permission, "access current location"), Toast.LENGTH_SHORT).show();
                } else {
                    updateCurrentLocation();
                }
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_locations;
    }

    @Override
    protected int getMenuRes() {
        return R.menu.fragment_menu_location;
    }

    @Override
    protected boolean hasMenu() {
        return true;
    }

    @Override
    public String getFragmentTag() {
        return LocationsFragment.class.getSimpleName();
    }

    @Override
    public int getTitleStringResource() {
        return R.string.locations;
    }
}
