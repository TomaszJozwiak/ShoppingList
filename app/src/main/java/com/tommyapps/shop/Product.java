package com.tommyapps.shop;

public class Product {

    private String product;
    private double price;
    private boolean bought;

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
