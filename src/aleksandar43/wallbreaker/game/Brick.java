/* */
package aleksandar43.wallbreaker.game;

import javafx.scene.Group;
import javafx.scene.shape.Shape;

/**
 *
 * @author Aleksandar
 */
public class Brick extends Group{
    protected String type;
    public Brick(String type){
        this.type=type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void destroy(){
        //???
    }
    
    //default behaviour is to be destroyed
    public void onHit(){
        destroy();
    }
    
    public Shape getShape(){
        return null;
    }
}
