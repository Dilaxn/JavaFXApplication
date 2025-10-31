package com.mycompany.javafxapplication1.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private DataSource dataSource;

    public void initialize() {
        setupDataSource();
    }

    private void setupDataSource() {
        try {
            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setURL("jdbc:mysql://localhost:3306/lb");
            mysqlDataSource.setUser("user");
            mysqlDataSource.setPassword("password");
            dataSource = mysqlDataSource;
        } catch (Exception e) {
            showAlert("Configuration Error", "Failed to configure the database connection: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.", AlertType.ERROR);
            return;
        }

        try (Connection connection = getConnection()) {
            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, "STANDARD"); // Default role
            statement.executeUpdate();

            showAlert("Success", "User registered successfully.", AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to register user: " + e.getMessage(), AlertType.ERROR);
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

