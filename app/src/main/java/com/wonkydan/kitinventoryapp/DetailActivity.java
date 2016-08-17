package com.wonkydan.kitinventoryapp;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity {


    ImageView itemPhoto;
    TextView nameView, sizeView, quantityView, priceView, selectedView;
    Button minusButton, plusButton, sellButton, deleteButton, orderMoreButton;
    String name, size, quantity, price, photoLocation;
    int qtySelected = 0, qtyAvailable;
    Context context = this;
    ProductDatabase productDatabase;

    //// TODO: 16/08/2016 get the order more button to open email with stock details ready to send to supplier
    //// TODO: 16/08/2016 get the sell button to amend stock or order more if the stock becomes zero
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        //receive the info from the intent
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        size = bundle.getString("size");
        quantity = bundle.getString("qty");
        price = bundle.getString("price");
        qtyAvailable = Integer.parseInt(bundle.getString("qty"));
        photoLocation = bundle.getString("photo");


        //get the textview details
        nameView = (TextView) findViewById(R.id.detail_name);
        sizeView = (TextView) findViewById(R.id.detail_size);
        quantityView = (TextView) findViewById(R.id.detail_quantity);
        priceView = (TextView) findViewById(R.id.detail_price);
        selectedView = (TextView) findViewById(R.id.quantitySelected);

        //set the text view text
        nameView.setText(name);
        sizeView.setText(size);
        quantityView.setText(quantity);
        priceView.setText(price);

        //get the image view
        itemPhoto = (ImageView) findViewById(R.id.detail_item_picture);
        rotateImage(setReducedImageSize());

        //adjust the selected quantity view
        minusButton = (Button) findViewById(R.id.minusButton);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(qtySelected > 0){
                    qtySelected -= 1;
                    selectedView.setText(Integer.toString(qtySelected));
                }
            }
        });

        plusButton = (Button) findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(qtySelected < qtyAvailable){
                    qtySelected ++;
                    selectedView.setText(Integer.toString(qtySelected));
                }
            }
        });

        //set up the delete button
        deleteButton = (Button) findViewById(R.id.deleteAllButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                productDatabase = new ProductDatabase(context);
                SQLiteDatabase sqLiteDatabase = productDatabase.getWritableDatabase();
                productDatabase.deleteItem(sqLiteDatabase, name, size, quantity, price);

                Toast.makeText(getBaseContext(), "Item Removed", Toast.LENGTH_LONG).show();

                finish();

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
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);

            startActivity(intent);
            return true;
        }else if(id == R.id.action_add_stock){
            Intent intent = new Intent(DetailActivity.this, AddStockActivity.class);

            startActivity(intent);
            return true;
        }else if(id == R.id.action_view_stock){
            Intent intent = new Intent(DetailActivity.this, ViewStockActivity.class);

            startActivity(intent);
            return true;
        }else if(id == R.id.action_amend_stock){
            Intent intent = new Intent(DetailActivity.this, AmendStockActivity.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //// TODO: 16/08/2016 get the imageview size to load correctly
        private Bitmap setReducedImageSize(){
            itemPhoto = (ImageView) findViewById(R.id.detail_item_picture);
//        int imageViewWidth = itemPhoto.getWidth();
//        int imageViewHeight = itemPhoto.getHeight();

        //set up the bitmap options
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        //set the image to get the information
//        bmOptions.inJustDecodeBounds = true;
//        //open the image
//        BitmapFactory.decodeFile(photoLocation, bmOptions);
        //get the width and height
//        int pictureWidth = bmOptions.outWidth;
//        int pictureHeight = bmOptions.outHeight;

        //scale the picture to the image view
//        bmOptions.inSampleSize = Math.min(pictureWidth/imageViewWidth, pictureHeight/imageViewHeight);

        //set the image to actually show
//        bmOptions.inJustDecodeBounds = false;

        //set the smaller image to the image view
        return BitmapFactory.decodeFile(photoLocation, bmOptions);
//        mPicture.setImageBitmap(smallerPhoto);
    }

    private void rotateImage(Bitmap bitmap){
        ExifInterface exifInterface = null;

        //get the exif data for the photo
        try {
            exifInterface = new ExifInterface(photoLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert exifInterface != null;
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();

        switch(orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        itemPhoto.setImageBitmap(rotatedBitmap);
    }

}
