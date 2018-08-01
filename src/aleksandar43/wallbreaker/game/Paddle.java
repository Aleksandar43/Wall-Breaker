/* */
package aleksandar43.wallbreaker.game;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

/**
 *
 * @author Aleksandar
 */
public class Paddle extends Group{
    protected Arc tempArc;
    public Paddle(){
        tempArc=new Arc(0, 0, 50, 50, 45, 90);
        tempArc.setStrokeWidth(10);
        tempArc.setFill(Color.RED);
        tempArc.setStroke(Color.BLACK);
        tempArc.setType(ArcType.OPEN);
        getChildren().add(tempArc);
    }
    
    public void move(double mouseX, Bounds playgroundBounds, Bounds windowBounds){
        setTranslateX(mouseX*(playgroundBounds.getMaxX()-playgroundBounds.getMinX())/(windowBounds.getMaxX()-windowBounds.getMinX()));
    }
    public void move(double mouseX, double maxX, double limitLeft, double limitRight){
        setTranslateX(mouseX/maxX*(limitRight-limitLeft)+limitLeft);
    }
    
    public Arc getShape(){
        return tempArc;
    }
}
