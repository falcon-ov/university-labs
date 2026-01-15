package com.example.librarymanagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookDTO {
    private Long id;
    private String title;
    private Long authorId;
    private Long publisherId;
    private List<Long> categoryIds;
}