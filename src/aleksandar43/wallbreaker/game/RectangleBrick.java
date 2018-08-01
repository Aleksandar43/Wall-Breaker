/* */
package aleksandar43.wallbreaker.game;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Aleksandar
 */
public class RectangleBrick extends Brick{
    protected Rectangle rectangle;
    public RectangleBrick(double x, double y, double width, double height, Paint fill){
        super("rectangle");
        rectangle=new Rectangle(width, height);
        rectangle.setTranslateX(x);
        rectangle.setTranslateY(y);
        rectangle.setFill(fill);
        getChildren().add(rectangle);
    }
    
    public RectangleBrick(double x, double y, double width, double height){
        this(x, y, width, height, Color.BLACK);
    }

    @Override
    public Rectangle getShape() {
        return rectangle;
    }
}
