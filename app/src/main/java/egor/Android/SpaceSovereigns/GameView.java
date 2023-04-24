package egor.Android.SpaceSovereigns;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint({"WrongCall", "ViewConstructor"})
public class GameView extends SurfaceView
{
    private final MainActivity activity;
    private final Context context;
    private final GameLoopThread gameLoopThread;

    // sound stuff
    private final SoundPool soundPool;
    private final int soundShootCannon, soundShootSovereign, soundExplosion, soundWallHit;
    private MediaPlayer mPlayer;

    Paint scorePaint = new Paint();
    Typeface scoreTypeFace;
    ScreenWelcome screenWelcome;
    ScreenNextLevel screenNextLevel;
    ScreenHighScore screenHighScore;
    private SovereignFleet sovereignFleet;
    private boolean firstStart = true;
    private Explosion explosion;
    private Cannon cannon;
    private Shoot cannonShoot, sovereignShoot;
    private BrickShield brickShield;
    private long lastClick;
    private Bitmap bmpExplosion, bmpCannonShoot, bmpSovereignShoot, bmpBrick;
    private int level = 1;
    private int scoreValue = 0;
    private int scoreBest3;
    private int gameState = 1; // 1 ScreenWelcome; 2 Play; 3 GameOver; 4 Next Level

    public GameView(Context context, MainActivity activity)
    {
        super(context);
        this.activity = activity;
        this.context = context;
        this.scoreTypeFace = Typeface.createFromAsset(context.getAssets(), "font/arcade_classic.ttf");

        // Config background Music
        if (mPlayer == null)
        {
            mPlayer = MediaPlayer.create(getContext(), R.raw.music_main);
            mPlayer.setLooping(true);
        }

        // Load sounds
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundShootCannon = soundPool.load(getContext(), R.raw.sound_shoot_cannon, 1);
        soundShootSovereign = soundPool.load(getContext(), R.raw.sound_shoot_sovereign, 1);
        soundExplosion = soundPool.load(getContext(), R.raw.sound_explosion, 1);
        soundWallHit = soundPool.load(getContext(), R.raw.sound_wall_hit, 1);

        gameLoopThread = new GameLoopThread(this);

        getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                boolean retry = true;
                gameLoopThread.setRunning(false);
                while (retry)
                {
                    try
                    {
                        gameLoopThread.join();
                        retry = false;
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                bmpExplosion = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
                bmpCannonShoot = BitmapFactory.decodeResource(getResources(), R.drawable.cannon_shoot);
                bmpSovereignShoot = BitmapFactory.decodeResource(getResources(), R.drawable.sovereign_shoot);
                bmpBrick = BitmapFactory.decodeResource(getResources(), R.drawable.brick);
                createScreens();
                createCannon();
                createSovereignFleet(level);
                createCannonShoot(false);
                createSovereignShoot(false);
                createExplosion(false, false);
                createShield();

                gameLoopThread.setRunning(true);
                if (firstStart)
                {
                    gameLoopThread.start();
                    firstStart = false;
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
            {
            }
        });
    }

    private void createScreens()
    {
        this.screenWelcome = new ScreenWelcome(this);
        this.screenNextLevel = new ScreenNextLevel(this);
        this.screenHighScore = new ScreenHighScore(this, context);
        SharedPreferences bestScores = context.getSharedPreferences("BestScores", Context.MODE_PRIVATE);
        scoreBest3 = bestScores.getInt("bestScore3", 0);
    }

    // Creates a good ship
    private void createCannon()
    {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cannon);
        this.cannon = new Cannon(this, bmp, 1, 13, context);
    }

    // Creates a sovereign ship
    private void createSovereignFleet(int level)
    {
        this.sovereignFleet = new SovereignFleet(this, this.getHeight() - cannon.getHeight(), level);
    }

