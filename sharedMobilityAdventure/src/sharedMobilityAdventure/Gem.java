package sharedMobilityAdventure;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;

public class Gem extends Collectable implements Serializable {

    private static final long serialVersionUID = 3349609681759015759L;
    final int width; // width of gem
    final int height; // height of gem
    transient BufferedImage image; // image of gem
    private transient GamePanel gamePanel; // GamePanel instance

    public Gem(String name, GamePanel gamePanel, int playerX, int playerY) {
        super(name);
        this.gamePanel = gamePanel; // Store the GamePanel instance
        width = 32;
        height = 32;

        int[] coordinates = super.dropRandomly(playerX, playerY); // Pass player's coordinates to dropRandomly
        this.collectabelX = coordinates[0]; // Set the x-coordinate obtained from dropRandomly
        this.collectabelY = coordinates[1]; // Set the y-coordinate obtained from dropRandomly

        // debugging statement to confirm that random method won't drop gem and carboncoin to the same location on the gamepanel
        System.out.println("Gem coordinates after dropRandomly(): x=" + collectabelX + ", y=" + collectabelY);
    }

    public void loadImage() {
        try {
            image = ImageIO.read(new File("images/gems/gem.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        int adjustedX = collectabelX - (width / 2);
        int adjustedY = collectabelY - (height / 2);
        g.drawImage(image, adjustedX, adjustedY, width, height, null);
    }

    public boolean getVisibility() {
        return this.visible;
    }

    public void setVisibility(boolean visibleUpdated) {
        this.visible = visibleUpdated;
    }
}