package tankgame;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Button;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import gamengine.game.Background;
import gamengine.game.BackgroundObject;
import gamengine.game.GameClock;
import gamengine.game.IGameWorld;
import gamengine.game.ObjectManager;
import gamengine.game.SchedulingObject;
import gamengine.game.Wall;
import gamengine.modifiers.AbstractGameModifier;
import gamengine.modifiers.weapons.AbstractWeapon;
import gamengine.utils.Constants;
import gamengine.utils.GameSounds;
import gamengine.utils.Utils;
import tankgame.android.JoystickView;
import tankgame.android.NetworkUtil;
import tankgame.game.BigExplosion;
import tankgame.game.Bullet;
import tankgame.game.PlayerShip;
import tankgame.game.PowerUp;
import tankgame.game.SmallExplosion;
import tankgame.game.UiInfo;
import tankgame.network.TCPClient;
import tankgame.ui.Dimension;
import tankgame.ui.InfoBar;
import tankgame.ui.InterfaceObject;
import tankgame.ui.ScoreTable;
import tankgame.weapons.SimpleWeapon;
import tankgame.weapons.WeaponBounceMissile;
import tankgame.weapons.WeaponRocket;
import tankgame.weapons.WeaponShield;

// extending JPanel to hopefully integrate this into an applet
// but I want to separate out the Applet and Application implementations
public final class TankWorld implements Runnable, Observer, IGameWorld, SurfaceHolder.Callback, TCPClient.OnMessageReceived {

    public static final int TIME_BETWEEN_FRAMES = 2;
    private Thread thread;

    // GameWorld is a singleton class!
    private static final TankWorld game = new TankWorld();
    public static final GameSounds sound = new GameSounds();
    public static final GameClock clock = new GameClock();
    public static float density;

//    GameMenu menu;
//    public Level level;

    private Bitmap bimg;
    //    int score = 0, life = 4;
//    Point speed = new Point(0,1);
//    Point noSpeed = new Point(0, 0);
    Random generator = new Random();
    int sizeX, sizeY;

    /*Some ArrayLists to keep track of game things*/
    private ArrayList<BackgroundObject> background;
    private ConcurrentHashMap<Integer, Bullet> bullets;
    private HashMap<Integer, PlayerShip> mEnemies;
    private PlayerShip mPlayer;
    private HashMap<Integer, InfoBar> ui;
    private ConcurrentHashMap<Integer ,PowerUp> powerups;
    private TCPClient mTcpClient;

    public static HashMap<String, Bitmap> sprites;
//    public static HashMap<String, MotionController> motions = new HashMap<String, MotionController>();

    // is player still playing, did they win, and should we exit
    private boolean gameOver, gameWon, gameFinished;
    //    BitmapObserver observer;
    private ObjectManager objectManager;
    private boolean running = false;
    private SurfaceHolder holder;
    private Activity activity;
    private JoystickView mHandleControls;
    private Button[] mControls;

    // constructors makes sure the game is focusable, then
    // initializes a bunch of ArrayLists
    private TankWorld() {
//        this.setFocusable(true);
        background = new ArrayList<BackgroundObject>();
        bullets = new ConcurrentHashMap<>();
        mEnemies = new HashMap<>();
//        playersInPlay = new ArrayList<PlayerShip>();
        ui = new HashMap<>();
        powerups = new ConcurrentHashMap<>();

        sprites = new HashMap<String, Bitmap>();
    }

    /* This returns a reference to the currently running game*/
    public static TankWorld getInstance() {
        return game;
    }

