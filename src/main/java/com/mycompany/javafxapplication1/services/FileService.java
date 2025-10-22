package com.mycompany.javafxapplication1.services;

import java.util.ArrayList;
import java.util.List;

public class FileService {
    private final List<String> files = new ArrayList<>();

    public List<String> getAllFiles() {
        return new ArrayList<>(files);
    }

    public void addFile(String fileName) {
        if (!files.contains(fileName)) {
            files.add(fileName);
        }
    }

    public void deleteFile(String fileName) {
        files.remove(fileName);
    }
}
