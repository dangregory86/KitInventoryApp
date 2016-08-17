package com.wonkydan.kitinventoryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Objects;

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

        if(method.equals("add_info")){
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
        else if(method.equals("get_info")) {
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
        else if(method.equals("update_info")){
            SQLiteDatabase sqLiteDatabase = productDatabase.getReadableDatabase();

            //get a cursor and move to first
            Cursor cursor = productDatabase.getInformation(sqLiteDatabase);
            cursor.moveToFirst();

            boolean stockExists = false;
            do{
                if(params[1].equals(cursor.getString(0)) && params[2].equals(cursor.getString(1))){
                    stockExists = true;

                }
            }while (cursor.moveToNext());
            if(stockExists){
                sqLiteDatabase = productDatabase.getWritableDatabase();
                productDatabase.updateStockInfo(sqLiteDatabase, params[1], params[2], cursor.getString(2), params[4], cursor.getString(3), params[6]);
            }
            return "Database updated";
        }else if(Objects.equals(method, "detail_info")){
            SQLiteDatabase sqLiteDatabase = productDatabase.getReadableDatabase();
            String name = "", size = "", qty = "", price = "", photo = "";

            Cursor cursor = productDatabase.getDetailInformation(sqLiteDatabase, params[1]);
            cursor.moveToFirst();
            int id = Integer.parseInt(params[1]);

            do{
                if(id == cursor.getInt(0)){
                    name = cursor.getString(cursor.getColumnIndex(ProductContract.Table1.NAME)) + "-";
                    size = cursor.getString(cursor.getColumnIndex(ProductContract.Table1.SIZE)) + "-";
                    qty = cursor.getString(cursor.getColumnIndex(ProductContract.Table1.QUANTITY)) + "-";
                    price = cursor.getString(cursor.getColumnIndex(ProductContract.Table1.PRICE)) + "-";
                    photo = cursor.getString(cursor.getColumnIndex(ProductContract.Table1.PHOTO));
                }
            }while(cursor.moveToNext());
             return name + size + qty + price + photo;

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

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                        Intent intent = new Intent(context, DetailIntermediary.class);

                        Bundle bundle = new Bundle();
                        bundle.putInt("id", position);

                        intent.putExtras(bundle);

                        context.startActivity(intent);

                    }
                });
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
                Intent intent = new Intent(context, DetailActivity.class);

                //split down the result string
                String[] parts = result.split("-");
                String name = parts[0];
                String size = parts[1];
                String qty = parts[2];
                String price = parts[3];
                String photo = parts[4];

                //add the item details to the intent
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("size", size);
                bundle.putString("qty", qty);
                bundle.putString("price", price);
                bundle.putString("photo", photo);

                intent.putExtras(bundle);

                context.startActivity(intent);
                break;
        }
    }
}
