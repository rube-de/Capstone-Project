package de.ruf2.rube.fridgeorganizer.data.entities;

import io.realm.RealmObject;

/**
 * Created by Bernhard Ruf on 25.09.2016.
 */
public class Fridge extends RealmObject{
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
