package de.ruf2.rube.fridgeorganizer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

import de.ruf2.rube.fridgeorganizer.data.entities.FridgeColums;
import de.ruf2.rube.fridgeorganizer.data.entities.ProductColumns;

/**
 * Created by Bernhard Ruf on 14.01.2017.
 */

@Database(version = FoDataBase.VERSION)
public class FoDataBase {

    public static final int VERSION = 1;



        @Table(FridgeColums.class)
        public static final String FRIDGES = "fridges";
        @Table(ProductColumns.class)
        public static final String PRODUCTS = "products";

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {
    }

    @OnConfigure
    public static void onConfigure(SQLiteDatabase db) {
    }

}
