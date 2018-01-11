package es.antoniopavonrodriguez.pongfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 600, 400);
        scene.setFill(Color.BLACK);
        primaryStage.setTitle("League of Draven");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Circle circleBall = new Circle();
        circleBall.setCenterX(10);
        circleBall.setCenterY(30);
        circleBall.setRadius(7);
        root.getChildren().add(circleBall);
        circleBall.setFill(Color.WHITE);
    }
}
