package com.mycompany.javafxapplication1.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminController {

    @FXML
    private ListView<String> userList;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    private Connection connection;

    public void initialize() {
        connectToDatabase();
        loadUsersFromDatabase();
    }

    private void connectToDatabase() {
        try {
            // Update with your Docker-based database credentials
            String url = "jdbc:mysql://localhost:3306/cloud_load_balancer";
            String user = "root";
            String password = "root"; // Matches MYSQL_ROOT_PASSWORD in Docker Compose

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to connect to the database: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void loadUsersFromDatabase() {
        try {
            if (connection != null) {
                String query = "SELECT username FROM users";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    userList.getItems().add(username);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load users: " + e.getMessage(), AlertType.ERROR);
        }
    }

    public void handleAddUser(ActionEvent event) {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            showAlert("Error", "Username cannot be empty.", AlertType.ERROR);
            return;
        }

        try {
            if (connection != null) {
                String query = "INSERT INTO users (username,password) VALUES (?,?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, passwordField.getText());
                statement.executeUpdate();

                userList.getItems().add(username);
                usernameField.clear();
                showAlert("Success", "User added successfully.", AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add user: " + e.getMessage(), AlertType.ERROR);
        }
    }

    public void handleDeleteUser(ActionEvent event) {
        String selectedUser = userList.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "No user selected.", AlertType.ERROR);
            return;
        }

        try {
            if (connection != null) {
                String query = "DELETE FROM users WHERE username = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, selectedUser);
                statement.executeUpdate();

                userList.getItems().remove(selectedUser);
                showAlert("Success", "User deleted successfully.", AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to delete user: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

