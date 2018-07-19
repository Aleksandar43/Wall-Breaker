/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aleksandar43.wallbreaker.main;

import aleksandar43.wallbreaker.gui.MenuText;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Aleksandar
 */
public class WallBreaker extends Application {
    VBox root;
    @Override
    public void start(Stage primaryStage) {
        root=new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black;");
        root.getChildren().add(new MenuText("Pokreni igru"));
        root.getChildren().add(new MenuText("Opcije"));
        root.getChildren().add(new MenuText("Rang lista"));
        root.getChildren().add(new MenuText("O igri"));
        MenuText qm=new MenuText("Izađi");
        root.getChildren().add(qm);
        qm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("BOOM");
                Platform.exit();
            }
        });
        
        Scene scene = new Scene(root, 800, 450);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
