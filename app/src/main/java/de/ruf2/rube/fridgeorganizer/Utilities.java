package de.ruf2.rube.fridgeorganizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Bernhard Ruf on 26.09.2016.
 */
public class Utilities {

    public static void hideKeyboard(Activity mContext) {
        //hide soft keyboard
        InputMethodManager inputManager = (InputMethodManager)
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == mContext.getCurrentFocus()) ? null :
                mContext.getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static String getDateString(Date date) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        return df.format(date);
    }

    public static String getTodayDateString() {
        Date today = new Date(System.currentTimeMillis());
        return getDateString(today);
    }

    public static String getZeroDateString() {
        Date zero = new Date(0);
        return getDateString(zero);
    }

    public static Date parseDate(String dateString) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        return df.parse(dateString);

    }

    public static DatePickerDialog initDatePicker(DatePickerDialog datePickerDialog, final EditText editTextDate, Activity activity) {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editTextDate.setText(Utilities.getDateString(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        return datePickerDialog;

    }

    public static String fetchProduct(String ean)  {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String productJsonString = null;

        try {
            final String DB_BASE_URL = "https://api.outpan.com/v2/products/";
            final String EAN_PARAM = ean;

            final String API_PARAM = "apikey";
            final String API_KEY = BuildConfig.OUTPAN_API_KEY;

            Uri builtUri = Uri.parse(DB_BASE_URL + EAN_PARAM + "?").buildUpon()
                    .appendQueryParameter(API_PARAM, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            productJsonString = buffer.toString();
            return getProductDataFromJSON(productJsonString, ean);
        } catch (Exception e) {
            Timber.e(e, "Error");
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Timber.e(e, "Error closing stream");
                }
            }

        }

    }

    private static String getProductDataFromJSON(String productJsonString, String ean) throws JSONException{
        final String NAME = "name";


            JSONObject productJson = new JSONObject(productJsonString);


            String productName = "";
            if (productJson.has(NAME)) {
                 productName = productJson.getString(NAME);
            }
        return productName;
    }

    public static Date changeDate(Integer days){
        // convert date to calendar
        Date currentDate  = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        // manipulate date
        if (days == 30){
            c.add(Calendar.MONTH, 1);
        }else {
            c.add(Calendar.DATE, days);
        }
        return c.getTime();
    }
}
