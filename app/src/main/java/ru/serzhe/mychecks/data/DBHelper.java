package ru.serzhe.mychecks.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by sergio on 15.04.2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "MyChecks.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DBContract.SQL_CREATE_SELLER);
            db.execSQL(DBContract.SQL_CREATE_PRODUCT);
            db.execSQL(DBContract.SQL_CREATE_CHECKH);
            db.execSQL(DBContract.SQL_CREATE_CHECKT);
            db.execSQL(DBContract.SQL_CREATE_CATEGORY);

            onUpgrade(db, 1, 2);
        }


        catch (Exception e) {
            //Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("LOAD_CHECKS", e.getMessage());
        }
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion)
        {
            case 1:
                db.execSQL(DBContract.SQL_UPDATE_V2);
        }
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
