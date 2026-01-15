package com.example.librarymanagement.service;

import com.example.librarymanagement.dao.AuthorDao;
import com.example.librarymanagement.dao.BookDao;
import com.example.librarymanagement.dao.CategoryDao;
import com.example.librarymanagement.dao.PublisherDao;
import com.example.librarymanagement.dto.BookDTO;
import com.example.librarymanagement.entity.Author;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.Category;
import com.example.librarymanagement.entity.Publisher;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final PublisherDao publisherDao;
    private final CategoryDao categoryDao;

    public BookService(BookDao bookDao, AuthorDao authorDao,
                       PublisherDao publisherDao, CategoryDao categoryDao) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
        this.publisherDao = publisherDao;
        this.categoryDao = categoryDao;
    }

    public List<BookDTO> getAllBooks() {
        return bookDao.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookDTO getBookById(Long id) {
        Book book = bookDao.findById(id);
        if (book == null) {
            throw new RuntimeException("Book not found");
        }
        return convertToDTO(book);
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());

        Author author = authorDao.findById(bookDTO.getAuthorId());
        if (author == null) {
            throw new RuntimeException("Author not found");
        }
        book.setAuthor(author);

        Publisher publisher = publisherDao.findById(bookDTO.getPublisherId());
        if (publisher == null) {
            throw new RuntimeException("Publisher not found");
        }
        book.setPublisher(publisher);

        List<Category> categoryList = categoryDao.findAllByIds(bookDTO.getCategoryIds());
        if (categoryList.size() != bookDTO.getCategoryIds().size()) {
            throw new RuntimeException("One or more categories not found");
        }
        book.setCategories(new HashSet<>(categoryList)); // Преобразуем List в Set

        Book savedBook = bookDao.save(book);
        return convertToDTO(savedBook);
    }

    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookDao.findById(id);
        if (book == null) {
            throw new RuntimeException("Book not found");
        }
        book.setTitle(bookDTO.getTitle());

        Author author = authorDao.findById(bookDTO.getAuthorId());
        if (author == null) {
            throw new RuntimeException("Author not found");
        }
        book.setAuthor(author);

        Publisher publisher = publisherDao.findById(bookDTO.getPublisherId());
        if (publisher == null) {
            throw new RuntimeException("Publisher not found");
        }
        book.setPublisher(publisher);

        List<Category> categoryList = categoryDao.findAllByIds(bookDTO.getCategoryIds());
        if (categoryList.size() != bookDTO.getCategoryIds().size()) {
            throw new RuntimeException("One or more categories not found");
        }
        book.setCategories(new HashSet<>(categoryList)); // Преобразуем List в Set

        Book updatedBook = bookDao.save(book);
        return convertToDTO(updatedBook);
    }

    public void deleteBook(Long id) {
        bookDao.delete(id);
    }

    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthorId(book.getAuthor().getId());
        dto.setPublisherId(book.getPublisher().getId());
        dto.setCategoryIds(book.getCategories().stream().map(Category::getId).collect(Collectors.toList()));
        return dto;
    }
}