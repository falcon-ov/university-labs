package org.example.format;

import org.example.model.Record;
import java.util.List;

public class JsonFormat implements SerializationFormat {
    @Override
    public String serialize(List<Record> data) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < data.size(); i++) {
            Record r = data.get(i);
            sb.append("{\"id\":\"").append(r.getId())
                    .append("\",\"name\":\"").append(r.getName())
                    .append("\",\"value\":").append(r.getValue()).append("}");
            if (i < data.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

}