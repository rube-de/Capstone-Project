package de.ruf2.rube.fridgeorganizer.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Bernhard Ruf on 15.01.2017.
 */

public class TestUriMatcher extends AndroidTestCase {
    private static final String NAME_QUERY = "TestProduct";
    private static final long TEST_DATE = 1419033600L;  // December 20th, 2014
    private static final long TEST_FRIDGE_ID = 10L;

    //product
    private static final Uri TEST_PRODUCT_DIR = FridgeContract.ProductEntry.CONTENT_URI;
    private static final Uri TEST_PRODUCT_WITH_NAME = FridgeContract.ProductEntry.buildProductWithName(NAME_QUERY);
    private static final Uri TEST_PRODUCT_WITH_FRIDGE = FridgeContract.ProductEntry.buildProductsOfFridge(TEST_FRIDGE_ID);
    private static final Uri TEST_PRODUCT_WITH_DATES = FridgeContract.ProductEntry.buildProductWithNameAndBuyAndExpiryDate(NAME_QUERY,TEST_DATE,TEST_DATE,TEST_DATE,TEST_DATE);

    // fridge
    private static final Uri TEST_FRIDGE_DIR = FridgeContract.FridgeEntry.CONTENT_URI;

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = FridgeProvider.buildUriMatcher();

        assertEquals("Error: The PRODUCT URI was matched incorrectly.",
                testMatcher.match(TEST_PRODUCT_DIR), FridgeProvider.PRODUCT);
        assertEquals("Error: The PRODUCT WITH NAME URI was matched incorrectly.",
                testMatcher.match(TEST_PRODUCT_WITH_NAME), FridgeProvider.PRODUCT_WITH_NAME_AND_BETWEEN_BUY_AND_EXP_DATES);
        assertEquals("Error: The PRODUCT IN FRIDGE URI was matched incorrectly.",
                testMatcher.match(TEST_PRODUCT_WITH_FRIDGE), FridgeProvider.PRODUCT_WITH_FRIDGE);
        assertEquals("Error: The PRODUCT WITH NAME AND DATE RANGE URI was matched incorrectly.",
                testMatcher.match(TEST_PRODUCT_WITH_DATES), FridgeProvider.PRODUCT_WITH_NAME_AND_BETWEEN_BUY_AND_EXP_DATES);
        assertEquals("Error: The FRIDGE URI was matched incorrectly.",
                testMatcher.match(TEST_FRIDGE_DIR), FridgeProvider.FRIDGE);
    }
}
