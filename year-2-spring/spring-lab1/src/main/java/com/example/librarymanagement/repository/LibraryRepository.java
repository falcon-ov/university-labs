package com.example.librarymanagement.repository;

import com.example.librarymanagement.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<Library, Long> {
}