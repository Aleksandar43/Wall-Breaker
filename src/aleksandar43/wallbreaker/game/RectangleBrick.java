/* */
package aleksandar43.wallbreaker.game;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
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
    
    public enum RectangleFill{
        BRICK_RED(0,0,32,16),
        BRICK_ORANGE(32,0,32,16),
        BRICK_YELLOW(64,0,32,16);
        
        private final double x,y,width,height;
        private RectangleFill(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    
    static{
        bricksImage=new Image("/resources/images/bricks.png");
    }
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
    
    public RectangleBrick(double x, double y, Paint fill){
        this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, fill);
    }
    
    public RectangleBrick(double x, double y){
        this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.BLACK);
    }
    
    public RectangleBrick(double x, double y, double width, double height, RectangleFill fill){
        super("rectangle");
        ImageView z=new ImageView(bricksImage);
        z.setViewport(new Rectangle2D(fill.x, fill.y, fill.width, fill.height));
        z.setTranslateX(x);
        z.setTranslateY(y);
        z.setFitWidth(width);
        z.setFitHeight(height);
        getChildren().add(z);
    }

    public RectangleBrick(double x, double y, RectangleFill fill){
        this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, fill);
    }

    @Override
    public Rectangle getShape() {
        return rectangle;
    }
}
