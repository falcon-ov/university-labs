package org.example.di;

import org.example.service.PipelineService;
import org.example.reader.SourceReader;
import org.example.writer.SourceWriter;
import org.example.processor.DataProcessor;
import org.example.format.SerializationFormat;
import java.util.HashMap;
import java.util.Map;

public class DIContainer {
    private Map<Class<?>, Object> services = new HashMap<>();

    public <T> void register(Class<T> clazz, T instance) {
        services.put(clazz, instance);
    }

    public <T> T resolve(Class<T> clazz) {
        return clazz.cast(services.get(clazz));
    }

    public PipelineService createPipeline() {
        return new PipelineService(
                resolve(SourceReader.class),
                resolve(SourceWriter.class),
                resolve(DataProcessor.class),
                resolve(SerializationFormat.class)
        );
    }
}