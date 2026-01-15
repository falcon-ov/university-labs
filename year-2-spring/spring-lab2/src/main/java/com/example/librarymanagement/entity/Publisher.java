package com.example.librarymanagement.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Publisher {
    private Long id;
    private String name;
    private Set<Book> books = new HashSet<>();
}