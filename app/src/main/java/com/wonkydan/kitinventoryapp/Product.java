package com.wonkydan.kitinventoryapp;

/**
 * Created by danth on 10/08/2016.
 */
public class Product {

    private String name, size, qty, price, photo;

    public Product(String name, String size, String qty, String price, String photoLocation) {
        this.name = name;
        this.size = size;
        this.qty = qty;
        this.price = price;
        this.photo = photoLocation;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
