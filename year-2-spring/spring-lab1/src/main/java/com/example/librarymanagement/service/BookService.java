package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.BookDTO;
import com.example.librarymanagement.entity.Author;
import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.Category;
import com.example.librarymanagement.entity.Publisher;
import com.example.librarymanagement.repository.AuthorRepository;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.CategoryRepository;
import com.example.librarymanagement.repository.PublisherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository,
                       PublisherRepository publisherRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return convertToDTO(book);
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());

        Author author = authorRepository.findById(bookDTO.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        book.setAuthor(author);

        Publisher publisher = publisherRepository.findById(bookDTO.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found"));
        book.setPublisher(publisher);

        List<Category> categories = categoryRepository.findAllById(bookDTO.getCategoryIds());
        if (categories.size() != bookDTO.getCategoryIds().size()) {
            throw new RuntimeException("One or more categories not found");
        }
        book.setCategories(categories);

        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setTitle(bookDTO.getTitle());

        Author author = authorRepository.findById(bookDTO.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        book.setAuthor(author);

        Publisher publisher = publisherRepository.findById(bookDTO.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found"));
        book.setPublisher(publisher);

        List<Category> categories = categoryRepository.findAllById(bookDTO.getCategoryIds());
        if (categories.size() != bookDTO.getCategoryIds().size()) {
            throw new RuntimeException("One or more categories not found");
        }
        book.setCategories(categories);

        Book updatedBook = bookRepository.save(book);
        return convertToDTO(updatedBook);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
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