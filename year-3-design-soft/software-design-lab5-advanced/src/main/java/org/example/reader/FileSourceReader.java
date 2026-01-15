package org.example.reader;

import org.example.model.Record;
import java.util.List;

public class FileSourceReader implements SourceReader {
    private String path;

    public FileSourceReader(String path) {
        this.path = path;
    }

    @Override
    public List<Record> read() {
        System.out.println("Reading from file: " + path);
        // Упрощенная реализация
        return List.of(
                new Record("1", "FileData1", 100),
                new Record("2", "FileData2", 200)
        );
    }
}