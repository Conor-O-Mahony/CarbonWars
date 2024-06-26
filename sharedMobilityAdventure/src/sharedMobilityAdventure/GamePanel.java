
package sharedMobilityAdventure;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;

public class GamePanel extends JPanel implements KeyListener {

	private static final long serialVersionUID = 1L;
	private Player player;
	private Board board;
    public static Map<String, Color> colorMap = new HashMap<String, Color>();
    private Map<String, String> pinMap = new HashMap<String, String>();

  String username; // Store the username
          
  private CarbonCoin[] carbonCoins;
  private int numCarbonCoins = 3;
  
  private Gem[] gems; //Array to store Gems
  private int numGems; // Number of gems to drop

  private PopUp[] popups; // Array to store Popups
  private int numPopups = 3; 

	int playerTime = 7200; // No. of mins in 5 days (60*24*5)

	int gemScore = 0;
	int coinScore = 200;
	int coinsCollected = 0;
	int gameScore = 0;
	int gameRound = 0;
	int showOption = 0; // Needed for dynamic Dialogue Display of Route Costs
	int carbonCost = 0;
	int timeCost = 0;
  public boolean gemScoreUpdate = true;
  public boolean coinScoreUpdate = true;
  public boolean showExitConfirmation = false;
  public boolean showTransportOption = true;
  public boolean haloDisplay = false;
  public boolean optionDisplay = false;
  public boolean waitingForInput = false;
  public boolean update = true;
  private Set<String> uniqueStrings = new LinkedHashSet<>(); // Game is in a Loop - Linked Hash Set prevents duplicates
  public List<String> uniqueStringsList = new ArrayList<>(); // List is much easier for indexing 
  public JButton button;
  
  private boolean isDirectionButton(KeyEvent e) { // Defining Direction Buttons so if the player moves away, the dialogue display is reset
	    if (	e.getKeyCode() == KeyEvent.VK_UP ||
	    		e.getKeyCode() == KeyEvent.VK_DOWN ||
	    		e.getKeyCode() == KeyEvent.VK_LEFT ||
	    		e.getKeyCode() == KeyEvent.VK_RIGHT) {
	        return true;
	    } else {
	        return false;
	    }
	}

    public GamePanel(String username){
		
		this.username = username; // Store the username
        
        addColors();
        addHaloNames();
        
        initGame();

        this.setFocusable(true);
        focus();

        addKeyListener(this);   
        setLayout(null);  //ELSE THE BUTTON WON'T PLACE CORRECTLY
        
        addButton();
    }
    
    void reloadImages() {
    	player.loadImage();
    	for (int i = 0; i < numGems; i++) {
    		gems[i].loadImage();
        }
    	for (int i = 0; i < numPopups; i++) {
            popups[i].loadImage();
        }
    	addColors();
    	
    }
    
    static void addColors()
    {
        colorMap.put("bikepinB", Color.BLUE);
        colorMap.put("buspinB", Color.BLUE);
        colorMap.put("trainpinB", Color.BLUE);
        colorMap.put("bikepinY", Color.YELLOW);
        colorMap.put("buspinY", Color.YELLOW);
        colorMap.put("trainpinY", Color.YELLOW);
        colorMap.put("bikepinG", Color.GREEN);
        colorMap.put("buspinG", Color.GREEN);
        colorMap.put("trainpinG", Color.GREEN);
    }
    
    private void addHaloNames()
    {
    	pinMap.put("bikepinB", "haloB");
    	pinMap.put("buspinB", "haloB");
    	pinMap.put("trainpinB", "haloB");
    	pinMap.put("bikepinY", "haloY");
    	pinMap.put("buspinY", "haloY");
    	pinMap.put("trainpinY", "haloY");
    	pinMap.put("bikepinG", "haloG");
    	pinMap.put("buspinG", "haloG");
    	pinMap.put("trainpinG", "haloG");
    }
    
    public void focus() {
    	requestFocus();
     }
	
