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
}
