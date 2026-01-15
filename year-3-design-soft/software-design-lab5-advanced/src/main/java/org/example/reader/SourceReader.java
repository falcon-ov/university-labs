package org.example.reader;

import org.example.model.Record;
import java.util.List;

public interface SourceReader {
    List<Record> read();
}