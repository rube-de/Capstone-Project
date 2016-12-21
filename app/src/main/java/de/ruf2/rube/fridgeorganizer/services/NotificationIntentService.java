package de.ruf2.rube.fridgeorganizer.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.preference.PreferenceManager;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Calendar;
import java.util.Date;

import de.ruf2.rube.fridgeorganizer.MainActivity;
import de.ruf2.rube.fridgeorganizer.R;
import de.ruf2.rube.fridgeorganizer.data.entities.Product;
import de.ruf2.rube.fridgeorganizer.receivers.NotificationEventReceiver;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
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
        // Do something. For example, fetch fresh data from backend to create a rich notification?
        //TODO: implement getting custom date
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isCustomDate = preferences.getBoolean(getString(R.string.key_custom_date), false);
        Integer expiryInt = NumberUtils.toInt(preferences.getString(getString(R.string.key_expiry_date), "0"));
        Date date = new Date();
        if (isCustomDate) {
            // convert date to calendar
            Date currentDate  = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);

            // manipulate date
            if (expiryInt == 30){
                c.add(Calendar.MONTH, 1);
            }else {
                c.add(Calendar.DATE, expiryInt);
            }
            date = c.getTime();
        }
        Realm realm =Realm.getDefaultInstance();
        RealmQuery<Product> query = realm.where(Product.class);
        query.lessThanOrEqualTo("expiryDate", date);
        RealmResults<Product> results = query.findAll();

        if(results.size() > 0 ) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("Scheduled Notification")
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentText("This notification has been triggered by Notification Service")
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
