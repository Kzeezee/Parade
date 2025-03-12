package com.g2t2.app;

import com.g2t2.game.*;

// import javafx.application.Application;
// import javafx.scene.Scene;
// import javafx.scene.control.Label;
// import javafx.scene.layout.StackPane;
// import javafx.stage.Stage;

// Uncomment this if we are doing GUI
// public class App extends Application {

//     @Override
//     public void start(Stage stage) {
//         String javaVersion = System.getProperty("java.version");
//         String javafxVersion = System.getProperty("javafx.version");
//         Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
//         Scene scene = new Scene(new StackPane(l), 640, 480);
//         stage.setScene(scene);
//         stage.show();
//     }

//     public static void main(String[] args) {
//         launch();
//     }

// }

/**
 * Main function in a console application
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello world! This is G2T2 Parade!");
        StartMenu.showStartMenu();
        GamePlay.startGame();
    }
}