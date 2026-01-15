public class PropertyKey<T> {
    private final int id;
    private final String name;

    PropertyKey(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
}