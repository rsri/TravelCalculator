package suntime.swindroid.travelcalculator.fragments;


import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import suntime.swindroid.travelcalculator.R;
import suntime.swindroid.travelcalculator.controller.LocationsController;
import suntime.swindroid.travelcalculator.controller.SunRiseSetAdapter;
import suntime.swindroid.travelcalculator.model.Location;
import suntime.swindroid.travelcalculator.util.Constants;
import suntime.swindroid.travelcalculator.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenerateFragment extends AbsFragment implements View.OnClickListener {

    private TextView fromDateTV;
    private TextView toDateTV;
    private ListView dataListView;
    private Spinner locationsSpinner;
    private List<Location> locations;
    private SunRiseSetAdapter adapter;

    public GenerateFragment() {
    }

    public static GenerateFragment newInstance() {
        return new GenerateFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_generate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fromDateTV = (TextView) view.findViewById(R.id.from_date_tv);
        toDateTV = (TextView) view.findViewById(R.id.to_date_tv);
        dataListView = (ListView) view.findViewById(R.id.data_listview);
        locationsSpinner = (Spinner) view.findViewById(R.id.locationSpinner);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fromDateTV.setOnClickListener(this);
        setToTodaysDate(fromDateTV);
        toDateTV.setOnClickListener(this);
        setToTodaysDate(toDateTV);
        LocationsController controller = getController();
        locations = controller.getLocations();
        ArrayAdapter<String> locationsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                controller.getLocationNames());
        locationsSpinner.setAdapter(locationsAdapter);
        locationsSpinner.setOnItemSelectedListener(selectedListener);
        adapter = new SunRiseSetAdapter(getSunriseSetBetweenDates());
        dataListView.setAdapter(adapter);
    }

    private AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateList();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void setToTodaysDate(TextView dateTV) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String date = String.format(Locale.getDefault(), Constants.DATE_FORMAT, dayOfMonth, month, year);
        dateTV.setText(date);
    }

    @Override
    protected int getMenuRes() {
        return R.menu.fragment_menu_gen_table;
    }

    @Override
    protected boolean hasMenu() {
        return true;
    }

    @Override
    public String getFragmentTag() {
        return GenerateFragment.class.getName();
    }

    @Override
    public int getTitleStringResource() {
        return R.string.generate_table;
    }

    @Override
    public void onClick(final View v) {
        int year;
        int month;
        int dayOfMonth;
        if (TextUtils.isEmpty(((TextView) v).getText())) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            CharSequence text = ((TextView) v).getText();
            String[] datePieces = text.toString().split(Constants.DATE_SEPARATOR);
            year = Integer.parseInt(datePieces[2]);
            month = Integer.parseInt(datePieces[1]);
            dayOfMonth = Integer.parseInt(datePieces[0]);
        }
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = String.format(Locale.getDefault(), Constants.DATE_FORMAT, dayOfMonth, month, year);
                ((TextView) v).setText(date);
                updateList();
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void updateList() {
        List<String[]> sunRiseSet = getSunriseSetBetweenDates();
        adapter.setSunriseSetData(sunRiseSet).notifyDataSetChanged();
    }

    private List<String[]> getSunriseSetBetweenDates() {
        Location location = locations.get(locationsSpinner.getSelectedItemPosition());
        int year, month, dayOfMonth;
        String[] datePieces = fromDateTV.getText().toString().split(Constants.DATE_SEPARATOR);
        year = Integer.parseInt(datePieces[2]);
        month = Integer.parseInt(datePieces[1]);
        dayOfMonth = Integer.parseInt(datePieces[0]);
        Calendar fromDate = Calendar.getInstance();
        fromDate.clear();
        fromDate.set(year, month, dayOfMonth);
        datePieces = toDateTV.getText().toString().split(Constants.DATE_SEPARATOR);
        year = Integer.parseInt(datePieces[2]);
        month = Integer.parseInt(datePieces[1]);
        dayOfMonth = Integer.parseInt(datePieces[0]);
        Calendar toDate = Calendar.getInstance();
        toDate.clear();
        toDate.set(year, month, dayOfMonth);

        return getController().getSunriseSetBetweenDates(getContext(), location, fromDate, toDate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        File savedImage;
        switch (item.getItemId()) {
            case R.id.item_save:
                savedImage = Util.saveImage(getActivity(), dataListView, Constants.REQ_CODE_WRITE_PERMISSION_SAVE);
                if (savedImage != null) {
                    Toast.makeText(getActivity(), getString(R.string.saved, savedImage.getAbsolutePath()), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.item_share:
                savedImage = Util.saveImage(getActivity(), dataListView, Constants.REQ_CODE_WRITE_PERMISSION_SHARE);
                if (savedImage != null) {
                    Toast.makeText(getActivity(), "Sharing this image...", Toast.LENGTH_SHORT).show();
                    Util.shareImage(savedImage, getActivity());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQ_CODE_WRITE_PERMISSION_SAVE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getActivity(), getString(R.string.need_permission, "save data"), Toast.LENGTH_SHORT).show();
                } else {
                    File savedImage = Util.saveImage(getActivity(), dataListView, Constants.REQ_CODE_WRITE_PERMISSION_SAVE);
                    if (savedImage != null) {
                        Toast.makeText(getActivity(), getString(R.string.saved, savedImage.getAbsolutePath()), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case Constants.REQ_CODE_WRITE_PERMISSION_SHARE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getActivity(), getString(R.string.need_permission, "share data"), Toast.LENGTH_SHORT).show();
                } else {
                    File savedImage = Util.saveImage(getActivity(), dataListView, Constants.REQ_CODE_WRITE_PERMISSION_SHARE);
                    if (savedImage != null) {
                        Util.shareImage(savedImage, getActivity());
                    }
                }
                break;
        }
    }
}
