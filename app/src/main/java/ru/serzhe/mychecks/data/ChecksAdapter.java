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
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ru.serzhe.mychecks.R;
import ru.serzhe.mychecks.objects.Check;

/**
 * Created by sergio on 04.02.2018.
 */

public class ChecksAdapter extends BaseAdapter {

    ArrayList<Check> checksList;
    private Context mContext;
    DateFormat dfSQL;
    DateFormat dfOutput;

    public ChecksAdapter(Context context) {
        mContext = context;
        checksList = new ArrayList<Check>();
        dfSQL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        dfOutput = DateFormat.getDateInstance();
        preapreCheckList();
        notifyDataSetChanged();
    }

    public void preapreCheckList() {
        checksList.clear();

        SQLiteOpenHelper mDbHelper = new DBHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] selectColumns = {
                DBContract.Check.TABLE_NAME + "." + DBContract.Check._ID,
                DBContract.Check.COLUMN_NAME_DT,
                "ltrim(replace(" + DBContract.Seller.COLUMN_NAME_NAME + ", '  ', ' ')) as " + DBContract.Seller.COLUMN_NAME_NAME,
                "case " + DBContract.Check.COLUMN_NAME_OPERATION_TYPE
                        + " when 2 then '+' || " + DBContract.Check.COLUMN_NAME_TOTAL_SUM
                        + " else " + DBContract.Check.COLUMN_NAME_TOTAL_SUM
                        + " end as " + DBContract.Check.COLUMN_NAME_TOTAL_SUM};
        String orderBy = DBContract.Check.COLUMN_NAME_DT + " DESC";

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DBContract.Check.TABLE_NAME +
                " LEFT JOIN " + DBContract.Seller.TABLE_NAME + " ON " +
                DBContract.Check.TABLE_NAME + "." + DBContract.Check.COLUMN_NAME_SELLER_ID + " = " +
                DBContract.Seller.TABLE_NAME + "." + DBContract.Seller._ID);

        try {
            Cursor c = queryBuilder.query(db, selectColumns, null, null, null, null, orderBy);

            if (c.moveToFirst()) {
                do {
                    Check check = new Check(mContext);
                    check.ID = c.getLong(c.getColumnIndexOrThrow(DBContract.Check._ID));
                    check.DT = dfSQL.parse(c.getString(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_DT)));
                    check.seller = c.getString(c.getColumnIndexOrThrow(DBContract.Seller.COLUMN_NAME_NAME));
                    check.totalSum = c.getFloat(c.getColumnIndexOrThrow(DBContract.Check.COLUMN_NAME_TOTAL_SUM));

                    checksList.add(check);
                }
                while (c.moveToNext());
            }
            c.close();
        }
        catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getCount() {
        return checksList.size();
    }

    @Override
    public Check getItem(int position) {
        return checksList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return checksList.get(position).ID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.check_list_item, parent, false);
        }
        Check check = getItem(position);
        ((TextView) convertView.findViewById(R.id.checkDT)).setText(dfOutput.format(check.DT));
        ((TextView) convertView.findViewById(R.id.checkUser)).setText(check.seller);
        ((TextView) convertView.findViewById(R.id.checkSum)).setText(String.format(Locale.getDefault(), "%.2f", check.totalSum));
        ((TextView) convertView.findViewById(R.id.checkID)).setText(Long.toString(check.ID));
        return convertView;
    }
}