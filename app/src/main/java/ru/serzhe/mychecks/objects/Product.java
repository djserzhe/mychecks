package ru.serzhe.mychecks.objects;

/**
 * Created by sergio on 17.06.2017.
 */

public class Product extends ProductGroupBase {
    public long categoryID;

    public Product(long _id, String _name, long _categoryID) {
        ID = _id;
        name = _name;
        categoryID = _categoryID;
    }

}
