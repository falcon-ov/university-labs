package org.example.processor;

import org.example.model.Record;
import java.util.List;
import java.util.stream.Collectors;

public class DataProcessor {
    public List<Record> process(List<Record> data) {
        System.out.println("Processing data...");
        // Простая обработка: uppercase имени и увеличение значения
        return data.stream()
                .map(r -> new Record(
                        r.getId(),
                        r.getName().toUpperCase(),
                        r.getValue() + 10
                ))
                .collect(Collectors.toList());
    }
}