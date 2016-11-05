package suntime.swindroid.travelcalculator.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import suntime.swindroid.travelcalculator.R;
import suntime.swindroid.travelcalculator.controller.LocationsController;
import suntime.swindroid.travelcalculator.model.Location;

public class SunCalcFragment extends AbsFragment {

    private Spinner locationsSpinner;
    private DatePicker datePicker;
    private List<Location> locations;

    public SunCalcFragment() {
    }

    public static SunCalcFragment newInstance() {
        return new SunCalcFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_sun_calc;
    }

    @Override
    protected int getMenuRes() {
        return R.menu.fragment_menu_sun_calc;
    }

    @Override
    protected boolean hasMenu() {
        return true;
    }

    @Override
    public String getFragmentTag() {
        return SunCalcFragment.class.getSimpleName();
    }

    @Override
    public int getTitleStringResource() {
        return R.string.sun_calc;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationsSpinner = (Spinner) view.findViewById(R.id.locationSpinner);
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocationsController controller = getController();
        locations = controller.getLocations();
        ArrayAdapter<String> locationsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                controller.getLocationNames());
        locationsSpinner.setAdapter(locationsAdapter);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, month, day, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_done) {
            LocationsController controller = getController();
            Location location = locations.get(locationsSpinner.getSelectedItemPosition());
            int year = datePicker.getYear();
            int monthOfYear = datePicker.getMonth();
            int dayOfMonth = datePicker.getDayOfMonth();
            Pair<String, String> sunrisesetPair = controller.getSunRiseSet(getContext(), location, year, monthOfYear, dayOfMonth);
            View rootView = getView();
            if (rootView != null) {
                rootView.findViewById(R.id.content_layout).setVisibility(View.VISIBLE);
                TextView sunriseTV = (TextView) rootView.findViewById(R.id.sunriseTimeTV);
                TextView sunsetTV = (TextView) rootView.findViewById(R.id.sunsetTimeTV);
                sunriseTV.setText(sunrisesetPair.first);
                sunsetTV.setText(sunrisesetPair.second);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
