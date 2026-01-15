package org.example.format;

import org.example.model.Record;
import java.util.List;

public class CsvFormat implements SerializationFormat {
    @Override
    public String serialize(List<Record> data) {
        StringBuilder sb = new StringBuilder("id,name,value\n");
        for (Record r : data) {
            sb.append(r.getId()).append(",")
                    .append(r.getName()).append(",")
                    .append(r.getValue()).append("\n");
        }
        return sb.toString();
    }
}