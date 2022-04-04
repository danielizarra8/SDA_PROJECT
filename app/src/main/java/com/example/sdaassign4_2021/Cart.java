package com.example.sdaassign4_2021;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Cart {
    private String productID, productName;
    private int price, quantity;

    public Cart(String productID, String pruductName, int price, int quantity) {
        this.productID = productID;
        this.productName = pruductName;
        this.price = price;
        this.quantity = quantity;
    }
    @Override
    public boolean equals(Object o) {
        return productID.equals(((Cart)o).productID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, productID);
    }


    public String getProductID() {
        return productID;
    }

    public String getProductName() {
        return getProductName();
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

}
