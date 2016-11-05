package suntime.swindroid.travelcalculator.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import suntime.swindroid.travelcalculator.R;
import suntime.swindroid.travelcalculator.model.Location;
import suntime.swindroid.travelcalculator.util.Constants;
import suntime.swindroid.travelcalculator.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends AbsFragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMyLocationButtonClickListener, LocationListener, LocationSource {

    private MapView locationsMapView;
    private TextView latitudeTV;
    private TextView longitudeTV;
    private EditText locationNameET;

    private GoogleMap map;
    private List<Location> locations;
    private Marker currentMarker;
    private OnLocationChangedListener onLocationChangedListener;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationsMapView = (MapView) view.findViewById(R.id.locations_mapview);
        latitudeTV = (TextView) view.findViewById(R.id.latitude_tv);
        longitudeTV = (TextView) view.findViewById(R.id.longitude_tv);
        locationNameET = (EditText) view.findViewById(R.id.location_name_et);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationsMapView.onCreate(savedInstanceState);
        locationsMapView.getMapAsync(this);
        locations = getController().getLocations();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        addMarkers();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMarkerDragListener(this);
        map.setOnMyLocationButtonClickListener(this);
        if (Util.ensureLocationPermission(getActivity(), Constants.REQ_CODE_ACCESS_LOCATION_MAP)) {
            map.setLocationSource(this);
            map.setMyLocationEnabled(true);
        }
        LatLng latLng = new LatLng(0,0);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(1).build();

        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        locationsMapView.onResume();
    }

    private void addMarkers() {
        for (Location location : locations) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            markerOptions.position(latLng).title(location.getLocationName()).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Marker marker = map.addMarker(markerOptions);
            marker.setDraggable(false);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(currentMarker)) {
            marker.remove();
            currentMarker = null;
            updateLatLong();
            return true;
        }
        return false;
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        currentMarker = marker;
        updateLatLong();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = map.addMarker(markerOptions);
        currentMarker.setDraggable(true);
        updateLatLong();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQ_CODE_ACCESS_LOCATION_MAP:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getActivity(), getString(R.string.need_permission, "access current location"), Toast.LENGTH_SHORT).show();
                } else {
                    map.setMyLocationEnabled(true);
                    updateCurrentLocation();
                }
        }
    }

    private void updateCurrentLocation() {
        if (Util.ensureLocationPermission(getActivity(), Constants.REQ_CODE_ACCESS_LOCATION_MAP)) {
            LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getActivity(), "Location is turned off.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            } else {
                Toast.makeText(getActivity(), "Please wait while you're being located.", Toast.LENGTH_SHORT).show();
                manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);

            }
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        if (onLocationChangedListener != null) {
            onLocationChangedListener.onLocationChanged(location);
        }
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        markerOptions.position(latLng).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = map.addMarker(markerOptions);
        currentMarker.setDraggable(true);
        updateLatLong();
    }

    private void updateLatLong() {
        if (currentMarker != null) {
            double lat = Util.trimPrecision(currentMarker.getPosition().latitude);
            double lng = Util.trimPrecision(currentMarker.getPosition().longitude);
            latitudeTV.setText(String.valueOf(lat));
            longitudeTV.setText(String.valueOf(lng));
        } else {
            latitudeTV.setText("");
            longitudeTV.setText("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                if (currentMarker == null) {
                    Toast.makeText(getActivity(), "No location selected.", Toast.LENGTH_SHORT).show();
                } else {
                    Location location = new Location();
                    location.setLocationName(locationNameET.getText().toString());
                    location.setLatitude(Util.trimPrecision(currentMarker.getPosition().latitude));
                    location.setLongitude(Util.trimPrecision(currentMarker.getPosition().longitude));
                    getController().insertLocation(location);
                    locations.add(location);
                    Toast.makeText(getActivity(), "Location saved.", Toast.LENGTH_SHORT).show();
                    currentMarker.remove();
                    currentMarker = null;
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    markerOptions.position(latLng).title(location.getLocationName()).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    Marker marker = map.addMarker(markerOptions);
                    marker.setDraggable(false);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        updateCurrentLocation();
        return true;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_map;
    }

    @Override
    protected int getMenuRes() {
        return R.menu.fragment_menu_map;
    }

    @Override
    protected boolean hasMenu() {
        return true;
    }

    @Override
    public String getFragmentTag() {
        return MapFragment.class.getSimpleName();
    }

    @Override
    public int getTitleStringResource() {
        return R.string.pick_from_map;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        this.onLocationChangedListener = null;
    }
}
