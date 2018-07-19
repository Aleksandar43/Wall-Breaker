/* */
package aleksandar43.wallbreaker.main;

import aleksandar43.wallbreaker.gui.MenuText;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author Aleksandar
 */
public class WallBreaker extends Application {
    public static double WINDOW_WIDTH=800;
    public static double WINDOW_HEIGHT=450;
    public static double FULLSCREEN_WIDTH=Screen.getPrimary().getBounds().getWidth();
    public static double FULLSCREEN_HEIGHT=Screen.getPrimary().getBounds().getHeight();
    private VBox mainMenu, optionsMenu;
    private BorderPane aboutMenu, highScoresMenu;
    private boolean musicOn=true, soundEffectsOn=true;
    private StackPane menusStackPane;
    @Override
    public void start(Stage primaryStage) {
        optionsMenu=new VBox();
        optionsMenu.setAlignment(Pos.CENTER);
        optionsMenu.setMaxWidth(WINDOW_WIDTH);
        optionsMenu.setMaxHeight(WINDOW_HEIGHT);
        optionsMenu.setStyle("-fx-background-color: black;-fx-border-color: red; -fx-border-width: 5");
        MenuText musicToggle=new MenuText("Muzika: uklj.");
        musicToggle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(musicOn){
                    musicToggle.setText("Muzika: isklj.");
                    //turn music off
                } else{
                    musicToggle.setText("Muzika: uklj.");
                    //turn music on
                }
                musicOn=!musicOn;
            }
        });
        MenuText soundEffectsToggle=new MenuText("Efekti: uklj.");
        soundEffectsToggle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(soundEffectsOn){
                    soundEffectsToggle.setText("Efekti: isklj.");
                    //turn soundEffects off
                } else{
                    soundEffectsToggle.setText("Efekti: uklj.");
                    //turn soundEffects on
                }
                soundEffectsOn=!soundEffectsOn;
            }
        });
        MenuText fullscreenToggle=new MenuText("Pređi na ceo ekran");
        fullscreenToggle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(primaryStage.isFullScreen()){
                    primaryStage.setFullScreen(false);
                    fullscreenToggle.setText("Pređi na ceo ekran");
                    menusStackPane.getTransforms().clear();
                } else{
                    primaryStage.setFullScreen(true);
                    fullscreenToggle.setText("Pređi na prozor");
                    menusStackPane.getTransforms().addAll(
                            new Scale(FULLSCREEN_WIDTH/WINDOW_WIDTH, FULLSCREEN_HEIGHT/WINDOW_HEIGHT, FULLSCREEN_WIDTH/2, FULLSCREEN_HEIGHT/2)
                    );
                }
            }
        });
        MenuText backToMainOptions=new MenuText("Nazad");
        backToMainOptions.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mainMenu.toFront();
            }
        });
        optionsMenu.getChildren().addAll(musicToggle,soundEffectsToggle,fullscreenToggle,backToMainOptions);
        
        aboutMenu=new BorderPane();
        aboutMenu.setStyle("-fx-background-color: black; -fx-border-width: 1; -fx-border-color: grey");
        aboutMenu.setMaxWidth(WINDOW_WIDTH);
        aboutMenu.setMaxHeight(WINDOW_HEIGHT);
        MenuText backToMainAbout=new MenuText("Nazad");
        backToMainAbout.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mainMenu.toFront();
            }
        });
        aboutMenu.setBottom(backToMainAbout);
        BorderPane.setAlignment(backToMainAbout, Pos.CENTER);
        
        highScoresMenu=new BorderPane();
        highScoresMenu.setMaxWidth(WINDOW_WIDTH);
        highScoresMenu.setMaxHeight(WINDOW_HEIGHT);
        highScoresMenu.setStyle("-fx-background-color: darkgreen; -fx-border-width: 5; -fx-border-color: yellow");
        MenuText backToMainHighScores=new MenuText("Nazad");
        backToMainHighScores.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mainMenu.toFront();
            }
        });
        highScoresMenu.setBottom(backToMainHighScores);
        BorderPane.setAlignment(backToMainHighScores, Pos.CENTER);
        
        mainMenu=new VBox();
        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setMaxWidth(WINDOW_WIDTH);
        mainMenu.setMaxHeight(WINDOW_HEIGHT);
        mainMenu.setStyle("-fx-background-color: darkblue; -fx-border-width: 5; -fx-border-color: yellow");
        mainMenu.getChildren().add(new MenuText("Pokreni igru"));
        MenuText goToOptions = new MenuText("Opcije");
        goToOptions.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                optionsMenu.toFront();
            }
        });
        mainMenu.getChildren().add(goToOptions);
        MenuText goToHighScores = new MenuText("Rang lista");
        goToHighScores.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                highScoresMenu.toFront();
            }
        });
        mainMenu.getChildren().add(goToHighScores);
        MenuText goToAbout = new MenuText("O igri");
        goToAbout.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                aboutMenu.toFront();
            }
        });
        mainMenu.getChildren().add(goToAbout);
        MenuText qm=new MenuText("Izađi");
        mainMenu.getChildren().add(qm);
        qm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("BOOM");
                Platform.exit();
            }
        });
        
        menusStackPane = new StackPane();
        menusStackPane.getChildren().addAll(optionsMenu,aboutMenu,highScoresMenu,mainMenu);
        Scene scene = new Scene(menusStackPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        primaryStage.setTitle("WallBreaker");
        primaryStage.setScene(scene);
        primaryStage.setFullScreenExitHint(null);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
