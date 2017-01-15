package de.ruf2.rube.fridgeorganizer.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import de.ruf2.rube.fridgeorganizer.data.FridgeContract.ProductEntry;

/**
 * Created by Bernhard Ruf on 28.08.2016.
 */
public class TestUtilities extends AndroidTestCase {

    static final String TEST_LOCATION = "99705";
    static final String TEST_PRODUCT_NAME = "Milch";
    static final long TEST_DATE = 1419033600L;  // December 20th, 2014


    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createProductValues(long fridgeRowId) {
        ContentValues productValues = new ContentValues();
        productValues.put(ProductEntry.COLUMN_FRIDGE_KEY, fridgeRowId);
        productValues.put(ProductEntry.COLUMN_BUY_DATE, TEST_DATE);
        productValues.put(ProductEntry.COLUMN_EXPIRE_DATE, TEST_DATE);
        productValues.put(ProductEntry.COLUMN_AMOUNT, 2);
        productValues.put(ProductEntry.COLUMN_NAME, TEST_PRODUCT_NAME);

        return productValues;
    }

    static ContentValues createFridgeValues(){
        ContentValues fridgeValues = new ContentValues();
        fridgeValues.put(FridgeContract.FridgeEntry.COLUMN_NAME, "TestFridge");
        return fridgeValues;
    }
}
