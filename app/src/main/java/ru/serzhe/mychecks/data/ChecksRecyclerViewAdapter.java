package ru.serzhe.mychecks.data;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ru.serzhe.mychecks.CheckDetailsFragment;
import ru.serzhe.mychecks.MainActivity;
import ru.serzhe.mychecks.R;
import ru.serzhe.mychecks.objects.Check;

/**
 * Created by sergio on 13.04.2018.
 */

public class ChecksRecyclerViewAdapter extends RecyclerView.Adapter<ChecksRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Check> checksList;
    private Context mContext;
    private DateFormat dfSQL;
    private DateFormat dfOutput;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView checkDT;
        public TextView checkUser;
        public TextView checkSum;
        public TextView checkID;
        public RelativeLayout viewBackground, viewForeground;
        private Context context;
        //private View checkListItem;
        public ViewHolder(View v, Context c) {
            super(v);
            //checkListItem = v;
            v.setOnClickListener(this);
            checkDT = (TextView) v.findViewById(R.id.checkDT);
            checkUser = (TextView) v.findViewById(R.id.checkUser);
            checkSum = (TextView) v.findViewById(R.id.checkSum);
            checkID = (TextView) v.findViewById(R.id.checkID);
            viewBackground = (RelativeLayout) v.findViewById(R.id.view_background);
            viewForeground = (RelativeLayout) v.findViewById(R.id.view_foreground);
            context = c;
        }

        @Override
        public void onClick(View v) {
            String curCheckID = ((TextView)v.findViewById(R.id.checkID)).getText().toString();
            CheckDetailsFragment fragment = CheckDetailsFragment.newInstance(Integer.parseInt(curCheckID));
            ((MainActivity)context).pushFragments(MainActivity.TAB_CHECKS, fragment,true);
        }
    }

    public ChecksRecyclerViewAdapter(Context context) {
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
    public ChecksRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.check_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        ViewHolder vh = new ViewHolder(v, mContext);
        return vh;
    }

    @Override
    public void onBindViewHolder(ChecksRecyclerViewAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Check check = checksList.get(position);
        holder.checkDT.setText(dfOutput.format(check.DT));
        holder.checkUser.setText(check.seller);
        holder.checkSum.setText(String.format(Locale.getDefault(), "%.2f", check.totalSum));
        holder.checkID.setText(Long.toString(check.ID));
    }

    @Override
    public int getItemCount() {
        return checksList.size();
    }

    public Check getItem(int position) {
        return new Check(mContext, checksList.get(position).ID);
    }

    public void removeItem(int position) {
        //cartList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        Check removedCheck = checksList.get(position);
        removedCheck.deleteFromSQL();
        checksList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Check item, int position) {
        //cartList.add(position, item);
        // notify item added by position
        item.ID = 0;
        item.saveToSQL();
        checksList.add(position, item);
        notifyItemInserted(position);
    }
}
