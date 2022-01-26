package com.example.sdaassign4_2021;

import java.security.PublicKey;
import java.util.Objects;
/*
 * @author Created by Rafael Izarra 2022
 *
 * A ViewAdapter class using RecycleView
 */
public class Book {
    private String bookAuthor;
    private String bookTitle;
    private String bookID;
    private String bookURL;
    private boolean availability;

    /**
     * Create a new Book object.
     * @param vBookAuthor is the name of the Book author (e.g. Agatta cristie)
     * @param vBookTitle is the name of the book's title.
     * @param  vBookId is the id of the book (0, 1 ,10, etc)
     * @param vAvailability is the availability state of the book (e.g. Available:true or false)
     * @param url is the link that point to the reference of the book's image
     *
     * */

    public Book (String vBookAuthor, String vBookTitle, String vBookId, String url, boolean vAvailability){
        this.bookAuthor = vBookAuthor;
        this.bookTitle = vBookTitle;
        this.bookID = vBookId;
        this.bookURL = url;
        this.availability = vAvailability;

    }

    public Book(){

    }

    public void setBookID(String vBookID){
        this.bookID = vBookID;
    }
    public void setBookAuthor(String author){
        this.bookAuthor = author;
    }

    public void setBookTitle(String title) {
        this.bookTitle = title;
    }
    // to check duplicate books in the list comparing by id
    @Override
    public boolean equals(Object o) {
        return bookID.equals(((Book)o).bookID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookAuthor, bookTitle, bookID);
    }

    /**
     *Get the bookd id, author, title, url and availability state of the selected book
     */
    public String getBookID(){ return bookID; }
    public String getBookAuthor(){ return bookAuthor; }
    public String getBookTitle(){ return bookTitle; }
    public String getBookURL(){ return bookURL; }
    public boolean getAvailability(){ return availability; }
}
