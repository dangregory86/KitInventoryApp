package com.wonkydan.kitinventoryapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AmendStockActivity extends AppCompatActivity {

    EditText etName, etSize, etQty, etPrice;
    Button update;
    String name, size, qty, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amend_stock_layout);

        etName = (EditText) findViewById(R.id.amend_item_name_entered);
        etSize = (EditText) findViewById(R.id.amend_item_size_entered);
        etQty = (EditText) findViewById(R.id.amend_item_quantity_entered);
        etPrice = (EditText) findViewById(R.id.amend_item_price_entered);

        update = (Button) findViewById(R.id.amend_stock_page_button);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                size = etSize.getText().toString();
                qty = etQty.getText().toString();
                price = "$" + etPrice.getText().toString();

                if(TextUtils.isEmpty(name)) {
                    etName.setError("Enter a name");
                } else if(TextUtils.isEmpty(size)){
                    etSize.setError("Enter a size");
                }else if(TextUtils.isEmpty(qty)){
                    etQty.setError("Enter a quantity");
                }else if(TextUtils.isEmpty(price)){
                    etPrice.setError("Enter a price");
                }else{
                    BackgroundTask backgroundTask = new BackgroundTask(AmendStockActivity.this);
                    backgroundTask.execute("update_info", name, size, qty, price);
                    finish();}
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_main_page) {
            Intent intent = new Intent(AmendStockActivity.this, MainActivity.class);

            startActivity(intent);
            return true;
        }else if(id == R.id.action_add_stock){
            Intent intent = new Intent(AmendStockActivity.this, AddStockActivity.class);

            startActivity(intent);
            return true;
        }else if(id == R.id.action_view_stock){
            Intent intent = new Intent(AmendStockActivity.this, ViewStockActivity.class);

            startActivity(intent);
            return true;
        }else if(id == R.id.action_amend_stock){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
