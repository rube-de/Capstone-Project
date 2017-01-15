package de.ruf2.rube.fridgeorganizer.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by Bernhard Ruf on 15.01.2017.
 */

public class TestProvider extends AndroidTestCase {

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                FridgeContract.FridgeEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                FridgeContract.ProductEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                FridgeContract.ProductEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from product table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                FridgeContract.FridgeEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from fridge table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testBasicFridgeQuery(){
        FridgeDbHelper dbHelper = new FridgeDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createFridgeValues();

        long fridgeRowId = db.insert(FridgeContract.FridgeEntry.TABLE_NAME, null, testValues);
        assertTrue("unable to insert into database", fridgeRowId != -1);

        db.close();

        Cursor fridgeCusor = mContext.getContentResolver().query(
                FridgeContract.FridgeEntry.CONTENT_URI,
                null,
                "fridge._id = ?",
                new String[] {"1"},
                null);
        fridgeCusor.moveToFirst();
        String test = fridgeCusor.getString(1);
        TestUtilities.validateCursor("testbasicfridge", fridgeCusor, testValues);
    }
}
