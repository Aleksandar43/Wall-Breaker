/* */
package aleksandar43.wallbreaker.game;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;

/**
 *
 * @author Aleksandar
 */
public class Paddle extends Group{
    private static double radius=50;
    private static double minAngle=45;
    private static double maxAngle=135;
    private static double bounceStrokeWidth=10;
    protected Arc tempArc;
    protected Circle debugCircle;
    public Paddle(){
        tempArc=new Arc(0, 0, radius, radius, minAngle, maxAngle-minAngle);
        tempArc.setStrokeWidth(bounceStrokeWidth);
        tempArc.setFill(Color.RED);
        tempArc.setStroke(Color.BLACK);
        tempArc.setType(ArcType.OPEN);
        getChildren().add(tempArc);
        debugCircle=new Circle(radius);
        debugCircle.setFill(null);
        debugCircle.setStrokeWidth(1);
        debugCircle.setStroke(Color.WHITE);
        //getChildren().add(debugCircle);
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
    
    public boolean isInHittableRange(double newBallX, double newBallY){
        double xLeft=newBallX-10; //ball radius
        double angle=Math.toDegrees(Math.atan2(getTranslateY()-newBallY, xLeft-getTranslateX()));
        if(angle>=minAngle && angle<=maxAngle) return true;
        double xRight=newBallX+10; //ball radius
        angle=Math.toDegrees(Math.atan2(getTranslateY()-newBallY, xRight-getTranslateX()));
        if(angle>=minAngle && angle<=maxAngle) return true;
        return false;
    }
}
