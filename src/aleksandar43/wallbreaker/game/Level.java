/* */
package aleksandar43.wallbreaker.game;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aleksandar
 */
public class Level {
    private String name;
    private List<Brick> bricks;
    
    public Level(String name, List<Brick> bricks){
        this.name=name;
        this.bricks=bricks;
    }
    
    public Level(String name){
        this.name=name;
        bricks=new ArrayList<>();
    }
    
    public Level(){
        this.name="New level";
        bricks=new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public void setName(String name) {
        this.name = name;
    }
}
