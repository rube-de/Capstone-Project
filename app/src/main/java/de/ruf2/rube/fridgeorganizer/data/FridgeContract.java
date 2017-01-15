package de.ruf2.rube.fridgeorganizer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Bernhard Ruf on 14.01.2017.
 */

public class FridgeContract {

    public static final String CONTENT_AUTHORITY = "de.ruf2.rube.fridgeorganizer";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FRIDGE = "fridge";
    public static final String PATH_PRODUCT = "product";
    public static final String PATH_PRODUCT_TYPE = "product_type";
    public static final String PATH_FRIDGE_TYPE = "fridge_type";

    //query helper constants
    private static final String EXPIRY_START = "expiry_start";
    private static final String EXPIRY_END = "expiry_end";
    private static final String BUY_START = "buy_start";
    private static final String BUY_END = "buy_end";


    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class FridgeEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FRIDGE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FRIDGE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FRIDGE;


        //table name
        public static final String TABLE_NAME = "fridge";

        //columns
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_FRIDGE_TYPE = "fridge_type";
        //order number to determine ordering of the fridges in the gui
        public static final String COLUMN_ORDER_NUMBER = "order_number";
        public static final String COLUMN_LOCATION = "location";

        public static Uri buildFridgeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getFridgeIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;


        //table name
        public static final String TABLE_NAME = "product";

        //columns

        public static final String COLUMN_FRIDGE_KEY = "fridge_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRODUCT_TYPE = "product_type";
        //dates stored as milliseconds in long since the epoch
        public static final String COLUMN_EXPIRE_DATE = "expire_date";
        public static final String COLUMN_BUY_DATE = "buy_date";
        public static final String COLUMN_AMOUNT = "amount";



        public static Uri buildProductUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildProductsOfFridge(Long fridge) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(fridge)).build();
        }

        public static Uri buildProductWithName(String name){
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }

        public static Uri buildProductWithStartAndEndDate(long startDate, long endDate){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(normalizeDate(startDate)))
                    .appendPath(Long.toString(normalizeDate(endDate))).build();
        }
        public static Uri buildProductWithNameAndBuyAndExpiryDate(String name, long buyStartDate,
                                                                  long buyEndDate, long expiryStartDate,
                                                                  long expiryEndDate){
            return CONTENT_URI.buildUpon()
                    .appendPath(name)
                    .appendQueryParameter(BUY_START, Long.toString(normalizeDate(buyStartDate)))
                    .appendQueryParameter(BUY_END, Long.toString(normalizeDate(buyEndDate)))
                    .appendQueryParameter(EXPIRY_START, Long.toString(normalizeDate(expiryStartDate)))
                    .appendQueryParameter(EXPIRY_END, Long.toString(normalizeDate(expiryEndDate)))
                    .build();
        }

        public static String getFridgeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getExpiryStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(EXPIRY_START);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
        public static long getExpiryEndDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(EXPIRY_END);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }

        public static long getBuyStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(BUY_START);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
        public static long getBuyEndDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(BUY_END);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
    }

    public static final class ProductTypeEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCT_TYPE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT_TYPE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT_TYPE;

        //table name
        public static final String TABLE_NAME = "product_type";

        //columns
        public static final String COLUMN_NAME = "name";

        public static Uri buildProductTypeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class FridgeTypeEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FRIDGE_TYPE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FRIDGE_TYPE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FRIDGE_TYPE;

        //table name
        public static final String TABLE_NAME = "fridge_type";

        //columns
        public static final String COLUMN_NAME = "name";

        public static Uri buildFridgeTypeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}