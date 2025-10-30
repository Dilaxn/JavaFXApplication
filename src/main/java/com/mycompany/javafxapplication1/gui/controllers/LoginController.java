package com.mycompany.javafxapplication1.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.sqlite.SQLiteDataSource;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private DataSource mysqlDataSource;
    private DataSource sqliteDataSource;

    public void initialize() {
        setupMySQLDataSource();
        setupSQLiteDataSource();
        initializeSQLiteDatabase();
    }

    private void setupMySQLDataSource() {
        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setURL("jdbc:mysql://localhost:3306/lb");
            dataSource.setUser("user");
            dataSource.setPassword("password");
            mysqlDataSource = dataSource;
            System.out.println("MySQL DataSource configured.");
        } catch (Exception e) {
            showAlert("Configuration Error", "Failed to configure MySQL DataSource: " + e.getMessage(), AlertType.ERROR);
        }
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

    private void initializeSQLiteDatabase() {
        String createTableQuery = """
                CREATE TABLE IF NOT EXISTS sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL,
                    role TEXT NOT NULL,
                    login_time DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                """;

        try (Connection connection = sqliteDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(createTableQuery)) {
            statement.executeUpdate();
            System.out.println("SQLite session database initialized.");
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to initialize SQLite database: " + e.getMessage(), AlertType.ERROR);
        }
    }

    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.", AlertType.ERROR);
            return;
        }

        String query = "SELECT role FROM users WHERE username = ? AND password = ?";

        try (Connection connection = mysqlDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");

                // Save session data to SQLite
                saveSession(username, role);

                if (role.equals("ADMIN")) {
                    navigateToView("/views/AdminView.fxml", "Admin Panel");
                } else {
                    navigateToView("/views/DashboardView.fxml", "Dashboard");
                }
            } else {
                showAlert("Error", "Invalid username or password.", AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to login: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void saveSession(String username, String role) {
        String query = "INSERT INTO sessions (username, role) VALUES (?, ?)";

        try (Connection connection = sqliteDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, role);
            statement.executeUpdate();
            System.out.println("Session saved for user: " + username);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save session: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void navigateToView(String viewPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load view: " + e.getMessage(), AlertType.ERROR);
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

