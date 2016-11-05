package suntime.swindroid.travelcalculator.fragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import suntime.swindroid.travelcalculator.MainActivity;
import suntime.swindroid.travelcalculator.controller.LocationsController;

/**
 * Created by srikaram on 14-Oct-16.
 */
public abstract class AbsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(hasMenu());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(getLayoutRes(), container, false);
    }

    @LayoutRes
    protected abstract int getLayoutRes();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (hasMenu()) {
            inflater.inflate(getMenuRes(), menu);
        }
    }

    protected LocationsController getController() {
        if (getActivity() == null) {
            return null;
        }
        return ((MainActivity) getActivity()).getController();
    }

    @MenuRes
    protected abstract int getMenuRes();

    protected abstract boolean hasMenu();

    public abstract String getFragmentTag();

    @StringRes
    public abstract int getTitleStringResource();
}
