package suntime.swindroid.travelcalculator.yahoo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import suntime.swindroid.travelcalculator.fragments.ViewForecastFragment;
import suntime.swindroid.travelcalculator.model.ForecastItem;
import suntime.swindroid.travelcalculator.model.Location;

/**
 * Created by srikaram on 16-Oct-16.
 */
public class YahooClient extends AsyncTask<Location, Void, List<ForecastItem>> {

    private static final String YAHOO_QUERY = "https://query.yahooapis.com/v1/public/yql" +
            "?q=select%20item.forecast%20from%20weather.forecast" +
            "%20where%20woeid%20in%20(SELECT%20woeid%20FROM%20geo.places" +
            "%20WHERE%20text%3D%22(%s%2C%s)%22)&format=json";

    private final FragmentActivity activity;
    private final ViewForecastFragment.ForecastListener forecastListener;
    private ProgressDialog progressDialog;

    public YahooClient(FragmentActivity activity, ViewForecastFragment.ForecastListener forecastListener) {
        this.activity = activity;
        this.forecastListener = forecastListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Fetching forecast for the location");
        progressDialog.show();
    }

    @Override
    protected List<ForecastItem> doInBackground(Location... params) {
        try {
            Location location = params[0];
            String query = YAHOO_QUERY.replaceFirst("%s", String.valueOf(location.getLatitude()))
                    .replaceFirst("%s", String.valueOf(location.getLongitude()));
            URLConnection connection = new URL(query).openConnection();
            System.out.println(query);
            InputStream response = connection.getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }

            JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
            JSONArray results = jsonObject.getJSONObject("query").getJSONObject("results").getJSONArray("channel");
            List<ForecastItem> forecastItems = new ArrayList<>();
            for (int i = 0 ; i < results.length() ; i++) {
                JSONObject jObject = results.getJSONObject(i);
                JSONObject item = jObject.getJSONObject("item");
                JSONObject forecast = item.getJSONObject("forecast");
                ForecastItem forecastItem = new ForecastItem();
                forecastItem.setDate(forecast.getString("date"));
                forecastItem.setDay(forecast.getString("day"));
                forecastItem.setHigh(forecast.getString("high"));
                forecastItem.setLow(forecast.getString("low"));
                forecastItem.setText(forecast.getString("text"));
                forecastItems.add(forecastItem);
            }
            return forecastItems;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<ForecastItem> result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        if (forecastListener != null) {
            forecastListener.onForecastReceived(result);
        }
    }
}
