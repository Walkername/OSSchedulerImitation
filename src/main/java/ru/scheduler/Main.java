package ru.scheduler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("OS Scheduler Imitation");
        stage.setResizable(false);
        stage.setWidth(800);
        stage.setHeight(650);
        stage.setMinWidth(500);
        stage.setMinHeight(200);
        stage.show();

        stage.setOnCloseRequest(event -> {
            event.consume();
            logout(stage, loader);
        });
    }

    private void logout(Stage stage, FXMLLoader loader) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You're about to logout!");
        alert.setContentText("Are you sure you want to exit?");

        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            stage.close();
        }
    }
}