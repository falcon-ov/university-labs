package org.example.writer;

import org.example.model.Record;
import org.example.format.SerializationFormat;
import java.util.List;

public class ConsoleSourceWriter implements SourceWriter {
    @Override
    public void write(List<Record> data, SerializationFormat format) {
        String output = format.serialize(data);
        System.out.println("Writing to console:");
        System.out.println(output);
    }
}