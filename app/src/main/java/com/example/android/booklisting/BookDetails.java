package com.example.android.booklisting;
/**
 * Created by ursum on 08/07/2017.
 */
//book details structure

class Book {

    final String title;
    final String author;
    final String Link;

    public Book(
            String title,
            String author,
            String Link
    ) {

        this.title = title;
        this.author = author;
        this.Link = Link;
    }
}
