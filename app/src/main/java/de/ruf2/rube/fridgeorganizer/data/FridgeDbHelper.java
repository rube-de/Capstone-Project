package de.ruf2.rube.fridgeorganizer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.ruf2.rube.fridgeorganizer.data.FridgeContract.FridgeEntry;

/**
 * Created by Bernhard Ruf on 28.08.2016.
 */
public class FridgeDbHelper extends SQLiteOpenHelper{

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
                FridgeEntry.COLUMN_ORDER_NUMBER + " INTEGER NOT NULL, " +
                FridgeEntry.COLUMN_LOCATION + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + FridgeEntry.COLUMN_FRIDGE_TYPE + ") REFERENCES " +
                FridgeContract.FridgeTypeEntry.TABLE_NAME + "(" + FridgeContract.FridgeTypeEntry._ID + ") " +
                " );";

        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + FridgeContract.ProductEntry.TABLE_NAME +" (" +
                FridgeContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FridgeContract.ProductEntry.COLUMN_FRIDGE_KEY + " INTEGER NOT NULL," +
                FridgeContract.ProductEntry.COLUMN_NAME + " TEXT NOT NULL," +
                FridgeContract.ProductEntry.COLUMN_PRODUCT_TYPE + " INTEGER," +
                FridgeContract.ProductEntry.COLUMN_AMOUNT + " INTEGER NOT NULL," +
                FridgeContract.ProductEntry.COLUMN_BUY_DATE + " INTEGER NOT NULL," +
                FridgeContract.ProductEntry.COLUMN_EXPIRE_DATE + " INTEGER NOT NULL, " +

                // Set up the fridge column as a foreign key to fridge table.
                " FOREIGN KEY (" + FridgeContract.ProductEntry.COLUMN_FRIDGE_KEY + ") REFERENCES " +
                FridgeEntry.TABLE_NAME + "(" + FridgeEntry._ID + "), " +

                " FOREIGN KEY (" + FridgeContract.ProductEntry.COLUMN_PRODUCT_TYPE + ") REFERENCES " +
                FridgeContract.ProductTypeEntry.TABLE_NAME + " (" + FridgeContract.ProductEntry._ID + ") " +

                " );";

        final String SQL_CREATE_FRIDGE_TYPE_TABLE = "CREATE TABLE " + FridgeContract.FridgeTypeEntry.TABLE_NAME + " (" +
                FridgeContract.FridgeTypeEntry._ID + " INTEGER PRIMARY KEY, " +
                FridgeContract.FridgeTypeEntry.COLUMN_NAME + " TEXT NOT NULL" +
                " );";

        final String SQL_CREATE_PRODUCT_TYPE_TABLE = "CREATE TABLE " + FridgeContract.ProductTypeEntry.TABLE_NAME + " (" +
                FridgeContract.ProductTypeEntry._ID + " INTEGER PRIMARY KEY, " +
                FridgeContract.ProductTypeEntry.COLUMN_NAME + " TEXT NOT NULL" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TYPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FRIDGE_TYPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FRIDGE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO: remove next to lines for not wiping data
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FridgeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FridgeContract.ProductEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FridgeContract.ProductTypeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FridgeContract.FridgeTypeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
