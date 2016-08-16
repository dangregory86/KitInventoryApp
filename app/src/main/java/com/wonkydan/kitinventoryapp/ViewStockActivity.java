package com.wonkydan.kitinventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ViewStockActivity extends AppCompatActivity {

    Button refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_stock_layout);

        BackgroundTask backgroundTask = new BackgroundTask(ViewStockActivity.this);

        backgroundTask.execute("get_info");

        refresh = (Button) findViewById(R.id.refresh_button);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
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
            Intent intent = new Intent(ViewStockActivity.this, MainActivity.class);

            startActivity(intent);
            return true;
        }else if(id == R.id.action_add_stock){
            Intent intent = new Intent(ViewStockActivity.this, AddStockActivity.class);

            startActivity(intent);
            return true;
        }else if(id == R.id.action_view_stock){
            return true;
        }else if(id == R.id.action_amend_stock){
            Intent intent = new Intent(ViewStockActivity.this, AmendStockActivity.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
