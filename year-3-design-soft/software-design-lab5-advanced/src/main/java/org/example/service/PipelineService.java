package org.example.service;

import org.example.model.Record;
import org.example.reader.SourceReader;
import org.example.writer.SourceWriter;
import org.example.processor.DataProcessor;
import org.example.format.SerializationFormat;
import java.util.List;

public class PipelineService {
    private SourceReader reader;
    private SourceWriter writer;
    private DataProcessor processor;
    private SerializationFormat format;

    public PipelineService(SourceReader reader, SourceWriter writer,
                           DataProcessor processor, SerializationFormat format) {
        this.reader = reader;
        this.writer = writer;
        this.processor = processor;
        this.format = format;
    }

    public void execute() {
        System.out.println("\n--- Executing Pipeline ---");
        List<Record> data = reader.read();
        List<Record> processed = processor.process(data);
        writer.write(processed, format);
        System.out.println("--- Pipeline Complete ---\n");
    }
}