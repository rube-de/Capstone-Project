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
 * Created by Bernhard Ruf on 14.01.2017.
 */

public class FridgeProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private FridgeDbHelper mOpenHelper;

    static final int PRODUCT = 100;
    static final int PRODUCT_ID = 101;
    static final int PRODUCT_WITH_FRIDGE = 102;
    static final int PRODUCT_WITH_TYPE = 103;
    static final int PRODUCT_BETWEEN_BUY_DATES = 104;
    static final int PRODUCT_BETWEEN_EXP_DATES = 105;
    static final int PRODUCT_BETWEEN_BUY_AND_EXP_DATES = 106;
    static final int PRODUCT_WITH_NAME = 110;
    static final int PRODUCT_WITH_NAME_AND_BETWEEN_BUY_AND_EXP_DATES = 111;
    static final int PRODUCT_TYPE = 200;
    static final int FRIDGE = 300;
    static final int FRIDGE_ID = 301;
    static final int FRIDGE_TYPE = 400;

    static final String DATE = "date";

    private static final SQLiteQueryBuilder sProductByFridgeQueryBuilder;

    static{
        sProductByFridgeQueryBuilder = new SQLiteQueryBuilder();

        //product INNER JOIN fridge ON product.fridge_id = fridge._id
        sProductByFridgeQueryBuilder.setTables(
                FridgeContract.ProductEntry.TABLE_NAME + " INNER JOIN " +
                        FridgeContract.FridgeEntry.TABLE_NAME +
                        " ON " + FridgeContract.ProductEntry.TABLE_NAME +
                        "." + FridgeContract.ProductEntry.COLUMN_FRIDGE_KEY +
                        " = " + FridgeContract.FridgeEntry.TABLE_NAME +
                        "." + FridgeContract.FridgeEntry._ID);
    }

    //

    //product with expiry date >= ?(start date) and <= ? (end date)
    private static final String sProductWithExpirySettingSelection =
            FridgeContract.ProductEntry.TABLE_NAME +
                    "." + FridgeContract.ProductEntry.COLUMN_EXPIRE_DATE + " >= ? AND " +
                    FridgeContract.ProductEntry.COLUMN_EXPIRE_DATE + " <= ? ";

    //product with buy date >= ?(start date) and <= ? (end date)
    private static final String sProductWithBuySettingSelection =
            FridgeContract.ProductEntry.TABLE_NAME +
                    "." + FridgeContract.ProductEntry.COLUMN_BUY_DATE + " >= ? AND " +
                    FridgeContract.ProductEntry.COLUMN_BUY_DATE + " <= ? ";

    //product with name
    private static final String sProductWithNameSettingSelection =
            FridgeContract.ProductEntry.TABLE_NAME +
                    "." + FridgeContract.ProductEntry.COLUMN_NAME + " LIKE ? ";

    //product with name and buy / expiry date between dates
    private static final String sProductWithNameAndBetweenBuyAndExpiryDatesSettingSelection =
            FridgeContract.ProductEntry.TABLE_NAME +
                    "." + FridgeContract.ProductEntry.COLUMN_NAME + " LIKE ? AND " +
                    FridgeContract.ProductEntry.COLUMN_BUY_DATE + " >= ? AND " +
                    FridgeContract.ProductEntry.COLUMN_BUY_DATE + " <= ? AND " +
                    FridgeContract.ProductEntry.COLUMN_EXPIRE_DATE + " >= ? AND " +
                    FridgeContract.ProductEntry.COLUMN_EXPIRE_DATE + " <= ? ";



    // product with fridge_id = ?
    private static final String sProductWithFridgeSelection =
            FridgeContract.ProductEntry.TABLE_NAME +
                    "." + FridgeContract.ProductEntry.COLUMN_FRIDGE_KEY + " = ? ";

    //fridge by fridge._id = ?
    private static final String sFridgeByIdSelection =
            FridgeContract.FridgeEntry.TABLE_NAME +
                    "." + FridgeContract.FridgeEntry._ID + " = ? ";

    private Cursor getProductByFridgeSetting(Uri uri, String[] projection, String sortOrder) {
        String fridgeSetting = FridgeContract.ProductEntry.getFridgeFromUri(uri);

        String[] selectionArgs = new String[] {fridgeSetting};
        String selection = sProductWithFridgeSelection;

        return sProductByFridgeQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getProductByNameSetting(Uri uri, String[] projection, String sortOrder){
        String productName = FridgeContract.ProductEntry.getNameFromUri(uri);

        String[] selectionArgs = new String[] {productName};
        String selection = sProductWithNameSettingSelection;

        return sProductByFridgeQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getProductByNameAndBuyAndExpiryRangeSetting(Uri uri, String[] projection, String sortOrder){
        String productName = FridgeContract.ProductEntry.getNameFromUri(uri);
        String buyStartDate = Long.toString(FridgeContract.ProductEntry.getBuyStartDateFromUri(uri));
        String buyEndDate = Long.toString(FridgeContract.ProductEntry.getBuyEndDateFromUri(uri));
        String expiryStartDate= Long.toString(FridgeContract.ProductEntry.getExpiryStartDateFromUri(uri));
        String expiryEndDate = Long.toString(FridgeContract.ProductEntry.getExpiryEndDateFromUri(uri));

        String[] selectionArgs;
        String selection;

        if(buyStartDate == "0" && buyEndDate == "0"   && expiryStartDate == "0"  && expiryEndDate== "0" ){
            selectionArgs = new String[] {productName};
            selection = sProductWithNameSettingSelection;

        }else {
            selectionArgs = new String[]{productName, buyStartDate, buyEndDate, expiryStartDate, expiryEndDate};
            selection = sProductWithNameAndBetweenBuyAndExpiryDatesSettingSelection;
        }

        return sProductByFridgeQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getProductWithExpirySetting(Uri uri, String[] projection, String sortOrder){
        String expiryEndDate = Long.toString(FridgeContract.ProductEntry.getExpiryEndDateFromUri(uri));
        //startdate = 0
        String startDate = "0";

        String[] selectionArgs = new String[] {startDate, expiryEndDate};
        String selection = sProductWithExpirySettingSelection;

        return sProductByFridgeQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private  Cursor getFridgeByIdSetting (Uri uri, String[] projection, String sortOrder){
        String fridgeId = FridgeContract.FridgeEntry.getFridgeIdFromUri(uri);
        String[] selectionArgs = new String[] {fridgeId};
        String selection = sFridgeByIdSelection;

        return mOpenHelper.getReadableDatabase().query(FridgeContract.FridgeEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FridgeContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FridgeContract.PATH_PRODUCT, PRODUCT);
        matcher.addURI(authority, FridgeContract.PATH_PRODUCT + "/#", PRODUCT_ID);
        matcher.addURI(authority, FridgeContract.PATH_PRODUCT + "/#", PRODUCT_WITH_FRIDGE);
        matcher.addURI(authority, FridgeContract.PATH_PRODUCT + "/" + FridgeContract.PATH_EXPIRY + "/*", PRODUCT_BETWEEN_EXP_DATES);
        matcher.addURI(authority, FridgeContract.PATH_PRODUCT + "/#/#", PRODUCT_BETWEEN_BUY_DATES);
//        matcher.addURI(authority, FridgeContract.PATH_PRODUCT + "/*", PRODUCT_WITH_NAME);
        matcher.addURI(authority, FridgeContract.PATH_PRODUCT + "/*", PRODUCT_WITH_NAME_AND_BETWEEN_BUY_AND_EXP_DATES);

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
        if (values.containsKey(FridgeContract.ProductEntry.COLUMN_BUY_DATE)) {
            long dateValue = values.getAsLong(FridgeContract.ProductEntry.COLUMN_BUY_DATE);
            values.put(FridgeContract.ProductEntry.COLUMN_BUY_DATE, FridgeContract.normalizeDate(dateValue));
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
            case FRIDGE: {
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
            case PRODUCT_WITH_NAME:{
                retCursor= getProductByNameSetting(uri, projection, sortOrder);
                break;
            }
            case PRODUCT_WITH_NAME_AND_BETWEEN_BUY_AND_EXP_DATES:{
                retCursor = getProductByNameAndBuyAndExpiryRangeSetting(uri,projection,sortOrder);
                break;
            }
            case PRODUCT_WITH_FRIDGE:{
                retCursor = getProductByFridgeSetting(uri,projection,sortOrder);
                break;
            }
            case PRODUCT_BETWEEN_BUY_DATES:{
                retCursor = null;
                break;
            }
            case PRODUCT_BETWEEN_EXP_DATES:{
                retCursor = getProductWithExpirySetting(uri, projection,sortOrder);
                break;
            }
            case FRIDGE_ID:{
                retCursor = getFridgeByIdSetting(uri,projection,sortOrder);
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
            case PRODUCT_BETWEEN_BUY_DATES:
                return FridgeContract.ProductEntry.CONTENT_TYPE;
            case PRODUCT_BETWEEN_EXP_DATES:
                return FridgeContract.ProductEntry.CONTENT_TYPE;
            case PRODUCT_WITH_NAME:
                return FridgeContract.ProductEntry.CONTENT_TYPE;
            case PRODUCT_WITH_NAME_AND_BETWEEN_BUY_AND_EXP_DATES:
                return FridgeContract.ProductEntry.CONTENT_TYPE;
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