    //Creates a cannon shoot
    private void createCannonShoot(boolean alive)
    {
        int spriteColumns = 2;
        int spriteRows = 1;
        int x = cannon.getX() + cannon.getWidth() / 2 - (bmpCannonShoot.getWidth() / spriteColumns) / 2;
        int y = cannon.getY();
        int xSpeed = cannon.getXSpeed();
        this.cannonShoot = new Shoot(this, bmpCannonShoot, spriteRows, spriteColumns, x, y, xSpeed, false, alive);
    }

    //Creates a sovereign shoot
    private void createSovereignShoot(boolean alive)
    {
        int spriteColumns = 9;
        int spriteRows = 1;
        Sprite shooterShip = sovereignFleet.getShooter();
        int x = shooterShip.getX() + shooterShip.getWidth() / 2 - (bmpSovereignShoot.getWidth() / spriteColumns) / 2;
        int y = shooterShip.getY();
        soundPool.play(soundShootSovereign, 1, 1, 1, 0, 1f);
        this.sovereignShoot = new Shoot(this, bmpSovereignShoot, spriteRows, spriteColumns, x, y, 0, true, alive);
    }

    //Creates a explosion
    private void createExplosion(boolean alive, boolean invaderShip)
    {
        int spriteColumns = 25;
        int spriteRows = 1;
        int x, y;

        if (invaderShip)
        {
            x = cannonShoot.getX() + cannonShoot.getWidth() / 2 - (bmpExplosion.getWidth() / spriteColumns) / 2;
            y = cannonShoot.getY() - cannonShoot.getHeight() / 2;
        }
        else
        {
            x = cannon.getX();
            y = cannon.getY();
        }

        soundPool.play(soundExplosion, 1, 1, 1, 0, 1f);
        explosion = new Explosion(this, bmpExplosion, spriteRows, spriteColumns, x, y, alive);
    }

    private void createShield()
    {
        int horizontalNumOfBrick = 8;
        int verticalNumOfBrick = 4;
        int spriteColumns = 4;
        int numberOfWalls = 3;

        int wallWidth = bmpBrick.getWidth() / spriteColumns * horizontalNumOfBrick;
        int separation = (this.getWidth() - wallWidth) / (numberOfWalls - 1);

        int x_ini = 0;
        int y_ini = cannon.getY() - bmpBrick.getWidth() - 10;

        for (int i = 0; i < numberOfWalls; i++)
        {
            brickShield = new BrickShield(this, bmpBrick, spriteColumns, horizontalNumOfBrick, verticalNumOfBrick, x_ini, y_ini, numberOfWalls, separation);
        }
    }

    private void drawScore(Canvas canvas)
    {
        scorePaint.setTypeface(scoreTypeFace);
        scorePaint.setColor(Color.GREEN);
        scorePaint.setTextSize(30);
        canvas.drawText("SCORE " + scoreValue, 30, getHeight() - 50, scorePaint);
        canvas.drawText("LEVEL " + level, getWidth() - 150, getHeight() - 50, scorePaint);
    }

    private void checkCollisions()
    {
        //Collision good ship shoot vs invader fleet
        if (cannonShoot.isAlive() && sovereignFleet.isCollision(this.cannonShoot))
        {
            scoreValue += sovereignFleet.getPoints();
            cannonShoot.setAlive(false);
            createExplosion(true, true);
        }

        //Collision good ship shoot vs invader ship shoot
        if (cannonShoot.isAlive() && sovereignShoot.isAlive()
                && sovereignShoot.isCollision(this.cannonShoot))
        {
            this.sovereignShoot.setAlive(false);
            this.cannonShoot.setAlive(false);
        }

        //Collision good ship       vs invader ship shoot
        if (sovereignShoot.getBottom() > cannon.getY() && sovereignShoot.isAlive()
                && cannon.isCollision(this.sovereignShoot))
        {
            this.sovereignShoot.setAlive(false);
            this.cannon.setAlive(false);
            createExplosion(true, false);
        }

        //BrickShield collisions
        if (brickShield.isAlive() && sovereignShoot.getBottom() > brickShield.getY()
                && sovereignShoot.isAlive() && brickShield.isCollision(sovereignShoot))
        {
            soundPool.play(soundWallHit, 1, 1, 1, 0, 1f);
            this.sovereignShoot.setAlive(false);
        }

        //Collision brickShield       vs good ship shoot
        if (cannonShoot.isAlive() && brickShield.isCollision(cannonShoot))
        {
            this.cannonShoot.setAlive(false);
        }

        //Collision brickShield       vs invader fleet
        brickShield.isCollisionFleet(sovereignFleet);
    }

