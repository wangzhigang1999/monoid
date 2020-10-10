package com.wang.pojo;

import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void testClone() {
        Book book = new Book();
        book.setImglink("hahaha");

        try {
            Book clone = (Book) book.clone();
            System.out.println(clone.getImglink());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}