package com.wonkydan.kitinventoryapp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Dan Gregory on 10/08/2016.
 */
public class BackgroundTask extends AsyncTask<String, Product, String> {

    String name, size, qty, price, photo;
    Context context;
    ProductAdapter productAdapter;
    Activity activity;
    ListView listView;
    View empty;

    BackgroundTask(Context context){
        this.context = context;
        activity = (Activity) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        //adding a new product to the database
        String method = params[0];
        ProductDatabase productDatabase = new ProductDatabase(context);

        switch (method) {
            case "add_info": {
                name = params[1];
                size = params[2];
                qty = params[3];
                price = params[4];
                photo = params[5];
                SQLiteDatabase sqLiteDatabase = productDatabase.getWritableDatabase();


                productDatabase.addInfo(sqLiteDatabase, name, size, qty, price, photo);

                return "One row inserted";
            }
            //filling the list view with available products
            case "get_info": {
                SQLiteDatabase sqLiteDatabase = productDatabase.getReadableDatabase();

                listView = (ListView) activity.findViewById(R.id.current_stock_list);
                empty = activity.findViewById(android.R.id.empty);


                Cursor cursor = productDatabase.getInformation(sqLiteDatabase);
                productAdapter = new ProductAdapter(context, R.layout.add_stock_layout);

                while (cursor.moveToNext()) {
                    name = cursor.getString(cursor.getColumnIndex(ProductContract.Table1.NAME));
                    size = cursor.getString(cursor.getColumnIndex(ProductContract.Table1.SIZE));
                    qty = cursor.getString(cursor.getColumnIndex(ProductContract.Table1.QUANTITY));
                    price = cursor.getString(cursor.getColumnIndex(ProductContract.Table1.PRICE));
                    photo = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.Table1.PHOTO));

                    Product product = new Product(name, size, qty, price, photo);
                    publishProgress(product);
                }

                return "get_info";
            }
            //updating price or quantity of an item
            //// TODO: 16/08/2016 fix amend item so it updates the SQL correctly.
            case "update_info": {

                SQLiteDatabase sqLiteDatabase = productDatabase.getWritableDatabase();
                productDatabase.updateStockInfo(sqLiteDatabase, params[1], params[2], params[3], params[4], params[5]);

                return "Database updated";
            }
        }
        return "failed...";
    }

    @Override
    protected void onProgressUpdate(Product... values) {
        productAdapter.add(values[0]);

    }

    @Override
    protected void onPostExecute(String result) {

        switch (result) {
            case "get_info":
                listView.setEmptyView(empty);
                listView.setAdapter(productAdapter);
                break;
            case "One row inserted":
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                break;
            case "Database updated":
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                break;
            case "failed...":
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                break;
            default:
                break;

        }
    }
}