    public void drawGameScreen(Canvas canvas)
    {
        if (this.cannon.isAlive())
        {
            this.cannon.onDraw(canvas);
        }

        this.sovereignFleet.onDraw(canvas);

        if (this.cannonShoot.isAlive())
        {
            this.cannonShoot.onDraw(canvas);
        }

        if (this.sovereignShoot.isAlive())
        {
            this.sovereignShoot.onDraw(canvas);
        }
        else if (Math.random() < 0.1)
        {
            createSovereignShoot(true);
        }

        if (brickShield.isAlive())
        {
            brickShield.onDraw(canvas);
        }

        drawScore(canvas);
        checkCollisions();

        if (explosion.isAlive())
        {
            explosion.onDraw(canvas);
        }

        //If an invader arrives or if the ship is destroyed... Game Over
        if (sovereignFleet.invaderArrive() || !cannon.isAlive())
        {
            gameState = 3;
        }

        //If all invaders destroyed... NextLevel
        if (sovereignFleet.allInvadersDestroyed())
        {
            level = level + 1;
            gameState = 4;
        }
    }

    @SuppressLint("WrongCall")
    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.BLACK);

        switch (gameState)
        {
            case 1: //Welcome Screen
                screenWelcome.draw(canvas);
                break;
            case 2: //Play game
                drawGameScreen(canvas);
                break;
            case 3: //Game Over, (if best score -> enter name)
                mPlayer.reset();
                mPlayer = MediaPlayer.create(getContext(), R.raw.music_game_over);
                mPlayer.start();
                if (scoreValue > scoreBest3)
                {
                    activity.runOnUiThread(() -> screenHighScore.showInput(scoreValue));
                }
                gameState = 5;
                break;
            case 4: //NextLevel
                screenNextLevel.draw(canvas, level);
                break;
            case 5://Scores
                screenHighScore.draw(canvas);
                break;
            default:
                gameState = 1;
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if ((System.currentTimeMillis() - lastClick > 300))
        {
            lastClick = System.currentTimeMillis();

            synchronized (getHolder())
            {
                switch (gameState)
                {
                    case 1://Welcome Screen
                        gameState = 2;
                        break;
                    case 2:// Play game
                        if (!cannonShoot.isAlive())
                        {
                            createCannonShoot(true);
                            soundPool.play(soundShootCannon, 1, 1, 0, 0, 1);
                        }
                        break;
                    case 3://Enter name
                        gameState = 5;
                        break;
                    case 4: //Next Level transition
                        gameState = 2;
                        createCannon();
                        createSovereignFleet(level);
                        createShield();
                        createCannonShoot(false);
                        createSovereignShoot(false);
                        break;
                    case 5:// Scores
                        gameState = 1;
                        level = 1;
                        scoreValue = 0;
                        createScreens();
                        createCannon();
                        createSovereignFleet(level);
                        createShield();
                        createCannonShoot(false);
                        createSovereignShoot(false);
                        break;
                    default:
                        gameState = 1;
                        break;
                }
            }
        }
        return true;
    }

    // Music functions
    public void startMusicPlayer()
    {
        mPlayer.start();
    }

    public void pauseMusicPlayer()
    {
        mPlayer.pause();
    }

    public void releaseMusicPlayer()
    {
        mPlayer.release();
        soundPool.release();
    }

    public void stopMusicPlayer()
    {
        mPlayer.stop();
    }
}