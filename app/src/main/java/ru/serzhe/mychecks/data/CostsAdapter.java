package ru.serzhe.mychecks.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import ru.serzhe.mychecks.R;
import ru.serzhe.mychecks.objects.Product;
import ru.serzhe.mychecks.objects.ProductGroup;

/**
 * Created by sergio on 21.08.2017.
 */

public class CostsAdapter extends BaseAdapter {

    ArrayList<ProductGroup> groupList;
    ArrayList<ProductGroup> allGroupList;
    private Context mContext;
    private int getItemCounter;

    public CostsAdapter(Context context) {
        mContext = context;
        //prepareGroupList();
        //notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = 0;
        for (ProductGroup pg : groupList) {
            count += calculateCountInGroup(pg);
        }
        return count;
    }

    @Override
    public ProductGroup getItem(int position) {
        getItemCounter = 0;
        ProductGroup obj = null;
        for (ProductGroup pg : groupList) {
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
        ProductGroup obj = getItem(position);
        if (obj == null)
            return 0;
        else
            return obj.ID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.cost_list_item, parent, false);
        }

        StringBuilder type = new StringBuilder();
        float density = mContext.getResources().getDisplayMetrics().density;
        ImageView typeImage = (ImageView)convertView.findViewById(R.id.costListType);
        typeImage.setColorFilter(R.color.colorBlack87);
        ProductGroup item = getItem(position);
//        for (int i = 0; i < item.level; i++)
//            type.append(' ');

        int imageResourceID;
        ProductGroup pg = (ProductGroup) item;
        if (pg.isOpen)
            imageResourceID = R.drawable.ic_folder_open_black_36dp;
        else
            if (pg.childGroups.size() > 0)
                imageResourceID = R.drawable.ic_folder_black_36dp;
            else
                imageResourceID = R.drawable.ic_folder_open_black_36dp;

        typeImage.setImageResource(imageResourceID);
        typeImage.setPadding((int) (item.level * density * 5), 0, 0, 0);

        ((TextView) convertView.findViewById(R.id.costListLabel)).setText(item.name);
        //((TextView) convertView.findViewById(R.id.costListType)).setText(type);
        //((TextView) convertView.findViewById(R.id.productListID)).setText(item.getClass().getSimpleName() + "/" + item.ID);
        ((TextView) convertView.findViewById(R.id.costListValue)).setText(String.format(Locale.getDefault(), "%.2f", item.costs));

