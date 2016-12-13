package de.ruf2.rube.fridgeorganizer;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return df.format(date);
    }
}
