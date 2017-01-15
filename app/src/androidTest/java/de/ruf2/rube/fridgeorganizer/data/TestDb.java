package de.ruf2.rube.fridgeorganizer.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import de.ruf2.rube.fridgeorganizer.data.FridgeContract.*;

/**
 * Created by Bernhard Ruf on 14.01.2017.
 */

public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(FridgeDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(FridgeContract.ProductTypeEntry.TABLE_NAME);
        tableNameHashSet.add(FridgeContract.FridgeTypeEntry.TABLE_NAME);
        tableNameHashSet.add(FridgeContract.ProductEntry.TABLE_NAME);
        tableNameHashSet.add(FridgeContract.FridgeEntry.TABLE_NAME);


        mContext.deleteDatabase(FridgeDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new FridgeDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that our database doesn't contain both the fridge entry
        // and product entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + FridgeContract.FridgeEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> fridgeColumnHashSet = new HashSet<String>();
        fridgeColumnHashSet.add(FridgeEntry._ID);
        fridgeColumnHashSet.add(FridgeEntry.COLUMN_NAME);
        fridgeColumnHashSet.add(FridgeEntry.COLUMN_FRIDGE_TYPE);
        fridgeColumnHashSet.add(FridgeEntry.COLUMN_ORDER_NUMBER);
        fridgeColumnHashSet.add(FridgeEntry.COLUMN_LOCATION);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            fridgeColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required fridge entry columns",
                fridgeColumnHashSet.isEmpty());
        db.close();
    }

    public void testFridgeTable(){
        insertFridge();
    }

    public long insertFridge() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        FridgeDbHelper dbHelper = new FridgeDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = new ContentValues();
        testValues.put(FridgeEntry.COLUMN_NAME, "Fridge1");
        testValues.put(FridgeEntry.COLUMN_FRIDGE_TYPE, 1);
        testValues.put(FridgeEntry.COLUMN_ORDER_NUMBER, 1);
        testValues.put(FridgeEntry.COLUMN_LOCATION, "Kitchen");


        // Third Step: Insert ContentValues into database and get a row ID back
        long fridgeRowId;
        fridgeRowId = db.insert(FridgeContract.FridgeEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(fridgeRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                FridgeContract.FridgeEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from fridge query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Fridge Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from fridge query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return fridgeRowId;
    }

    public void testProductTable(){
        long fridgeRowId = insertFridge();

        // Make sure we have a valid row ID.
        assertFalse("Error: fridge Not Inserted Correctly", fridgeRowId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        FridgeDbHelper dbHelper = new FridgeDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (Weather): Create weather values
        ContentValues productValues = TestUtilities.createProductValues(fridgeRowId);

        // Third Step (Weather): Insert ContentValues into database and get a row ID back
        long productRowId = db.insert(FridgeContract.ProductEntry.TABLE_NAME, null, productValues);
        assertTrue(productRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor productCursor = db.query(
                ProductEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from fridge query", productCursor.moveToFirst() );

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb productEntry failed to validate",
                productCursor, productValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from weather query",
                productCursor.moveToNext() );

        // Sixth Step: Close cursor and database
        productCursor.close();
        dbHelper.close();
    }
}
