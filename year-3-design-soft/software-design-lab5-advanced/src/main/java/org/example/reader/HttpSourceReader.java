package org.example.reader;

import org.example.model.Record;
import java.util.List;

public class HttpSourceReader implements SourceReader {
    private String url;

    public HttpSourceReader(String url) {
        this.url = url;
    }

    @Override
    public List<Record> read() {
        System.out.println("Reading from HTTP: " + url);
        // Упрощенная реализация
        return List.of(
                new Record("h1", "HttpData1", 300),
                new Record("h2", "HttpData2", 400)
        );
    }
}