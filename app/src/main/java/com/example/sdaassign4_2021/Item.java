package com.example.sdaassign4_2021;

import java.util.Objects;
/*
 * @author Created by Rafael Izarra 2022
 *
 * A ViewAdapter class using RecycleView
 */
public class Item {
    private String productName;
    private String productDescription;
    private String productID;
    private String productURL;
    private int productQuantity;
    private int productPrice;

    /**
     * Create a new Product object.
     * @param vProductName is the name of the Book author (e.g. Agatta cristie)
     * @param vProductPrice is the name of the book's title.
     * @param  vBookId is the id of the book (0, 1 ,10, etc)
     * @param vProductQty is the availability state of the book (e.g. Available:true or false)
     * @param url is the link that point to the reference of the book's image
     *
     * */

    public Item(String vProductName, int vProductPrice, String vProductDescription, String vBookId, String url, int vProductQty){
        this.productName = vProductName;
        this.productPrice = vProductPrice;
        this.productDescription = vProductDescription;
        this.productID = vBookId;
        this.productURL = url;
        this.productQuantity = vProductQty;

    }

    public Item(){

    }

    public void setProductID(String vBookID){
        this.productID = vBookID;
    }
    public void setProductName(String author){
        this.productName = author;
    }

    public void setProductDescription(String title) {
        this.productDescription = title;
    }
    // to check duplicate books in the list comparing by id
    @Override
    public boolean equals(Object o) {
        return productID.equals(((Item)o).productID);
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
