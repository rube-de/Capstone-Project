package de.ruf2.rube.fridgeorganizer.data.entities;

import de.ruf2.rube.fridgeorganizer.data.RealmAutoIncrement;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Bernhard Ruf on 25.09.2016.
 */
public class Fridge extends RealmObject{
    @Required
    @PrimaryKey
    private Integer _id = RealmAutoIncrement.getInstance().getNextIdFromModel(Fridge.class);
    public Integer getId() {
        return _id;
    }

    @Required
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }


}
