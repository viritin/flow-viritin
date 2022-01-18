package org.vaadin.firitin.testdomain;

import java.nio.file.Path;

public class Attachment {
    private String name;
    private long size;
    private Path path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
