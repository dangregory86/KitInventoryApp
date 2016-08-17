package com.wonkydan.kitinventoryapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailIntermediary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("id") + 1;
        String idString = Integer.toString(id);

        BackgroundTask backgroundTask = new BackgroundTask(DetailIntermediary.this);
        backgroundTask.execute("detail_info", idString);
        finish();

    }
}
