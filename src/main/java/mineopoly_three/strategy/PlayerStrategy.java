package mineopoly_three.strategy;

import java.awt.Point;
import java.util.Random;
import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.tiles.TileType;

public class PlayerStrategy implements MinePlayerStrategy {
    private static int boardSize; // length and width of the square game board
    private int maxInventorySize; // maximum number of items that your player can carry at one time
    private int maxCharge; // amount of charge your robot starts with (number of tile moves before needing to recharge)
    private int winningScore; // first player to reach this score wins the round
    private PlayerBoardView startingBoard; // view of the GameBoard at the start of the game
    private Point startTileLocation; // Point representing your starting location in (x, y) coordinates
    private boolean isRedPlayer; // true if this strategy is the red player, false otherwise
    private Random random; // random number generator
    private PlayerBoardView playerBoardView; // PlayerBoardView object representing all the information about the board and the other player that strategy is allowed to access
    private Economy economy; // GameEngine's economy object which holds current prices for resources
    private int currentCharge; // amount of charge your robot has (number of tile moves before needing to recharge)
    private boolean isRedTurn; // true if red will move to the spot, and blue will do nothing. false if vice versa
    private PlayerBoardView currentBoard; // current game board
    private Point currentLocation; // current location of player on board
    private Point destination; // destination player tries to reach
    private int currentInventorySize = 0;
    private int currentScore = 0;
    private int mineCount = 0;
    private int numTurns = 0;

    public static int getBoardSize() {
        return boardSize;
    }

    public PlayerBoardView getStartingBoard() {
        return startingBoard;
    }

    public Point getDestination() {
        return destination;
    }

    public Point getCurrentLocation() {
        return currentLocation;
    }

    public Economy getEconomy() {
        return economy;
    }

    @Override
    public void initialize(int boardSize, int maxInventorySize, int maxCharge, int winningScore,
        PlayerBoardView startingBoard, Point startTileLocation, boolean isRedPlayer,
        Random random) {
        this.boardSize = boardSize;
        this.maxInventorySize = maxInventorySize;
        this.maxCharge = maxCharge;
        this.startingBoard = startingBoard;
        this.startTileLocation = startTileLocation;
        this.isRedPlayer = isRedPlayer;

        Tool tool = new Tool();
        destination = tool.nearestTile(TileType.RESOURCE_DIAMOND, startingBoard, startTileLocation);

    }

    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge,
            boolean isRedTurn) {
        numTurns += 1;
        System.out.println(numTurns);
        this.currentBoard = boardView;
        Tool tool = new Tool();
        currentLocation = currentBoard.getYourLocation();
        TileType mostExpensiveTile = tool.itemToTile(tool.mostExpensiveResource(economy, currentBoard));

        if (currentCharge < maxCharge && boardView.getTileTypeAtLocation(currentLocation).equals(TileType.RECHARGE)) {
            return null;
        }
        // if robot needs charging
        if (currentCharge <= maxCharge * 0.25 && currentCharge <= maxCharge) {
            destination = tool.setDestination(TileType.RECHARGE, currentBoard);
            return tool.moveToDestination(currentLocation, destination);
        } else {
            if (currentInventorySize < maxInventorySize) {
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
            } else if (currentInventorySize == maxInventorySize) {
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

    @Override
    public void onReceiveItem(InventoryItem itemReceived) {
        currentInventorySize++;
    }

    @Override
    public void onSoldInventory(int totalSellPrice) {
        currentScore += totalSellPrice;
        currentInventorySize = 0;
    }

    @Override
    public String getName() {
        return "UrMum";
    }

    @Override
    public void endRound(int pointsScored, int opponentPointsScored) {
    }
}
