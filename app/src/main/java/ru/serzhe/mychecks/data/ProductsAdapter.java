package ru.serzhe.mychecks.data;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ru.serzhe.mychecks.R;
import ru.serzhe.mychecks.objects.Product;
import ru.serzhe.mychecks.objects.ProductGroup;
import ru.serzhe.mychecks.objects.ProductGroupBase;

/**
 * Created by sergio on 15.06.2017.
 */

public class ProductsAdapter extends BaseAdapter {
    private ArrayList<ProductGroup> productList;
    private ArrayList<Product> products;
    private ArrayList<Product> checkedProducts;
    private Context mContext;
    private int getItemCounter;
    private boolean hideProducts = false;

    public ProductsAdapter(Context context) {
        mContext = context;
        prepareProductList();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = 0;
        for (ProductGroup pg : productList) {
            count += calculateCountInGroup(pg);
        }
        return count;
    }

    @Override
    public ProductGroupBase getItem(int position) {
        getItemCounter = 0;
        ProductGroupBase obj = null;
        for (ProductGroup pg : productList) {
            if (getItemCounter == position)
                return pg;
            getItemCounter++;
            obj = findItemAtPosition(pg, position);
            if ((getItemCounter == position) && (obj != null))
                return obj;
        }
        return obj;
    }

    @Override
    public long getItemId(int position) {
        ProductGroupBase obj = getItem(position);
        if (obj == null)
            return 0;
        else
            return obj.ID;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup container) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.product_list_item, container, false);
        }

        //StringBuilder type = new StringBuilder();
        float density = mContext.getResources().getDisplayMetrics().density;
        ImageView typeImage = (ImageView)convertView.findViewById(R.id.productListType);
        typeImage.setColorFilter(R.color.colorBlack87);
        ProductGroupBase item = getItem(position);
//        for (int i = 0; i < item.level; i++)
//            type.append(' ');
        if (item instanceof Product) {
            //type.append('●');
            if (checkedProducts.contains((Product)item))
                typeImage.setImageResource(R.drawable.ic_check_box_black_24dp);
            else
                typeImage.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
            typeImage.setPadding(32 + (int)((item.level - 1) * density * 5), 0, 0, 0);
        }
        else if (item instanceof ProductGroup) {
            ProductGroup pg = (ProductGroup) item;
            if (pg.isOpen) {
                //type.append('V');
                typeImage.setImageResource(R.drawable.ic_folder_open_black_36dp);
                typeImage.setPadding((int)(item.level * density * 5), 0, 0, 0);
            }
            else {
                //type.append('>');
                typeImage.setImageResource(R.drawable.ic_folder_black_36dp);
                typeImage.setPadding((int)(item.level * density * 5), 0, 0, 0);
            }
        }

        ((TextView) convertView.findViewById(R.id.productListLabel)).setText(item.name);
        //((TextView) convertView.findViewById(R.id.productListType)).setText(type);
        //((ImageView) convertView.findViewById(R.id.productListType)).setImageResource(imageResource);
        //((ImageView) convertView.findViewById(R.id.productListType)).setPadding();
        ((TextView) convertView.findViewById(R.id.productListID)).setText(item.getClass().getSimpleName() + "/" + item.ID);
