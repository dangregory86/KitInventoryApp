package com.wonkydan.kitinventoryapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity {


    ImageView itemPhoto;
    TextView nameView, sizeView, quantityView, priceView, selectedView;
    Button minusButton, plusButton, sellButton, deleteButton, orderMoreButton, receiveStock;
    String name, size, quantity, price, photoLocation, newQtyString, qtyAvailableString;
    int qtySelected = 0, qtyAvailable, photoHeight, photoWidth, newQty;
    Context context = this;
    ProductDatabase productDatabase;

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

        //adjust the selected quantity view
        minusButton = (Button) findViewById(R.id.minusButton);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qtySelected > 0) {
                    qtySelected -= 1;
                    selectedView.setText(Integer.toString(qtySelected));
                }
            }
        });

        plusButton = (Button) findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qtySelected < qtyAvailable + 10) {
                    qtySelected++;
                    selectedView.setText(Integer.toString(qtySelected));
                }
            }
        });

        //set up the order more button
        orderMoreButton = (Button) findViewById(R.id.orderMore);
        orderMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //subject string
                String stockRequired = "More " + name + " in size " + size + " required";

                //main body string
                String emailMainBody = "Dear supplier,\n" +
                        "I would like to order 10 more " + name + " in size " + size + ".\n" +
                        "For delivery ASAP\n" +
                        "Kind regards";

                //supplier email address
                String[] emailAddress = new String[]{"Supplier@morestuff.com"};

                //send the email
                orderMoreStock(stockRequired, emailMainBody, emailAddress);
            }
        });

        //set up the delete button
        deleteButton = (Button) findViewById(R.id.deleteAllButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteItemConfirmation();


            }
        });
        //get the image view
        itemPhoto = (ImageView) findViewById(R.id.detail_item_picture);

        ViewTreeObserver vto = itemPhoto.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                itemPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                photoHeight = itemPhoto.getMeasuredHeight();
                photoWidth = itemPhoto.getMeasuredWidth();
                rotateImage(setReducedImageSize());
                return true;
            }
        });

        //sell button to sell the selected number of stock
        sellButton = (Button) findViewById(R.id.sellButton);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qtySelected > 0 && qtySelected <= qtyAvailable) {
                    newQty = qtyAvailable - qtySelected;
                    newQtyString = Integer.toString(newQty);
                    qtyAvailableString = Integer.toString(qtyAvailable);
                    BackgroundTask bTask = new BackgroundTask(context);

                    //toast to say how many have been sold
                    Toast.makeText(DetailActivity.this, qtySelected + " sold", Toast.LENGTH_SHORT).show();
                    //adjust quantity shown in detail view
                    quantityView.setText(newQtyString);
                    //update database
                    bTask.execute("update_info", name, size, price, qtyAvailableString, newQtyString);

                } else {
                    Toast.makeText(DetailActivity.this, "We don't have that many in stock, please select less and try again", Toast.LENGTH_LONG).show();
                }
            }
        });

        //the button to receive stock
        receiveStock = (Button) findViewById(R.id.receiveMore);
        receiveStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newQty = qtyAvailable + qtySelected;
                newQtyString = Integer.toString(newQty);
                qtyAvailableString = Integer.toString(qtyAvailable);
                BackgroundTask bTask = new BackgroundTask(context);
                //toast to say how many have been sold
                Toast.makeText(DetailActivity.this, qtySelected + " received", Toast.LENGTH_SHORT).show();

                //adjust the qty shown in detail view
                quantityView.setText(newQtyString);
                //update database
                bTask.execute("update_info", name, size, price, qtyAvailableString, newQtyString);
                qtyAvailable = newQty;

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
        } else if (id == R.id.action_add_stock) {
            Intent intent = new Intent(DetailActivity.this, AddStockActivity.class);

            startActivity(intent);
            return true;
        } else if (id == R.id.action_view_stock) {
            Intent intent = new Intent(DetailActivity.this, ViewStockActivity.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        //get the image view
        rotateImage(setReducedImageSize());
    }

    private Bitmap setReducedImageSize() {

        //set up the bitmap options
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        //set the image to get the information
        bmOptions.inJustDecodeBounds = true;

        //open the image
        BitmapFactory.decodeFile(photoLocation, bmOptions);

        //get the width and height
        int pictureWidth = bmOptions.outWidth;
        int pictureHeight = bmOptions.outHeight;

        //scale the picture to the image view
        bmOptions.inSampleSize = Math.min(pictureWidth / photoWidth, pictureHeight / photoHeight);

        //set the image to actually show
        bmOptions.inJustDecodeBounds = false;

        //set the smaller image to the image view
        return BitmapFactory.decodeFile(photoLocation, bmOptions);
    }

    private void rotateImage(Bitmap bitmap) {
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

        switch (orientation) {
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

    //method to start an email intent to order more stock from the supplier
    public void orderMoreStock(String emailDescription, String emailBody, String[] supplier) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, supplier);
        intent.putExtra(Intent.EXTRA_SUBJECT, emailDescription);
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void deleteItemConfirmation() {
        new AlertDialog.Builder(context)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        productDatabase = new ProductDatabase(context);
                        SQLiteDatabase sqLiteDatabase = productDatabase.getWritableDatabase();
                        productDatabase.deleteItem(sqLiteDatabase, name, size, quantity, price);

                        Toast.makeText(getBaseContext(), "Item Removed", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
