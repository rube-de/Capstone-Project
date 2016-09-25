package de.ruf2.rube.fridgeorganizer.data.entities;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Bernhard Ruf on 25.09.2016.
 */
public class Product extends RealmObject {

    private String name;
    private Fridge fridge;
    private int amount;
    private Date expireDate;
    private Date buyDate;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Fridge getFridge() {
        return fridge;
    }

    public void setFridge(Fridge fridge) {
        this.fridge = fridge;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }
}
