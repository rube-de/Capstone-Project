package de.ruf2.rube.fridgeorganizer;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.widget.RemoteViews;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

import de.ruf2.rube.fridgeorganizer.data.FridgeContract;

/**
 * Implementation of App Widget functionality.
 */
public class ExpiryWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //get for shared preferences date for expiry time of the products
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean isCustomDate = preferences.getBoolean(context.getString(R.string.key_custom_date), false);
        Integer expiryInt = NumberUtils.toInt(preferences.getString(context.getString(R.string.key_expiry_date), "0"));
        Date expiryDate = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), 1);
        if (isCustomDate) {
            expiryDate = Utilities.changeDate(expiryInt);
        }

        Uri fridgeUri = FridgeContract.ProductEntry.buildProductWithExpiryEndDate(expiryDate.getTime());
        Cursor query = context.getContentResolver().query(fridgeUri, null, null, null, null);
        int expiringProductsNumber = query.getCount();
        query.close();


//        CharSequence widgetText = context.getString(R.string.appwidget_text);
        CharSequence widgetText = Integer.toString(expiringProductsNumber);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.expiry_widget);
        views.setTextViewText(R.id.appwidget_number, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

