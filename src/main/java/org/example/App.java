package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override // переопределили start
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("Layout hw1.fxml")); // Создаем форму из дизайна, что сделали в SceneBuilder
        primaryStage.setScene(new Scene(parent)); // Передали внутрь сцены(запустили)
        primaryStage.show(); // Сделали видимым
    }
}
