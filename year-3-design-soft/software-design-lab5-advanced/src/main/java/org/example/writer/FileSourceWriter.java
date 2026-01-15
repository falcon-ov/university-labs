package org.example.writer;

import org.example.model.Record;
import org.example.format.SerializationFormat;
import java.util.List;

public class FileSourceWriter implements SourceWriter {
    private String path;

    public FileSourceWriter(String path) {
        this.path = path;
    }

    @Override
    public void write(List<Record> data, SerializationFormat format) {
        String output = format.serialize(data);
        System.out.println("Writing to file: " + path);
        System.out.println(output);
    }
}