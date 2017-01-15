package de.ruf2.rube.fridgeorganizer;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

/**
 * Implementation of App Widget functionality.
 */
public class ExpiryWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //get for shared preferences date for expiry time of the products
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        Boolean isCustomDate = preferences.getBoolean(getString(R.string.key_custom_date), false);
//        Integer expiryInt = NumberUtils.toInt(preferences.getString(getString(R.string.key_expiry_date), "0"));
//        Date date = new Date();
//        if (isCustomDate) {
//            date = Utilities.changeDate(expiryInt);
//        }
//        Realm realm = Realm.getDefaultInstance();
//        RealmQuery<Product> query = realm.where(Product.class);
//        query.lessThanOrEqualTo("expiryDate", date);
//        RealmResults<Product> results = query.findAll();
//
//        CharSequence widgetText = context.getString(R.string.appwidget_text);
//        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.expiry_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
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

