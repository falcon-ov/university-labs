import java.util.HashMap;
import java.util.Map;

public class Context {
    private final Map<Integer, Object> properties = new HashMap<>();

    public <T> void set(PropertyKey<T> key, T value) {
        properties.put(key.getId(), value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(PropertyKey<T> key) {
        return (T) properties.get(key.getId());
    }
}