package com.tommyapps.shop;

public class Product {

    private String product;
    private double price = 0;
    private boolean bought = false;

    public Product(String product) {
        this.product = product;
    }

    public Product(String product, double price) {
        this.product = product;
        this.price = price;
    }

    public Product(String product, double price, boolean bought) {
        this.product = product;
        this.price = price;
        this.bought = bought;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }
}
