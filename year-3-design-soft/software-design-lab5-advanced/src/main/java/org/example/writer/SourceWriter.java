package org.example.writer;

import org.example.model.Record;
import org.example.format.SerializationFormat;
import java.util.List;

public interface SourceWriter {
    void write(List<Record> data, SerializationFormat format);
}