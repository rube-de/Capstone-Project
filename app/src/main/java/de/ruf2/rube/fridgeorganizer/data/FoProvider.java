package de.ruf2.rube.fridgeorganizer.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import de.ruf2.rube.fridgeorganizer.data.entities.FridgeColums;
import de.ruf2.rube.fridgeorganizer.data.entities.ProductColumns;

/**
 * Created by Bernhard Ruf on 14.01.2017.
 */


@ContentProvider(authority = FoProvider.AUTHORITY, database = FoDataBase.class)
public class FoProvider {

    public static final String AUTHORITY = "de.ruf2.rube.fridgeorganizer.data.FoProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String FRIDGES = "fridges";
        String PRODUCTS = "products";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = FoDataBase.FRIDGES) public static class Fridges {

        @ContentUri(
                path = "fridges",
                type = "vnd.android.cursor.dir/fridge",
                defaultSort = FridgeColums.NAME + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.FRIDGES);

        @InexactContentUri(
                path = Path.FRIDGES + "/#",
                name = "FRIDGE_ID",
                type = "vnd.android.cursor.item/fridge",
                whereColumn = FridgeColums._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.FRIDGES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = FoDataBase.PRODUCTS) public static class Products {

        @ContentUri(
                path = "products",
                type = "vnd.android.cursor.dir/product",
                defaultSort = ProductColumns.NAME + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.FRIDGES);
    }
}
