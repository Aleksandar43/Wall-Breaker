/* */
package aleksandar43.wallbreaker.main;

import aleksandar43.wallbreaker.game.Ball;
import aleksandar43.wallbreaker.game.Brick;
import aleksandar43.wallbreaker.game.Level;
import aleksandar43.wallbreaker.game.Paddle;
import aleksandar43.wallbreaker.game.RectangleBrick;
import aleksandar43.wallbreaker.gui.HighScores;
import aleksandar43.wallbreaker.gui.MenuText;
import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
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
import javafx.scene.shape.Shape;
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
            newMouseX=MouseInfo.getPointerInfo().getLocation().x;
            newMouseY=MouseInfo.getPointerInfo().getLocation().y;
            int mx=newMouseX-oldMouseX;
            int my=newMouseY-oldMouseY;
            if(oldMouseX+mx<stage.getX()){
                newMouseX=(int)stage.getX();
            }
            if(oldMouseX+mx>stage.getX()+stage.getWidth()){
                newMouseX=(int)(stage.getX()+stage.getWidth());
            }
            if(oldMouseY+my<stage.getY()){
                newMouseY=(int)stage.getY();
            }
            if(oldMouseY+my>stage.getY()+stage.getHeight()){
                newMouseY=(int)(stage.getY()+stage.getHeight());
            }
            if (inGame && !paused && robot!=null) robot.mouseMove(newMouseX, newMouseY);
            oldMouseX=newMouseX;
            oldMouseY=newMouseY;
            //0.2 is for debugging
            double deltaTime=Math.min((now-time)/1e9, 0.2);
            if(inGame && !paused) {
                if(stage.isFullScreen()) {
                    paddle.move(newMouseX-stage.getX(), FULLSCREEN_WIDTH, 0, WINDOW_WIDTH-gameStats.getPrefWidth());
                } else {
                    paddle.move(newMouseX-stage.getX(), WINDOW_WIDTH, 0, WINDOW_WIDTH-gameStats.getPrefWidth());
                }
                moveBall(firstBall, deltaTime, playground.getBoundsInLocal(), bricks);
                levelTime+=now-time;
                long seconds=(long) (levelTime/1e9);
                long minutes=seconds/60;
                seconds=seconds%60;
                long hundreds=(long) (levelTime/1e7) % 100;
                String s=minutes+":";
                if(seconds<=9) s+="0"+seconds;
                else s+=seconds;
                s+=".";
                if(hundreds<=9) s+="0"+hundreds;
                else s+=hundreds;
                levelTimeText.setText(s);
                pointsText.setText(Integer.toString(points));
                livesText.setText(Integer.toString(lives));
                if(bricks.size()==0) showLevelResults();
            }
            time=now;
        }
    }
    
    private void moveBall(Ball b, double deltaTime, Bounds bounds, List<Brick> bricks){
        if (ballLaunched) {
            double movementX=b.getSpeedX()*deltaTime, movementY=b.getSpeedY()*deltaTime;
            double currentBallX=b.getTranslateX(), currentBallY=b.getTranslateY();
            boolean hitByPaddle=false;
            double newAngle = 0, newX = 0, newY = 0;
            
            //checking hit with paddle, assuming it is circular
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
                    double yc = k * (xc - currentBallX) + currentBallY;
                    if (Math.abs(xc - currentBallX) < Math.abs(movementX) && paddle.isInHittableRange(xc,yc)) {
                        hitByPaddle = true;
                        movementX=Math.signum(movementX)*(Math.abs(movementX)-Math.abs(xc - currentBallX));
                        movementY=Math.signum(movementY)*(Math.abs(movementY)-Math.abs(yc - currentBallY));
                        newAngle = (Math.atan2(yc - (paddle.getShape().getCenterY() + paddle.getTranslateY()) - 25, xc - (paddle.getShape().getCenterX() + paddle.getTranslateX())));
                    }
                } else {
                    //two collision points
                    double xc1 = (-B + Math.sqrt(D)) / (2 * A);
                    double yc1 = k * (xc1 - currentBallX) + currentBallY;
                    if (Math.abs(xc1 - currentBallX) < Math.abs(movementX) && paddle.isInHittableRange(xc1,yc1)) {
                        hitByPaddle = true;
                        movementX=Math.signum(movementX)*(Math.abs(movementX)-Math.abs(xc1 - currentBallX));
                        movementY=Math.signum(movementY)*(Math.abs(movementY)-Math.abs(yc1 - currentBallY));
                        newAngle = (Math.atan2(yc1 - (paddle.getShape().getCenterY() + paddle.getTranslateY()) - 25, xc1 - (paddle.getShape().getCenterX() + paddle.getTranslateX())));
                    }
                    double xc2 = (-B - Math.sqrt(D)) / (2 * A);
                    double yc2 = k * (xc2 - currentBallX) + currentBallY;
                    if (Math.abs(xc2 - currentBallX) < Math.abs(movementX) && paddle.isInHittableRange(xc2,yc2)) {
                        hitByPaddle = true;
                        movementX=Math.signum(movementX)*(Math.abs(movementX)-Math.abs(xc2 - currentBallX));
                        movementY=Math.signum(movementY)*(Math.abs(movementY)-Math.abs(yc2 - currentBallY));
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
                    if (Math.abs(yc - currentBallY) < Math.abs(movementY) && paddle.isInHittableRange(b.getTranslateX(),yc)) {
                        hitByPaddle = true;
                        //movementX==0
                        movementY=Math.signum(movementY)*(Math.abs(movementY)-Math.abs(yc - currentBallY));
                        newAngle = (Math.atan2(yc - (paddle.getShape().getCenterY() + paddle.getTranslateY()) - 25, currentBallX - (paddle.getShape().getCenterX() + paddle.getTranslateX())));
                    }
                } else {
                    //two collision points
                    double yc1 = (-B + Math.sqrt(D)) / (2 * A);
                    if (Math.abs(yc1 - currentBallY) < Math.abs(movementY) && paddle.isInHittableRange(b.getTranslateX(),yc1)) {
                        hitByPaddle = true;
                        //movementX==0
                        movementY=Math.signum(movementY)*(Math.abs(movementY)-Math.abs(yc1 - currentBallY));
                        newAngle = (Math.atan2(yc1 - (paddle.getShape().getCenterY() + paddle.getTranslateY()) - 25, currentBallX - (paddle.getShape().getCenterX() + paddle.getTranslateX())));
                    }
                    double yc2 = (-B - Math.sqrt(D)) / (2 * A);
                    if (Math.abs(yc2 - currentBallY) < Math.abs(movementY) && paddle.isInHittableRange(b.getTranslateX(),yc2)) {
                        hitByPaddle = true;
                        //movementX==0
                        movementY=Math.signum(movementY)*(Math.abs(movementY)-Math.abs(yc2 - currentBallY));
                        newAngle = (Math.atan2(yc2 - (paddle.getShape().getCenterY() + paddle.getTranslateY()) - 25, currentBallX - (paddle.getShape().getCenterX() + paddle.getTranslateX())));
                    }
                }
            }
            if(hitByPaddle){
                b.setAngle(newAngle);
            }
            
            //for the remaining movement, check if it hits walls or bricks
            newX=b.getTranslateX()+movementX;
            newY=b.getTranslateY()+movementY;
            boolean negateSpeedX=false, negateSpeedY=false;
            if(newX+b.getRadius()>bounds.getMaxX()){
                b.setSpeedX(-b.getSpeedX());
                newX=newX-((newX+b.getRadius())-bounds.getMaxX());
            }
            if(newX-b.getRadius()<bounds.getMinX()){
                b.setSpeedX(-b.getSpeedX());
                newX=newX+(bounds.getMinX()-(newX-b.getRadius()));
            }
            if(newY+b.getRadius()>bounds.getMaxY()){
                //the ball is lost
                lives--;
                //also play lost ball sound
                if(lives==0){
                    inGame=false;
                    gameFinished();
                    return;
                }
                ballLaunched=false;
                return;
            }
            if(newY-b.getRadius()<bounds.getMinY()){
                b.setSpeedY(-b.getSpeedY());
                newY=newY+(bounds.getMinY()-(newY-b.getRadius()));
            }
            for(Iterator<Brick> it=bricks.iterator(); it.hasNext();){
                Brick brick=it.next();
                Shape intersection=Shape.intersect(b.getShape(), brick.getShape());
                if(intersection.getBoundsInLocal().getWidth() != -1){
                    it.remove();
                    playground.getChildren().remove(brick);
                    points+=5;
                    Bounds lBounds=intersection.getBoundsInLocal();
                    if (lBounds.getWidth() < lBounds.getHeight()) {
                        negateSpeedX = true;
                        if(b.getSpeedX()>0){
                            newX=(2*(lBounds.getMinX()-b.getShape().getRadius())-newX);
                        } else {
                            newX=(2*(lBounds.getMaxX()+b.getShape().getRadius())-newX);
                        }
                    } else {
                        negateSpeedY = true;
                        if(b.getSpeedY()>0){
                            newY=(2*(lBounds.getMinY()-b.getShape().getRadius())-newY);
                        } else {
                            newY=(2*(lBounds.getMaxY()+b.getShape().getRadius())-newY);
                        }
                    }
                }
            }

            if(negateSpeedX) b.setSpeedX(-b.getSpeedX());
            if(negateSpeedY) b.setSpeedY(-b.getSpeedY());
            b.setTranslateX(newX);
            b.setTranslateY(newY);
        }
        else{
            b.setTranslateX(paddle.getTranslateX()+launchRadius*Math.cos(launchAngle));
            b.setTranslateY(paddle.getTranslateY()+launchRadius*Math.sin(launchAngle));
        }
    }
    
    public static double WINDOW_WIDTH=800;
    public static double WINDOW_HEIGHT=450;
    public static double FULLSCREEN_WIDTH=Screen.getPrimary().getBounds().getWidth();
    public static double FULLSCREEN_HEIGHT=Screen.getPrimary().getBounds().getHeight();
    private static double launchRadius=55+10;
    private static double launchAngle=Math.toRadians(-60);
    private VBox mainMenu, optionsMenu, gameStats, pauseMenu, resultsMenu, enterHighScoreMenu;
    private BorderPane aboutMenu, highScoresMenu;
    private Group gameGroup;
    private Group playground;
    private boolean musicOn=true, soundEffectsOn=true, inGame=false, paused=false, ballLaunched=false;
    private Group menusStackPane;
    private Paddle paddle;
    private Ball firstBall;
    private GameAnimationTimer gameAnimationTimer;
    private int oldMouseX, oldMouseY, newMouseX, newMouseY;
    private Robot robot;
    private Stage stage;
    private List<Brick> bricks;
    private List<Level> levelSet;
    private int levelCounter;
    private int points, lives;
    private long levelTime;
    private Text pointsText, levelTimeText, livesText;
    private TextField playerNameTextField;
    //time bonus - parabolic function
    private static double bonusAtStart=500;
    private static double bonusExpirationTime=180; //in seconds
    private static double bonusC=bonusAtStart;
    private static double bonusA=bonusC/(bonusExpirationTime*bonusExpirationTime);
    private static double bonusB=-bonusA*(bonusExpirationTime*2);
    @Override
    public void start(Stage primaryStage) {
        stage=primaryStage;
        gameAnimationTimer=new GameAnimationTimer();
        try {
            robot=new Robot();
        } catch (AWTException ex) {
            System.err.println("Robot cannot be constructed, cursor cannot be stopped to go outside of game window");
        }
        
        makeOptionsMenu(primaryStage);
        makeAboutMenu();
        makeHighScoresMenu();
        makePauseMenu();
        makeResultsMenu();
        makeEnterHighScoreMenu();
        
        gameStats=new VBox();
        gameStats.setAlignment(Pos.TOP_RIGHT);
        gameStats.setPrefWidth(250);
        gameStats.setPrefHeight(WINDOW_HEIGHT);
        gameStats.setStyle("-fx-background-color: pink; -fx-border-width: 5; -fx-border-color: white");
        gameStats.getChildren().add(new MenuText("Poeni", MenuText.DEFAULT_NORMAL, MenuText.DEFAULT_NORMAL));
        pointsText=new MenuText("0", MenuText.DEFAULT_NORMAL, MenuText.DEFAULT_NORMAL);
        gameStats.getChildren().add(pointsText);
        gameStats.getChildren().add(new MenuText("Vreme", MenuText.DEFAULT_NORMAL, MenuText.DEFAULT_NORMAL));
        levelTimeText=new MenuText("0:00:00", MenuText.DEFAULT_NORMAL, MenuText.DEFAULT_NORMAL);
        gameStats.getChildren().add(levelTimeText);
        gameStats.getChildren().add(new MenuText("Životi", MenuText.DEFAULT_NORMAL, MenuText.DEFAULT_NORMAL));
        livesText=new MenuText("0", MenuText.DEFAULT_NORMAL, MenuText.DEFAULT_NORMAL);
        gameStats.getChildren().add(livesText);
        MenuText pause = new MenuText("Pauza"); //this may be unnecessary
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
        //dummy brick to stop gameAnimationHandler to "go to next level" at the program start
        bricks.add(new Brick(""));
        for(Brick b:bricks) playground.getChildren().add(b);
                
        gameGroup=new Group();
        gameGroup.getChildren().addAll(playground, paddle, gameStats);
        gameStats.setTranslateX(WINDOW_WIDTH-gameStats.getPrefWidth());
        firstBall=new Ball(10, Color.YELLOW);
        firstBall.setTranslateX(60);
        firstBall.setTranslateY(300);
        firstBall.setSpeedX(0);
        firstBall.setSpeedY(200);
        playground.getChildren().add(firstBall);
        gameGroup.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!ballLaunched){
                    ballLaunched=true;
                    firstBall.setAngle(-60);
                }
            }
        });
        
        makeLevelSet();
        
        makeMainMenu();
        
        menusStackPane = new Group();
        menusStackPane.getChildren().addAll(enterHighScoreMenu,resultsMenu,optionsMenu,aboutMenu,highScoresMenu,gameGroup,pauseMenu,mainMenu);
        Scene scene = new Scene(menusStackPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (isNodeInFront(gameGroup)) {
                    if(inGame && event.getCode().equals(KeyCode.ESCAPE) && !paused){
                        System.out.println("ESC key pressed");
                        System.out.println("Scene x: "+scene.getX());
                        System.out.println("Stage x: "+stage.getX());
                        paused=true;
                        pauseMenu.toFront();
                    }
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
        updateHighScoresMenu();
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
        MenuText help = new MenuText("Pomoć");
        help.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Desktop.getDesktop().open(new File("Funkcionalna specifikacija.docx"));
                } catch (IOException ex) {
                    System.err.println("Help cannot be opened\n"+ex.toString());
                }
            }
        });
        pauseMenu.getChildren().add(help);
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
                startGame();
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
        MenuText help = new MenuText("Pomoć");
        help.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Desktop.getDesktop().open(new File("Funkcionalna specifikacija.docx"));
                } catch (IOException ex) {
                    System.err.println("Help cannot be opened\n"+ex.toString());
                }
            }
        });
        mainMenu.getChildren().add(help);
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
    }

    public void makeLevelSet(){
        levelSet=new ArrayList<>();
        //level 1
        Level level;
        level = new Level("Level 1");
        RectangleBrick.Fill[] fills={RectangleBrick.Fill.GRAY,RectangleBrick.Fill.PURPLE,RectangleBrick.Fill.BLUE,RectangleBrick.Fill.GREEN,RectangleBrick.Fill.YELLOW,RectangleBrick.Fill.RED};
        for (int row=0;row<6;row++){
            for (double col =(WINDOW_WIDTH-gameStats.getPrefWidth())/2-4*RectangleBrick.DEFAULT_WIDTH; col<(WINDOW_WIDTH-gameStats.getPrefWidth())/2+4*RectangleBrick.DEFAULT_WIDTH; col+=RectangleBrick.DEFAULT_WIDTH){
                level.getBricks().add(new RectangleBrick(col, row*20+100, fills[row]));
            }
        }
        levelSet.add(level);
        //level 2
        level = new Level("Another level");
        for(int i=0;i<6;i++)
            level.getBricks().add(new RectangleBrick(i*50, i*50, Color.ORANGE));
        levelSet.add(level);
    }

    private void startGame(){
        levelCounter=0;
        points=0;
        lives=3;
        loadLevel(0);
        inGame=true;
    }
    
    private void loadLevel(int index){
        for (Iterator<Node> it = playground.getChildren().iterator(); it.hasNext();) {
            Node n = it.next();
            if(n instanceof Brick) it.remove();
        }
        bricks.clear();
        levelTime=0;
        ballLaunched=false;
        for(Brick b:levelSet.get(index).getBricks()){
            bricks.add(b);
            playground.getChildren().add(b);
        }
        //also bring back ball to the paddle
    }
    
    private void goToNextLevel(){
        inGame=false;
        levelCounter++;
        if(levelCounter<levelSet.size()){
            loadLevel(levelCounter);
            inGame=true;
        }
        else{ //yay, game finished
            gameFinished();
        }
    }
    
    private void makeResultsMenu() {
        resultsMenu=new VBox();
        resultsMenu.setAlignment(Pos.CENTER);
        resultsMenu.setMaxWidth(WINDOW_WIDTH);
        resultsMenu.setMaxHeight(WINDOW_HEIGHT);
        resultsMenu.setPrefWidth(WINDOW_WIDTH);
        resultsMenu.setPrefHeight(WINDOW_HEIGHT);
        resultsMenu.setStyle("-fx-background-color: rgba(0,0,255,0.5); -fx-border-width: 5; -fx-border-color: white");
        //update in showLevelResults()
    }

    private void showLevelResults(){
        inGame=false;
        resultsMenu.getChildren().clear();
        String pointsSoFar="Osvojeno: "+points;
        MenuText mt1=new MenuText(pointsSoFar);
        //calculate bonus
        int bonus;
        if(levelTime<=bonusExpirationTime*1e9){
            double seconds=levelTime/1e9;
            bonus=(int)(bonusA*seconds*seconds+bonusB*seconds+bonusC);
        }
        else bonus=0;
        String bonusString="Bonus: "+bonus;
        MenuText mt2=new MenuText(bonusString);
        points+=bonus;
        String total="Ukupno: "+(points);
        MenuText mt3=new MenuText(total);
        MenuText empty=new MenuText("");
        MenuText toNextLevel=new MenuText("Nastavi");
        toNextLevel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                goToNextLevel();
                resultsMenu.toBack();
            }
        });
        resultsMenu.getChildren().addAll(mt1,mt2,mt3,empty,toNextLevel);
        resultsMenu.toFront();
    }
    
    private void makeEnterHighScoreMenu(){
        enterHighScoreMenu=new VBox();
        enterHighScoreMenu.setAlignment(Pos.CENTER);
        enterHighScoreMenu.setMaxWidth(WINDOW_WIDTH);
        enterHighScoreMenu.setMaxHeight(WINDOW_HEIGHT);
        enterHighScoreMenu.setPrefWidth(WINDOW_WIDTH);
        enterHighScoreMenu.setPrefHeight(WINDOW_HEIGHT);
        enterHighScoreMenu.setStyle("-fx-background-color: rgba(0,0,255,0.5); -fx-border-width: 5; -fx-border-color: white");
        MenuText mt1=new MenuText("Bravo!", MenuText.DEFAULT_NORMAL, MenuText.DEFAULT_NORMAL);
        enterHighScoreMenu.getChildren().add(mt1);
        MenuText mt2=new MenuText("Imaš jedan od najboljih rezultata! Unesi ime:", MenuText.DEFAULT_NORMAL, MenuText.DEFAULT_NORMAL);
        enterHighScoreMenu.getChildren().add(mt2);
        playerNameTextField=new TextField();
        playerNameTextField.setFont(MenuText.exoFont);
        playerNameTextField.setPrefColumnCount(20);
        enterHighScoreMenu.getChildren().add(playerNameTextField);
        MenuText goToHighScores=new MenuText("Nastavi");
        goToHighScores.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                HighScores.addHighScore(playerNameTextField.getText(), points);
                updateHighScoresMenu();
                highScoresMenu.toFront();
                playerNameTextField.setText("");
            }
        });
        enterHighScoreMenu.getChildren().add(goToHighScores);
    }

    private void gameFinished(){
        if(HighScores.isInHighScores(points)){
            enterHighScoreMenu.toFront();
        }else{
            highScoresMenu.toFront();
        }
    }

    private void updateHighScoresMenu(){
        VBox scores=new VBox();
        List<Pair<String, Integer>> highScores = HighScores.getHighScores();
        for(Pair<String, Integer> p:highScores){
            scores.getChildren().add(new MenuText(p.getKey()+" "+p.getValue(), MenuText.DEFAULT_NORMAL, MenuText.DEFAULT_NORMAL));
        }
        highScoresMenu.setCenter(scores);
    }

    private boolean isNodeInFront(Node node){
        return menusStackPane.getChildren().get(menusStackPane.getChildren().size()-1)==node;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
