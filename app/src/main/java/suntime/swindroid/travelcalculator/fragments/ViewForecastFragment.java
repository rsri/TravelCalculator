package suntime.swindroid.travelcalculator.fragments;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import suntime.swindroid.travelcalculator.R;
import suntime.swindroid.travelcalculator.controller.ForecastListAdapter;
import suntime.swindroid.travelcalculator.controller.LocationsController;
import suntime.swindroid.travelcalculator.model.ForecastItem;
import suntime.swindroid.travelcalculator.model.Location;
import suntime.swindroid.travelcalculator.util.Constants;
import suntime.swindroid.travelcalculator.util.Util;
import suntime.swindroid.travelcalculator.yahoo.YahooClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewForecastFragment extends AbsFragment {

    private Spinner locationsSpinner;
    private ListView dataListView;
    private List<Location> locations;
    private ForecastListAdapter listAdapter;

    public ViewForecastFragment() {
    }

    public static ViewForecastFragment newInstance() {
        return new ViewForecastFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationsSpinner = (Spinner) view.findViewById(R.id.locationSpinner);
        dataListView = (ListView) view.findViewById(R.id.data_listview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocationsController controller = getController();
        locations = controller.getLocations();
        ArrayAdapter<String> locationsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                controller.getLocationNames());
        locationsSpinner.setAdapter(locationsAdapter);
        locationsSpinner.setOnItemSelectedListener(selectedListener);
        listAdapter = new ForecastListAdapter(Collections.<ForecastItem>emptyList());
        dataListView.setAdapter(listAdapter);
    }

    private AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (Util.ensureNetworkPermission(getActivity())) {
                YahooClient client = new YahooClient(getActivity(), forecastListener);
                Location location = locations.get(position);
                client.execute(location);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQ_CODE_ACCESS_INTERNET:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getActivity(), getString(R.string.need_permission, "view forecast"), Toast.LENGTH_SHORT).show();
                } else {
                    YahooClient client = new YahooClient(getActivity(), forecastListener);
                    Location location = locations.get(locationsSpinner.getSelectedItemPosition());
                    client.execute(location);
                }
        }
    }

    private ForecastListener forecastListener = new ForecastListener() {
        @Override
        public void onForecastReceived(List<ForecastItem> forecastItems) {
            if (forecastItems == null) {
                Toast.makeText(getContext(), "Couldn't fetch forecast for the location.", Toast.LENGTH_LONG).show();
                listAdapter.setForecastItems(Collections.<ForecastItem>emptyList()).notifyDataSetChanged();
            } else {
                listAdapter.setForecastItems(forecastItems).notifyDataSetChanged();
            }
        }
    };

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_view_forecast;
    }

    @Override
    protected int getMenuRes() {
        return 0;
    }

    @Override
    protected boolean hasMenu() {
        return false;
    }

    @Override
    public String getFragmentTag() {
        return ViewForecastFragment.class.getSimpleName();
    }

    @Override
    public int getTitleStringResource() {
        return R.string.view_forecast;
    }

    public interface ForecastListener {
        void onForecastReceived(List<ForecastItem> forecastItems);
    }
}
