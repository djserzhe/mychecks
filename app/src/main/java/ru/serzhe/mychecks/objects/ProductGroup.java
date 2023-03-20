package ru.serzhe.mychecks.objects;

import java.util.ArrayList;

/**
 * Created by sergio on 17.06.2017.
 */

public class ProductGroup extends ProductGroupBase {
    public long parentID;
    public boolean isOpen;
    public ArrayList<ProductGroup> childGroups;
    public ArrayList<Product> products;
    public float costs;
    public float nds;

    public ProductGroup(long _id, String _name, long _parentID) {
        ID = _id;
        name = _name;
        isOpen = false;
        parentID = _parentID;
        products = new ArrayList<Product>();
        childGroups = new ArrayList<ProductGroup>();
    }

    public ProductGroup(long _id, String _name, long _parentID, float sum, float _nds) {
        ID = _id;
        name = _name;
        isOpen = false;
        parentID = _parentID;
        costs = sum;
        nds = _nds;
        childGroups = new ArrayList<ProductGroup>();
    }
}
