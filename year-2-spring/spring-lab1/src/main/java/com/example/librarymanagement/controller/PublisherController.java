package com.example.librarymanagement.controller;

import com.example.librarymanagement.dto.PublisherDTO;
import com.example.librarymanagement.service.PublisherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publishers")
public class PublisherController {
    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping
    public List<PublisherDTO> getAllPublishers() {
        return publisherService.getAllPublishers();
    }

    @GetMapping("/{id}")
    public PublisherDTO getPublisherById(@PathVariable Long id) {
        return publisherService.getPublisherById(id);
    }

    @PostMapping
    public PublisherDTO createPublisher(@RequestBody PublisherDTO publisherDTO) {
        return publisherService.createPublisher(publisherDTO);
    }

    @PutMapping("/{id}")
    public PublisherDTO updatePublisher(@PathVariable Long id, @RequestBody PublisherDTO publisherDTO) {
        return publisherService.updatePublisher(id, publisherDTO);
    }

    @DeleteMapping("/{id}")
    public void deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
    }
}