package de.ruf2.rube.fridgeorganizer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Bernhard Ruf on 28.08.2016.
 */
public class FridgeContract {

    public static final String CONTENT_AUTHORITY = "de.ruf2.rube.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FRIDGE = "fridge";
    public static final String PATH_PRODUCT = "product";
    public static final String PATH_PRODUCT_TYPE = "product_type";
    public static final String PATH_FRIDGE_TYPE = "fridge_type";


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

        public static Uri buildProductsOfFridge(String fridge) {
            return CONTENT_URI.buildUpon().appendPath(fridge).build();
        }

        public static String getFridgeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
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
