package org.example.model;

public class Record {
    private String id;
    private String name;
    private int value;

    public Record(String id, String name, int value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getValue() { return value; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setValue(int value) { this.value = value; }
}