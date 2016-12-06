package de.ruf2.rube.fridgeorganizer.data.entities;

import java.util.Date;

import de.ruf2.rube.fridgeorganizer.data.RealmAutoIncrement;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Bernhard Ruf on 25.09.2016.
 */
public class Product extends RealmObject {
    @Required
    @PrimaryKey
    private Integer _id = RealmAutoIncrement.getInstance().getNextIdFromModel(Product.class);
    public Integer getId() {
        return _id;
    }

    @Required
    private String name;
    private Fridge fridge;
    private Integer amount;
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
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
