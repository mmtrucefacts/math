package com.regawmod.hardestgame.level;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Polygon;
import com.regawmod.entity.Entity;
import com.regawmod.hardestgame.Resources;
import com.regawmod.hardestgame.entity.Enemy;
import com.regawmod.hardestgame.entity.GoldCoin;
import com.regawmod.hardestgame.entity.Player;
import com.regawmod.slick.interfaces.Renderable;
import com.regawmod.slick.interfaces.Updatable;

/**
 * An abstract level for the game.
 * 
 * @author Dan Wager
 */
public abstract class Level implements Updatable, Renderable
{
    /** The offset of the level in the window, public for now :( */
    public static final float LEVEL_OFFSET = 60f;

    /** The player in the level */
    private Player player;

    /** Our level's bounding polygon */
    private Polygon boundingPoly;
    /** The bounding poly for the start zone */
    private Polygon startZone;
    /** The bounding poly for the end zone */
    private Polygon endZone;
    /** The color for the zones */
    private Color zoneColor;

    /** The collection of enemies in the level */
    private List<Enemy> enemies;
    /** The colection of gold coins in the level */
    private List<GoldCoin> goldCoins;

    /** The level's background image */
    private Image levelImage;

    /** The number of gold coings collected by the player */
    private int coinsCollected;
    /** The total number of coins in the level */
    private int totalCoins;

    /** If the level has been completed */
    private boolean levelCompleted;

    /** The player's starting X coordinate */
    private float playerStartX;
    /** The player's starting Y coordinate */
    private float playerStartY;

    /** If the player has died */
    private boolean playerDied;

    /**
     * Creates a new {@link Level}.
     */
    protected Level()
    {
        this.enemies = new ArrayList<Enemy>();
        this.goldCoins = new ArrayList<GoldCoin>();

        this.boundingPoly = new Polygon();
        this.startZone = new Polygon();
        this.endZone = new Polygon();

        this.zoneColor = new Color(181, 254, 180);

        this.playerStartX = 0;
        this.playerStartY = 0;

        loadLevelImage();

        initBoundingPolygon();
        initStartZonePolygon();
        initEndZonePolygon();
        initPlayerStartPosition();

        checkZoneStates();

        initEnemies();
        initGoldCoins();

        this.totalCoins = this.goldCoins.size();
        this.coinsCollected = 0;

        this.levelCompleted = false;

        this.playerDied = false;
    }

    /**
     * Sets the player's starting position.
     * 
     * @param x The X coordinate
     * @param y The Y coordinate
     */
    protected final void setPlayerStartPosition(float x, float y)
    {
        this.playerStartX = x;
        this.playerStartY = y + LEVEL_OFFSET;

        this.player = new Player(this);
    }

    /**
     * Checks that the bounding polygons have been set up correctly.
     * Also makes sure that the player starts in the start zone.
     */
    private void checkZoneStates()
    {
        if (this.boundingPoly.getPointCount() < 4)
            throw new IllegalStateException("Bounding polygon is not set up correctly!");

        if (this.startZone.getPointCount() < 4)
            throw new IllegalStateException("Start zone is not set up correctly!");

        if (this.endZone.getPointCount() < 4)
            throw new IllegalStateException("End zone is not set up correctly!");

        if (!this.startZone.contains(this.player.getBody()))
            throw new IllegalStateException("Player must start in the Start zone.");
    }

    /**
     * Loads the level image so we can render it.
     */
    private void loadLevelImage()
    {
        this.levelImage = Resources.getLevelImage(this.getClass().getSimpleName());
        //new Image(LevelLoader.LEVEL_RES_DIRECTORY + File.separator + this.getClass().getSimpleName() + ".png");
    }

    /**
     * Initialize the bounding polygon for the level here.
     * Must initialize in clockwise order.
     */
    protected abstract void initBoundingPolygon();

    /**
     * Initialize the bounding polygon for the start zone here.
     * Must initialize in clockwise order.
     */
    protected abstract void initStartZonePolygon();

    /**
     * Initialize the bounding polygon for the end zone here.
     * Must initialize in clockwise order.
     */
    protected abstract void initEndZonePolygon();

    /**
     * Initialize the player's start position here.
     */
    protected abstract void initPlayerStartPosition();

    /**
     * Initialize and add the enemies for the level here.
     */
    protected abstract void initEnemies();

    /**
     * Initialize and add the gold coins for the level here.
     */
    protected abstract void initGoldCoins();

    /**
     * Adds an enemy to the level.
     * 
     * @param enemy The enemy to add to the level
     */
    protected final void addEnemy(Enemy enemy)
    {
        enemy.setCenterY(enemy.getCenterY() + LEVEL_OFFSET);

        if (enemy.isBoundedByLevel() && !this.boundingPoly.contains(enemy.getBody()))
            throw new IllegalStateException("Bounded Enemy at x:" + enemy.getCenterX() + " y:" +
                    (enemy.getCenterY() - LEVEL_OFFSET) + " is placed out of bounds of the level!");

        this.enemies.add(enemy);
    }

