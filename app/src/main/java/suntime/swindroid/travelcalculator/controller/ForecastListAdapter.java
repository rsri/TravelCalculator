package suntime.swindroid.travelcalculator.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import suntime.swindroid.travelcalculator.R;
import suntime.swindroid.travelcalculator.model.ForecastItem;

/**
 * Created by srikaram on 16-Oct-16.
 */
public class ForecastListAdapter extends BaseAdapter {

    private List<ForecastItem> forecastItems;

    public ForecastListAdapter(List<ForecastItem> forecastItems) {
        this.forecastItems = forecastItems;
    }

    public ForecastListAdapter setForecastItems(List<ForecastItem> forecastItems) {
        this.forecastItems = forecastItems;
        return this;
    }

    @Override
    public int getCount() {
        return forecastItems.size();
    }

    @Override
    public Object getItem(int position) {
        return forecastItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        ForecastItem forecastItem = forecastItems.get(position);
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.date = (TextView) convertView.findViewById(R.id.date_tv);
            viewHolder.day = (TextView) convertView.findViewById(R.id.day_tv);
            viewHolder.low = (TextView) convertView.findViewById(R.id.low_tv);
            viewHolder.high = (TextView) convertView.findViewById(R.id.high_tv);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text_tv);

            convertView.setTag(viewHolder);
        }
        viewHolder.date.setText(forecastItem.getDate());
        viewHolder.day.setText(forecastItem.getDay());
        viewHolder.low.setText("Low:"+forecastItem.getLow());
        viewHolder.high.setText("High:"+forecastItem.getHigh());
        viewHolder.text.setText(forecastItem.getText());

        return convertView;
    }

    private class ViewHolder {
        private TextView date, day, low, high, text;
    }
}
