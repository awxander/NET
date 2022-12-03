package ru.nsu.fit.tsibin.entities;

import java.io.PrintStream;

public class Place {
    private String name;
    private String category;
    private String description;
    private String xid;

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getXid() {
        return xid;
    }

    public void printPlaceData(PrintStream outStream) {
        if (name != null) {
            outStream.println("name: " + name);
        } else {
            outStream.println("no name");
        }
        if (category != null) outStream.println("category: " + category);
        if (description != null) outStream.println("description: " + description);
        else outStream.println("no description");

    }
}
