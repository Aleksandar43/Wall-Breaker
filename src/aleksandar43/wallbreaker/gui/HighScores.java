/* */
package aleksandar43.wallbreaker.gui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author Aleksandar
 */
public class HighScores {
    private static final List<Pair<String, Integer>> defaultHighScores;
    private static List<Pair<String, Integer>> scores;
    
    static{
        defaultHighScores=new ArrayList<>(10);
        defaultHighScores.add(new Pair<>("Mika", 100));
        defaultHighScores.add(new Pair<>("Rika", 90));
        defaultHighScores.add(new Pair<>("Lika", 80));
        defaultHighScores.add(new Pair<>("Dika", null));
    }
    
    public static List<Pair<String, Integer>> getHighScores(){
        if (scores==null) {
            try (BufferedReader reader = new BufferedReader(new FileReader("highscores.dat"));) {
                scores = new ArrayList<>();
                String line = reader.readLine();
                while (line != null) {
                    //lines in format <points> <name>
                    String[] split = line.split(" ", 2);
                    scores.add(new Pair<>(split[1], Integer.parseInt(split[0])));
                    line = reader.readLine();
                }
            } catch (FileNotFoundException ex) {
                System.err.println("Highscores not found, using default");
                scores = defaultHighScores;
            } catch (IOException ex) {
                System.err.println("Error while reading highscores, using default");
                scores = defaultHighScores;
            }
        }
        return scores;
    }
    
    public static void addHighScore(){
        
    }
    
    public static void saveHighScores(){
        try (PrintWriter writer=new PrintWriter(new FileWriter("highscores.dat"))) {
            for(Pair<String, Integer> p:scores){
                writer.println(p.getValue()+" "+p.getKey());
            }
        } catch (IOException ex) {
            System.err.println("Error while writing highscores");
        }
    }
}
