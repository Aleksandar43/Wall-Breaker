/* */
package aleksandar43.wallbreaker.main;

import aleksandar43.wallbreaker.game.Ball;
import aleksandar43.wallbreaker.game.Brick;
import aleksandar43.wallbreaker.game.Paddle;
import aleksandar43.wallbreaker.game.RectangleBrick;
import aleksandar43.wallbreaker.gui.HighScores;
import aleksandar43.wallbreaker.gui.MenuText;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 *
 * @author Aleksandar
 */
public class WallBreaker extends Application {
    
    private class GameAnimationTimer extends AnimationTimer{
        private long time;
        @Override
        public void handle(long now) {
            //change for debugging
            double deltaTime=Math.min((now-time)/1e9, 0.2);
            if (inGame && !paused) {
                if (stage.isFullScreen()) {
                    paddle.move(mouseX, FULLSCREEN_WIDTH, 0, WINDOW_WIDTH-gameStats.getPrefWidth());
                } else {
                    paddle.move(mouseX, WINDOW_WIDTH, 0, WINDOW_WIDTH-gameStats.getPrefWidth());
                }
                moveBall(firstBall, deltaTime, playground.getBoundsInLocal(), bricks);
            }
            time=now;
        }
    }
    
    private void moveBall(Ball b, double deltaTime, Bounds bounds, List<Brick> bricks){
        double movementX=b.getSpeedX()*deltaTime, movementY=b.getSpeedY()*deltaTime;
        double currentBallX=b.getTranslateX(), currentBallY=b.getTranslateY();
        boolean keepChecking=true, hitByPaddle=false, alreadyHitByPaddle=false;
        double minMovementX, minMovementY;
        double newAngle = 0;
        Brick brickToBeHitX=null, brickToBeHitY=null;
        while (keepChecking) {
            keepChecking=false;
            hitByPaddle=false;
            brickToBeHitX=null;
            brickToBeHitY=null;
            minMovementX = Math.abs(movementX);
            minMovementY = Math.abs(movementY);
            
            if(currentBallX+b.getRadius()+movementX>bounds.getMaxX()){
                double moveByX=bounds.getMaxX()-b.getRadius()-currentBallX;
                if(moveByX<minMovementX){
                    //hit is now guaranteed (but not by X axis)
                    keepChecking=true;
                    minMovementX=moveByX;
                }
            }
            if(currentBallX-b.getRadius()+movementX<bounds.getMinX()){
                double moveByX=currentBallX-(bounds.getMinX()+b.getRadius());
                if(moveByX<minMovementX){
                    keepChecking=true;
                    minMovementX=moveByX;
                }
            }
            if(currentBallY+b.getRadius()+movementY>bounds.getMaxY()){
                //the ball should be lost here, but here is test check
                double moveByY=bounds.getMaxY()-b.getRadius()-currentBallY;
                if(moveByY<minMovementY){
                    //hit is now guaranteed (but not by Y axis)
                    keepChecking=true;
                    minMovementY=moveByY;
                }
            }
            if(currentBallY-b.getRadius()+movementY<bounds.getMinY()){
                double moveByY=currentBallY-(bounds.getMinY()+b.getRadius());
                if(moveByY<minMovementY){
                    keepChecking=true;
                    minMovementY=moveByY;
                }
            }
            //checking hit with paddle, assuming it is circular
            if (!alreadyHitByPaddle) {
                if (b.getSpeedX()!=0) {
                    double k = b.getSpeedY() / b.getSpeedX();
                    double lc = -k * currentBallX + currentBallY;
                    double A = 1 + k * k;
                    double xp = paddle.getTranslateX();
                    double yp = paddle.getTranslateY();
                    double B = -2 * xp + 2 * k * lc - 2 * yp * k;
                    double R = paddle.getShape().getRadiusY() + paddle.getShape().getStrokeWidth() / 2 + b.getShape().getRadius();
                    double C = xp * xp + lc * lc - 2 * yp * lc + yp * yp - R * R;
                    double D = B * B - 4 * A * C;
                    if (D < 0) {
                        //no collision points, do nothing
                    } else if (D == 0) {
                        //one collision point
                        double xc = -B / (2 * A);
                        double yc = k * (xc - currentBallX) + currentBallY; //do I need this?
                        if (Math.abs(xc - currentBallX) < minMovementX) {
                            keepChecking = true;
                            minMovementX = Math.abs(xc - currentBallX);
                            hitByPaddle = true;
                            newAngle = (Math.atan2(yc - (paddle.getShape().getCenterY() + paddle.getTranslateY()) - 25, xc - (paddle.getShape().getCenterX() + paddle.getTranslateX())));
                        }
                    } else {
                        //two collision points
                        double xc1 = (-B + Math.sqrt(D)) / (2 * A);
                        double yc1 = k * (xc1 - currentBallX) + currentBallY;
                        if (Math.abs(xc1 - currentBallX) < minMovementX) {
                            keepChecking = true;
                            minMovementX = Math.abs(xc1 - currentBallX);
                            hitByPaddle = true;
                            newAngle = (Math.atan2(yc1 - (paddle.getShape().getCenterY() + paddle.getTranslateY()) - 25, xc1 - (paddle.getShape().getCenterX() + paddle.getTranslateX())));
                        }
                        double xc2 = (-B - Math.sqrt(D)) / (2 * A);
                        double yc2 = k * (xc2 - currentBallX) + currentBallY;
                        if (Math.abs(xc2 - currentBallX) < minMovementX) {
                            keepChecking = true;
                            minMovementX = Math.abs(xc2 - currentBallX);
                            hitByPaddle = true;
                            newAngle = (Math.atan2(yc2 - (paddle.getShape().getCenterY() + paddle.getTranslateY()) - 25, xc2 - (paddle.getShape().getCenterX() + paddle.getTranslateX())));
                        }
                    }
                } else {
                    double R = paddle.getShape().getRadiusY() + paddle.getShape().getStrokeWidth() / 2 + b.getShape().getRadius();
                    double xp = paddle.getTranslateX();
                    double yp = paddle.getTranslateY();
                    double c=R*R-(currentBallX-xp)*(currentBallX-xp);
                    double A=1;
                    double B=-2*yp;
                    double C=yp*yp-c;
                    double D = B * B - 4 * A * C;
                    if (D < 0) {
                        //no collision points, do nothing
                    } else if (D == 0) {
                        //one collision point
                        double yc = -B / (2 * A);
                        if (Math.abs(yc - currentBallY) < minMovementY) {
                            keepChecking = true;
                            minMovementY = Math.abs(yc - currentBallY);
                            hitByPaddle = true;
                            newAngle = (Math.atan2(yc - (paddle.getShape().getCenterY() + paddle.getTranslateY()) - 25, currentBallX - (paddle.getShape().getCenterX() + paddle.getTranslateX())));
                        }
                    } else {
                        //two collision points
                        double yc1 = (-B + Math.sqrt(D)) / (2 * A);
                        if (Math.abs(yc1 - currentBallY) < minMovementY) {
                            keepChecking = true;
                            minMovementY = Math.abs(yc1 - currentBallY);
                            hitByPaddle = true;
                            newAngle = (Math.atan2(yc1 - (paddle.getShape().getCenterY() + paddle.getTranslateY()) - 25, currentBallX - (paddle.getShape().getCenterX() + paddle.getTranslateX())));
                        }
                        double yc2 = (-B - Math.sqrt(D)) / (2 * A);
                        if (Math.abs(yc2 - currentBallY) < minMovementY) {
                            keepChecking = true;
                            minMovementY = Math.abs(yc2 - currentBallY);
                            hitByPaddle = true;
                            newAngle = (Math.atan2(yc2 - (paddle.getShape().getCenterY() + paddle.getTranslateY()) - 25, currentBallX - (paddle.getShape().getCenterX() + paddle.getTranslateX())));
                        }
                    }
                }
            }
            
            for(Brick brick:bricks){
                Bounds brickBounds=brick.getBoundsInParent();
                if(currentBallX+b.getRadius()+movementX>brickBounds.getMinX()){
                    double moveByX=(brickBounds.getMinX()-b.getRadius())-currentBallX;
                    double moveByY=moveByX*movementY/movementX;
                    if(moveByX>0 && moveByX<minMovementX && currentBallY+moveByY>=brickBounds.getMinY() && currentBallY+b.getRadius()+moveByY<=brickBounds.getMaxY()){
                        keepChecking=true;
                        minMovementX=moveByX;
                        brickToBeHitX=brick;
                    }
                }
                if(currentBallX-b.getRadius()+movementX<brickBounds.getMaxX()){
                    double moveByX=currentBallX-(brickBounds.getMaxX()+b.getRadius());
                    double moveByY=moveByX*movementY/movementX;
                    if(moveByX>0 && moveByX<minMovementX && currentBallY+moveByY>=brickBounds.getMinY() && currentBallY-b.getRadius()+moveByY<=brickBounds.getMaxY()){
                        keepChecking=true;
                        minMovementX=moveByX;
                        brickToBeHitX=brick;
                    }
                }
                if(currentBallY+b.getRadius()+movementY>brickBounds.getMinY()){
                    double moveByY=(brickBounds.getMinY()-b.getRadius())-currentBallY;
                    double moveByX=moveByY*movementX/movementY;
                    if(moveByY>0 && moveByY<minMovementY && currentBallX+moveByX>=brickBounds.getMinX() && currentBallX+b.getRadius()+moveByX<=brickBounds.getMaxX()){
                        keepChecking=true;
                        minMovementY=moveByY;
                        brickToBeHitY=brick;
                    }
                }
                if(currentBallY-b.getRadius()+movementY<brickBounds.getMaxY()){
                    double moveByY=currentBallY-(brickBounds.getMaxY()+b.getRadius());
                    double moveByX=moveByY*movementX/movementY;
                    if(moveByY>0 && moveByY<minMovementY && currentBallX+moveByX>=brickBounds.getMinX() && currentBallX-b.getRadius()+moveByX<=brickBounds.getMaxX()){
                        keepChecking=true;
                        minMovementY=moveByY;
                        brickToBeHitY=brick;
                    }
                }
            }
            
            //if hit guaranteed, update ball position and speed
            if(keepChecking){
                double ratioX = movementX!=0 ? minMovementX/Math.abs(movementX) : Double.MAX_VALUE;
                double ratioY = movementY!=0 ? minMovementY/Math.abs(movementY) : Double.MAX_VALUE;
                if(ratioX==0)
                    System.out.println("ratioX=0");
                if(ratioY==0)
                    System.out.println("ratioY=0");
                if(ratioX < ratioY){
                    currentBallX+=movementX*ratioX;
                    currentBallY+=movementY*ratioX;
                    movementX=movementX-movementX*ratioX;
                    movementY=movementY-movementY*ratioX;
                    if(brickToBeHitX!=null){
                        movementX=-movementX;
                        b.setSpeedX(-b.getSpeedX());
                        brickToBeHitX.onHit();
                        bricks.remove(brickToBeHitX);
                        playground.getChildren().remove(brickToBeHitX);
                    } else if (!hitByPaddle) {
                        movementX=-movementX;
                        b.setSpeedX(-b.getSpeedX());
                    } else {
                        b.setAngle(newAngle);
                        movementX=Math.hypot(movementX, movementY)*Math.cos(newAngle);
                        movementY=Math.hypot(movementX, movementY)*Math.sin(newAngle);
                        alreadyHitByPaddle=true;
                    }
                } else{
                    currentBallX+=movementX*ratioY;
                    currentBallY+=movementY*ratioY;
                    movementX=movementX-movementX*ratioY;
                    movementY=movementY-movementY*ratioY;
                    if(brickToBeHitY!=null){
                        movementY=-movementY;
                        b.setSpeedY(-b.getSpeedY());
                        brickToBeHitY.onHit();
                        bricks.remove(brickToBeHitY);
                        playground.getChildren().remove(brickToBeHitY);
                    } else if (!hitByPaddle) {
                        movementY=-movementY;
                        b.setSpeedY(-b.getSpeedY());
                    } else {
                        b.setAngle(newAngle);
                        movementX=Math.hypot(movementX, movementY)*Math.cos(newAngle);
                        movementY=Math.hypot(movementX, movementY)*Math.sin(newAngle);
                    }
                }
            }
        }
        //end of hits
        currentBallX+=movementX;
        currentBallY+=movementY;
        b.setTranslateX(currentBallX);
        b.setTranslateY(currentBallY);        
    }
    
