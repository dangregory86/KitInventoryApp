package com.wonkydan.kitinventoryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AddStockActivity extends AppCompatActivity {

    private final static int CAMERA_INT = 0;
    private final static int PERMISSION_CHECK = 1;
    EditText name, size, qty, price;
    Button addPhoto, addStock;
    String mName, mSize, mQty, mPrice, mPhoto = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_stock_layout);

        name = (EditText) findViewById(R.id.add_item_name_entered);
        size = (EditText) findViewById(R.id.add_item_size_entered);
        qty = (EditText) findViewById(R.id.add_item_quantity_entered);
        price = (EditText) findViewById(R.id.add_item_price_entered);
        addStock = (Button) findViewById(R.id.add_item_stock_button);
        addPhoto = (Button) findViewById(R.id.add_photo_button);

        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mName = name.getText().toString();
                mSize = size.getText().toString();
                mQty = qty.getText().toString();
                mPrice = "$" + price.getText().toString();

                if (TextUtils.isEmpty(mName)) {
                    name.setError("Enter a name");
                } else if (TextUtils.isEmpty(mSize)) {
                    size.setError("Enter a size");
                } else if (TextUtils.isEmpty(mQty)) {
                    qty.setError("Enter a quantity");
                } else if (TextUtils.isEmpty(mPrice)) {
                    price.setError("Enter a price");
                } else if (mPhoto == "") {
                    Toast.makeText(AddStockActivity.this, "You need to add a photo", Toast.LENGTH_SHORT).show();
                } else {
                    BackgroundTask backgroundTask = new BackgroundTask(AddStockActivity.this);
                    backgroundTask.execute("add_info", mName, mSize, mQty, mPrice, mPhoto);
                    finish();
                }
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddStockActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    takePhotoMethod();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(AddStockActivity.this, "Permission needed to write to file", Toast.LENGTH_SHORT).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CHECK);
                    }
                }
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
            Intent intent = new Intent(AddStockActivity.this, MainActivity.class);

            startActivity(intent);
            return true;
        } else if (id == R.id.action_add_stock) {
            return true;
        } else if (id == R.id.action_view_stock) {
            Intent intent = new Intent(AddStockActivity.this, ViewStockActivity.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CHECK) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoMethod();
            } else {
                Toast.makeText(this, "Permission is not granted, can't save images", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void takePhotoMethod() {
        Intent callCamera = new Intent();
        callCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoCaptured = null;

        try {
            photoCaptured = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        callCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoCaptured));
        startActivityForResult(callCamera, CAMERA_INT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_INT && resultCode == RESULT_OK) {
            Toast.makeText(AddStockActivity.this, "Photo taken", Toast.LENGTH_SHORT).show();

        }

    }

    File createImageFile() throws IOException {

        //create a date stamp to make the photo name unique
        String timeStamp = SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).toString();

        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageLocation);
        mPhoto = image.getAbsolutePath();

        return image;
    }

}
