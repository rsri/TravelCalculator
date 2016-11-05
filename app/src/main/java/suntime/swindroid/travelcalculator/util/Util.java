package suntime.swindroid.travelcalculator.util;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import suntime.swindroid.travelcalculator.R;

/**
 * Created by srikaram on 15-Oct-16.
 */
public class Util {

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
        }
    }

    public static File saveImage(FragmentActivity activity, ListView listview, int requestCode) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            ListAdapter adapter = listview.getAdapter();
            int itemsCount = adapter.getCount();
            int allItemsHeight = 0;
            List<Bitmap> bmps = new ArrayList<>();

            for (int i = 0; i < itemsCount; i++) {
                View childView = adapter.getView(i, null, listview);
                childView.measure(View.MeasureSpec.makeMeasureSpec(listview.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
                childView.setDrawingCacheEnabled(true);
                childView.buildDrawingCache();
                bmps.add(childView.getDrawingCache());
                allItemsHeight += childView.getMeasuredHeight();
            }

            Bitmap bigBitmap = Bitmap.createBitmap(listview.getMeasuredWidth(), allItemsHeight, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);

            Paint paint = new Paint();
            int iHeight = 0;

            for (int i = 0; i < bmps.size(); i++) {
                Bitmap bmp = bmps.get(i);
                bigCanvas.drawBitmap(bmp, 0, iHeight, paint);
                iHeight += bmp.getHeight();

                bmp.recycle();
                bmp = null;
            }

            String timestamp = String.valueOf(System.currentTimeMillis());
            File image = new File(getOurFolder(), timestamp + ".png");
            try {
                image.createNewFile();
                bigBitmap.compress(Bitmap.CompressFormat.PNG, 0, new FileOutputStream(image));
                bigBitmap.recycle();
            } catch (IOException e) {
                Toast.makeText(activity, "Couldn\'t save image.", Toast.LENGTH_SHORT).show();
            }
            return image;
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }
        return null;
    }

    public static void shareImage(File image, FragmentActivity activity) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
        activity.startActivity(Intent.createChooser(intent, "Share Image"));
        image.deleteOnExit();
    }

    public static boolean ensureNetworkPermission(FragmentActivity activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            Toast.makeText(activity, activity.getString(R.string.need_permission, "view forecast"), Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.INTERNET}, Constants.REQ_CODE_ACCESS_INTERNET);
            return false;
        }
    }

    public static boolean ensureLocationPermission(FragmentActivity activity, int requestCode) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
            return false;
        }
    }

    private static File getOurFolder() {
        File root = Environment.getExternalStorageDirectory();
        File savePath = new File(root.getAbsolutePath() + "/TravelCalculator");
        savePath.mkdir();
        return savePath;
    }

    public static double trimPrecision(double input) {
        String doubleStr = String.format(Locale.getDefault(), "%.4f", input);
        return Double.parseDouble(doubleStr);
    }
}
