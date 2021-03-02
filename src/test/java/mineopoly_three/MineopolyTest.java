package mineopoly_three;

import java.awt.Point;
import java.util.HashMap;
import java.util.Random;
import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.ItemType;
import mineopoly_three.strategy.PlayerBoardView;
import mineopoly_three.strategy.PlayerStrategy;
import mineopoly_three.strategy.Tool;
import mineopoly_three.tiles.TileType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MineopolyTest {
    PlayerStrategy playerStrategy;
    Tool tool;
    TileType[][] boardTileType;
    PlayerBoardView playerBoardView;
    Economy economy;
    ItemType[] itemTypes;

    @Before
    public void setUp() {
        playerStrategy = new PlayerStrategy();
        tool = new Tool();
        itemTypes = new ItemType[]{ItemType.DIAMOND, ItemType.EMERALD, ItemType.RUBY};
        economy = new Economy(itemTypes);

        boardTileType = new TileType[][]{
            {TileType.RECHARGE, TileType.RED_MARKET, TileType.BLUE_MARKET, TileType.RESOURCE_RUBY},
            {TileType.EMPTY, TileType.RESOURCE_RUBY, TileType.RESOURCE_DIAMOND, TileType.RESOURCE_EMERALD},
            {TileType.RESOURCE_DIAMOND, TileType.EMPTY, TileType.RESOURCE_EMERALD, TileType.EMPTY},
            {TileType.RESOURCE_EMERALD, TileType.EMPTY, TileType.RESOURCE_DIAMOND, TileType.RESOURCE_RUBY}};

        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(), new Point(), 0);
    }

    /**
     * Helper method that updates playerBoardView.
     *
     * @param action as a turnAction
     */
    public void test_updateBoardView(TurnAction action) {
        if (action.equals(TurnAction.MOVE_UP)) {
            playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(),
                new Point((int)playerBoardView.getYourLocation().getX(),
                       (int)playerBoardView.getYourLocation().getY() + 1), new Point(), 0);
        } else if (action.equals(TurnAction.MOVE_DOWN)) {
            playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(),
                new Point((int)playerBoardView.getYourLocation().getX(),
                       (int)playerBoardView.getYourLocation().getY() - 1), new Point(), 0);
        } else if (action.equals(TurnAction.MOVE_LEFT)) {
            playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(),
                new Point((int)playerBoardView.getYourLocation().getX() - 1,
                             (int)playerBoardView.getYourLocation().getY()), new Point(), 0);
        } else if (action.equals(TurnAction.MOVE_RIGHT)) {
            playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(),
                new Point((int)playerBoardView.getYourLocation().getX() + 1,
                             (int)playerBoardView.getYourLocation().getY()), new Point(), 0);
        }
    }

    // test units for player strategy methods
    @Test
    public void test_initialize() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        assertEquals(new Point(2,2), playerStrategy.getDestination());
    }

    @Test
    public

    // test units for helper methods in Tool class
    @Test
    public void test_mostExpensiveResource(){
        assertEquals(ItemType.DIAMOND, tool.mostExpensiveResource(economy, playerBoardView));
    }

    @Test
    public void test_nearestTile() {
    }

    @Test
    public void test_moveToDestination() {
    }

    @Test
    public void test_minesRequired() {
        assertEquals(1, tool.minesRequired(TileType.RESOURCE_RUBY));
        assertEquals(2, tool.minesRequired(TileType.RESOURCE_EMERALD));
        assertEquals(3, tool.minesRequired(TileType.RESOURCE_DIAMOND));
    }

    @Test
    public void test_chargingLimit() {
    }
}