    public void initGame() {
    	playAudio();

    	gameRound += 1;
    	board = new Board(Main.DEFAULT_BOARD_SIZE, Main.DEFAULT_BOARD_SIZE);
        player = new Player(this);

        // Get player's initial coordinates
        int playerX = player.getPlayerX();
        int playerY = player.getPlayerY();
        
        Collectable.clearDroppedCoordinates();
         if (gameRound == 1 || gameRound == 2 || gameRound == 3) {
        	numGems = 1;
        	gems = new Gem[numGems]; // Initialize array
        	gems[0] = new Gem("Diamond", board, this, playerX, playerY); // Pass player's coordinates to the Gem constructor

        } else {
        	numGems = 3;
        	gems = new Gem[numGems]; // Initialize array

        	for (int i = 0; i < numGems; i++) {
        		gems[i] = new Gem("Diamond", board, this, playerX, playerY); // Pass player's coordinates to the Gem constructor
            }        	

        }     
         
        carbonCoins = new CarbonCoin[numCarbonCoins];
        for (int i = 0; i < numCarbonCoins; i++) {
            carbonCoins[i] = new CarbonCoin("Carbon Credit", board, this, playerX, playerY); // Pass the GamePanel instance to the CarbonCoin constructor
        }

	    startRotation();

        popups = new PopUp[numPopups];
    	
    	for (int i = 0; i < numPopups; i++) {
            popups[i] = new PopUp();
        }
        
    	if (gameRound == 1) {
    		JOptionPane.showMessageDialog(null, "Day: " + gameRound + ". Use the arrows keys to get " + username + " to the gem. (Walking takes 50 mins)");
    	} else {
    		JOptionPane.showMessageDialog(null, "Day: " + gameRound + ". Click OK!");
    	}
        
    }
    
    void playAudio() {
    	try {
    	    // Load sound clips only if they haven't been loaded yet
    	    if (Main.defaultGameAudioClip == null) {
    	        Main.loadSoundClips();
    	    }
    	} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
    	    e.printStackTrace();
    	}

