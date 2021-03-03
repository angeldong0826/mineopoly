package mineopoly_three;

import java.awt.Point;
import java.util.HashMap;
import java.util.Random;
import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;
import mineopoly_three.strategy.PlayerBoardView;
import mineopoly_three.strategy.PlayerStrategy;
import mineopoly_three.strategy.Tool;
import mineopoly_three.tiles.TileType;
import org.junit.Assert;
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
            {TileType.RECHARGE, TileType.RED_MARKET, TileType.RESOURCE_DIAMOND, TileType.RESOURCE_RUBY},
            {TileType.EMPTY, TileType.RESOURCE_RUBY, TileType.BLUE_MARKET, TileType.RESOURCE_EMERALD},
            {TileType.RESOURCE_DIAMOND, TileType.EMPTY, TileType.RESOURCE_EMERALD, TileType.RECHARGE},
            {TileType.RESOURCE_EMERALD, TileType.RED_MARKET, TileType.RESOURCE_DIAMOND, TileType.RESOURCE_RUBY}};

        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(1,3), new Point(), 0);
    }

    // test units for player strategy methods
    @Test
    public void test_initialize() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        assertEquals(new Point(2,3), playerStrategy.getDestination());
    }

    @Test
    public void test_moveToClosestMostExpensiveResource() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(0,0),
            new Point(), 0);
        assertEquals(ItemType.DIAMOND, tool.mostExpensiveResource(economy, playerBoardView));
        assertEquals(TurnAction.MOVE_RIGHT, playerStrategy.getTurnAction(playerBoardView, economy,
            70, true));
    }

    // test units for charging-related moves
    @Test
    public void test_lowChargeMoveToClosestFirstCharger() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(3,3),
            new Point(), 0);
        assertEquals(TurnAction.MOVE_DOWN, playerStrategy.getTurnAction(playerBoardView, economy,
        20, true));
    }

    @Test
    public void test_lowChargeMoveToClosestSecondCharger() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(0,0),
            new Point(), 0);
        assertEquals(TurnAction.MOVE_UP, playerStrategy.getTurnAction(playerBoardView, economy,
            19, true));
    }

    @Test
    public void test_stayOnFirstBatteryWhenNotFullyCharged() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(3,1),
            new Point(), 0);
        Assert.assertNull(playerStrategy.getTurnAction(playerBoardView, economy,
            60, true));
    }

    @Test
    public void test_stayOnSecondBatteryWhenNotFullyCharged() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(0,3),
            new Point(), 0);
        Assert.assertNull(playerStrategy.getTurnAction(playerBoardView, economy,
            58, true));
    }

    @Test
    public void test_leaveFirstBatteryOnFullChargeToMostExpensiveTile() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(3,1),
            new Point(), 0);
        assertEquals(TurnAction.MOVE_LEFT, playerStrategy.getTurnAction(playerBoardView, economy,
            80, true)); // moves left first due to the order I wrote my moveToDestination in
        assertEquals(ItemType.DIAMOND, tool.mostExpensiveResource(economy, playerBoardView));
    }

    @Test
    public void test_leaveSecondBatteryOnFullChargeToMostExpensiveTile() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(0,3),
            new Point(), 0);
        assertEquals(TurnAction.MOVE_RIGHT, playerStrategy.getTurnAction(playerBoardView, economy,
            80, true)); // moves right first due to the order I wrote my moveToDestination in
        assertEquals(ItemType.DIAMOND, tool.mostExpensiveResource(economy, playerBoardView));
    }

    // test units for market-related moves
    @Test
    public void test_fullInventoryGoToClosestFirstMarket() {
        playerStrategy.initialize(4, 0, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(0,0),
            new Point(), 0);
        assertEquals(TurnAction.MOVE_RIGHT, playerStrategy.getTurnAction(playerBoardView, economy,
        77, true)); // moves right first because of the order I wrote moveToDestination in
    }

    @Test
    public void test_fullInventoryGoToClosestSecondMarket() {
        playerStrategy.initialize(4, 0, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(3,3),
            new Point(), 0);
        assertEquals(TurnAction.MOVE_LEFT, playerStrategy.getTurnAction(playerBoardView, economy,
            77, true)); // moves left first because of the order I wrote moveToDestination in
    }

    @Test
    public void test_clearInventorySizeOnFirstMarket() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(1,0),
            new Point(), 0);
        assertEquals(0, playerStrategy.getCurrentInventorySize());
    }

    @Test
    public void test_clearInventorySizeOnSecondMarket() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(1,3),
            new Point(), 0);
        assertEquals(0, playerStrategy.getCurrentInventorySize());
    }

    // test units for mining-related moves
    @Test
    public void test_mineMostExpensiveResource() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(2,3),
            new Point(), 0);
        assertEquals(ItemType.DIAMOND, tool.mostExpensiveResource(economy, playerBoardView));
        assertEquals(TurnAction.MINE, playerStrategy.getTurnAction(playerBoardView, economy, 79, true));
    }

    @Test
    public void test_pickUp() {
        playerStrategy.initialize(4, 5, 80, 480,
            playerBoardView, new Point(1,3), true, new Random());
        playerBoardView = new PlayerBoardView(boardTileType, new HashMap<>(), new Point(2,3),
            new Point(), 0);
        playerStrategy.getTurnAction(playerBoardView, economy, 75, true);
        playerStrategy.getTurnAction(playerBoardView, economy, 75, true);
        playerStrategy.getTurnAction(playerBoardView, economy, 75, true);
        assertEquals(TurnAction.PICK_UP_RESOURCE, playerStrategy.getTurnAction(playerBoardView, economy, 75, true));
    }

    // test units for helper methods in Tool class
    @Test
    public void test_mostExpensiveResource() {
        assertEquals(ItemType.DIAMOND, tool.mostExpensiveResource(economy, playerBoardView));
    }

    @Test
    public void test_nearestTile() {
        playerStrategy.initialize(4,5,80,750,
            playerBoardView, new Point(1,3), true, new Random());
        playerStrategy.getTurnAction(playerBoardView, economy, 80, true);
        Point point = tool.nearestTile(TileType.RESOURCE_RUBY, playerBoardView,
            playerStrategy.getCurrentLocation());
        assertEquals(new Point(1,2), point);
    }

    @Test
    public void test_moveToDestination() {
        assertEquals(TurnAction.MOVE_RIGHT, tool.moveToDestination(playerStrategy.getCurrentLocation(),
            new Point(2,3)));
    }

    @Test
    public void test_minesRequired() {
        assertEquals(1, tool.minesRequired(TileType.RESOURCE_RUBY));
        assertEquals(2, tool.minesRequired(TileType.RESOURCE_EMERALD));
        assertEquals(3, tool.minesRequired(TileType.RESOURCE_DIAMOND));
    }

    @Test
    public void test_setDestination() {
        playerStrategy.initialize(4,5,80,750,
            playerBoardView, new Point(1,3), true, new Random());
        assertEquals(new Point(2,2), tool.setDestination(TileType.BLUE_MARKET, playerBoardView));
    }

    @Test
    public void test_itemToTile() {
        assertEquals(TileType.RESOURCE_EMERALD, tool.itemToTile(ItemType.EMERALD));
        assertEquals(TileType.RESOURCE_DIAMOND, tool.itemToTile(ItemType.DIAMOND));
        assertEquals(TileType.RESOURCE_RUBY, tool.itemToTile(ItemType.RUBY));
    }
}
