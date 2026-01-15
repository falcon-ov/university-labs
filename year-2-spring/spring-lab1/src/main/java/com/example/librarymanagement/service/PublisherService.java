package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.PublisherDTO;
import com.example.librarymanagement.entity.Publisher;
import com.example.librarymanagement.repository.PublisherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherService {
    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public List<PublisherDTO> getAllPublishers() {
        return publisherRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PublisherDTO getPublisherById(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found"));
        return convertToDTO(publisher);
    }

    public PublisherDTO createPublisher(PublisherDTO publisherDTO) {
        Publisher publisher = new Publisher();
        publisher.setName(publisherDTO.getName());
        Publisher savedPublisher = publisherRepository.save(publisher);
        return convertToDTO(savedPublisher);
    }

    public PublisherDTO updatePublisher(Long id, PublisherDTO publisherDTO) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found"));
        publisher.setName(publisherDTO.getName());
        Publisher updatedPublisher = publisherRepository.save(publisher);
        return convertToDTO(updatedPublisher);
    }

    public void deletePublisher(Long id) {
        publisherRepository.deleteById(id);
    }

    private PublisherDTO convertToDTO(Publisher publisher) {
        PublisherDTO dto = new PublisherDTO();
        dto.setId(publisher.getId());
        dto.setName(publisher.getName());
        return dto;
    }
}