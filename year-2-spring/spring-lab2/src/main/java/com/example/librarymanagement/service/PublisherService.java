package com.example.librarymanagement.service;

import com.example.librarymanagement.dao.PublisherDao; // Новый DAO
import com.example.librarymanagement.dto.PublisherDTO;
import com.example.librarymanagement.entity.Publisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherService {
    private final PublisherDao publisherDao; // Заменяем PublisherRepository на PublisherDao

    public PublisherService(PublisherDao publisherDao) {
        this.publisherDao = publisherDao;
    }

    public List<PublisherDTO> getAllPublishers() {
        return publisherDao.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PublisherDTO getPublisherById(Long id) {
        Publisher publisher = publisherDao.findById(id);
        if (publisher == null) {
            throw new RuntimeException("Publisher not found");
        }
        return convertToDTO(publisher);
    }

    public PublisherDTO createPublisher(PublisherDTO publisherDTO) {
        Publisher publisher = new Publisher();
        publisher.setName(publisherDTO.getName());
        Publisher savedPublisher = publisherDao.save(publisher);
        return convertToDTO(savedPublisher);
    }

    public PublisherDTO updatePublisher(Long id, PublisherDTO publisherDTO) {
        Publisher publisher = publisherDao.findById(id);
        if (publisher == null) {
            throw new RuntimeException("Publisher not found");
        }
        publisher.setName(publisherDTO.getName());
        Publisher updatedPublisher = publisherDao.save(publisher);
        return convertToDTO(updatedPublisher);
    }

    public void deletePublisher(Long id) {
        publisherDao.delete(id);
    }

    private PublisherDTO convertToDTO(Publisher publisher) {
        PublisherDTO dto = new PublisherDTO();
        dto.setId(publisher.getId());
        dto.setName(publisher.getName());
        return dto;
    }
}