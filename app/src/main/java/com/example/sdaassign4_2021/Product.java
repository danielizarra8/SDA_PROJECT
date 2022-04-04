package com.example.sdaassign4_2021;

import java.util.Objects;
/*
 * @author Created by Rafael Izarra 2022
 *
 * A ViewAdapter class using RecycleView
 */
public class Product {
    private String productName;
    private String productDescription;
    private String productID;
    private String productURL;
    private int productQuantity;
    private int productPrice;

    /**
     * Create a new Product object.
     * @param vProductName is the name of the product name (Oreo)
     * @param vProductPrice product price (3$).
     * @param  vProductID product id  (random generated by the firestore db)
     * @param vProductQty is the availability state of the product (e.g. Available:true or false)
     * @param url is the link that point to the reference of the product's image
     *
     * */

    public Product(String vProductName, int vProductPrice, String vProductDescription, String vProductID, String url, int vProductQty){
        this.productName = vProductName;
        this.productPrice = vProductPrice;
        this.productDescription = vProductDescription;
        this.productID = vProductID;
        this.productURL = url;
        this.productQuantity = vProductQty;

    }

    public Product(){

    }


    // to check duplicate books in the list comparing by id
    @Override
    public boolean equals(Object o) {
        return productID.equals(((Product)o).productID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, productDescription, productID);
    }

    /**
     *Get the product id, author, title, url and availability state of the selected book
     */
    public String getProductID(){ return productID; }
    public String getProductName(){ return productName; }
    public String getProductDescription(){ return productDescription; }
    public String getProductURL(){ return productURL; }
    public int getProductQuantity(){ return productQuantity; }
    public int getProductPrice(){ return productPrice; }
}
