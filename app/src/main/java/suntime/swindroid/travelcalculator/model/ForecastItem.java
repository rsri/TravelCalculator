package suntime.swindroid.travelcalculator.model;

/**
 * Created by srikaram on 16-Oct-16.
 */
public class ForecastItem {

    private String date;

    private String day;

    private String high;

    private String low;

    private String text;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ForecastItem{" +
                "date='" + date + '\'' +
                ", day='" + day + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
