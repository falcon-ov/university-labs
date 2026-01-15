package com.example.librarymanagement.service;

import com.example.librarymanagement.dao.AuthorDao; // Новый DAO
import com.example.librarymanagement.dto.AuthorDTO;
import com.example.librarymanagement.entity.Author;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    private final AuthorDao authorDao; // Заменяем AuthorRepository на AuthorDao

    public AuthorService(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    public List<AuthorDTO> getAllAuthors() {
        return authorDao.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AuthorDTO getAuthorById(Long id) {
        Author author = authorDao.findById(id);
        if (author == null) {
            throw new RuntimeException("Author not found");
        }
        return convertToDTO(author);
    }

    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        Author author = new Author();
        author.setName(authorDTO.getName());
        Author savedAuthor = authorDao.save(author);
        return convertToDTO(savedAuthor);
    }

    public AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO) {
        Author author = authorDao.findById(id);
        if (author == null) {
            throw new RuntimeException("Author not found");
        }
        author.setName(authorDTO.getName());
        Author updatedAuthor = authorDao.save(author);
        return convertToDTO(updatedAuthor);
    }

    public void deleteAuthor(Long id) {
        authorDao.delete(id);
    }

    private AuthorDTO convertToDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        return dto;
    }
}