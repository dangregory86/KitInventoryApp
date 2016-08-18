package com.wonkydan.kitinventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Dan Gregory on 10/08/2016.
 */
public class ProductDatabase extends SQLiteOpenHelper {
    public ProductDatabase(Context context) {
        super(context, ProductContract.DATABASE_NAME, null, ProductContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(ProductContract.Table1.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(ProductContract.Table1.DELETE_TABLE);
        onCreate(sqLiteDatabase);

    }

    public void addInfo(SQLiteDatabase sqLiteDatabase, String name, String size, String qty, String price, String photo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductContract.Table1.NAME, name);
        contentValues.put(ProductContract.Table1.SIZE, size);
        contentValues.put(ProductContract.Table1.QUANTITY, qty);
        contentValues.put(ProductContract.Table1.PRICE, price);
        contentValues.put(ProductContract.Table1.PHOTO, photo);

        sqLiteDatabase.insert(ProductContract.Table1.TABLE_TITLE, null, contentValues);

        Log.d("DB:", "One row inserted");
    }

    public Cursor getInformation(SQLiteDatabase sqLiteDatabase) {

        String[] columnNames = {ProductContract.Table1.NAME, ProductContract.Table1.SIZE, ProductContract.Table1.QUANTITY, ProductContract.Table1.PRICE, ProductContract.Table1.PHOTO};

        return sqLiteDatabase.query(ProductContract.Table1.TABLE_TITLE, columnNames,
                null, null, null, null, null);
    }

    public void updateStockInfo(SQLiteDatabase sqLiteDatabase, String name, String size, String price, String qtyOld, String qtyNew) {

        String selection = ProductContract.Table1.NAME + " LIKE ? AND " + ProductContract.Table1.SIZE + " LIKE ? AND " + ProductContract.Table1.PRICE + " LIKE ? AND "
                + ProductContract.Table1.QUANTITY + " LIKE ?";
        String args[] = {name, size, price, qtyOld};
        ContentValues contentValues = new ContentValues();

        contentValues.put(ProductContract.Table1.QUANTITY, qtyNew);

        sqLiteDatabase.update(ProductContract.Table1.TABLE_TITLE, contentValues, selection, args);

    }

    public void deleteItem(SQLiteDatabase sqLiteDatabase, String name, String size, String qty, String price) {
        String selection = ProductContract.Table1.NAME + " LIKE ? AND " + ProductContract.Table1.SIZE + " LIKE ? AND "
                + ProductContract.Table1.PRICE + " LIKE ? AND " + ProductContract.Table1.QUANTITY + " LIKE ?";

        String arg[] = {name, size, price, qty};

        sqLiteDatabase.delete(ProductContract.Table1.TABLE_TITLE, selection, arg);

    }
}
