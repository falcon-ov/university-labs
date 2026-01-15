package com.example.librarymanagement.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Book {
    private Long id;
    private String title;
    private Author author;
    private Publisher publisher;
    private Set<Category> categories = new HashSet<>();
    private Library library;
}