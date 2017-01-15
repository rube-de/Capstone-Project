package de.ruf2.rube.fridgeorganizer.data;

/**
 * Created by Bernhard Ruf on 15.01.2017.
 */

public class DataUtilities {


    public static final String[] FRIDGE_COLUMNS = {
            FridgeContract.FridgeEntry.TABLE_NAME + "." + FridgeContract.FridgeEntry._ID,
            FridgeContract.FridgeEntry.COLUMN_NAME
    };

    public static final int COL_FRIDGE_ID = 0;
    public static final int COL_FRIDGE_NAME = 1;

    public static final String[] PRODUCT_COLUMNS = {
            FridgeContract.ProductEntry.TABLE_NAME + "." + FridgeContract.ProductEntry._ID,
            FridgeContract.ProductEntry.COLUMN_FRIDGE_KEY,
            FridgeContract.ProductEntry.TABLE_NAME + "." + FridgeContract.ProductEntry.COLUMN_NAME,
            FridgeContract.ProductEntry.COLUMN_PRODUCT_TYPE,
            FridgeContract.ProductEntry.COLUMN_BUY_DATE,
            FridgeContract.ProductEntry.COLUMN_EXPIRE_DATE,
            FridgeContract.ProductEntry.COLUMN_AMOUNT,

    };

    public static final int COL_PRODUCT_ID = 0;
    public static final int COL_PRODUCT_FRIDGE_KEY =1;
    public static final int COL_PRODUCT_NAME = 2;
    public static final int COL_PRODUCT_TYPE = 3;
    public static final int COL_PRODUCT_BUY_DATE = 4;
    public static final int COL_PRODUCT_EXPIRY_DATE= 5;
    public static final int COL_PRODUCT_AMOUNT= 6;
}
