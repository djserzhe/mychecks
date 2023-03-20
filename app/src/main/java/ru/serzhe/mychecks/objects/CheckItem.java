package ru.serzhe.mychecks.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sergio on 07.05.2017.
 */

public class CheckItem implements Parcelable {

    public String product;
    public String category;
    public float quantity;
    public float price;
    public float sum;
    public float nds10;
    public float nds18;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {product, category} );
        dest.writeFloatArray(new float[] {quantity, price, sum, nds10, nds18} );
    }

    public static final Parcelable.Creator<CheckItem> CREATOR
            = new Parcelable.Creator<CheckItem>() {
        public CheckItem createFromParcel(Parcel in) {
            String[] strings = new String[2];
            in.readStringArray(strings);
            float[] floats = new float[5];
            in.readFloatArray(floats);

            CheckItem item = new CheckItem();
            item.product = strings[0];
            item.category = strings[1];
            item.quantity = floats[0];
            item.price = floats[1];
            item.sum = floats[2];
            item.nds10 = floats[3];
            item.nds18 = floats[4];

            return item;
        }

        public CheckItem[] newArray(int size) {
            return new CheckItem[size];
        }
    };
}
