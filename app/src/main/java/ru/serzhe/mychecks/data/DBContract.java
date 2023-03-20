package ru.serzhe.mychecks.data;

import android.provider.BaseColumns;

/**
 * Created by sergio on 02.04.2017.
 */

public final class DBContract {

    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String REAL_TYPE = " REAL";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_SELLER =
            "CREATE TABLE " + Seller.TABLE_NAME + " (" +
                    Seller._ID + " INTEGER PRIMARY KEY," +
                    //Seller.COLUMN_NAME_ID + INTEGER_TYPE + COMMA_SEP +
                    Seller.COLUMN_NAME_INN + TEXT_TYPE + COMMA_SEP +
                    Seller.COLUMN_NAME_NAME + TEXT_TYPE +
            ")";
    public static final String SQL_CREATE_PRODUCT =
            "CREATE TABLE " + Product.TABLE_NAME + " (" +
                    Product._ID + " INTEGER PRIMARY KEY," +
                    //Product.COLUMN_NAME_ID + INTEGER_TYPE + COMMA_SEP +
                    Product.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    Product.COLUMN_NAME_CATEGORY_ID + INTEGER_TYPE +
            ")";
    public static final String SQL_CREATE_CHECKH =
            "CREATE TABLE " + Check.TABLE_NAME + " (" +
                    Check._ID + " INTEGER PRIMARY KEY," +
                    //Check.COLUMN_NAME_ID + INTEGER_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_DT + TEXT_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_OPERATION_TYPE + INTEGER_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_CASH_TOTAL_SUM + REAL_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_ECASH_TOTAL_SUM + REAL_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_RECEIPT_CODE + INTEGER_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_NDS_18 + REAL_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_NDS_10 + REAL_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_SELLER_ID + INTEGER_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_TOTAL_SUM + REAL_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_OPERATOR + TEXT_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_REQUEST_NUMBER + INTEGER_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_FISCAL_SIGN + TEXT_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_FISCAL_DRIVE_NUMBER + TEXT_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_KKT_REG_ID + TEXT_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_TAXATION_TYPE + INTEGER_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_FISCAL_DOCUMENT_NUMBER + INTEGER_TYPE + COMMA_SEP +
                    Check.COLUMN_NAME_SHIFT_NUMBER + INTEGER_TYPE +
            ")";
    public static final String SQL_CREATE_CHECKT =
            "CREATE TABLE " + CheckItem.TABLE_NAME + " (" +
                    CheckItem._ID + " INTEGER PRIMARY KEY," +
                    CheckItem.COLUMN_NAME_CHECK_ID + INTEGER_TYPE + COMMA_SEP +
                    CheckItem.COLUMN_NAME_NUM_STR + INTEGER_TYPE + COMMA_SEP +
                    CheckItem.COLUMN_NAME_PRODUCT_ID + INTEGER_TYPE + COMMA_SEP +
                    CheckItem.COLUMN_NAME_QUANTITY + REAL_TYPE + COMMA_SEP +
                    CheckItem.COLUMN_NAME_PRICE + REAL_TYPE + COMMA_SEP +
                    CheckItem.COLUMN_NAME_SUM + REAL_TYPE + COMMA_SEP +
                    CheckItem.COLUMN_NAME_NDS_10 + REAL_TYPE + COMMA_SEP +
                    CheckItem.COLUMN_NAME_NDS_18 + REAL_TYPE +
            ")";
    public static final String SQL_CREATE_CATEGORY =
            "CREATE TABLE " + Category.TABLE_NAME + " (" +
                    Category._ID + " INTEGER PRIMARY KEY," +
                    //Product.COLUMN_NAME_ID + INTEGER_TYPE + COMMA_SEP +
                    Category.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    Category.COLUMN_NAME_PARENT_ID + INTEGER_TYPE +
                    ")";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Seller.TABLE_NAME +
            "DROP TABLE IF EXISTS " + Product.TABLE_NAME +
            "DROP TABLE IF EXISTS " + Check.TABLE_NAME +
            "DROP TABLE IF EXISTS " + CheckItem.TABLE_NAME +
            "DROP TABLE IF EXISTS " + Category.TABLE_NAME;

    public static final String SQL_UPDATE_V2 = "ALTER TABLE "
            + Check.TABLE_NAME + " ADD COLUMN " + Check.COLUMN_NAME_RETAIL_PLACE_ADDRESS + TEXT_TYPE + ";";

    public DBContract() {}

    public static abstract class Seller implements BaseColumns {
        public static final String TABLE_NAME = "sellers";
        //public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_INN = "INN";
        public static final String COLUMN_NAME_NAME = "name";
    }

    public static abstract class Product implements BaseColumns {
        public static final String TABLE_NAME = "products";
        //public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CATEGORY_ID = "categoryID";
    }

    public static abstract class Check implements BaseColumns {
        public static final String TABLE_NAME = "checkH";
        //public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_DT = "DT";
        public static final String COLUMN_NAME_OPERATION_TYPE = "operationType";
        public static final String COLUMN_NAME_CASH_TOTAL_SUM = "cashTotalSum";
        public static final String COLUMN_NAME_ECASH_TOTAL_SUM = "ecashTotalSum";
        public static final String COLUMN_NAME_RECEIPT_CODE = "receiptCode";
        public static final String COLUMN_NAME_NDS_18 = "nds18";
        public static final String COLUMN_NAME_NDS_10 = "nds10";
        public static final String COLUMN_NAME_SELLER_ID = "sellerID";
        public static final String COLUMN_NAME_TOTAL_SUM = "totalSum";
        public static final String COLUMN_NAME_OPERATOR = "operator";
        public static final String COLUMN_NAME_REQUEST_NUMBER = "requestNumber";
        public static final String COLUMN_NAME_FISCAL_SIGN = "fiscalSign";
        public static final String COLUMN_NAME_FISCAL_DRIVE_NUMBER = "fiscalDriveNumber";
        public static final String COLUMN_NAME_KKT_REG_ID = "kktRegId";
        public static final String COLUMN_NAME_TAXATION_TYPE = "taxationType";
        public static final String COLUMN_NAME_FISCAL_DOCUMENT_NUMBER = "fiscalDocumentNumber";
        public static final String COLUMN_NAME_SHIFT_NUMBER = "shiftNumber";
        public static final String COLUMN_NAME_RETAIL_PLACE_ADDRESS = "retailPlaceAddress";
    }

    public static abstract class CheckItem implements BaseColumns {
        public static final String TABLE_NAME = "checkT";
        public static final String COLUMN_NAME_CHECK_ID = "checkID";
        public static final String COLUMN_NAME_NUM_STR = "numStr";
        public static final String COLUMN_NAME_PRODUCT_ID = "productID";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_SUM = "sum";
        public static final String COLUMN_NAME_NDS_10 = "nds10";
        public static final String COLUMN_NAME_NDS_18 = "nds18";
    }

    public static abstract class Category implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        //public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PARENT_ID = "parentID";
    }
}
