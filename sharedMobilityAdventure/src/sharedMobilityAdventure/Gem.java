
package sharedMobilityAdventure;

import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class Gem extends Collectable implements Serializable {

    private static final long serialVersionUID = 3349609681759015759L;
    
    
    public Gem(String name, Board board, GamePanel gamePanel, int playerX, int playerY) {
        super(name, board);
        
        // Get initial coordinates using dropRandomly method
        super.dropRandomly(playerY, playerX);
        System.out.println("Gem coordinates after dropRandomly: (" + collectabelX + ", " + collectabelY + ")");

        // Continue generating new coordinates until the Euclidean distance condition is satisfied
        while (!euclideanDistanceProximityCalculator(collectabelX, collectabelY, board)) {
            super.dropRandomly(playerX, playerY);
            System.out.println("Gem coordinates adjusted to satisfy Euclidean distance.");
        }
        
        // Assign the final coordinates that satisfy the Euclidean distance condition
        this.collectabelX = super.collectabelX;
        this.collectabelY = super.collectabelY;
        
        loadImage();
    }
    
    public void loadImage() {
    	if (imageCache.containsKey(name)) {
    		filledImage = imageCache.get(name);
    	} else {
    		try {
				filledImage = ImageIO.read(new File("images/gems/gem.png"));
				imageCache.put(name, filledImage);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    @Override
    public void draw(Graphics g) {
        super.draw(g); // Call superclass draw method to draw the gem image and its outline
    }
}
