package org.example.format;

import org.example.model.Record;
import java.util.List;

public interface SerializationFormat {
    String serialize(List<Record> data);
}