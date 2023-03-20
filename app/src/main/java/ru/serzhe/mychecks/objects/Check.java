package ru.serzhe.mychecks.objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.serzhe.mychecks.data.DBContract;
import ru.serzhe.mychecks.data.DBHelper;

/**
 * Created by sergio on 05.05.2017.
 */

public class Check {
    
    public long ID;
    public Date DT;
    public int operationType;
    public float cashTotalSum;
    public float ecashTotalSum;
    public int receiptCode;
    public float nds18;
    public float nds10;
    public String seller;
    public String sellerInn;
    public float totalSum;
    public String operator;
    public int requestNumber;
    public String fiscalSign;
    public String fiscalDriveNumber;
    public String kktRegId;
    public int taxationType;
    public int fiscalDocumentNumber;
    public int shiftNumber;
    public String retailPlaceAddress;

    private Context mContext;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public ArrayList<CheckItem> items;

    public Check(Context context) {
        mContext = context;
    }
    public Check(Context context, long checkID) {
        mContext = context;
        ID = checkID;
        loadFromSQL();
    }

    public void loadFromSQL() {

        try {

            SQLiteOpenHelper mDbHelper = new DBHelper(mContext);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projection;
            String selection;
            String[] selectionArgs;
            Cursor c;
            StringBuilder sb;
            SQLiteQueryBuilder queryBuilder;

            projection = new String[]{
                    DBContract.Check.TABLE_NAME + "." + DBContract.Check._ID,
                    DBContract.Check.COLUMN_NAME_DT,
                    DBContract.Check.COLUMN_NAME_OPERATION_TYPE,
                    DBContract.Check.COLUMN_NAME_CASH_TOTAL_SUM,
                    DBContract.Check.COLUMN_NAME_ECASH_TOTAL_SUM,
                    DBContract.Check.COLUMN_NAME_RECEIPT_CODE,
                    DBContract.Check.COLUMN_NAME_NDS_18,
                    DBContract.Check.COLUMN_NAME_NDS_10,
                    DBContract.Check.COLUMN_NAME_SELLER_ID,
                    DBContract.Check.COLUMN_NAME_TOTAL_SUM,
                    DBContract.Check.COLUMN_NAME_OPERATOR,
                    DBContract.Check.COLUMN_NAME_REQUEST_NUMBER,
                    DBContract.Check.COLUMN_NAME_FISCAL_SIGN,
                    DBContract.Check.COLUMN_NAME_FISCAL_DRIVE_NUMBER,
                    DBContract.Check.COLUMN_NAME_KKT_REG_ID,
                    DBContract.Check.COLUMN_NAME_TAXATION_TYPE,
                    DBContract.Check.COLUMN_NAME_FISCAL_DOCUMENT_NUMBER,
                    DBContract.Check.COLUMN_NAME_SHIFT_NUMBER,
                    DBContract.Check.COLUMN_NAME_RETAIL_PLACE_ADDRESS,
                    DBContract.Seller.COLUMN_NAME_NAME,
                    DBContract.Seller.COLUMN_NAME_INN
            };

            sb = new StringBuilder();
            sb.append(DBContract.Check.TABLE_NAME + "." + DBContract.Check._ID);
            sb.append(" = ?");
            selection = sb.toString();

            selectionArgs = new String[]{
                    String.valueOf(ID)
            };

            queryBuilder = new SQLiteQueryBuilder();

            queryBuilder.setTables(DBContract.Check.TABLE_NAME +
                    " LEFT JOIN " + DBContract.Seller.TABLE_NAME + " ON " +
                    DBContract.Check.TABLE_NAME + "." + DBContract.Check.COLUMN_NAME_SELLER_ID + " = " +
                    DBContract.Seller.TABLE_NAME + "." + DBContract.Seller._ID);

            c = queryBuilder.query(db, projection, selection, selectionArgs, null, null, null);

            if (c.moveToFirst()) {
                ID = c.getLong(c.getColumnIndexOrThrow(DBContract.Check.TABLE_NAME + "." + DBContract.Check._ID));
                DT = df.parse(c.getString(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_DT)));
                operationType = c.getInt(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_OPERATION_TYPE));
                cashTotalSum = c.getFloat(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_CASH_TOTAL_SUM));
                ecashTotalSum = c.getFloat(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_ECASH_TOTAL_SUM));
                receiptCode = c.getInt(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_RECEIPT_CODE));
                nds18 = c.getFloat(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_NDS_18));
                nds10 = c.getFloat(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_NDS_10));
                seller = c.getString(c.getColumnIndexOrThrow(DBContract.Seller.COLUMN_NAME_NAME));
                sellerInn = c.getString(c.getColumnIndexOrThrow(DBContract.Seller.COLUMN_NAME_INN));
                totalSum = c.getFloat(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_TOTAL_SUM));
                operator = c.getString(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_OPERATOR));
                requestNumber = c.getInt(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_REQUEST_NUMBER));
                fiscalSign = c.getString(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_FISCAL_SIGN));
                fiscalDriveNumber = c.getString(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_FISCAL_DRIVE_NUMBER));
                kktRegId = c.getString(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_KKT_REG_ID));
                taxationType = c.getInt(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_TAXATION_TYPE));
                fiscalDocumentNumber = c.getInt(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_FISCAL_DOCUMENT_NUMBER));
                shiftNumber = c.getInt(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_SHIFT_NUMBER));
                retailPlaceAddress = c.getString(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_RETAIL_PLACE_ADDRESS));

                c.close();
            }
            else {
                c.close();
                throw new Exception("Чек с идентификатором " + ID + " отсутствует!");
            }

            items = new ArrayList<>();

            projection = new String[]{
                    DBContract.Product.TABLE_NAME + "." + DBContract.Product.COLUMN_NAME_NAME +
                        " as " + DBContract.Product.TABLE_NAME + DBContract.Product.COLUMN_NAME_NAME,
                    DBContract.Category.TABLE_NAME + "." + DBContract.Category.COLUMN_NAME_NAME +
                        " as " + DBContract.Category.TABLE_NAME + DBContract.Category.COLUMN_NAME_NAME,
                    DBContract.CheckItem.COLUMN_NAME_QUANTITY,
                    DBContract.CheckItem.COLUMN_NAME_PRICE,
                    DBContract.CheckItem.COLUMN_NAME_SUM,
                    DBContract.CheckItem.COLUMN_NAME_NDS_10,
                    DBContract.CheckItem.COLUMN_NAME_NDS_18
            };

            sb = new StringBuilder();
            sb.append(DBContract.CheckItem.COLUMN_NAME_CHECK_ID);
            sb.append(" = ?");
            selection = sb.toString();

            selectionArgs = new String[]{
                    String.valueOf(ID)
            };

            queryBuilder.setTables(DBContract.CheckItem.TABLE_NAME +
                    " LEFT JOIN " + DBContract.Product.TABLE_NAME + " ON " +
                    DBContract.CheckItem.TABLE_NAME + "." + DBContract.CheckItem.COLUMN_NAME_PRODUCT_ID + " = " +
                    DBContract.Product.TABLE_NAME + "." + DBContract.Product._ID +
                    " LEFT JOIN " + DBContract.Category.TABLE_NAME + " ON " +
                    DBContract.Product.TABLE_NAME + "." + DBContract.Product.COLUMN_NAME_CATEGORY_ID + " = " +
                    DBContract.Category.TABLE_NAME + "." + DBContract.Category._ID);

            c = queryBuilder.query(db, projection, selection, selectionArgs, null, null, null);

            if (c.moveToFirst()) {
                do {
                    CheckItem item = new CheckItem();
                    item.product = c.getString(c.getColumnIndexOrThrow(DBContract.Product.TABLE_NAME + DBContract.Product.COLUMN_NAME_NAME));
                    item.category = c.getString(c.getColumnIndexOrThrow(DBContract.Category.TABLE_NAME + DBContract.Category.COLUMN_NAME_NAME));
                    item.quantity = c.getFloat(c.getColumnIndexOrThrow(DBContract.CheckItem.COLUMN_NAME_QUANTITY));
                    item.price = c.getFloat(c.getColumnIndexOrThrow(DBContract.CheckItem.COLUMN_NAME_PRICE));
                    item.sum = c.getFloat(c.getColumnIndexOrThrow(DBContract.CheckItem.COLUMN_NAME_SUM));
                    item.nds10 = c.getFloat(c.getColumnIndexOrThrow(DBContract.CheckItem.COLUMN_NAME_NDS_10));
                    item.nds18 = c.getFloat(c.getColumnIndexOrThrow(DBContract.CheckItem.COLUMN_NAME_NDS_18));

                    items.add(item);
                }
                while (c.moveToNext());
            }
            c.close();
        }
        catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveToSQL() {
        if (ID != 0)
            return;

        if (seller == null)
            seller = "";
        if (retailPlaceAddress == null)
            retailPlaceAddress = "";

        SQLiteOpenHelper mDbHelper = new DBHelper(mContext);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String[] projection;
        String selection;
        String[] selectionArgs;
        Cursor c;

        if (fiscalDriveNumber != null && fiscalDocumentNumber > 0) {
            projection = new String[] {DBContract.Check._ID};

            StringBuilder sb = new StringBuilder();
            sb.append(DBContract.Check.COLUMN_NAME_FISCAL_DRIVE_NUMBER);
            sb.append(" = ? and ");
            sb.append(DBContract.Check.COLUMN_NAME_FISCAL_DOCUMENT_NUMBER);
            sb.append(" = ?");
            selection = sb.toString();

            selectionArgs = new String[] {
                    fiscalDriveNumber,
                    String.valueOf(fiscalDocumentNumber)
            };

            c = db.query(
                    DBContract.Check.TABLE_NAME,              // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            if (c.moveToFirst()) {
                ID = c.getLong(c.getColumnIndexOrThrow(DBContract.Check._ID));
                c.close();
                return;
            }
            c.close();
        }

        ContentValues values;
        CheckItem item;
        long sellerID;
        long productID;
        projection = new String[] { DBContract.Seller._ID };
        selection = DBContract.Seller.COLUMN_NAME_NAME + " = ?";
        selectionArgs = new String[] { seller };

        c = db.query(
                DBContract.Seller.TABLE_NAME,              // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if (c.moveToFirst()) {
            sellerID = c.getLong(c.getColumnIndexOrThrow(DBContract.Seller._ID));
            c.close();
        }
        else {
            c.close();
            values = new ContentValues();
            values.put(DBContract.Seller.COLUMN_NAME_INN, sellerInn);
            values.put(DBContract.Seller.COLUMN_NAME_NAME, seller);

            // Insert the new row, returning the primary key value of the new row
            sellerID = db.insertOrThrow(
                    DBContract.Seller.TABLE_NAME,
                    "null",
                    values);
        }

        values = new ContentValues();
        values.put(DBContract.Check.COLUMN_NAME_DT, df.format(DT));
        values.put(DBContract.Check.COLUMN_NAME_OPERATION_TYPE, operationType);
        values.put(DBContract.Check.COLUMN_NAME_CASH_TOTAL_SUM, cashTotalSum);
        values.put(DBContract.Check.COLUMN_NAME_ECASH_TOTAL_SUM, ecashTotalSum);
        values.put(DBContract.Check.COLUMN_NAME_RECEIPT_CODE, receiptCode);
        values.put(DBContract.Check.COLUMN_NAME_NDS_18, nds18);
        values.put(DBContract.Check.COLUMN_NAME_NDS_10, nds10);
        values.put(DBContract.Check.COLUMN_NAME_SELLER_ID, sellerID);
        values.put(DBContract.Check.COLUMN_NAME_TOTAL_SUM, totalSum);
        values.put(DBContract.Check.COLUMN_NAME_OPERATOR, operator);
        values.put(DBContract.Check.COLUMN_NAME_REQUEST_NUMBER, requestNumber);
        values.put(DBContract.Check.COLUMN_NAME_FISCAL_SIGN, fiscalSign);
        values.put(DBContract.Check.COLUMN_NAME_FISCAL_DRIVE_NUMBER, fiscalDriveNumber);
        values.put(DBContract.Check.COLUMN_NAME_KKT_REG_ID, kktRegId);
        values.put(DBContract.Check.COLUMN_NAME_TAXATION_TYPE, taxationType);
        values.put(DBContract.Check.COLUMN_NAME_FISCAL_DOCUMENT_NUMBER, fiscalDocumentNumber);
        values.put(DBContract.Check.COLUMN_NAME_SHIFT_NUMBER, shiftNumber);
        values.put(DBContract.Check.COLUMN_NAME_RETAIL_PLACE_ADDRESS, retailPlaceAddress);

        ID = db.insertOrThrow(
                DBContract.Check.TABLE_NAME,
                "null",
                values);

        for (int i = 0; i < items.size(); i++) {
            item = items.get(i);

            projection = new String[] { DBContract.Product._ID };
            selection = "name = ?";
            selectionArgs = new String[] { item.product };

            c = db.query(
                    DBContract.Product.TABLE_NAME,              // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            if (c.moveToFirst()) {
                productID = c.getLong(c.getColumnIndexOrThrow(DBContract.Product._ID));
                c.close();
            }
            else {
                c.close();
                values = new ContentValues();
                values.put(DBContract.Product.COLUMN_NAME_NAME, item.product);

                // Insert the new row, returning the primary key value of the new row
                productID = db.insertOrThrow(
                        DBContract.Product.TABLE_NAME,
                        "null",
                        values);
            }

            values = new ContentValues();
            values.put(DBContract.CheckItem.COLUMN_NAME_CHECK_ID, ID);
            values.put(DBContract.CheckItem.COLUMN_NAME_NUM_STR, i + 1);
            values.put(DBContract.CheckItem.COLUMN_NAME_PRODUCT_ID, productID);
            values.put(DBContract.CheckItem.COLUMN_NAME_QUANTITY, item.quantity);
            values.put(DBContract.CheckItem.COLUMN_NAME_PRICE, item.price);
            values.put(DBContract.CheckItem.COLUMN_NAME_SUM, item.sum);
            values.put(DBContract.CheckItem.COLUMN_NAME_NDS_10, item.nds10);
            values.put(DBContract.CheckItem.COLUMN_NAME_NDS_18, item.nds18);

            db.insertOrThrow(
                    DBContract.CheckItem.TABLE_NAME,
                    "null",
                    values);
        }
        db.close();
    }

    public void deleteFromSQL() {
        String selection;
        String[] selectionArgs;
        int deletedRows;
        try {
            SQLiteOpenHelper mDbHelper = new DBHelper(mContext);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            selection = DBContract.Check._ID + " = ?";
            selectionArgs = new String[]{String.valueOf(ID)};
            deletedRows = db.delete(DBContract.Check.TABLE_NAME, selection, selectionArgs);

            selection = DBContract.CheckItem.COLUMN_NAME_CHECK_ID + " = ?";
            selectionArgs = new String[]{String.valueOf(ID)};
            deletedRows = db.delete(DBContract.CheckItem.TABLE_NAME, selection, selectionArgs);

            ID = 0;
        }
        catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
