/* */
package aleksandar43.wallbreaker.game;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Aleksandar
 */
public class Paddle extends Group{
    protected Rectangle rectangle;
    public Paddle(double layoutX, double layoutY){
        rectangle = new Rectangle(layoutX, layoutY, 100, 25);
        getChildren().add(rectangle);
    }
    
    public void move( ){
    
    }
}
