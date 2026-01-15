package org.example;

import org.example.di.*;
import org.example.service.PipelineService;
import org.example.reader.*;
import org.example.writer.*;
import org.example.processor.DataProcessor;
import org.example.format.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Подход 1: Построение контейнера для конкретной цепочки ===");
        DIContainer container1 = new DIContainer();
        container1.register(SourceReader.class, new RandomSourceReader());
        container1.register(SourceWriter.class, new ConsoleSourceWriter());
        container1.register(DataProcessor.class, new DataProcessor());
        container1.register(SerializationFormat.class, new JsonFormat());

        PipelineService pipeline1 = container1.createPipeline();
        pipeline1.execute();

        System.out.println("\n=== Подход 2: Использование фабрики ===");
        PipelineFactory factory = new PipelineFactory();

        System.out.println("\n--- Конфигурация 1: File -> CSV ---");
        PipelineService pipeline2 = factory.createPipeline("file", "console", "csv");
        pipeline2.execute();

        System.out.println("\n--- Конфигурация 2: HTTP -> JSON ---");
        PipelineService pipeline3 = factory.createPipeline("http", "file", "json");
        pipeline3.execute();

        System.out.println("\n--- Конфигурация 3: Random -> CSV ---");
        PipelineService pipeline4 = factory.createPipeline("random", "console", "csv");
        pipeline4.execute();
    }
}