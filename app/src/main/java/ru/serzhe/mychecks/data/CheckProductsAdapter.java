package ru.serzhe.mychecks.data;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import ru.serzhe.mychecks.NewCheckFragment;
import ru.serzhe.mychecks.R;
import ru.serzhe.mychecks.objects.CheckItem;

/**
 * Created by sergio on 30.09.2017.
 */

public class CheckProductsAdapter extends BaseAdapter {

    public ArrayList<CheckItem> items;
    private Context mContext;
    //private int lastFocussedPosition = -1;
    //private Handler handler = new Handler();
    private static final String KEY_ADAPTER_STATE = "CheckProductsAdapter.KEY_ADAPTER_STATE";
    private NewCheckFragment.DynamicHeightListView listView;

    public CheckProductsAdapter(Context context, NewCheckFragment.DynamicHeightListView lv) {
        mContext = context;
        listView = lv;
        items = new ArrayList<>();
        CheckItem item = new CheckItem();
        items.add(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CheckItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        CheckItem item = getItem(position);
        return item.hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.check_product_list_item, parent, false);
        }

        CheckItem item = getItem(position);

//        final EditText checkProductEditText = (EditText)convertView.findViewById(R.id.checkProductEditText);
//        final EditText countEditText = (EditText)convertView.findViewById(R.id.countEditText);
//        final EditText priceEditText = (EditText)convertView.findViewById(R.id.priceEditText);
        ((TextView) convertView.findViewById(R.id.checkProductTextView)).setText(item.product);
        ((TextView) convertView.findViewById(R.id.countTextView)).setText(String.valueOf(item.quantity));
        ((TextView) convertView.findViewById(R.id.priceTextView)).setText(String.valueOf(item.price));
        ((TextView) convertView.findViewById(R.id.sumTextView)).setText(String.valueOf(item.sum));
        Button addItemButton = convertView.findViewById(R.id.addItemButton);
        Button delItemButton = convertView.findViewById(R.id.delItemButton);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.add(new CheckItem());
                notifyDataSetChanged();
                listView.justifyListViewHeightBasedOnChildren();
            }
        });
        delItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.remove(position);
                notifyDataSetChanged();
                listView.justifyListViewHeightBasedOnChildren();
            }
        });
//        checkProductEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                item.product = s.toString();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//        countEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                item.quantity = Float.parseFloat(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//        priceEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                item.price = Float.parseFloat(s.toString());
//                item.sum = item.quantity * item.price;
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });

        return convertView;
    }

    public void onItemClick(int position) {
        final CheckItem item = getItem(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View mView = inflater.inflate(R.layout.add_check_product_dialog, null);
        ((EditText) mView.findViewById(R.id.checkProductEditText)).setText(item.product);
        if (item.quantity > 0) { ((EditText) mView.findViewById(R.id.countEditText)).setText(String.valueOf(item.quantity)); }
        if (item.price > 0) { ((EditText) mView.findViewById(R.id.priceEditText)).setText(String.valueOf(item.price)); }
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mContext);
        alertDialogBuilderUserInput.setView(mView);

        //final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.categoryInputText);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        Dialog d = (Dialog) dialogBox;
                        EditText checkProduct = d.findViewById(R.id.checkProductEditText);
                        EditText count = d.findViewById(R.id.countEditText);
                        EditText price = d.findViewById(R.id.priceEditText);
                        item.product = checkProduct.getText().toString();
                        item.quantity = Float.parseFloat(count.getText().toString());
                        item.price = Float.parseFloat(price.getText().toString());
                        item.sum = item.price * item.quantity;
                        notifyDataSetChanged();
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    public void onSaveInstanceState(Bundle outState) {
        ArrayList<Parcelable> objects = new ArrayList<>(items.size());
        for (CheckItem item : items) {
            objects.add(item);
        }
        outState.putParcelableArrayList(KEY_ADAPTER_STATE, objects);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_ADAPTER_STATE)) {
            items.clear();
            ArrayList<Parcelable> objects = savedInstanceState
                    .getParcelableArrayList(KEY_ADAPTER_STATE);
            for (Parcelable item : objects) {
                items.add((CheckItem)item);
            }
        }
    }
}
