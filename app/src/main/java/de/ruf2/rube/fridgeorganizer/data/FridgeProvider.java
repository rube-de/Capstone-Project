package de.ruf2.rube.fridgeorganizer.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Bernhard Ruf on 28.08.2016.
 */
public class FridgeProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = null;

    private FridgeDbHelper mOpenHelper;

    static final int PRODUCT = 100;
    static final int PRODUCT_ID = 101;
    static final int PRODUCT_WITH_FRIDGE = 102;
    static final int PRODUCT_WITH_TYPE = 103;
    static final int PRODUCT_WITH_BUY_DATE = 104;
    static final int PRODUCT_WITH_EXP_DATE = 105;
    static final int PRODUCT_TYPE = 200;
    static final int FRIDGE = 300;
    static final int FRIDGE_ID = 301;
    static final int FRIDGE_TYPE = 400;

    private static final SQLiteQueryBuilder sProductByFridgeQueryBuilder;

    static{
        sProductByFridgeQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sProductByFridgeQueryBuilder.setTables(
                FridgeContract.ProductEntry.TABLE_NAME + " INNER JOIN " +
                        FridgeContract.FridgeEntry.TABLE_NAME +
                        " ON " + FridgeContract.ProductEntry.TABLE_NAME +
                        "." + FridgeContract.ProductEntry.COLUMN_FRIDGE_KEY +
                        " = " + FridgeContract.FridgeEntry.TABLE_NAME +
                        "." + FridgeContract.FridgeEntry._ID);
    }

    //

    //TODO: crete remaining selection helper

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FridgeContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, FridgeContract.PATH_PRODUCT, PRODUCT);
        matcher.addURI(authority, FridgeContract.PATH_PRODUCT + "/#", PRODUCT_ID);
        matcher.addURI(authority, FridgeContract.PATH_PRODUCT + "/#/"+ FridgeContract.PATH_FRIDGE, PRODUCT_WITH_FRIDGE);
        //TODO: finish matching codes
        //matcher.addURI(authority, FridgeContract.PATH_PRODUCT + "/*/#", PRODUCT_WITH_EXP_DATE);

        matcher.addURI(authority, FridgeContract.PATH_FRIDGE, FRIDGE);
        matcher.addURI(authority, FridgeContract.PATH_FRIDGE+ "/#", FRIDGE_ID);
        return matcher;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(FridgeContract.ProductEntry.COLUMN_EXPIRE_DATE)) {
            long dateValue = values.getAsLong(FridgeContract.ProductEntry.COLUMN_EXPIRE_DATE);
            values.put(FridgeContract.ProductEntry.COLUMN_EXPIRE_DATE, FridgeContract.normalizeDate(dateValue));
        }
    }



    @Override
    public boolean onCreate() {
        mOpenHelper = new FridgeDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            //TODO: build querys
            case PRODUCT:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FridgeContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FRIDGE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FridgeContract.FridgeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            // TODO: correct this when queries finished
            case PRODUCT_WITH_FRIDGE:
                return FridgeContract.ProductEntry.CONTENT_TYPE;
            case PRODUCT_WITH_TYPE:
                return FridgeContract.ProductEntry.CONTENT_TYPE;
            case PRODUCT:
                return FridgeContract.ProductEntry.CONTENT_TYPE;
            case FRIDGE:
                return FridgeContract.FridgeEntry.CONTENT_TYPE;
            case FRIDGE_TYPE:
                return FridgeContract.FridgeTypeEntry.CONTENT_TYPE;
            case PRODUCT_TYPE:
                return FridgeContract.ProductTypeEntry.CONTENT_TYPE;
            case PRODUCT_ID:
                return FridgeContract.ProductEntry.CONTENT_ITEM_TYPE;
            case FRIDGE_ID:
                return FridgeContract.FridgeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PRODUCT: {
                normalizeDate(values);
                long _id = db.insert(FridgeContract.ProductEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = FridgeContract.ProductEntry.buildProductUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FRIDGE: {
                long _id = db.insert(FridgeContract.FridgeEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = FridgeContract.FridgeEntry.buildFridgeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FRIDGE_TYPE: {
                long _id = db.insert(FridgeContract.FridgeTypeEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = FridgeContract.FridgeTypeEntry.buildFridgeTypeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PRODUCT_TYPE: {
                long _id = db.insert(FridgeContract.ProductTypeEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = FridgeContract.ProductTypeEntry.buildProductTypeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case PRODUCT:
                rowsDeleted = db.delete(
                        FridgeContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FRIDGE:
                rowsDeleted = db.delete(
                        FridgeContract.FridgeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FRIDGE_TYPE:
                rowsDeleted = db.delete(
                        FridgeContract.FridgeTypeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_TYPE:
                rowsDeleted = db.delete(
                        FridgeContract.ProductTypeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PRODUCT:
                normalizeDate(values);
                rowsUpdated = db.update(FridgeContract.ProductEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FRIDGE:
                rowsUpdated = db.update(FridgeContract.FridgeEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FRIDGE_TYPE:
                rowsUpdated = db.update(FridgeContract.FridgeTypeEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PRODUCT_TYPE:
                rowsUpdated = db.update(FridgeContract.ProductTypeEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
