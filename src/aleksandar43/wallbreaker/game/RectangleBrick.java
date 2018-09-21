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
    
    public enum Fill{
        RED(0,0,32,16),
        ORANGE(32,0,32,16),
        YELLOW(64,0,32,16),
        GREEN(96,0,32,16),
        BLUE(128,0,32,16),
        PURPLE(0,16,32,16),
        PINK(32,16,32,16),
        TEAL(64,16,32,16),
        CYAN(96,16,32,16),
        CRIMSON(128,16,32,16),
        GRAY(64,32,32,16),
        DARKGREEN(0,48,32,16),
        LIME(32,48,32,16),
        DARKRED(64,48,32,16),
        BROWN(96,48,32,16),
        BRONZE(128,48,32,16),
        SILVER(0,64,32,16),
        GOLD(32,64,32,16),
        SHINYBLACK(128,64,32,16),
        ;
        
        private final double x,y,width,height;
        private Fill(double x, double y, double width, double height) {
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
    
    public RectangleBrick(double x, double y, double width, double height, Fill fill){
        super("rectangle");
        ImageView z=new ImageView(bricksImage);
        z.setViewport(new Rectangle2D(fill.x, fill.y, fill.width, fill.height));
        z.setTranslateX(x);
        z.setTranslateY(y);
        z.setFitWidth(width);
        z.setFitHeight(height);
        getChildren().add(z);
        //for getShape()
        rectangle=new Rectangle(width, height);
        rectangle.setTranslateX(x);
        rectangle.setTranslateY(y);
        getChildren().add(0,rectangle);
    }

    public RectangleBrick(double x, double y, Fill fill){
        this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, fill);
    }

    @Override
    public Rectangle getShape() {
        return rectangle;
    }
}
