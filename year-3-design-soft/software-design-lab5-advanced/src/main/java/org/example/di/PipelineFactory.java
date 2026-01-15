package org.example.di;

import org.example.service.PipelineService;
import org.example.reader.*;
import org.example.writer.*;
import org.example.processor.DataProcessor;
import org.example.format.*;

public class PipelineFactory {
    public PipelineService createPipeline(String readerType, String writerType, String formatType) {
        SourceReader reader = createReader(readerType);
        SourceWriter writer = createWriter(writerType);
        SerializationFormat format = createFormat(formatType);
        DataProcessor processor = new DataProcessor();

        return new PipelineService(reader, writer, processor, format);
    }

    private SourceReader createReader(String type) {
        return switch (type) {
            case "file" -> new FileSourceReader("input.txt");
            case "random" -> new RandomSourceReader();
            case "http" -> new HttpSourceReader("http://api.example.com");
            default -> throw new IllegalArgumentException("Unknown reader type: " + type);
        };
    }

    private SourceWriter createWriter(String type) {
        return switch (type) {
            case "file" -> new FileSourceWriter("output.txt");
            case "console" -> new ConsoleSourceWriter();
            default -> throw new IllegalArgumentException("Unknown writer type: " + type);
        };
    }

    private SerializationFormat createFormat(String type) {
        return switch (type) {
            case "json" -> new JsonFormat();
            case "csv" -> new CsvFormat();
            default -> throw new IllegalArgumentException("Unknown format type: " + type);
        };
    }
}