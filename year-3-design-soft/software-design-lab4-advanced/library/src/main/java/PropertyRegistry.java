import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PropertyRegistry {
    private static final Map<String, Integer> keys = new ConcurrentHashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static <T> PropertyKey<T> register(String name) {
        Integer existing = keys.putIfAbsent(name, counter.get());
        if (existing != null) {
            throw new IllegalStateException("Ключ '" + name + "' уже зарегистрирован");
        }
        return new PropertyKey<>(counter.getAndIncrement(), name);
    }

    public static Map<String, Integer> getKeys(){
        return keys;
    }
}