        return convertView;
    }

    public void prepareGroupList(Date startDate, Date endDate) {
        groupList = new ArrayList<ProductGroup>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        SQLiteOpenHelper mDbHelper = new DBHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(DBContract.Check.TABLE_NAME +
                " LEFT JOIN " + DBContract.CheckItem.TABLE_NAME + " ON " +
                DBContract.Check.TABLE_NAME + "." + DBContract.Check._ID + " = " + DBContract.CheckItem.TABLE_NAME + "." + DBContract.CheckItem.COLUMN_NAME_CHECK_ID +
                " LEFT JOIN " + DBContract.Product.TABLE_NAME + " ON " +
                DBContract.CheckItem.TABLE_NAME + "." + DBContract.CheckItem.COLUMN_NAME_PRODUCT_ID + " = " + DBContract.Product.TABLE_NAME + "." + DBContract.Product._ID +
                " LEFT JOIN " + DBContract.Category.TABLE_NAME + " ON " +
                DBContract.Product.TABLE_NAME + "." + DBContract.Product.COLUMN_NAME_CATEGORY_ID + " = " + DBContract.Category.TABLE_NAME + "." + DBContract.Category._ID
        );

        String sumColumn = "(case " + DBContract.Check.TABLE_NAME + "." + DBContract.Check.COLUMN_NAME_OPERATION_TYPE + " " +
                "when 1 then 1 " +
                "when 2 then -1 " +
                "else 0 " +
                "end) * SUM(" + DBContract.CheckItem.COLUMN_NAME_SUM + ") as " + DBContract.CheckItem.COLUMN_NAME_SUM;
        String ndsColumn = "(case " + DBContract.Check.TABLE_NAME + "." + DBContract.Check.COLUMN_NAME_OPERATION_TYPE + " " +
                "when 1 then 1 " +
                "when 2 then -1 " +
                "else 0 " +
                "end) * SUM(ifnull(" + DBContract.CheckItem.TABLE_NAME + "." + DBContract.CheckItem.COLUMN_NAME_NDS_10 + ", 0) + ifnull(" + DBContract.CheckItem.TABLE_NAME + "." + DBContract.CheckItem.COLUMN_NAME_NDS_18 + ", 0)) as NDS";
        String orderBy = DBContract.CheckItem.COLUMN_NAME_SUM + " DESC";

        String[] projection = {"ifnull(" + DBContract.Category.TABLE_NAME + "." + DBContract.Category._ID + ", 0) as " + DBContract.Category._ID,
                "ifnull(" + DBContract.Category.TABLE_NAME + "." + DBContract.Category.COLUMN_NAME_NAME + ", '" + mContext.getString(R.string.unsorted) + "') as " + DBContract.Category.COLUMN_NAME_NAME,
                "ifnull(" + DBContract.Category.TABLE_NAME + "." + DBContract.Category.COLUMN_NAME_PARENT_ID + ", 0) as " + DBContract.Category.COLUMN_NAME_PARENT_ID,
                sumColumn, ndsColumn
        };

        String selection = DBContract.Check.TABLE_NAME + "." + DBContract.Check.COLUMN_NAME_DT + " between ? and ?";
        String[] selectionArgs = new String[] { df.format(startDate), df.format(endDate) };
        String groupBy = DBContract.Category.TABLE_NAME  + "." + DBContract.Category._ID + ", " +
                DBContract.Category.TABLE_NAME  + "." + DBContract.Category.COLUMN_NAME_NAME + ", " +
                DBContract.Category.TABLE_NAME  + "." + DBContract.Category.COLUMN_NAME_PARENT_ID;

        Cursor c = qb.query(
                db,
                projection,
                selection,
                selectionArgs,
                groupBy,
                null,
                orderBy
        );

        if (c.moveToFirst()) {
            do {
                long ID = c.getLong(c.getColumnIndexOrThrow(DBContract.Category._ID));
                String name = c.getString(c.getColumnIndexOrThrow(DBContract.Category.COLUMN_NAME_NAME));
                long parentID = c.getLong(c.getColumnIndexOrThrow(DBContract.Category.COLUMN_NAME_PARENT_ID));
                float costs = c.getFloat(c.getColumnIndexOrThrow(DBContract.CheckItem.COLUMN_NAME_SUM));
                float nds = c.getFloat(c.getColumnIndexOrThrow("NDS"));

                groupList.add(new ProductGroup(ID, name, parentID, costs, nds));
            }
            while (c.moveToNext());
        }
        c.close();

        allGroupList = new ArrayList<ProductGroup>();

        projection = new String[]{DBContract.Category._ID, DBContract.Category.COLUMN_NAME_NAME, DBContract.Category.COLUMN_NAME_PARENT_ID};

        c = db.query(
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

                allGroupList.add(new ProductGroup(ID, name, parentID));
            }
            while (c.moveToNext());
        }
        c.close();

        int groupCount = groupList.size();
        for (int i = 0; i < groupCount; i++) {
            ProductGroup pg = groupList.get(i);
            addGroupParents(pg, groupList, allGroupList);
        }

        ArrayList<ProductGroup> groupsToRemove = new ArrayList<ProductGroup>();
        sortGroupsByParent(null, 0, groupsToRemove, 0);
        groupList.removeAll(groupsToRemove);
        Collections.sort(groupList, new Comparator<ProductGroup>() {
            @Override
            public int compare(ProductGroup o1, ProductGroup o2) {
                return (int)((o2.costs - o1.costs) * 100);
            }
        });

        groupsToRemove = null;
        notifyDataSetChanged();
    }

    public void onItemClick(int position) {
        String type = "";
        ProductGroup item = getItem(position);

        ProductGroup pg = (ProductGroup) item;
        if (pg.isOpen)
            pg.isOpen = false;
        else
            if (pg.childGroups.size() > 0)
            pg.isOpen = true;

        notifyDataSetChanged();
    }

    public float getTotalCosts() {
        float totalCosts = 0;
        for (ProductGroup pg : groupList) {
            totalCosts += pg.costs;
        }
        return totalCosts;
    }

    public float getTotalNDS() {
        float totalNDS = 0;
        for (ProductGroup pg : groupList) {
            totalNDS += pg.nds;
        }
        return Math.round(totalNDS * 100.0f) / 100.0f;
    }

    private void sortGroupsByParent(ProductGroup curGroup, long curID, ArrayList<ProductGroup> toRemove, int level) {
        for (ProductGroup pg : groupList) {
            if (toRemove.contains(pg))
                continue;

            if ((pg.parentID == curID) && (pg.ID != 0)) {
                sortGroupsByParent(pg, pg.ID, toRemove, level + 1);
                if (curID != 0) {
                    pg.level = level;
                    curGroup.childGroups.add(pg);
                    curGroup.costs += pg.costs;
                    curGroup.nds += pg.nds;
                    toRemove.add(pg);
                }
            }
        }
    }

    private void addGroupParents(ProductGroup curGroup, ArrayList<ProductGroup> listTo, ArrayList<ProductGroup> listFrom) {
        for (ProductGroup pg : listFrom) {
            if ((curGroup.parentID == pg.ID) && !(listTo.contains(pg))) {
                listTo.add(pg);
                addGroupParents(pg, listTo, listFrom);
            }
        }
    }

    private int calculateCountInGroup(ProductGroup curGroup) {
        int count = 1;
        if (curGroup.isOpen)
        {
            for (ProductGroup pg : curGroup.childGroups) {
                count += calculateCountInGroup(pg);
            }
        }
        return count;
    }

    private ProductGroup findItemAtPosition(ProductGroup curGroup, int position) {
        if (curGroup.isOpen)
        {
            for (ProductGroup pg : curGroup.childGroups) {
                if (getItemCounter == position) {
                    return pg;
                }
                getItemCounter++;
                ProductGroup obj = findItemAtPosition(pg, position);
                if (obj != null)
                    return obj;
            }
        }
        return null;
    }
}
