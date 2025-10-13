package com.mycompany.javafxapplication1.gui.controllers;

import com.mycompany.javafxapplication1.balancer.Request;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileController {
    private DataSource sqliteDataSource;

    @FXML
    private ListView<String> fileList;

    private final List<String> files = new ArrayList<>();
    private final FileChooser fileChooser = new FileChooser();
    private int currentStorageIndex = 0;

    private static final String[] STORAGE_URLS = {
            "http://localhost:9001",
            "http://localhost:9002",
            "http://localhost:9003",
            "http://localhost:9004"
    };

    public void initialize() {
        setupSQLiteDataSource();

        String username = getCurrentUsernameFromSession();
        if (username != null) {
            listFilesFromContainer("roundrobin", username);
        } else {
            showAlert("Error", "Failed to retrieve session data.", AlertType.ERROR);
        }


    }

    public void handleSelectFile(ActionEvent event) {
        Stage stage = (Stage) fileList.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String fileName = selectedFile.getName();
            files.add(fileName);
            fileList.getItems().add(fileName);

            String username = getCurrentUsernameFromSession();
            if (username != null) {
                Request request = new Request(files.size(), 1); // Priority 1 for uploads
                sendFileToContainer(selectedFile, request, "roundrobin", username);
                showAlert("Success", "File uploaded successfully.", AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to retrieve session data.", AlertType.ERROR);
            }
        } else {
            showAlert("Error", "No file selected.", AlertType.ERROR);
        }
    }

    public void handleDeleteFile(ActionEvent event) {
        String selectedFile = fileList.getSelectionModel().getSelectedItem();

        if (selectedFile == null) {
            showAlert("Error", "No file selected.", AlertType.ERROR);
            return;
        }

        String username = getCurrentUsernameFromSession();
        if (username != null) {
            deleteFileFromContainer(selectedFile, "roundrobin", username);
            files.remove(selectedFile);
            fileList.getItems().remove(selectedFile);
            showAlert("Success", "File deleted successfully.", AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to retrieve session data.", AlertType.ERROR);
        }
    }

    private void sendFileToContainer(File file, Request request, String loadBalancingStrategy, String username) {
        try {
            String targetUrl = getTargetStorageUrl(loadBalancingStrategy) + "/upload";

            HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("File-Name", file.getName());
            connection.setRequestProperty("Username", username);

            try (OutputStream outputStream = connection.getOutputStream();
                 FileInputStream fileInputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("File uploaded successfully to " + targetUrl);
            } else {
                showAlert("Error", "Failed to upload file. Server responded with: " + responseCode, AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Error", "Error sending file to storage: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void deleteFileFromContainer(String fileName, String loadBalancingStrategy, String username) {
        try {
            String targetUrl = getTargetStorageUrl(loadBalancingStrategy) + "/delete?file=" + fileName + "&username=" + username;

            HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).openConnection();
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("File deleted successfully from " + targetUrl);
            } else {
                showAlert("Error", "Failed to delete file. Server responded with: " + responseCode, AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Error", "Error deleting file from storage: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void listFilesFromContainer(String loadBalancingStrategy, String username) {
        try {
            String targetUrl = getTargetStorageUrl(loadBalancingStrategy) + "/list?username=" + username;

            HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        files.add(line);
                        fileList.getItems().add(line);
                    }
                }
            } else {
                showAlert("Error", "Failed to list files. Server responded with: " + responseCode, AlertType.ERROR);
            }
        } catch (IOException e) {
            showAlert("Error", "Error listing files from storage: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private String getCurrentUsernameFromSession() {
        String query = "SELECT username FROM sessions ORDER BY login_time DESC LIMIT 1";

        try (Connection connection = sqliteDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to retrieve session data: " + e.getMessage(), AlertType.ERROR);
        }

        return null;
    }

    private void setupSQLiteDataSource() {
        try {
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl("jdbc:sqlite:session_data.db");
            sqliteDataSource = dataSource;
            System.out.println("SQLite DataSource configured.");
        } catch (Exception e) {
            showAlert("Configuration Error", "Failed to configure SQLite DataSource: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private String getTargetStorageUrl(String loadBalancingStrategy) {
        switch (loadBalancingStrategy.toLowerCase()) {
            case "roundrobin":
                return getNextStorageUrl();
            default:
                throw new IllegalArgumentException("Unsupported load balancing strategy: " + loadBalancingStrategy);
        }
    }

    private String getNextStorageUrl() {
        String url = STORAGE_URLS[currentStorageIndex];
        currentStorageIndex = (currentStorageIndex + 1) % STORAGE_URLS.length;
        return url;
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

