package com.example.sdaassign4_2021;

import java.security.PublicKey;
import java.util.Objects;

public class Book {
    private String bookAuthor;
    private String bookTitle;
    private String bookID;


    public Book (String vBookAuthor, String vBookTitle, String bookId){
        this.bookAuthor = vBookAuthor;
        this.bookTitle = vBookTitle;
        this.bookID = bookId;
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

    public String getBookID(){ return bookID; }
    public String getBookAuthor(){ return bookAuthor; }
    public String getBookTitle(){ return bookTitle; }
}
