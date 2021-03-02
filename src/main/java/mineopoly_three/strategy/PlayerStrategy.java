package mineopoly_three.strategy;

import com.sun.istack.internal.NotNull;
import java.awt.Point;
import java.util.Random;
import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.tiles.TileType;

/**
 * PlayerStrategy class that implements MinePlayerStrategy interface and contains one strategy of
 *  winning the Mine-opoly game.
 */
public class PlayerStrategy implements MinePlayerStrategy {
    private static int boardSize; // length and width of the square game board
    private int maxInventorySize; // maximum number of items that your player can carry at one time
    private int maxCharge; // amount of charge your robot starts with (number of tile moves before needing to recharge)
    private PlayerBoardView startingBoard; // view of the GameBoard at the start of the game
    private Point startTileLocation; // Point representing your starting location in (x, y) coordinates
    private Economy economy; // GameEngine's economy object which holds current prices for resources
    private PlayerBoardView currentBoard; // current game board
    private Point currentLocation = new Point(); // current location of player on board
    private Point destination = new Point(); // destination player tries to reach
    private int currentInventorySize = 0; // current size of inventory
    private int currentScore = 0; // current score of game
    private int mineCount = 0; // number of times mined
//    private int numTurns = 0; // number of turns that takes to win

    public static int getBoardSize() {
        return boardSize;
    }

    public Point getDestination() {
        return destination;
    }

    public Economy getEconomy() {
        return economy;
    }

    public Point getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Initialize method that gets called on at the beginning of every game round.
     *
     * @param boardSize The length and width of the square game board
     * @param maxInventorySize The maximum number of items that your player can carry at one time
     * @param maxCharge The amount of charge your robot starts with (number of tile moves before needing to recharge)
     * @param winningScore The first player to reach this score wins the round
     * @param startingBoard A view of the GameBoard at the start of the game. You can use this to pre-compute fixed
     *                       information, like the locations of market or recharge tiles
     * @param startTileLocation A Point representing your starting location in (x, y) coordinates
     *                              (0, 0) is the bottom left and (boardSize - 1, boardSize - 1) is the top right
     * @param isRedPlayer True if this strategy is the red player, false otherwise
     * @param random A random number generator, if your strategy needs random numbers you should use this.
     */
    @Override
    public void initialize(int boardSize, int maxInventorySize, int maxCharge, int winningScore,
            PlayerBoardView startingBoard, Point startTileLocation, boolean isRedPlayer, Random random) {
        this.boardSize = boardSize;
        this.maxInventorySize = maxInventorySize;
        this.maxCharge = maxCharge;
        this.startingBoard = startingBoard;
        this.startTileLocation = startTileLocation;

        Tool tool = new Tool(); // new instance of tool
        // first destination is diamond for it will always be the most expensive at the beginning of round
        destination = tool.nearestTile(TileType.RESOURCE_DIAMOND, startingBoard, startTileLocation);
    }

    /**
     * Method that returns what action my robot will do on this round.
     * Called at the beginning of every turn.
     *
     * @param boardView A PlayerBoardView object representing all the information about the board and the other player
     *                   that your strategy is allowed to access
     * @param economy The GameEngine's economy object which holds current prices for resources
     * @param currentCharge The amount of charge your robot has (number of tile moves before needing to recharge)
     * @param isRedTurn For use when two players attempt to move to the same spot on the same turn
     *                   If true: The red player will move to the spot, and the blue player will do nothing
     *                   If false: The blue player will move to the spot, and the red player will do nothing
     * @return action as a TurnAction
     */
    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge,
            boolean isRedTurn) {
//        numTurns += 1;
//        System.out.println(numTurns);
        this.currentBoard = boardView;
        Tool tool = new Tool();
        currentLocation = currentBoard.getYourLocation(); // sets current location to player location on board
        TileType mostExpensiveTile = tool.itemToTile(tool.mostExpensiveResource(economy, currentBoard)); // updated most expensive tile type

        // can't leave charging station until fully charged
        if (currentCharge < maxCharge && boardView.getTileTypeAtLocation(currentLocation).equals(TileType.RECHARGE)) {
            return null;
        }
        // if robot needs to recharge
        if (currentCharge <= maxCharge * 0.25 && currentCharge <= maxCharge) {
            destination = tool.setDestination(TileType.RECHARGE, currentBoard);
            return tool.moveToDestination(currentLocation, destination);
        } else {
            if (currentInventorySize < maxInventorySize) { // mines most expensive resource until full inventory
                if (currentLocation.x != destination.x || currentLocation.y != destination.y) {
                    return tool.moveToDestination(currentLocation, destination);
                }
                if (mineCount < tool.minesRequired(mostExpensiveTile)) {
                    mineCount++;
                    return TurnAction.MINE;
                }
                destination = tool.nearestTile(mostExpensiveTile, currentBoard, currentLocation);
                mineCount = 0;
                return TurnAction.PICK_UP_RESOURCE;
            } else if (currentInventorySize == maxInventorySize) { // goes to market when inventory is full
                destination = tool.setDestination(TileType.RED_MARKET, currentBoard);

                if (currentLocation.x != destination.x || currentLocation.y != destination.y) {
                    return tool.moveToDestination(currentLocation, destination);
                }
                destination = tool.nearestTile(mostExpensiveTile, currentBoard, currentLocation);
                return tool.moveToDestination(currentLocation, destination);
            }
        }
        return null;
    }

    /**
     * Method that gets called on every time an item is picked up.
     *  Inventory size gets increased by one every time method is called.
     *
     * @param itemReceived The item received from the player's TurnAction on their last turn
     */
    @Override
    public void onReceiveItem(InventoryItem itemReceived) {
        currentInventorySize++;
    }

    /**
     * Method that gets called on every time robot steps on market and sells items in inventory.
     *  Clears out current inventory size and adds points earned to an int current score tracker.
     *
     * @param totalSellPrice The combined sell price for all items in your strategy's inventory
     */
    @Override
    public void onSoldInventory(int totalSellPrice) {
        currentScore += totalSellPrice;
        currentInventorySize = 0;
    }

    /**
     * Method that returns the name of strategy.
     *
     * @return name of strategy
     */
    @Override
    public String getName() {
        return "iTriedStrategy";
    }

    /**
     * Method called at the end of round to reset.
     *
     * @param pointsScored The total number of points this strategy scored
     * @param opponentPointsScored The total number of points the opponent's strategy scored
     */
    @Override
    public void endRound(int pointsScored, int opponentPointsScored) {
    }
}
