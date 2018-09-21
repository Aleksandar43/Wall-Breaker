/* */
package aleksandar43.wallbreaker.gui;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Aleksandar
 */
public class MenuText extends Text{
    public static Font exoFont;
    public static Color DEFAULT_NORMAL=Color.WHITE;
    public static Color DEFAULT_HOVER=Color.PURPLE;

    static{
        try {
            exoFont = Font.loadFont(MenuText.class.getResource("Exo2-ExtraBold.otf").toExternalForm(), 32);
        } catch (Exception e) {
            exoFont=Font.font(32);
        }
        System.out.println("Font: "+exoFont);
    }

    public MenuText(String text, Paint normalPaint, Paint hoverPaint){
        super(text);
        setFill(normalPaint);
        setFont(exoFont);
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setFill(hoverPaint);
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setFill(normalPaint);
            }
        });
        //default if nothing else is put as event handler
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("You clicked on "+text+"!");
            }
        });
    }

    public MenuText(String text){
        this(text, DEFAULT_NORMAL, DEFAULT_HOVER);
    }
}
