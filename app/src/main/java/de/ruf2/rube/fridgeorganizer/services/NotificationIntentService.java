package de.ruf2.rube.fridgeorganizer.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.preference.PreferenceManager;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

import de.ruf2.rube.fridgeorganizer.MainActivity;
import de.ruf2.rube.fridgeorganizer.R;
import de.ruf2.rube.fridgeorganizer.Utilities;
import de.ruf2.rube.fridgeorganizer.data.FridgeContract;
import de.ruf2.rube.fridgeorganizer.receivers.NotificationEventReceiver;
import timber.log.Timber;

/**
 * Created by Bernhard Ruf on 20.12.2016.
 */

public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {

        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        Timber.d("delete notification");
    }

    private void processStartNotification() {
        //get for shared preferences date for expiry time of the products
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isCustomDate = preferences.getBoolean(getString(R.string.key_custom_date), false);
        Integer expiryInt = NumberUtils.toInt(preferences.getString(getString(R.string.key_expiry_date), "0"));
        Date expiryDate = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), 1);
        if (isCustomDate) {
            expiryDate = Utilities.changeDate(expiryInt);
        }

        Uri fridgeUri = FridgeContract.ProductEntry.buildProductWithExpiryEndDate(expiryDate.getTime());
        Cursor query = getContentResolver().query(fridgeUri, null, null, null, null);
//        Realm realm = Realm.getDefaultInstance();
//        RealmQuery<Product> query = realm.where(Product.class);
//        query.lessThanOrEqualTo("expiryDate", date);
//        RealmResults<Product> results = query.findAll();

        if (query.getCount() > 0) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            String text = getString(R.string.notify_text_there_are) + query.getCount() + getString(R.string.notify_text_expiring_products);
            builder.setContentTitle(getString(R.string.notify_title))
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentText(text)
                    .setSmallIcon(R.drawable.ic_menu_send);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.NOTIFICATION_EXTRA, true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    NOTIFICATION_ID,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

            final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
