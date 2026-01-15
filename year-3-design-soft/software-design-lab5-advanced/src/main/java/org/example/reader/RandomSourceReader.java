package org.example.reader;

import org.example.model.Record;
import java.util.List;
import java.util.Random;

public class RandomSourceReader implements SourceReader {
    @Override
    public List<Record> read() {
        System.out.println("Generating random data");
        Random rand = new Random();
        return List.of(
                new Record("r1", "Random" + rand.nextInt(100), rand.nextInt(1000)),
                new Record("r2", "Random" + rand.nextInt(100), rand.nextInt(1000))
        );
    }
}