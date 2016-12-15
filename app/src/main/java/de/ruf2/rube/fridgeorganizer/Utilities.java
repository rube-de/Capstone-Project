package de.ruf2.rube.fridgeorganizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Bernhard Ruf on 26.09.2016.
 */
public class Utilities {

    public static void hideKeyboard(Activity mContext){
    //hide soft keyboard
    InputMethodManager inputManager = (InputMethodManager)
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    inputManager.hideSoftInputFromWindow((null == mContext.getCurrentFocus()) ? null :
            mContext.getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static String getDateString(Date date){
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        return df.format(date);
    }

    public static String getTodayDateString(){
        Date today = new Date(System.currentTimeMillis());
        return getDateString(today);
    }

    public static Date parseDate(String dateString) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        return df.parse(dateString);

    }

    public static DatePickerDialog initDatePicker(DatePickerDialog datePickerDialog, final EditText editTextDate, Activity activity){
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
}