    	// Check if the default game audio clip is loaded and not already playing
    	if (Main.defaultGameAudioClip != null && !Main.defaultGameAudioClip.isRunning()) {
    	    // Set loop count to LOOP_CONTINUOUSLY
    	    Main.defaultGameAudioClip.loop(Clip.LOOP_CONTINUOUSLY);
    	    // Start the default game audio clip
    	    Main.defaultGameAudioClip.start();
    	} else {
    	    // Handle the case where the default game audio clip is already playing
    	    // System.out.println("Default game audio clip is already playing or not loaded.");
    	}
    }

    void startRotation() {
    	for (int i = 0; i < numCarbonCoins; i++) {
            carbonCoins[i].startRotation();
        }
    }

    void addButton() {
    	add(createButton(Main.Frame, Main.GAME_WIDTH+15, Main.GAME_HEIGHT-165, "Save Game"));
    }
    
    private JButton createButton(JFrame frame, int buttonX, int buttonY, String text) {
        button = new JButton(text);
        setButtonIcon(button, "images/tiles/savegamebuttondefault.png");
        setButtonHoverIcon(button, "images/tiles/savegamebuttonhovered.png");
        int buttonWidth = 226;
        int buttonHeight = 65;
        Rectangle bounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        button.setBounds(bounds);

	    addActionListener();

        return button;    
    }

	void addActionListener() {
    	button.addActionListener(e -> {     
    		CarbonCoin.stopRotation();
            Main.openSaveLoadWindow(this,"save");
        }); 
    }

    private void setButtonIcon(JButton button, String imagePath) {
        button.setBorderPainted(false); // Remove button border
        button.setFocusPainted(false); // Remove focus border
        button.setContentAreaFilled(false); // Remove default content fill
        button.setBorder(null); // Remove button border
        ImageIcon icon = new ImageIcon(imagePath);
        button.setIcon(icon);
    }

    private void setButtonHoverIcon(JButton button, String hoverImagePath) {
        button.setRolloverIcon(new ImageIcon(hoverImagePath));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        //System.gc();
        
        for (int row = 0; row < Main.DEFAULT_BOARD_SIZE; row++) {
			for (int col = 0; col < Main.DEFAULT_BOARD_SIZE; col++) {		
								
				Route[] routes = board.tiles[row][col].getRoutes();
				
				g.drawImage(Main.tileImage, col*Main.TILE_SIZE, row*Main.TILE_SIZE, Main.TILE_SIZE, Main.TILE_SIZE, null);
				
				int extra = (int) Math.round(0.3*Main.TILE_SIZE);
				if (routes[0]!=null) {
					g.drawImage(Main.getImageFromCache(routes[0].getPinName()), col*Main.TILE_SIZE, row*Main.TILE_SIZE, Main.TILE_SIZE*2/3, Main.TILE_SIZE*2/3, null);
				}
				if (routes[1]!=null) {
					g.drawImage(Main.getImageFromCache(routes[1].getPinName()), col*Main.TILE_SIZE + extra, row*Main.TILE_SIZE - extra, Main.TILE_SIZE*2/3, Main.TILE_SIZE*2/3, null);
				}
				//if (routes[2]!=null) {
				//	g.drawImage(Main.getImageFromCache(routes[2].getPinName()), col*Main.TILE_SIZE + extra, row*Main.TILE_SIZE - extra, Main.TILE_SIZE*2/3, Main.TILE_SIZE*2/3, null);
				//}
			}
        }
        
        player.draw(g);       

        for (int i = 0; i < popups.length; i++) {
            PopUp popup = popups[i];
            if (popup.getVisibility()) {
                popup.draw(g);
            }
        }

        for (int i = 0; i < gems.length; i++) {
            Gem gem = gems[i];
            if (gem.getVisibility()) {
                gem.draw(g);
            }
        }
        
        for (int i = 0; i < carbonCoins.length; i++) {
            CarbonCoin coin = carbonCoins[i];
            if (coin.getVisibility()) {
                coin.draw(g);
            }

        }
        //g.drawImage(sidebarImage, Main.GAME_WIDTH, 0, Main.SIDEBAR_WIDTH, Main.GAME_WIDTH, null);
        g.drawImage(Main.sidebarImage,Main.GAME_WIDTH,0,Main.sidebarImage.getWidth(),Main.WINDOW_HEIGHT, null);
        
        paintHalos(g);
        
        // Player
        //g.setColor(Color.BLACK); // Set color to black
        //g.setFont(new Font("Tahoma", Font.BOLD, 16));
        //g.drawString(username, Main.GAME_WIDTH+90, 70);
        
        // Time
        g.setColor(Color.BLACK);
        g.setFont(new Font("Tahoma", Font.BOLD, 16));
        g.drawString("" + playerTime + " mins", Main.GAME_WIDTH+25, 75);
        
        // Gems
        g.setColor(Color.BLACK);
        g.setFont(new Font("Tahoma", Font.BOLD, 16));
        g.drawString("" + checkGemScore(), Main.GAME_WIDTH+40, 165);
        gemScoreUpdate = true;
                     
        // Carbon Coins
        g.setColor(Color.BLACK);
        g.setFont(new Font("Tahoma", Font.BOLD, 16));
        g.drawString("" + checkCoinScore(), Main.GAME_WIDTH+175, 75);
        coinScoreUpdate = true;
        
        // Score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Tahoma", Font.BOLD, 16));
        g.drawString("" + gameScore, Main.GAME_WIDTH+155, 165);
        
        if (showExitConfirmation) { // Part of Press Esc to Exit logic
	        String exitMessage = "Press Esc again to Exit!";
	        g.setColor(Color.BLACK); 
	        g.setFont(new Font("Tahoma", Font.BOLD, 14));
	        g.drawString(exitMessage, Main.GAME_WIDTH + 30, 280);
       
        } else {
        	//Do nothing
        	}
        }
    
    public void paintHalos(Graphics g) {
    	int player_x = player.getPlayerXTile();
    	int player_y = player.getPlayerYTile();
    	Tile currentTile = board.tiles[player_y][player_x];
    	Route[] tileRoutes = currentTile.getRoutes();
    	if (update) { //Prevents a calculaion loop occuring when not needed
            uniqueStrings.clear();
            carbonCost = 0;
            timeCost = 0;
        }
    	update = false;
    	for (int i = 0; i < tileRoutes.length; i++) {
            if (tileRoutes[i] != null) {
                Route route = tileRoutes[i];
                route.updateTravel();  // Update Route to get values for Carbon and Time Cost

                TransportTypes type = route.getTransportType();
                double carbonCost = route.getCarbonCost();
                int travelTime = route.getTravelTime();

                // Easiest way to store the values temporarily was as a string in the set
                String travelDetails = String.format("%s|%d|%d", type, (int) carbonCost, travelTime);

                uniqueStrings.add(travelDetails);

    			String nameOfPin = tileRoutes[i].getPinName();
    			String haloName = pinMap.get(nameOfPin);

    			Tile[] tilesInRoute = tileRoutes[i].getTiles();
    			for (int j=0; j<tilesInRoute.length; j++) {
    				int tile_x = tilesInRoute[j].getX();
    				int tile_y = tilesInRoute[j].getY();
    				
    				g.drawImage(Main.getImageFromCache(haloName), tile_x*Main.TILE_SIZE, tile_y*Main.TILE_SIZE, Main.TILE_SIZE, Main.TILE_SIZE, null);
    			}
    			
    			String typeString = type.toString();
    			if (showTransportOption) { 
                	haloDisplay = true;
    			
                    String text1 = "Press "+(i+1)+" to take ";
                    String text2 = typeString;
                    Font font = new Font("Tahoma", Font.BOLD, 16);
                    FontMetrics fontMetrics = g.getFontMetrics(font);
                    g.setFont(font);
                    
                    int string1Width = fontMetrics.stringWidth(text1);
                        
                    g.setColor(Color.BLACK); // Set color to black
                    g.drawString(text1, Main.GAME_WIDTH+25, 285 + i*25);

                    g.setColor(colorMap.get(nameOfPin)); // Set color to black
                    g.drawString(text2, Main.GAME_WIDTH+25+string1Width, 285 + i*25);
    	        
    	        
    		    }
            }
        }
    			
    	uniqueStringsList = new ArrayList<>(uniqueStrings); // Need to convert the Linked Set to a list for easy indexing
        if (showOption != 0) { // showOption is 0 when the first screen is displayed i.e. Press X to take Y
            if (showOption <= uniqueStringsList.size()) {  // Check if the selected option is valid - Option 1 must have  1 string in the set / list...
                String[] detailsArray = uniqueStringsList.get(showOption - 1).split("\\|"); // Make an array from the 3 pieces of travelDetails - type, carbonCost, timeCost 
                carbonCost = Integer.parseInt(detailsArray[1]); // As its a string, the int needs to be converted back to an int
            	timeCost = Integer.parseInt(detailsArray[2]);
                if (detailsArray.length >= 3) {  // Ensure there are enough parts in the split result
                    System.out.println("list:" + uniqueStringsList.get(showOption - 1));
                	g.setColor(Color.BLACK);
                	g.setFont(new Font("Tahoma", Font.BOLD, 14));
                	g.drawString("You Have Chosen " + detailsArray[0], Main.GAME_WIDTH + 30, 280);// type
                	g.drawString("Carbon Coin Cost: " + detailsArray[1], Main.GAME_WIDTH + 30, 295); // Carbon Cost
                	g.drawString("Time Cost: " + detailsArray[2] + " mins", Main.GAME_WIDTH + 30, 310); // Time Cost
                	if (coinScore - carbonCost < 0) { // Pre check of the calculation
                	    g.drawString("Not Enough Carbon Coins!", Main.GAME_WIDTH + 30, 330);
                     	g.drawString("Press 0 to Return", Main.GAME_WIDTH + 30, 345);
                	} else { // If there is enough coin balance
                	    g.drawString("Press 1 to Continue", Main.GAME_WIDTH + 30, 330);
                	    g.drawString("Press 0 to Return", Main.GAME_WIDTH + 30, 345);
                    }    
                }
            }
        }          
    }

    public void restartGame() {
	    //CLEAR OLD OBJECTS OUT
	    calculateGameScore();   

        player = null;
        for (int i = 0; i < numPopups; i++) {
            popups[i] = null;
        }
        popups = null;

        for (int i = 0; i < numGems; i++) {
            gems[i] = null;
        }
        gems = null;

        CarbonCoin.stopRotation();
        for (int i = 0; i < numCarbonCoins; i++) {
            carbonCoins[i] = null;
        }
        carbonCoins = null;

        board = null;

        System.gc();

    	//Initialise new objects
        initGame();
        repaint();
    } 
 
    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (showExitConfirmation) {
        	showOption = 0;
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.exit(0); // Exit the game
            } else {
                showExitConfirmation = false; // Hide the exit confirmation message
                showTransportOption = true; // Show transport options (again)
                repaint();
                player.keyPressed(e); // Pass Through the key press - prevents having to press direction button twice
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            showExitConfirmation = true; // Check if needed
            showTransportOption = false; // Hide transport options - Prevent overlap
            showOption = 0; // Hide Options - Prevent Overlap
        } else if (isDirectionButton(e)) {
        	player.keyPressed(e); // Pass Through the key press - prevents having to press direction button twice
        }

        // Key press logic when haloDisplay is true i.e when a route is mapped out / when player is stood at transport option
        if (haloDisplay) {
          //System.out.println("Halo Display");
            if (!waitingForInput) { //waitingForInput is true when it is one one of the option screens
                if (e.getKeyCode() == KeyEvent.VK_1) {
                	if (uniqueStrings != null && uniqueStringsList.size() > 0) { // Ensure theres a first transport option
                        showTransportOption = false;
                        showOption = 1; // Show the Carbon / Time info for option 1
                        waitingForInput = true;
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_2) {
                	if (uniqueStrings != null && uniqueStringsList.size() > 1) { // Ensure theres a second transport option
	                    showTransportOption = false;
	                    showOption = 2; // Show the Carbon / Time info for option 2
	                    waitingForInput = true;
                }}
            } else {
                if (e.getKeyCode() == KeyEvent.VK_1) {                	
                	if (coinScore - carbonCost >= 0) {
                        timer(timeCost - 50); // It automatically takes out the standard walk movement of the character - this fixes it
                        coinCount(carbonCost);
                        System.out.println("Coins: -" + carbonCost + "Time: -" + timeCost);
                    takeTransportRoute(showOption, player.getPlayerXTile(), player.getPlayerYTile());
                    showOption = 0;
                    showTransportOption = true;
                    waitingForInput = false;
                	} 
                }
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_0 || isDirectionButton(e)) { // Returns to normal if 0 is pressed or if the player moves
            showOption = 0;
            optionDisplay = false;
            showTransportOption = true;
            repaint(); // Check if needed
            waitingForInput = false;
            update = true;
            carbonCost = 0;
            timeCost = 0;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Not needed
    }


    public boolean takeTransportRoute(int mode, int player_x, int player_y) {
        // Retrieve the number of routes available at the current player's tile
        int numberOfRoutes = board.tiles[player_y][player_x].getNumberOfRoutes();
        if (numberOfRoutes >= mode) {
            Route routeToTake = board.tiles[player_y][player_x].getRoutes()[mode - 1];
            
            // Check if the player is at the end or start of the route and move them accordingly
            int new_player_x, new_player_y;
            if (routeToTake.getFinalRow() == player_y && routeToTake.getFinalCol() == player_x) {
                // Player is at the end of the route, move them to the start
                new_player_x = routeToTake.getStartCol();
                new_player_y = routeToTake.getStartRow();
            } else {
                // Move player to the end of the route
                new_player_x = routeToTake.getFinalCol();
                new_player_y = routeToTake.getFinalRow();
            }
            
            // Update player's position
            player.setPlayerX(new_player_x);
            player.setPlayerY(new_player_y);
            
            // Calculate travel costs
            routeToTake.updateTravel();

            return true;
        } else {
            // No route available for the given mode
            return false;
        }
    }

 
	public int checkGemScore() {
	    for (int i = 0; i < gems.length; i++) {
	        Gem gem = gems[i];
	        if (player.getPlayerX() == gem.collectabelX && player.getPlayerY() == gem.collectabelY && gem.getVisibility()) {
	            gemScore++; // Increase the score
	            gem.setVisibility(false);
	            gem.playSound();
	        }
	    }
	    return gemScore;
	}

	public int checkCoinScore() {
	    for (int i = 0; i < carbonCoins.length; i++) {
	        CarbonCoin coin = carbonCoins[i];
	        if (player.getPlayerX() == coin.collectabelX && player.getPlayerY() == coin.collectabelY && coin.getVisibility()) {

	            coinScore += 20; // Increase the score
	            coinsCollected++;
	            coin.setVisibility(false);

	            coin.playSound();
	        }
	    }
	    return coinScore;
	}


	public boolean allGemsCollected() {
	    for (Gem gem : gems) {
	        if (gem.getVisibility()) {
	            return false; 
	        }
	    }
	    return true; // All gems are collected
	}

    public void popupIntersection() {
	    for (int i = 0; i < popups.length; i++) {
	    	PopUp popup = popups[i];
	        if (player.getPlayerX() == popup.popupX && player.getPlayerY() == popup.popupY && popup.getVisibility()) {
	        	popup.setVisibility(false);
	        	popup.displayPopup();
		}}}

    public void calculateGameScore() {        
    	gameScore += (int) ((100 * playerTime)/7200 + (100 * checkCoinScore())/200)*(1+0.2*gameRound);
    }
       
    public void timer(int movement) {  	
    	if ((playerTime - movement) <= 0) {  //Prevent negative playertime
    		playerTime = 0;
		    CarbonCoin.stopRotation();
    		Main.openEndWindow(username, gameRound, gemScore, coinsCollected, gameScore);
    	}
    	else {
    		playerTime -= movement;
    	}
    }
    
    public void coinCount(int movement) {  	
    	if ((coinScore - movement) <= 0) { 
    		coinScore = 0;
    		Main.openEndWindow(username, gameRound, gemScore, coinsCollected, gameScore);
    	}
    	else {
    		coinScore -= movement;
    	}
    }
 
}
