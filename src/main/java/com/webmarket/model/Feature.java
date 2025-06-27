package com.webmarket.model;

public class Feature {
    private int id;
    private String name;

    // Constructors
    public Feature() {}

    public Feature(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
