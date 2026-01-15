package com.example.librarymanagement.repository;

import com.example.librarymanagement.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}