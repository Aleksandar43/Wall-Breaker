/* */
package aleksandar43.wallbreaker.game;

import java.io.File;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Aleksandar
 */
public class RectangleBrick extends Brick{
    public static final double DEFAULT_WIDTH=40;
    public static final double DEFAULT_HEIGHT=20;
    private static Image bricksImage;
    public static Rectangle2D BRICK_RED, BRICK_YELLOW;
    
    static{
        bricksImage=new Image("/resources/images/bricks.png");
        BRICK_YELLOW=new Rectangle2D(0, 0, 32, 16);
    }
    protected Rectangle rectangle;
    public RectangleBrick(double x, double y, double width, double height, Paint fill){
        super("rectangle");
        rectangle=new Rectangle(width, height);
        rectangle.setTranslateX(x);
        rectangle.setTranslateY(y);
        rectangle.setFill(fill);
        ImageView z=new ImageView(bricksImage);
        z.setViewport(new Rectangle2D(64, 0, 32, 16));
        z.setTranslateX(x);
        z.setTranslateY(y);
        z.setFitWidth(width);
        z.setFitHeight(height);
        getChildren().add(rectangle);
        getChildren().add(z);
    }
    
    public RectangleBrick(double x, double y, double width, double height){
        this(x, y, width, height, Color.BLACK);
    }
    
    public RectangleBrick(double x, double y, Paint fill){
        this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, fill);
    }
    
    public RectangleBrick(double x, double y){
        this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
    }

    @Override
    public Rectangle getShape() {
        return rectangle;
    }
}
