/* */
package aleksandar43.wallbreaker.gui;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Aleksandar
 */
public class MenuText extends Text{
    public static Font exoFont;
    static{
        try {
            exoFont = Font.loadFont(MenuText.class.getResource("Exo2-ExtraBold.otf").toExternalForm(), 36);
        } catch (Exception e) {
            exoFont=Font.font(36);
        }
        System.out.println("Font: "+exoFont);
    }
    public MenuText(String text){
        super(text);
        setFill(Color.WHITE);
        setFont(exoFont);
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setFill(Color.PURPLE);
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setFill(Color.WHITE);
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
}