    /**
     * Adds a collection of enemies to the level.
     * 
     * @param enemies The collection of enemies to add to the level
     */
    protected final void addEnemies(List<Enemy> enemies)
    {
        for (Enemy e : enemies)
        {
            e.setCenterY(e.getCenterY() + LEVEL_OFFSET);

            if (e.isBoundedByLevel() && !this.boundingPoly.contains(e.getBody()))
                throw new IllegalStateException("Bounded Enemy at x:" + e.getCenterX() + " y:" +
                        (e.getCenterY() - LEVEL_OFFSET) + " is placed out of bounds of the level!");

            this.enemies.add(e);
        }
    }

    /**
     * Adds a gold coin to the level.
     * 
     * @param goldCoin The gold coin to add
     */
    protected final void addGoldCoin(GoldCoin goldCoin)
    {
        goldCoin.setCenterY(goldCoin.getCenterY() + LEVEL_OFFSET);

        if (!this.boundingPoly.contains(goldCoin.getBody()))
            throw new IllegalStateException("GoldCoin at x:" + goldCoin.getCenterX() + " y:" +
                    (goldCoin.getCenterY() - LEVEL_OFFSET) + " is placed out of bounds of the level!");

        this.goldCoins.add(goldCoin);
    }

    /**
     * Gets a value indicating the player's current X position.
     * 
     * @return The player's current X position
     */
    public final float getPlayerX()
    {
        return this.player.getCenterX();
    }

    /**
     * Gets a value indicating the player's current Y position.
     * 
     * @return The player's current Y position
     */
    public final float getPlayerY()
    {
        return this.player.getCenterY();
    }

    /**
     * Adds a point to the level's bounding polygon.
     * 
     * @param x The X coordinate
     * @param y The Y coordinate
     */
    protected final void addBoundingPolygonPoint(float x, float y)
    {
        this.boundingPoly.addPoint(x, y + LEVEL_OFFSET);
    }

    /**
     * Adds a point to the start zone's bounding polygon.
     * 
     * @param x The X coordinate
     * @param y The Y coordinate
     */
    protected final void addStartZonePolygonPoint(float x, float y)
    {
        this.startZone.addPoint(x, y + LEVEL_OFFSET);
    }

    /**
     * Adds a point to the end zone's bounding polygon.
     * 
     * @param x The X coordinate
     * @param y The Y coordinate
     */
    protected final void addEndZonePolygonPoint(float x, float y)
    {
        this.endZone.addPoint(x, y + LEVEL_OFFSET);
    }

    /**
     * Gets a value indicating if the entity collided with the start or end zone.
     * 
     * @param entity The entity to check for collisions
     * @return If the entity collided with the start or end zone
     */
    public final boolean collidesWithZones(Entity entity)
    {
        return entity.getBody().intersects(this.startZone) || this.startZone.contains(entity.getBody()) ||
                entity.getBody().intersects(this.endZone) || this.endZone.contains(entity.getBody());
    }

    /**
     * Gets a value indicating if the entity collided with the level walls.
     * 
     * @param entity The entity to check for collisions
     * @return If the entity collided with the level walls
     */
    public final boolean collidesWithWall(Entity entity)
    {
        // We need this !contains instead of intersects because large deltas 
        // might bring the entity outside of the bounding poly

        return !this.boundingPoly.contains(entity.getBody());
    }

    /**
     * Gets a value indicating if the entity collided with an enemy.
     * 
     * @param entity The entity to check for collisions
     * @return If the entity collided with an enemy
     */
    public final boolean collidesWithEnemy(Entity entity)
    {
        for (Entity e : this.enemies)
            if (entity.getBody().intersects(e.getBody()) && !e.equals(entity))
                return true;

        return false;
    }

    /**
     * Gets a value indicating if the entity collided with a gold coin.
     * 
     * @param entity The entity to check for collisions
     * @return If the entity collided with a gold coin
     */
    public final boolean collidesWithGoldCoin(Entity entity)
    {
        boolean collided = false;

        for (GoldCoin coin : this.goldCoins)
        {
            if (!coin.hasBeenCollected() && entity.getBody().intersects(coin.getBody()))
            {
                onGoldCoinCollected(coin);

                coin.flagAsCollected();
                collided = true;
            }
        }

        return collided;
    }

    /**
     * Resets the level after a player has died.
     * "Revives" the player.
     * Resets all gold coins to original positions.
     */
    private final void resetLevelAfterEnemyCollision()
    {
        this.player.revive();
        this.playerDied = true;

        if (this.coinsCollected > 0)
        {
            this.coinsCollected = 0;
            this.goldCoins.clear();
            initGoldCoins();
        }

        onPlayerRespawn();
    }

    /**
     * Notifies all enemies that the player has just respawned.
     */
    private void onPlayerRespawn()
    {
        for (Enemy e : this.enemies)
            e.onPlayerRespawn();
    }

    /**
     * Notifies all enemies that the player has died.
     */
    private void onPlayerDeath()
    {
        for (Enemy e : this.enemies)
            e.onPlayerDeath();
    }

    @Override
    public final void update(GameContainer gc, float dt)
    {
        updateEnemies(gc, dt);
        updateGoldCoins(gc, dt);
        updatePlayer(gc, dt);

        checkLevelState();
    }

