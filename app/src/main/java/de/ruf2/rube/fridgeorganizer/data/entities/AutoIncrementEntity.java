package de.ruf2.rube.fridgeorganizer.data.entities;

import de.ruf2.rube.fridgeorganizer.data.KnownClasses;
import de.ruf2.rube.fridgeorganizer.exception.UnknownModelException;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * @author Carlos Eduardo
 * @since 03/09/2016
 */
public class AutoIncrementEntity extends RealmObject {

    @Required
    @PrimaryKey
    private Integer _id = 1;

    private Integer fridge;

    private Integer product;

    public Integer getId() {
        return _id;
    }

    public Integer getFridge() {
        return fridge;
    }

    public void setFridge(Integer fridge) {
        this.fridge = fridge;
    }

    public Integer getProduct() {
        return product;
    }

    public void setProduct(Integer product) {
        this.product = product;
    }

    public void incrementByClassName(String className) {

        switch (className) {

            case KnownClasses.FRIDGE:
                fridge = fridge == null ? 1 : ++fridge;
                break;

            case KnownClasses.PRODUCT:
                product = product == null ? 1 : ++product;
                break;

            default:
                throw new UnknownModelException("Class name: " + className);
        }
    }

    public Integer findByClassName(String className) {

        switch (className) {

            case KnownClasses.FRIDGE:
                return this.fridge;

            case KnownClasses.PRODUCT:
                return this.product;

            default:
                throw new UnknownModelException("Class name: " + className);
        }
    }

}
