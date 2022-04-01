package com.example.sdaassign4_2021;

import java.util.ArrayList;
import java.util.HashMap;

public class Cart {
    private String productID, productName;
    private int price, quantity;

    public Cart(String productID, String productName, int price, int quantity) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public String getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

}
