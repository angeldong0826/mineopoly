package mineopoly_three.strategy;

import com.sun.istack.internal.NotNull;
import java.awt.Point;
import javax.print.attribute.standard.Destination;
import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.ItemType;
import mineopoly_three.tiles.Tile;
import mineopoly_three.tiles.TileType;
import mineopoly_three.util.DistanceUtil;

/**
 * Helper class that contains functions PlayerStrategy calls on to run game.
 */
public class Tool {
    PlayerStrategy playerStrategy = new PlayerStrategy(); // an instance of PlayerStrategy

    /**
     * Helper method to determine the most expensive resource in economy during game.
     *
     * @param board current game board setup
     * @return most expensive resource as a ItemType
     */
    public ItemType mostExpensiveResource(Economy economy, PlayerBoardView board) {
        if (economy == null || board == null) {
            return null;
        }
        ItemType mostExpensiveTile = null;
        int maxValue = 0;
        for (ItemType item: economy.getCurrentPrices().keySet()) {
            if (economy.getCurrentPrices().get(item) > maxValue) {
                maxValue = economy.getCurrentPrices().get(item);
                mostExpensiveTile = item;
            }
        }
        return mostExpensiveTile;
    }

    /**
     * Helper method to convert an item to a tile.
     *
     * @param itemType to be converted to tile
     * @return tile converted from item
     */
    public TileType itemToTile(ItemType itemType) {
        if (itemType == null) {
            return null;
        }
        TileType tileToReturn = TileType.EMPTY;
        if (itemType.equals(ItemType.RUBY)) {
            tileToReturn = TileType.RESOURCE_RUBY;
        }
        if (itemType.equals(ItemType.EMERALD)) {
            tileToReturn = TileType.RESOURCE_EMERALD;
        }
        if (itemType.equals(ItemType.DIAMOND)) {
            tileToReturn = TileType.RESOURCE_DIAMOND;
        }
        return tileToReturn;
    }

    /**
     * Helper method that calculates the closest tile to go to.
     *
     * @param tile type wanted to reach
     * @return location to go where the tile is at
     */
    public Point nearestTile(TileType tile, PlayerBoardView currentBoard, Point currentLocation) {
        Point destination = new Point(Integer.MAX_VALUE/3, Integer.MAX_VALUE/3); // destination that bot goes toward

        if (tile == null) {
            return null;
        }
        for (int row = 0; row < PlayerStrategy.getBoardSize(); row++) {
            for (int col = 0; col < PlayerStrategy.getBoardSize(); col++) {
                if (currentBoard.getTileTypeAtLocation(row, col).equals(tile)) {
                    Point temp = new Point(row, col);
                    if (DistanceUtil.getManhattanDistance(temp, currentLocation) < DistanceUtil.getManhattanDistance(destination, currentLocation)) {
                        destination = temp;
                    }
                }
            }
        }
        return destination;
    }

    /**
     * Helper method to determine the number of mine times needed.
     *
     * @return number of times to mine as an int
     */
    public int minesRequired(TileType tile) {
        if (tile.equals(TileType.RESOURCE_RUBY)) {
            return 1;
        } else if (tile.equals(TileType.RESOURCE_EMERALD)) {
            return 2;
        } else if (tile.equals(TileType.RESOURCE_DIAMOND)) {
            return 3;
        }
        return 0;
    }

    /**
     * Helper method to move robot to destination desired from current location.
     *
     * @param currentLocation current location of player
     * @param destination destination wanting to reach
     * @return actions performed by robot
     */
    public TurnAction moveToDestination(Point currentLocation, Point destination) {
        if (currentLocation == null || destination == null) {
            return null;
        }
        if (currentLocation.x < destination.x) {
            return TurnAction.MOVE_RIGHT;
        } else if (currentLocation.x > destination.x) {
            return TurnAction.MOVE_LEFT;
        } else if (currentLocation.y < destination.y) {
            return TurnAction.MOVE_UP;
        } else if (currentLocation.y > destination.y) {
            return TurnAction.MOVE_DOWN;
        }
        return null;
    }

    /**
     * Helper method to set destination as points for robot to go to.
     *
     * @param tile to get to
     * @param currentBoard current game board
     * @return destination as point
     */
    public Point setDestination(TileType tile, PlayerBoardView currentBoard) {
        if (tile == null || currentBoard == null) {
            return null;
        }
        Point destination = playerStrategy.getDestination();
        for (int row = 0; row < PlayerStrategy.getBoardSize(); row++) {
            for (int col = 0; col < PlayerStrategy.getBoardSize(); col++) {
                if (currentBoard.getTileTypeAtLocation(row, col).equals(tile)) {
                    destination = new Point (row, col);
                }
            }
        }
        return destination;
    }
}
