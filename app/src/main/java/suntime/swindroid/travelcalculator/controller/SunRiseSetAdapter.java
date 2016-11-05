package suntime.swindroid.travelcalculator.controller;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import suntime.swindroid.travelcalculator.R;

/**
 * Created by srikaram on 16-Oct-16.
 */
public class SunRiseSetAdapter extends BaseAdapter {

    private List<String[]> sunriseSetData;

    public SunRiseSetAdapter(List<String[]> sunriseSetData) {
        this.sunriseSetData = sunriseSetData;
    }

    public SunRiseSetAdapter setSunriseSetData(List<String[]> sunriseSetData) {
        this.sunriseSetData = sunriseSetData;
        return this;
    }

    @Override
    public int getCount() {
        return sunriseSetData.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return position == 0? new String[] {"Date", "Sunrise", "Sunset"} : sunriseSetData.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        String[] data = position == 0? new String[] {"Date", "Sunrise", "Sunset"} : sunriseSetData.get(position - 1);
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sunriseset_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.date = (TextView) convertView.findViewById(R.id.date_tv);
            viewHolder.sunrise = (TextView) convertView.findViewById(R.id.sunrise_tv);
            viewHolder.sunset = (TextView) convertView.findViewById(R.id.sunset_tv);

            convertView.setTag(viewHolder);
        }
        viewHolder.date.setText(data[0]);
        viewHolder.sunrise.setText(data[1]);
        viewHolder.sunset.setText(data[2]);

        viewHolder.date.setTypeface(null, position == 0? Typeface.BOLD : Typeface.NORMAL);
        viewHolder.sunrise.setTypeface(null, position == 0? Typeface.BOLD : Typeface.NORMAL);
        viewHolder.sunset.setTypeface(null, position == 0? Typeface.BOLD : Typeface.NORMAL);

        return convertView;
    }

    private class ViewHolder {
        private TextView date, sunrise, sunset;
    }
}
