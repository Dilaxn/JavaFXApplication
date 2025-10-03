package com.mycompany.javafxapplication1.models;
public class File {
    private String fileName;
    private String owner;

    public File(String fileName, String owner) {
        this.fileName = fileName;
        this.owner = owner;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
