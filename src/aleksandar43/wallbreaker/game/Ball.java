/* */
package aleksandar43.wallbreaker.game;

import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 *
 * @author Aleksandar
 */
public class Ball extends Group{
    private Circle circle;
    private double speedX, speedY;
    
    public Ball(double radius, Paint paint){
        circle=new Circle(radius, paint);
        getChildren().add(circle);
    }

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }
    
    public double getRadius(){
        return circle.getRadius();
    }
    
    public void setRadius(double r){
        circle.setRadius(r);
    }
    
    public double getSpeed(){
        return Math.hypot(speedX, speedY);
    }
    
    public double getAngle(){
        return Math.atan2(speedY, speedX)*360/2*Math.PI;
    }
    
    public void setAngle(double angle){
        double speedM=getSpeed();
        speedX=speedM*Math.cos(angle);
        speedY=speedM*Math.sin(angle);
    }
    
    public Circle getShape(){
        return circle;
    }
}