//        pgv.productGroupObject = item;
//        convertView = pgv;

        class myDragListener implements View.OnDragListener {
            private int lastEvent;
            private Date lastChange;

            @Override
            public boolean onDrag(View v, DragEvent event) {
                String curItem = ((TextView)v.findViewById(R.id.productListID)).getText().toString();
                String[] curItemArr = curItem.split("/");
                String receiverItemType = curItemArr[0];
                long receiverItemID = Long.parseLong(curItemArr[1]);
                ProductGroupBase item = (ProductGroupBase) event.getLocalState();
                String dragItemType = item.getClass().getSimpleName();//curItemArr[0];
                long dragItemID = item.ID;//curItemArr[1];

                if (event.getAction() != lastEvent) {
                    lastEvent = event.getAction();
                    lastChange = new Date();
                }

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        hideProducts = true;
                        notifyDataSetChanged();
                        //if (receiverItemType.equals("ProductGroup"))
                        return true;
                    //else
                    //    return false;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        //Log.d("DRAG_EVENT", "Location on " + item.name + " till " + lastChange);
                        v.setBackgroundResource(R.drawable.view_border);
                        int duration = (int)TimeUnit.MILLISECONDS.toSeconds((new Date()).getTime() - lastChange.getTime());
                        if (receiverItemType.equals("ProductGroup") && duration > 1) {
                            ProductGroup receiverItem = (ProductGroup) findItemInProductList(receiverItemID, "ProductGroup", null, false);
                            if (!receiverItem.isOpen) {
                                receiverItem.isOpen = true;
                                notifyDataSetChanged();
                            }
                        }
                        ListView listView = (ListView)container;
                        int listPosition = ((ListView)listView).getPositionForView(v);
                        if (listPosition >= listView.getLastVisiblePosition())
                            listView.smoothScrollByOffset(2);
                        else if (listPosition <= listView.getFirstVisiblePosition())
                            listView.smoothScrollByOffset(-2);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackground(null);
                        break;
//                    case DragEvent.ACTION_DRAG_LOCATION:
//                        Log.d("DRAG_EVENT", "Location on " + item.name);
//                        break;
//                    case DragEvent.ACTION_DRAG_EXITED:
//                        if (receiverItemType.equals("ProductGroup")) {
//                            ProductGroup receiverItem = (ProductGroup) findItemInProductList(receiverItemID, "ProductGroup", null, false);
//                            if (receiverItem.isOpen) {
//                                receiverItem.isOpen = false;
//                                notifyDataSetChanged();
//                            }
//                        }
                    case DragEvent.ACTION_DROP:
                        v.setBackground(null);
                        if (receiverItemType.equals("ProductGroup")) {
                            if (checkedProducts.contains(item)) {
                                for (Product product : checkedProducts) {
                                    changeCategory(product.ID, product.getClass().getSimpleName(), receiverItemID);
                                }
                                checkedProducts.clear();
                            }
                            else
                                changeCategory(dragItemID, dragItemType, receiverItemID);
                            notifyDataSetChanged();
                            return true;
                        }
                        else
                            return false;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackground(null);
                        if (!event.getResult() && dragItemType.equals("ProductGroup")) {
                            if (((ProductGroup)item).parentID != 0) {
                                changeCategory(dragItemID, dragItemType, 0);
                            }
                        }
                        hideProducts = false;
                        notifyDataSetChanged();
                        return true;
                }
                return false;
            }
        }

        convertView.setOnDragListener(new myDragListener());

        return convertView;
    }

    public void prepareProductList() {
        productList = new ArrayList<ProductGroup>();
        products = new ArrayList<Product>();
        checkedProducts = new ArrayList<Product>();

        SQLiteOpenHelper mDbHelper = new DBHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {DBContract.Category._ID, DBContract.Category.COLUMN_NAME_NAME, DBContract.Category.COLUMN_NAME_PARENT_ID};

        Cursor c = db.query(
                DBContract.Category.TABLE_NAME,              // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if (c.moveToFirst()) {
            do {
                long ID = c.getLong(c.getColumnIndexOrThrow(DBContract.Category._ID));
                String name = c.getString(c.getColumnIndexOrThrow(DBContract.Category.COLUMN_NAME_NAME));
                long parentID = c.getLong(c.getColumnIndexOrThrow(DBContract.Category.COLUMN_NAME_PARENT_ID));

                productList.add(new ProductGroup(ID, name, parentID));
            }
            while (c.moveToNext());
        }
        c.close();

        projection = new String[]{DBContract.Product._ID, DBContract.Product.COLUMN_NAME_NAME, DBContract.Product.COLUMN_NAME_CATEGORY_ID};

        c = db.query(
                DBContract.Product.TABLE_NAME,              // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if (c.moveToFirst()) {
            do {
                long ID = c.getLong(c.getColumnIndexOrThrow(DBContract.Product._ID));
                String name = c.getString(c.getColumnIndexOrThrow(DBContract.Product.COLUMN_NAME_NAME));
                long categoryID = c.getLong(c.getColumnIndexOrThrow(DBContract.Product.COLUMN_NAME_CATEGORY_ID));

                products.add(new Product(ID, name, categoryID));
            }
            while (c.moveToNext());
        }
        c.close();

        productList.add(new ProductGroup(0, mContext.getString(R.string.unsorted), 0));

        ArrayList<ProductGroup> productGroupsToRemove = new ArrayList<ProductGroup>();
        sortGroupsByParent(null, 0, productGroupsToRemove, 0);
        sortGroupsProductsAlphabetically();
        productList.removeAll(productGroupsToRemove);

        products = null;
        productGroupsToRemove = null;
    }

    public void onItemLongClick(int position, View v) {

        ProductGroupBase item = getItem(position);
        // Create a new ClipData.
        // This is done in two steps to provide clarity. The convenience method
        // ClipData.newPlainText() can create a plain text ClipData in one step.

        // Create a new ClipData.Item from the ImageView object's tag
        CharSequence label = item.getClass().getSimpleName() + "/" + item.ID;
//        if (item instanceof Product)
//            if (checkedProducts.contains((Product) item))
//                label = item.getClass().getSimpleName() + "/-1";
        //ClipData.Item item = new ClipData.Item(label);

        // Create a new ClipData using the tag as a label, the plain text MIME type, and
        // the already-created item. This will create a new ClipDescription object within the
        // ClipData, and set its MIME type entry to "text/plain"
        //ClipData dragData = new ClipData(label,  item);

        ClipData clipData = ClipData.newPlainText(label, label);

        ConstraintLayout layout = new ConstraintLayout(mContext);
        layout.setId(R.id.dragShadowLayout);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View shadowView = inflater.inflate(R.layout.product_list_item, layout, false);
        shadowView.setId(R.id.dragShadowView);
        ((TextView) shadowView.findViewById(R.id.productListLabel)).setText(item.name);
        ConstraintLayout.LayoutParams shadowParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        shadowView.setLayoutParams(shadowParams);
        layout.addView(shadowView);

        //v.setBackgroundResource(R.drawable.view_border);

        // Instantiates the drag shadow builder.
        //View.DragShadowBuilder dsb = new View.DragShadowBuilder(layout);
        String[] itemsNames;
        if (checkedProducts.contains(item)) {
            itemsNames = new String[checkedProducts.size()];
            for (int i = 0; i < checkedProducts.size(); i++)
                itemsNames[i] = checkedProducts.get(i).name;
        }
        else
            itemsNames = new String[]{item.name};

        CustomDragShadowBuilder customDSB = new CustomDragShadowBuilder(itemsNames, v.getWidth(), v.getHeight(), mContext);

        // Starts the drag

//                v.startDrag(dragData,  // the data to be dragged
//                        myShadow,  // the drag shadow builder
//                        null,      // no need to use local data
//                        0          // flags (not currently used, set to 0)
//                );
        v.startDrag(clipData, customDSB, item, 0);
    }

    public void onItemClick(int position) {
        String type = "";
        ProductGroupBase item = getItem(position);
        if (item instanceof Product) {
            Product product = (Product) item;
            if (checkedProducts.contains(product))
                checkedProducts.remove(product);
            else
                checkedProducts.add(product);
        }
        else if (item instanceof ProductGroup) {
            ProductGroup pg = (ProductGroup) item;
            if (pg.isOpen)
                pg.isOpen = false;
            else
                pg.isOpen = true;
        }
        notifyDataSetChanged();
    }

    private void sortGroupsByParent(ProductGroup curGroup, long curID, ArrayList<ProductGroup> toRemove, int level) {
        for (ProductGroup pg : productList) {
            if (toRemove.contains(pg))
                continue;

            if (pg.parentID == curID)//(pg.products.size() == 0)
                for (Product prod : products)
                    if (prod.categoryID == pg.ID) {
                        prod.level = level + 1;
                        pg.products.add(prod);
                    }

            if ((pg.parentID == curID) && (pg.ID != 0)) {
                sortGroupsByParent(pg, pg.ID, toRemove, level + 1);
                if (curID != 0) {
                    pg.level = level;
                    curGroup.childGroups.add(pg);
                    toRemove.add(pg);
                }
            }
        }
    }

    private void sortGroupsProductsAlphabetically() {
        for (ProductGroup pg : productList) {
            Collections.sort(pg.childGroups, new ProductGroupBaseComparator());
            Collections.sort(pg.products, new ProductGroupBaseComparator());
        }
        Collections.sort(productList, new ProductGroupBaseComparator());
    }

    private int calculateCountInGroup(ProductGroup curGroup) {
        int count = 1;
        if (curGroup.isOpen)
        {
            for (ProductGroup pg : curGroup.childGroups) {
                count += calculateCountInGroup(pg);
            }
            if (!hideProducts)
                count += curGroup.products.size();
        }
//        else
//            count++;
        return count;
    }

    private ProductGroupBase findItemAtPosition(ProductGroup curGroup, int position) {
        if (curGroup.isOpen)
        {
            for (ProductGroup pg : curGroup.childGroups) {
                if (getItemCounter == position) {
                    return pg;
                }
                getItemCounter++;
                ProductGroupBase obj = findItemAtPosition(pg, position);
                if (obj != null)
                    return obj;
            }
            if (!hideProducts) {
                for (Product prod : curGroup.products) {
                    if (getItemCounter == position) {
                        return prod;
                    }
                    getItemCounter++;
                }
            }
        }
        return null;
    }

    private void changeCategory(long ID, String type, long categoryID) {

        SQLiteOpenHelper mDbHelper = new DBHelper(mContext);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsAffected = 0;

        if (type.equals("ProductGroup")) {
            if (ID == categoryID)
                return;

            ContentValues values = new ContentValues();
            values.put(DBContract.Category.COLUMN_NAME_PARENT_ID, categoryID);

            String selection = DBContract.Category._ID + " = ?";
            String[] selectionArgs = {String.valueOf(ID)};

            rowsAffected = db.update(
                    DBContract.Category.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);


        } else if (type.equals("Product")) {
            ContentValues values = new ContentValues();
            values.put(DBContract.Product.COLUMN_NAME_CATEGORY_ID, categoryID);

            String selection = DBContract.Product._ID + " = ?";
            String[] selectionArgs = {String.valueOf(ID)};

            rowsAffected = db.update(
                    DBContract.Product.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }

        if (rowsAffected == 0)
            return;

        ProductGroupBase item = findItemInProductList(ID, type, null, true);
        ProductGroup group = (ProductGroup)findItemInProductList(categoryID, "ProductGroup", null, false);
        //if (categoryID > 0)
        //     group = (ProductGroup)findItemInProductList(categoryID, "ProductGroup", null, false);
        if (type.equals("ProductGroup")) {
            if (group == null) {
                productList.add((ProductGroup)item);
                ((ProductGroup) item).parentID = 0;
                item.level = 0;
            }
            else {
                group.childGroups.add((ProductGroup) item);
                ((ProductGroup) item).parentID = group.ID;
                item.level = group.level + 1;
            }
        }
        else if (type.equals("Product")) {
            group.products.add((Product) item);
            ((Product) item).categoryID = group.ID;
            item.level = group.level + 1;
        }
    }

    public ProductGroupBase findItemInProductList(long ID, String type, ProductGroup curGroup, boolean remove) {
        if (curGroup == null) {
            for (ProductGroup pg : productList) {
                ProductGroupBase item = findItemInProductList(ID, type, pg, remove);
                if (item != null) {
                    if (remove) {
                        productList.remove(item);
                    }
                    return item;
                }
            }
            return null;
        }
        else {
            if (type.equals("ProductGroup") && (curGroup.ID == ID))
                return curGroup;
            if (type.equals("Product")) {
                for (Product p : curGroup.products) {
                    if (p.ID == ID) {
                        if (remove) {
                            remove = false;
                            curGroup.products.remove(p);
                        }
                        return p;
                    }
                }
            }
            for (ProductGroup pg : curGroup.childGroups) {
                ProductGroupBase item = findItemInProductList(ID, type, pg, remove);
                if (item != null) {
                    if (remove) {
                        remove = false;
                        curGroup.childGroups.remove(item);
                    }
                    return item;
                }
            }
            return null;
        }
    }

    public void createCategory(String categoryName) {
        try {
            SQLiteOpenHelper mDbHelper = new DBHelper(mContext);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            long categoryID = 0;

            ContentValues values = new ContentValues();
            values.put(DBContract.Category.COLUMN_NAME_NAME, categoryName);
            values.put(DBContract.Category.COLUMN_NAME_PARENT_ID, 0);

            categoryID = db.insertOrThrow(
                    DBContract.Category.TABLE_NAME,
                    "null",
                    values);

            productList.add(new ProductGroup(categoryID, categoryName, 0));
            notifyDataSetChanged();
        }
        catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean removeGroup(long ID) {

        SQLiteOpenHelper mDbHelper = new DBHelper(mContext);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsAffected = 0;

        String selection = DBContract.Category._ID + " = ?";
        String[] selectionArgs = {String.valueOf(ID)};

        rowsAffected = db.delete(
                DBContract.Category.TABLE_NAME,
                selection,
                selectionArgs);

        if (rowsAffected == 0) {
            Toast.makeText(mContext, mContext.getString(R.string.group_not_removed), Toast.LENGTH_SHORT).show();
            return false;
        }

        ProductGroupBase item = findItemInProductList(ID, "ProductGroup", null, true);
        if (item == null)
            return false;
        else {
            notifyDataSetChanged();
            return true;
        }
    }

    public class ProductGroupBaseComparator implements Comparator<ProductGroupBase>
    {
        public int compare(ProductGroupBase left, ProductGroupBase right) {
            return left.name.compareTo(right.name);
        }
    }

    public class CustomDragShadowBuilder extends View.DragShadowBuilder {
        String[] shadowStrings;
        int shadowWidth;
        int shadowHeight;
        Context mContext;
        Paint textPaint;
        Paint borderPaint;
        float density;
        Rect textBounds;
        //private Point mScaleFactor;

        public CustomDragShadowBuilder(String[] strings, int width, int height, Context context) {
            super();
            shadowStrings = strings;
            shadowWidth = width;
            shadowHeight = height;
            mContext = context;

            textPaint = new Paint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            density = mContext.getResources().getDisplayMetrics().density;
            float textSize = 16.0f * density;
            textPaint.setTextSize(textSize);

            borderPaint = new Paint();
            borderPaint.setColor(Color.BLACK);
            borderPaint.setStyle(Paint.Style.STROKE);
            float borderWidth = 2.0f * density;
            borderPaint.setStrokeWidth(borderWidth);

            textBounds = new Rect();
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            //canvas.scale(mScaleFactor.x/(float)shadowWidth, mScaleFactor.y/shadowHeight);
            float d = density;
            String str;

            for (int i = 0; i < shadowStrings.length && i <= 5; i++) {
                str = shadowStrings[i];
                canvas.drawText(str, d + 20.0f * density, d + (shadowHeight - textPaint.getTextSize()), textPaint);
                canvas.drawRect(d, d, d + shadowWidth, d + shadowHeight, borderPaint);
                d += density * 3;
            }
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            shadowSize.x = shadowWidth + shadowStrings.length * (int)density * 3;
            shadowSize.y = shadowHeight + shadowStrings.length * (int)density * 3;

            //mScaleFactor = shadowSize;

            shadowTouchPoint.x = (int)(shadowSize.x / 2);
            shadowTouchPoint.y = (int)(shadowSize.y / 2);
        }
    }

}
