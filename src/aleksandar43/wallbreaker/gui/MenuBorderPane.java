/* */
package aleksandar43.wallbreaker.gui;

import aleksandar43.wallbreaker.main.WallBreaker;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Aleksandar
 */
public class MenuBorderPane extends BorderPane{
    public static String STYLE_STANDARD="-fx-background-color: darkblue; -fx-border-width: 5; -fx-border-color: yellow";
    public static String STYLE_PAUSE="-fx-background-color: rgba(0,0,255,0.5); -fx-border-width: 5; -fx-border-color: white";
    public MenuBorderPane(String style){
        setMaxWidth(WallBreaker.WINDOW_WIDTH);
        setMaxHeight(WallBreaker.WINDOW_HEIGHT);
        setPrefWidth(WallBreaker.WINDOW_WIDTH);
        setPrefHeight(WallBreaker.WINDOW_HEIGHT);
        if(style!=null) setStyle(style);
    }
}
