package de.ruf2.rube.fridgeorganizer.data.entities;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by Bernhard Ruf on 14.01.2017.
 */

public interface FridgeColums {

    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT) @NotNull
    String NAME = "name";
}