    public static double WINDOW_WIDTH=800;
    public static double WINDOW_HEIGHT=450;
    public static double FULLSCREEN_WIDTH=Screen.getPrimary().getBounds().getWidth();
    public static double FULLSCREEN_HEIGHT=Screen.getPrimary().getBounds().getHeight();
    private VBox mainMenu, optionsMenu, gameStats, pauseMenu;
    private BorderPane aboutMenu, highScoresMenu, gamePane;
    private Group gameGroup;
    private Group playground;
    private boolean musicOn=true, soundEffectsOn=true, inGame=false, paused=false;
    private Group menusStackPane;
    private Paddle paddle;
    private Ball firstBall;
    private GameAnimationTimer gameAnimationTimer;
    private double mouseX, mouseY;
    private Stage stage;
    private List<Brick> bricks;
    @Override
    public void start(Stage primaryStage) {
        stage=primaryStage;
        gameAnimationTimer=new GameAnimationTimer();
        
        makeOptionsMenu(primaryStage);
        makeAboutMenu();
        makeHighScoresMenu();
        makePauseMenu();
        
        //make gameStats a Group
        gameStats=new VBox();
        gameStats.setAlignment(Pos.TOP_RIGHT);
        gameStats.setPrefWidth(250);
        gameStats.setPrefHeight(WINDOW_HEIGHT);
        gameStats.setStyle("-fx-background-color: pink; -fx-border-width: 5; -fx-border-color: white");
        gameStats.getChildren().add(new Text("Poeni"));
        gameStats.getChildren().add(new Text("0"));
        gameStats.getChildren().add(new Text("Vreme"));
        gameStats.getChildren().add(new Text("0:00:00"));
        gameStats.getChildren().add(new Text("Životi"));
        gameStats.getChildren().add(new Text("0"));
        MenuText pause = new MenuText("Pauza");
        pause.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                paused=true;
                pauseMenu.toFront();
            }
        });
        gameStats.getChildren().add(pause);
        
        playground=new Group();
        Rectangle temp=new Rectangle(0, 0, WINDOW_WIDTH-gameStats.getPrefWidth(), WINDOW_HEIGHT);
        temp.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(1, Color.BLUE)));
        playground.getChildren().add(temp);
        paddle=new Paddle();
        paddle.setTranslateY(WINDOW_HEIGHT);
        paddle.setTranslateX(WINDOW_WIDTH/2);
        //wrap playground and paddle into a new group
        //playground.getChildren().add(paddle);
        bricks=new ArrayList<>();
        for(int i=0;i<20;i++){
            Brick b=new RectangleBrick(Math.random()*500, Math.random()*300, 30, 15, Color.RED);
            bricks.add(b);
            playground.getChildren().add(b);
        }
        
        gamePane=new BorderPane();
        gamePane.setMaxWidth(WINDOW_WIDTH);
        gamePane.setMaxHeight(WINDOW_HEIGHT);
        gamePane.setStyle("-fx-background-color: brown;");
        gamePane.setRight(gameStats);
        gamePane.setCenter(playground);
        
        gameGroup=new Group();
        gameGroup.getChildren().addAll(playground, paddle, gameStats);
        gameStats.setTranslateX(WINDOW_WIDTH-gameStats.getPrefWidth());
        firstBall=new Ball(10, Color.YELLOW);
        firstBall.setTranslateX(60);
        firstBall.setTranslateY(300);
        firstBall.setSpeedX(0);
        firstBall.setSpeedY(200);
        playground.getChildren().add(firstBall);
        
        makeMainMenu();
        
        menusStackPane = new Group();
        menusStackPane.getChildren().addAll(optionsMenu,aboutMenu,highScoresMenu,gameGroup,pauseMenu,mainMenu);
        Scene scene = new Scene(menusStackPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseX=event.getSceneX();
                mouseY=event.getSceneY();
            }
        });
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(inGame && event.getCode().equals(KeyCode.ESCAPE) && !paused){
                    System.out.println("ESC key pressed");
                    System.out.println(gameGroup.getBoundsInParent());
                    System.out.println(pauseMenu.getBoundsInParent());
                    System.out.println(mainMenu.getBoundsInParent());
                    paused=true;
                    pauseMenu.toFront();
                }
            }
        });
        
        gameAnimationTimer.start();
        primaryStage.setTitle("WallBreaker");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene(); //because setResizable(false) enlarges the stage for some reason
        primaryStage.setFullScreenExitHint(null);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.show();
        
        System.out.println("Dimensions: "+scene.getWidth()+", "+scene.getHeight());
        System.out.println("Highscores: "+HighScores.getHighScores());
        System.out.println("Paddle bounds: "+paddle.getBoundsInParent());
        System.out.println("Ball stroke width : "+firstBall.getShape().getStrokeWidth());
        System.out.println("Paddle stroke width : "+paddle.getShape().getStrokeWidth());
    }

    private void makeOptionsMenu(Stage primaryStage) {
        optionsMenu=new VBox();
        optionsMenu.setAlignment(Pos.CENTER);
        optionsMenu.setMaxWidth(WINDOW_WIDTH);
        optionsMenu.setMaxHeight(WINDOW_HEIGHT);
        optionsMenu.setPrefWidth(WINDOW_WIDTH);
        optionsMenu.setPrefHeight(WINDOW_HEIGHT);
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
        MenuText fullscreenToggle=new MenuText("Pređi na ceo ekran".toUpperCase());
        fullscreenToggle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(primaryStage.isFullScreen()){
                    primaryStage.setFullScreen(false);
                    fullscreenToggle.setText("Pređi na ceo ekran".toUpperCase());
                    menusStackPane.getTransforms().clear();
                } else{
                    primaryStage.setFullScreen(true);
                    fullscreenToggle.setText("Pređi na prozor".toUpperCase());
                    menusStackPane.getTransforms().addAll(
                            new Scale(FULLSCREEN_WIDTH/WINDOW_WIDTH, FULLSCREEN_HEIGHT/WINDOW_HEIGHT, 0, 0)
                    );
                }
            }
        });
        MenuText backOptions=new MenuText("Nazad");
        backOptions.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(paused) {
                    gameGroup.toFront(); //now PauseMenu is always in front of the game, maybe these could be in their own Group
                    pauseMenu.toFront();
                }
                else mainMenu.toFront();
            }
        });
        optionsMenu.getChildren().addAll(musicToggle,soundEffectsToggle,fullscreenToggle,backOptions);
    }

    private void makeAboutMenu() {
        aboutMenu=new BorderPane();
        aboutMenu.setStyle("-fx-background-color: black; -fx-border-width: 1; -fx-border-color: grey");
        aboutMenu.setMaxWidth(WINDOW_WIDTH);
        aboutMenu.setMaxHeight(WINDOW_HEIGHT);
        aboutMenu.setPrefWidth(WINDOW_WIDTH);
        aboutMenu.setPrefHeight(WINDOW_HEIGHT);
        MenuText backToMainAbout=new MenuText("Nazad");
        backToMainAbout.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mainMenu.toFront();
            }
        });
        aboutMenu.setBottom(backToMainAbout);
        BorderPane.setAlignment(backToMainAbout, Pos.CENTER);
    }

    private void makeHighScoresMenu() {
        highScoresMenu=new BorderPane();
        highScoresMenu.setMaxWidth(WINDOW_WIDTH);
        highScoresMenu.setMaxHeight(WINDOW_HEIGHT);
        highScoresMenu.setPrefWidth(WINDOW_WIDTH);
        highScoresMenu.setPrefHeight(WINDOW_HEIGHT);
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
        VBox scores=new VBox();
        List<Pair<String, Integer>> highScores = HighScores.getHighScores();
        for(Pair<String, Integer> p:highScores){
            scores.getChildren().add(new MenuText(p.getKey()+" "+p.getValue()));
        }
        highScoresMenu.setCenter(scores);
    }

    private void makePauseMenu() {
        pauseMenu=new VBox();
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setMaxWidth(WINDOW_WIDTH);
        pauseMenu.setMaxHeight(WINDOW_HEIGHT);
        pauseMenu.setPrefWidth(WINDOW_WIDTH);
        pauseMenu.setPrefHeight(WINDOW_HEIGHT);
        pauseMenu.setStyle("-fx-background-color: rgba(0,0,255,0.5); -fx-border-width: 5; -fx-border-color: white");
        MenuText backtoGame = new MenuText("Nastavi");
        backtoGame.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                paused=false;
                gameGroup.toFront();
            }
        });
        pauseMenu.getChildren().add(backtoGame);
        MenuText goToOptionsPause = new MenuText("Opcije");
        goToOptionsPause.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                optionsMenu.toFront();
            }
        });
        pauseMenu.getChildren().add(goToOptionsPause);
        MenuText exitToMain = new MenuText("Izađi");
        exitToMain.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                inGame=false;
                paused=false;
                mainMenu.toFront();
            }
        });
        pauseMenu.getChildren().add(exitToMain);
    }

    private void makeMainMenu() {
        mainMenu=new VBox();
        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setMaxWidth(WINDOW_WIDTH);
        mainMenu.setMaxHeight(WINDOW_HEIGHT);
        mainMenu.setPrefWidth(WINDOW_WIDTH);
        mainMenu.setPrefHeight(WINDOW_HEIGHT);
        mainMenu.setStyle("-fx-background-color: darkblue; -fx-border-width: 5; -fx-border-color: yellow");
        MenuText startGame = new MenuText("Pokreni igru");
        startGame.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                inGame=true;
                //gamePane.toFront();
                gameGroup.toFront();
            }
        });
        mainMenu.getChildren().add(startGame);
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
                //temporary, should be saved after a game
                HighScores.saveHighScores();
                Platform.exit();
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
