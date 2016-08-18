package com.wonkydan.kitinventoryapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan Gregory on 10/08/2016.
 */
public class ProductAdapter extends ArrayAdapter {

    Product product;
    List list = new ArrayList();

    public ProductAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(Product object) {
        list.add(object);

        super.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    //// TODO: 16/08/2016 make a delete item button on each listview item 
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ProductHolder productHolder;
        Button button;
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_item, parent, false);
            productHolder = new ProductHolder();
            product = (Product) getItem(position);
            productHolder.stockName = (TextView) row.findViewById(R.id.item_description);
            productHolder.stockPrice = (TextView) row.findViewById(R.id.item_price);
            productHolder.stockQty = (TextView) row.findViewById(R.id.item_qty);

            row.setTag(productHolder);
        } else {
            productHolder = (ProductHolder) row.getTag();
        }
        product = (Product) getItem(position);
        productHolder.stockName.setText(product.getName());
        productHolder.stockPrice.setText(product.getPrice());
        productHolder.stockQty.setText(product.getQty());
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product = (Product) getItem(position);
                Intent intent = new Intent(getContext(), DetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("name", product.getName());
                bundle.putString("size", product.getSize());
                bundle.putString("price", product.getPrice());
                bundle.putString("qty", product.getQty());
                bundle.putString("photo", product.getPhoto());

                intent.putExtras(bundle);
                getContext().startActivity(intent);
            }
        });

        //list view sell button method
        button = (Button) row.findViewById(R.id.list_sell_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product = (Product) getItem(position);
                //get the relevant strings for updating the data
                String name = product.getName();
                String size = product.getSize();
                String price = product.getPrice();
                String qty = product.getQty();

                //set the new qty after selling an item
                int newQtyNum = Integer.parseInt(qty) - 1;
                String newQtyString = Integer.toString(newQtyNum);

                //update the database
                BackgroundTask backgroundTask = new BackgroundTask(getContext());
                backgroundTask.execute("update_info", name, size, price, qty, newQtyString);

                //set a toast to refresh the page
                Toast.makeText(getContext(), "Sold 1, refresh to show", Toast.LENGTH_SHORT).show();

            }
        });

        return row;
    }

    static class ProductHolder {

        TextView stockName, stockQty, stockPrice;
    }

}