    public Dimension getSize() {
        return new Dimension(sizeX, sizeY);
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    /*Game Initialization*/
    public void init(Activity context, SurfaceHolder holder, Button[] controls, JoystickView jv) {
//        setBackground(new ColorDrawable(Color.WHITE));
        this.activity = context;
        this.holder = holder;
        ScoreTable.getInstance().init(activity);
        this.density = context.getResources().getDisplayMetrics().density;

        loadSprites();

        gameOver = false;
//        observer = this;

        objectManager = new ObjectManager(this);
        addClockObserver(objectManager);
        initBattleField();
        mControls = controls;
        mHandleControls = jv;
    }

    /*Functions for loading Bitmap resources*/
    private void loadSprites() {
        sprites.put("wall1", getSprite("wall1"));
        sprites.put("wall2", getSprite("wall2"));
        sprites.put("background", getSprite("background"));

        sprites.put("bullet", BitmapFactory.decodeResource(activity.getResources(), R.drawable.bullet1));

        sprites.put("player1", getSprite("tank1"));
        sprites.put("player2", getSprite("tank2"));
        Bitmap explosion_large_strip7 = getSprite("explosion_large_strip7");
        Bitmap explosion_small_strip6 = getSprite("explosion_small_strip6");
        int boxSize = explosion_small_strip6.getHeight();
        int i;
        for (i = 1; i <= 6; i++) {
            Bitmap bitmap = Bitmap.createBitmap(explosion_small_strip6, (i - 1) * boxSize, 0, boxSize, boxSize);
            sprites.put("explosion1_" + i, bitmap);
        }
        boxSize = explosion_large_strip7.getHeight();
        for (i = 1; i <= 7; i++) {
            Bitmap bitmap = Bitmap.createBitmap(explosion_large_strip7, (i - 1) * boxSize, 0, boxSize, boxSize);
            sprites.put("explosion2_" + i, bitmap);
        }

        sprites.put("weapons", getSprite("weapon_strip3"));
        sprites.put("pickups", getSprite("pickup_strip4"));
        sprites.put("rocket", getSprite("rocket_strip60"));
        sprites.put("powerup", getSprite("powerup"));
        sprites.put("youwon", getSprite("youwin"));
    }

    public Bitmap getSprite(String name) {
        Resources resources = activity.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable", activity.getPackageName());
        Bitmap bm = BitmapFactory.decodeResource(resources, resourceId);
        return bm;
    }

    /**
     * ******************************
     * These functions GET things	* from the game world	*
     * ******************************
     */
    @Override
    public int getFrameNumber() {
        return clock.getFrame();
    }

    public int getTime() {
        return clock.getTime();
    }

    /**
     * @param theObject
     */
    public void removeClockObserver(Observer theObject) {
        clock.deleteObserver(theObject);
    }

    public ListIterator<BackgroundObject> getBackgroundObjects() {
        return background.listIterator();
    }
//    

    public Iterator<PlayerShip> getEnemies() {
//        return mEnemies.listIterator();
        return mEnemies.values().iterator();
    }
//    

    public Iterator<Bullet> getBullets() {
        return bullets.values().iterator();
    }

    public ConcurrentHashMap<Integer, Bullet> getBulletsHashMap()
    {
        return bullets;
    }

    public HashMap<Integer, PlayerShip> getEnemiesHashMap()
    {
        return mEnemies;
    }
    /**
     * ******************************
     * These functions ADD things	* to the game world	*
     * ******************************
     */
    public void addBullet(Bullet... newObjects) {
        for (Bullet bullet : newObjects) {
            bullets.put(bullet.getId(), bullet);
        }
    }
//    

    public void addPlayer(PlayerShip... newObjects) {
        for (PlayerShip player : newObjects) {
            mEnemies.put(player.getId(), player);
//            playersInPlay.add(player);
//            ui.add(new InfoBar(player));
        }
//        mScoreInfo = new ScoreInfo(newObjects);
    }

    // add background items (islands)
    public void addBackground(BackgroundObject newObjects) {
//        for (BackgroundObject object : newObjects) {
        background.add(newObjects);
//        }
    }

    //    // add power ups to the game world
//    public void addPowerUp(Ship powerup) {
//        powerups.add(powerup);
//    }
//    

    public void addRandomPowerUp() {
//        if (generator.nextInt(10) % 2 == 0) {
            Point p = new Point();
            PowerUp pu;

            int pickupIndex = generator.nextInt(4);

            while (true) {
                p.x = generator.nextInt(sizeX);
                p.y = generator.nextInt(sizeY);

                pu = new PowerUp();
                pu.setLocation(p);
                pu.setPickUpIndex(pickupIndex);
                if (pu.checkForCollision(background)) {
                    continue;
                }
//                if (pu.checkForCollision(mEnemies)) {
//                    continue;
//                }
                ArrayList<PowerUp> alPowerUps = new ArrayList<>(powerups.values());
                if (pu.checkForCollision(alPowerUps)) {
                    continue;
                }
                break;
            }
            getTcpClient().sendMessage(NetworkUtil.marshall(pu, Constants.CMSG_ADD_POWER_UP));
//        }
    }

    public void addPowerUpFromServer(DataInputStream dataInput) throws IOException {
        //read data
        PowerUp powerUp = new PowerUp();
        powerUp.fromDataInputStream(dataInput);

        //if power up is in hm then remove it
        if (powerups.containsKey(powerUp.getId())) {
            powerups.remove(powerUp.getId());
            return;
        }
        //if its not in the hm, then add it
        //add powerup
        Bitmap biPickupBitmap = sprites.get("pickups");
        int boxSize = biPickupBitmap.getHeight();
        Bitmap pickupBitmap =
                Bitmap.createBitmap(biPickupBitmap, powerUp.getPickUpIndex() * boxSize, 0,
                        boxSize, boxSize);
        powerUp.setImage(pickupBitmap);
        switch (powerUp.getPickUpIndex()) {
            case 2:
                powerUp.setWeapon(new WeaponShield(10, 10));
                break;
            case 1:
                powerUp.setWeapon(new WeaponBounceMissile(10, 10));
                break;
            case 0:
                powerUp.setWeapon(new WeaponRocket(20, 10));
                break;
            default:
                powerUp.setWeapon(new SimpleWeapon(10, 10));
        }
        powerups.put(powerUp.getId(), powerUp);
    }

    public void addClockObserver(Observer theObject) {
        clock.addObserver(theObject);
    }

    // this is the main function where game stuff happens!
    // each frame is also drawn here
    public void drawFrame(int w, int h, Canvas g2) {
        ListIterator<?> iterator = getBackgroundObjects();
        while (iterator.hasNext()) {
            BackgroundObject obj = (BackgroundObject) iterator.next();
            obj.update(w, h);
            if (obj.getY() > h || !obj.show) {
                iterator.remove();
            }
            obj.draw(g2);
        }

        if (!gameFinished) {

            //draw enemies
            Iterator<PlayerShip> iteratorPlayers = getEnemies();
            while (iteratorPlayers.hasNext())
            {
                PlayerShip playerShipEntry = iteratorPlayers.next();
                playerShipEntry.draw(g2);
//                player.draw(g2);
            }
            // remove stray enemy bullets and draw
            Iterator<Bullet> iteratorBullets = getBullets();
            while (iteratorBullets.hasNext()) {
                Bullet bullet = iteratorBullets.next();
                Iterator<PlayerShip> enemies = getEnemies();
                boolean collision = false;
//                while (enemies.hasNext()) {
//                    PlayerShip player = enemies.next();
//                    PlayerShip mPlayer = this.mPlayer;
                    if (collision = bullet.collision(mPlayer) && bullet.getOwner() != mPlayer) {
                        mPlayer.damage(bullet.getStrength());
                        UiInfo uiInfo = ui.get(mPlayer.getId()).getUiInfo();
                        uiInfo.setHealth(mPlayer.getHealth());
                        getTcpClient().sendMessage(NetworkUtil.marshall(uiInfo, Constants.CMSG_UI_INFO_UPDATE));
                        bullet.show = false;
                        getTcpClient().sendMessage(NetworkUtil.marshall(bullet, Constants.CMSG_UPDATE_BULLET));
                        if (!mPlayer.show) {
                            bullet.getOwner().incrementScore(1);
                            uiInfo = ui.get(bullet.getOwner().getId()).getUiInfo();
                            uiInfo.setScore(uiInfo.getScore()+1);
                            getTcpClient().sendMessage(NetworkUtil.marshall(uiInfo, Constants.CMSG_UI_INFO_UPDATE));
                            mPlayer.show = true;
                        }
                        else
                            addBackground(new SmallExplosion(bullet.getLocationPoint()));
                        //i actually dont need to do this server will send an update, but it will be fast for current user
                        iteratorBullets.remove();
//                        if (player.isDead()) {
//                            enemies.remove();
//                            if (mEnemies.size() == 0) {
//                                gameOver = true;
//                            }
//                        }
                        break;
                    }

//                }
//                if (collision) {
//                    break;
//                }

                while (enemies.hasNext()) {
                    PlayerShip player = enemies.next();
                    if (collision = bullet.collision(player) && bullet.getOwner() != player) {
//                        player.damage(bullet.getStrength());
                        if (player.getHealth()-bullet.getStrength()<=0)
                            addBackground(new BigExplosion(player.getLocationPoint()));
                        bullet.show = false;
                        getTcpClient().sendMessage(NetworkUtil.marshall(bullet, Constants.CMSG_UPDATE_BULLET));
                        iteratorBullets.remove();
//                        if (player.show) {
//                            player.show = true;
//                        }
                        addBackground(new SmallExplosion(bullet.getLocationPoint()));

                    }
                }
                ListIterator<BackgroundObject> backgroundObjects = getBackgroundObjects();
                while (backgroundObjects.hasNext()) {
                    Object bkg = backgroundObjects.next();
                    Wall wall;
                    if (bkg instanceof Wall) {
                        wall = (Wall) bkg;
                    } else {
                        continue;
                    }
                    if (collision = bullet.collision(wall)) {
                        addBackground(new SmallExplosion(bullet.getLocationPoint()));
                        bullet.show = false;
                        if (wall.isBreakable()) {
                            try {
                                wall.show = false;
                                Method m = TankWorld.class.getMethod("addBackground", BackgroundObject.class);
                                SchedulingObject so = new SchedulingObject(wall, m, getFrameNumber() + 600);
                                objectManager.addObject(so);
                            } catch (NoSuchMethodException ex) {
                                Logger.getLogger(TankWorld.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SecurityException ex) {
                                Logger.getLogger(TankWorld.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                    }
                }
                if (collision) {
                    getTcpClient().sendMessage(NetworkUtil.marshall(bullet, Constants.CMSG_UPDATE_BULLET));
                    break;
                }

                if (bullet.getY() > h + 10 || bullet.getY() < -10) {
                    iteratorBullets.remove();
                }
                bullet.update(w, h);
                //update bullet for other players on server
                //thinking about moving it to update method??
                getTcpClient().sendMessage(NetworkUtil.marshall(bullet, Constants.CMSG_UPDATE_BULLET));
                bullet.draw(g2);
            }

            //draw player
            if (mPlayer!=null)
            {

                boolean collision = false;
                ListIterator<BackgroundObject> backgroundObjects = getBackgroundObjects();
                while (backgroundObjects.hasNext()) {
                    Object background = backgroundObjects.next();
                    Wall wall = null;
                    if (background instanceof Wall) {
                        wall = (Wall) background;
                    } else {
                        continue;
                    }
                    if (collision = mPlayer.collision(wall)) {
                        break;
                    }
                }
                if (!collision) {
                    mPlayer.update(w, h);
                } else {
                    mPlayer.bounce(w, h);
                }
                if (mPlayer.canBeDrawn())
                    mPlayer.draw(g2);
            }
//            
//            // powerups
            Iterator<PowerUp> iteratorPowerUps = powerups.values().iterator();
            while (iteratorPowerUps.hasNext() ) {
                PowerUp powerup = iteratorPowerUps.next();
                if (mPlayer == null)
                    break;
//                ListIterator<PlayerShip> players = getEnemies();
//                while (players.hasNext()) {
//                    PlayerShip player = players.next();

                    if (powerup.collision(mPlayer)) {
                        AbstractWeapon weapon = powerup.getWeapon();
                        mPlayer.setWeapon(weapon);

                        UiInfo uiInfo = ui.get(mPlayer.getId()).getUiInfo();
                        uiInfo.setWeaponId(weapon.getWeaponImageID());
                        getTcpClient().sendMessage(NetworkUtil.marshall(uiInfo, Constants.CMSG_UI_INFO_UPDATE));

//                        ByteBuffer bb = ByteBuffer.allocate(4).putInt(powerup.getId());
                        getTcpClient().sendMessage(NetworkUtil.marshall(powerup, Constants.CMSG_ADD_POWER_UP));
//                        powerup.die();
                        iteratorPowerUps.remove();
                    }
//                }
                powerup.draw(g2);
            }
            if (getFrameNumber()!=0 && powerups.size() < 10 && getFrameNumber() % 100 == 0) {
                addRandomPowerUp();
            }
//            
//            // interface stuff
            Iterator<Map.Entry<Integer, InfoBar>> iteratorUI = ui.entrySet().iterator();
            while (iteratorUI.hasNext()) {
                Map.Entry<Integer, InfoBar> ioMap = iteratorUI.next();
                InterfaceObject object = ioMap.getValue();
                object.draw(g2);
            }
        } // end game stuff
        else {
            Paint p = new Paint();
            p.setColor(Color.WHITE);
//            g2.setFont(new Font("Calibri", Font.PLAIN, 24));
            if (!gameWon) {
                g2.drawBitmap(sprites.get("gameover"), w / 3 - 50, h / 2, null);
            } else {
                g2.drawBitmap(sprites.get("youwon"), sizeX / 3, 100, null);
            }
//            g2.drawText("Score", sizeX / 3, 400, p);
//            int i = 1;
//            for (PlayerShip player : mEnemies) {
//                g2.drawText("sss" + ": " + Integer.toString(player.getScore()), sizeX / 3, 375 + 50 * i, new Paint());
//                i++;
//            }
        }

        getTcpClient().sendMessage(NetworkUtil.marshall(new byte[0], Constants.CMSG_HEARTBEAT));

    }

    public ArrayList<PowerUp> getPowerups() {
        return new ArrayList<>(powerups.values());
    }

    public Canvas createGraphics2D(int w, int h) {

        Canvas canvas;
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
//            bimg = (Bitmap) createBitmap(w, h);
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            bimg = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap

        }
        canvas = new Canvas(bimg);
        canvas.drawColor(Color.WHITE);
        return canvas;
    }

    /* paint each frame */
    public void paint(Canvas g) {
        if (g==null)
            return;
        if (!mEnemies.isEmpty() || mPlayer!=null) {
            clock.tick();
        }
//        Dimension windowSize = getWindowSize();
//        Graphics2D g2 = createGraphics2D(windowSize.width, windowSize.height);
        Canvas g2 = createGraphics2D(sizeX, sizeY);     //g2 is connected to bimg
        drawFrame(sizeX, sizeY, g2);        //this draws on bimg
//        g2.dispose();
//        g.drawBitmap(bimg, 0, 0, new Paint());

        //draw viewing area for player
        if (mPlayer != null) {
            Dimension viewDim = new Dimension(g.getWidth(), g.getHeight());
            checkViewDimension(viewDim);
            Point loc = getPlayerWindowLocation(mPlayer, viewDim);
            Bitmap viewArea = Bitmap.createBitmap(bimg, loc.x, loc.y, viewDim.width, viewDim.height);
            g.drawBitmap(viewArea, 0, 0, new Paint());
        }

        //drawn mini map
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bimg, bimg.getWidth() / 8, bimg.getHeight() / 8, false);
        Rect bounds = g.getClipBounds();
        Paint transparentPaint = new Paint();
        transparentPaint.setAlpha(50);
        g.drawBitmap(resizedBitmap, g.getWidth() / 2 - resizedBitmap.getWidth() / 2, bounds.bottom - resizedBitmap.getHeight(), null);

//        mScoreInfo.draw(g);
    }

    private void checkViewDimension(Dimension dim) {
//        Dimension d = new Dimension(0,0);
        if (bimg.getWidth() < dim.width)
            dim.width = bimg.getWidth();
        if (bimg.getHeight() < dim.height)
            dim.height = bimg.getHeight();
    }

    /* start the game thread*/
    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /* run the game */
    public void run() {

        while (running) {
            Canvas canvas = null;

            try {
                canvas = holder.lockCanvas();
                synchronized (holder) {
                    // draw
//                    canvas.drawColor(Color.BLACK);
//                    Paint paint = new Paint();
//                    paint.setColor(Color.WHITE);
//                    canvas.drawCircle(i++, 100, 50, paint);
                    paint(canvas);
                    if (mTcpClient!=null && mTcpClient.isReady() && mPlayer!=null)
                        mTcpClient.sendMessage(
                                NetworkUtil.marshall(ByteBuffer.allocate(4).putInt(mPlayer.getId()).array()
                                        ,Constants.CMSG_ENEMIES));
                    Thread.sleep(TIME_BETWEEN_FRAMES);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    /* End the game, and signal either a win or loss */
    public void endGame(boolean win) {
        this.gameOver = true;
        this.gameWon = win;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    // signal that we can stop entering the game loop
    public void finishGame() {
        gameFinished = true;
    }


    /*I use the 'read' function to have observables act on their observers.
     */
    @Override
    public void update(Observable o, Object arg) {
        AbstractGameModifier modifier = (AbstractGameModifier) o;
        modifier.read(this);
    }

    private void initBattleField() {

        HashMap<String, ArrayList<int[]>> configHashMap = Utils.getHashConfigFromConfigFile(activity);
        if (configHashMap.isEmpty()) {
            return;
        }
        Bitmap wall1 = sprites.get("wall1");
        Bitmap wall2 = sprites.get("wall2");
        sizeX = configHashMap.get("bf").get(0)[0];
        sizeY = configHashMap.get("bf").get(0)[1];
        int tileSize = (int) (configHashMap.get("bf").get(0)[2] * density);
        addBackground(new Background(sizeX, sizeY, new Point(0, 0), sprites.get("background")));

        for (int[] vals : configHashMap.get("w")) {
//            addBackground(new Wall(vals[0], vals[1], new Point(vals[2] * tileSize, vals[3] * tileSize), wall1, false));
            addWall(wall1, tileSize, vals, false);
        }

        for (int[] vals : configHashMap.get("bw")) {
//            int TileWidth = wall2.getWidth(observer);
//            int TileHeight = wall2.getHeight(observer);

            addWall(wall2, tileSize, vals, true);

        }
    }

    private void addWall(Bitmap wall, int tileSize, int[] vals, boolean isBreakable) {
        int NumberX = vals[0] / tileSize;
        int NumberY = vals[1] / tileSize;

        for (int i = 0; i < NumberY; i++) {
            for (int j = 0; j < NumberX; j++) {
                addBackground(
                        new Wall(tileSize, tileSize,
                                new Point((vals[2] + j) * tileSize, (vals[3] + i) * tileSize),
                                wall, isBreakable));
            }
        }
    }


    private Point getPlayerWindowLocation(PlayerShip playerTank, Dimension d) {
        Point p = new Point(playerTank.getLocationPoint());
        p.offset(-d.width / 2, -d.height / 2);
        p.x = p.x > bimg.getWidth() - d.width ? bimg.getWidth() - d.width : p.x;
//        p.x = p.x+d.width > bimg.getWidth() ? bimg.getWidth() - d.width : p.x;
        p.x = p.x < 0 ? 0 : p.x;
        p.y = p.y > bimg.getHeight() - d.height ? bimg.getHeight() - d.height : p.y;
        p.y = p.y < 0 ? 0 : p.y;
        return p;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
//        animThread = new AnimThread(holder);
        running = true;
        start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        running = false;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void stop() {
        running = false;
    }

    public static int getDevicePixelValue(int pixels) {
        return (int) (pixels * density);
    }

    @Override
    public void messageReceived(InputStream is) {

        try {
            DataInputStream networkDataInput = new DataInputStream(is);
            short size = networkDataInput.readShort();
            //separate data from input stream
            byte[] buffer = new byte[size];
            is.read(buffer);
            DataInputStream dataInput = new DataInputStream(new ByteArrayInputStream(buffer));
//            Log.e("Network", "received message");
            // Extract the request code number
            short requestCode = dataInput.readShort();
            switch (requestCode) {
                case Constants.SMSG_AUTH:
                    setPlayer(dataInput);
                    log(mPlayer.toString());
                    break;
                case Constants.SMSG_ENEMIES:
                    setEnemies(dataInput);
                    break;
                case Constants.SMSG_ADD_BULLET:
                    addBulletFromServer(dataInput);
                    break;
                case Constants.SMSG_ADD_POWER_UP:
                    addPowerUpFromServer(dataInput);
                    break;
                case Constants.SMSG_UPDATE_BULLET:
                    handleBulletUpdateMessageFromServer(dataInput);
                    break;
                case Constants.SMSG_UI_INFO_UPDATE:
                    handleUiInfoUpdate(dataInput);
                    break;
                case Constants.SMSG_REMOVE_POWER_UP:
                    PowerUp powerUp = new PowerUp();
                    powerUp.fromDataInputStream(dataInput);
                    int puIdForRemoval = powerUp.getId();
                    powerups.remove(puIdForRemoval);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void log(String message)
    {
        Log.e(this.getClass().getCanonicalName(), message);
    }

    private void handleUiInfoUpdate(DataInputStream dataInput) throws IOException{
        UiInfo uiInfo = new UiInfo();
        uiInfo.fromDataInputStream(dataInput);
        InfoBar infoBar = ui.get(uiInfo.getOwnerID());
        if (infoBar!=null)
        {
            infoBar.setUiInfo(uiInfo);
            if (uiInfo.getOwnerID()!=mPlayer.getId())
                getEnemiesHashMap().get(uiInfo.getOwnerID()).setHealth(uiInfo.getHealth());
        }
//        log(uiInfo.toString());
    }

    private void addBulletFromServer(DataInputStream dataInput) throws IOException {
        int id = dataInput.readInt();
        Bullet bullet = new Bullet(id);
        bullet.fromDataInputStream(dataInput);
        bullet.initBulletByID();
        bullets.put(id, bullet);
    }

    private void handleBulletUpdateMessageFromServer(DataInputStream dataInput) throws IOException {
        int id = dataInput.readInt();
        Bullet serverBullet = new Bullet(id);
        serverBullet.fromDataInputStream(dataInput);
//        serverBullet.initBulletByID();
        Bullet bullet = bullets.get(serverBullet.getId());
        if (bullet!=null)
        {
            if (serverBullet.show)
                bullet.setData(serverBullet);
            else
                bullets.remove(serverBullet.getId());
        }
    }

    private void setEnemies(DataInputStream dataInput) throws IOException {
        short numOfEnemies=dataInput.readShort();
        for (short i=0;i<numOfEnemies;i++)
        {
//            mEnemies.clear();
//            int id = dataInput.readInt();
            PlayerShip servEnemy = new PlayerShip();
            servEnemy.fromDataInputStream(dataInput);
            PlayerShip enemy = mEnemies.get(servEnemy.getId());
            if (enemy==null)
            {
                enemy = new PlayerShip();
                enemy.setData(servEnemy);
                enemy.initEnemyByID();
                mEnemies.put(enemy.getId(), enemy);
                ui.put(enemy.getId(), new InfoBar(enemy, new UiInfo(enemy.getId(), enemy.getScore(), enemy.getHealth(),
                        enemy.getWeapon().getWeaponImageID())));
                log(enemy.toString());
            }
            else
            {
                enemy.setData(servEnemy);
            }
        }


    }

    private void setPlayer(DataInputStream dataInput) throws IOException {
        mPlayer = new PlayerShip();
        mPlayer.fromDataInputStream(dataInput);
        mPlayer.initPLayerByID();
        mPlayer.attachInputController(mControls, mHandleControls);
        InfoBar ib = new InfoBar(mPlayer, new UiInfo(mPlayer.getId(), mPlayer.getScore(), mPlayer.getHealth(),
                mPlayer.getWeapon().getWeaponImageID()));
        ui.put(mPlayer.getId(),ib);
    }

    public void setTcpClient(TCPClient tcpClient) {
        this.mTcpClient = tcpClient;
    }
    public TCPClient getTcpClient() {
        return this.mTcpClient;
    }

    public PlayerShip getPlayer() {
        return mPlayer;
    }
}