    /**
     * Checks the ending conditions of the level.
     * Has the level been completed?
     */
    private void checkLevelState()
    {
        if (this.player.hasDied())
        {
            onPlayerDeath();
        }
        else if (this.player.shouldRevive())
        {
            resetLevelAfterEnemyCollision();
        }
        else if (playerHasWon())
        {
            this.levelCompleted = true;
        }
    }

    /**
     * Gets a value indicating if the player has beat the level.
     * 
     * @return If the player has beat the level
     */
    private boolean playerHasWon()
    {
        return allCoinsCollected() && playerInEndZone();
    }

    /**
     * Gets a value indicating if the player is in the end zone.
     * 
     * @return If the player is in the end zone
     */
    private boolean playerInEndZone()
    {
        return this.endZone.contains(this.player.getCenterX(), this.player.getCenterY());
    }

    /**
     * Gets a value indicating if all of the gold coins have been collected.
     * 
     * @return If all of the gold coins have been collected
     */
    private boolean allCoinsCollected()
    {
        return this.coinsCollected == this.totalCoins;
    }

    @Override
    public final void render(Graphics g)
    {
        this.levelImage.draw(0, LEVEL_OFFSET);

        renderZones(g);

        renderEnemies(g);
        renderPlayer(g);
        renderGoldCoins(g);
    }

    /**
     * Renders the bounding polygon for the level.
     * 
     * @param g The graphics object
     */
    private void renderBoundingPoly(Graphics g)
    {
        g.setColor(Color.cyan);
        g.draw(this.boundingPoly);
    }

    /**
     * Renders the start and end zones.
     * 
     * @param g The graphics object
     */
    private void renderZones(Graphics g)
    {
        g.setColor(this.zoneColor);
        g.fill(this.startZone);
        g.fill(this.endZone);
    }

    /**
     * Updates the player in the game.
     * 
     * @param gc The game container
     * @param dt The delta time
     */
    private void updatePlayer(GameContainer gc, float dt)
    {
        this.playerDied = false;
        this.player.update(gc, dt);
    }

    /**
     * Updates the enemies in the game.
     * 
     * @param gc The game container
     * @param dt The delta time
     */
    private void updateEnemies(GameContainer gc, float dt)
    {
        for (Entity e : this.enemies)
            e.update(gc, dt);

        removeFlaggedEnemies();
    }

    /**
     * Removes all enemies flagged for removal.
     */
    private void removeFlaggedEnemies()
    {
        for (int i = this.enemies.size() - 1; i >= 0; i--)
            if (this.enemies.get(i).shouldRemove())
                this.enemies.remove(i);
    }

    /**
     * Updates the gold coins in the game.
     * 
     * @param gc The game container
     * @param dt The delta time
     */
    private void updateGoldCoins(GameContainer gc, float dt)
    {
        for (Entity g : this.goldCoins)
            g.update(gc, dt);

        removeFlaggedGoldCoins();
    }

    /**
     * Renders the player.
     * 
     * @param g The graphics object
     */
    private void renderPlayer(Graphics g)
    {
        g.setColor(Color.white);
        this.player.render(g);
    }

    /**
     * Renders all of the enemies in the level.
     * 
     * @param g The graphics object
     */
    private void renderEnemies(Graphics g)
    {
        g.setColor(Color.white);
        for (Entity e : this.enemies)
            e.render(g);
    }

    /**
     * Renders all of the gold coins in the level.
     * 
     * @param g The graphics object
     */
    private void renderGoldCoins(Graphics g)
    {
        g.setColor(Color.white);
        for (Entity e : this.goldCoins)
            e.render(g);
    }

    /**
     * Removes all gold coins flagged for removal.
     */
    private void removeFlaggedGoldCoins()
    {
        for (int i = this.goldCoins.size() - 1; i >= 0; i--)
        {
            if (this.goldCoins.get(i).shouldRemove())
            {
                this.goldCoins.remove(i);
                this.coinsCollected++;
            }
        }
    }

    /**
     * Notifies all enemies that the player has collected a gold coin.
     * 
     * @param coin The coin that was collected
     */
    private void onGoldCoinCollected(GoldCoin coin)
    {
        for (Enemy e : this.enemies)
            e.onCoinCollected(coin.getCenterX(), coin.getCenterY(), this.totalCoins - this.coinsCollected);
    }

    /**
     * Gets a value indicating if the level has been completed.
     * 
     * @return If the level has been completed
     */
    public final boolean isLevelComplete()
    {
        return this.levelCompleted;
    }

    /**
     * Gets a value indicating the starting X coordinate of the player.
     * 
     * @return The starting X coordinate of the player
     */
    public final float getPlayerStartX()
    {
        return this.playerStartX;
    }

    /**
     * Gets a value indicating the starting Y coordinate of the player.
     * 
     * @return The starting Y coordinate of the player
     */
    public final float getPlayerStartY()
    {
        return this.playerStartY;
    }

    /**
     * Gets a value indicating if the player has died.
     * 
     * @return If the player has died
     */
    public final boolean hasPlayerDied()
    {
        return this.playerDied;
    }
}
