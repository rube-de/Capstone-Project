package de.ruf2.rube.fridgeorganizer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.ruf2.rube.fridgeorganizer.data.FridgeContract.*;

/**
 * Created by Bernhard Ruf on 14.01.2017.
 */

public class FridgeDbHelper extends SQLiteOpenHelper {

    // increment the database version, when database schema changes
    private static final int DATABASE_VERSION = 1 ;

    static final String DATABASE_NAME = "fridge.db";

    public FridgeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FRIDGE_TABLE = "CREATE TABLE " + FridgeEntry.TABLE_NAME + " (" +
                FridgeEntry._ID + " INTEGER PRIMARY KEY, " +
                FridgeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                FridgeEntry.COLUMN_FRIDGE_TYPE + " INTEGER, " +
                FridgeEntry.COLUMN_ORDER_NUMBER + " INTEGER, " +
                FridgeEntry.COLUMN_LOCATION + " TEXT, " +
                " FOREIGN KEY (" + FridgeEntry.COLUMN_FRIDGE_TYPE + ") REFERENCES " +
                FridgeContract.FridgeTypeEntry.TABLE_NAME + "(" + FridgeContract.FridgeTypeEntry._ID + ") " +
                " );";

        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME +" (" +
                ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProductEntry.COLUMN_FRIDGE_KEY + " LONG NOT NULL," +
                ProductEntry.COLUMN_NAME + " TEXT NOT NULL," +
                ProductEntry.COLUMN_PRODUCT_TYPE + " INTEGER," +
                ProductEntry.COLUMN_AMOUNT + " INTEGER NOT NULL," +
                ProductEntry.COLUMN_BUY_DATE + " LONG NOT NULL," +
                ProductEntry.COLUMN_EXPIRE_DATE + " LONG NOT NULL, " +

                // Set up the fridge column as a foreign key to fridge table.
                " FOREIGN KEY (" + ProductEntry.COLUMN_FRIDGE_KEY + ") REFERENCES " +
                FridgeEntry.TABLE_NAME + "(" + FridgeEntry._ID + "), " +

                " FOREIGN KEY (" + ProductEntry.COLUMN_PRODUCT_TYPE + ") REFERENCES " +
                FridgeContract.ProductTypeEntry.TABLE_NAME + " (" + FridgeContract.ProductEntry._ID + ") " +

                " );";

        final String SQL_CREATE_FRIDGE_TYPE_TABLE = "CREATE TABLE " + FridgeTypeEntry.TABLE_NAME + " (" +
                FridgeTypeEntry._ID + " INTEGER PRIMARY KEY, " +
                FridgeTypeEntry.COLUMN_NAME + " TEXT NOT NULL" +
                " );";

        final String SQL_CREATE_PRODUCT_TYPE_TABLE = "CREATE TABLE " + ProductTypeEntry.TABLE_NAME + " (" +
                ProductTypeEntry._ID + " INTEGER PRIMARY KEY, " +
                ProductTypeEntry.COLUMN_NAME + " TEXT NOT NULL" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TYPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FRIDGE_TYPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FRIDGE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO: remove next  lines for not wiping data
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FridgeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductTypeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FridgeTypeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
