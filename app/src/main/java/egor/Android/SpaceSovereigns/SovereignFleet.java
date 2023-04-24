package egor.Android.SpaceSovereigns;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressLint("WrongCall")
public class SovereignFleet
{
    private final GameView gameView;
    private final int periodMotherShip = 600;
    private final int landingHeight;
    Bitmap bmpMotherShip;
    Bitmap bmpInvader_1;
    Bitmap bmpInvader_2;
    private final List<List<SovereignShip>> invaderSpaceShips;
    private int spaceBetweenRows = 50;
    private int timerMotherShip = 600;
    private int periodMoveDown = 200;
    private int timerMoveDown = 200;
    private List<SovereignShip> motherShipList;
    private int lastDestroyedShipPoints;
    private boolean arrive = false;

    public SovereignFleet(GameView gameView, int landingHeight, int level)
    {
        timerMoveDown /= (2 * level);
        periodMoveDown /= (2 * level);
        int numberOfShips = (level <= 5) ? (2 + level) : 7;

        this.gameView = gameView;
        this.landingHeight = landingHeight;
        this.bmpMotherShip = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.sovereign_special);
        this.bmpInvader_1 = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.sovereign_secondary);
        this.bmpInvader_2 = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.sovereign_primary);

        invaderSpaceShips = new ArrayList<>();
        invaderSpaceShips.add(createInvaderSpaceFleetRow(bmpInvader_2, 2, numberOfShips, 200));
        invaderSpaceShips.add(createInvaderSpaceFleetRow(bmpInvader_1, 15, numberOfShips - 1, 100));
        invaderSpaceShips.add(createInvaderSpaceFleetRow(bmpInvader_2, 2, numberOfShips, 50));
        invaderSpaceShips.add(createInvaderSpaceFleetRow(bmpInvader_1, 15, numberOfShips - 1, 25));
    }

    //Getters and setters
    public List<List<SovereignShip>> getInvaderSpaceShips()
    {
        return this.invaderSpaceShips;
    }

    public void onDraw(Canvas canvas)
    {
        for (List<SovereignShip> fleetRow : invaderSpaceShips)
        {
            for (Sprite sprite : fleetRow)
            {
                sprite.onDraw(canvas);
            }
        }

        //Timers
        timerMotherShip -= 1;
        timerMoveDown -= 1;
        if (timerMotherShip == 0)
        {
            if (!invaderSpaceShips.contains(motherShipList))
            {
                createMotherShip();
            }
            else
            {
                timerMotherShip = periodMotherShip;
            }
        }

        if (timerMoveDown == 0)
        {
            moveDown();
            timerMoveDown = periodMoveDown;
        }
    }

    private void moveDown()
    {
        for (List<SovereignShip> fleetRow : invaderSpaceShips)
        {
            for (SovereignShip sprite : fleetRow)
            {
                int stepDown = 4;
                sprite.moveDown(stepDown);

                if (sprite.getY() > landingHeight)
                {
                    arrive = true;
                }
            }
        }
    }

    // Creates an invader space fleet
    private List<SovereignShip> createInvaderSpaceFleetRow(Bitmap bmp, int spriteColumns, int numberOfShips, int points)
    {
        ArrayList<SovereignShip> row = new ArrayList<>();
        int shipWidth = bmp.getWidth() / spriteColumns;
        int dx = (gameView.getWidth() - shipWidth) / (numberOfShips - 1);

        int x = 0;
        int y = spaceBetweenRows + 100; // offset further down to make space for special sovereign

        for (int i = 0; i < numberOfShips; i++)
        {
            row.add(createInvaderSpaceShip(bmp, spriteColumns, x, y, points));
            x = x + dx;
        }
        spaceBetweenRows += bmp.getHeight();
        return row;
    }

    // Creates an invader ship
    private SovereignShip createInvaderSpaceShip(Bitmap bmp, int spriteColumns, int x, int y, int points)
    {
        return new SovereignShip(gameView, bmp, 1, spriteColumns, x, y, points);
    }

    // Create a motherShip
    public void createMotherShip()
    {
        int spriteRows = 1;
        int spriteColumns = 2;
        int x = 0;
        int y = 0;
        int xSpeed = 15;
        int ySpeed = 0;

        motherShipList = new ArrayList<>();
        SovereignShip motherShip = new SovereignSpecial(gameView, bmpMotherShip, spriteRows, spriteColumns, x, y, xSpeed, ySpeed, 300);
        motherShipList.add(motherShip);
        invaderSpaceShips.add(motherShipList);

        //Initiate timer
        timerMotherShip = periodMotherShip;
    }

    public boolean invaderArrive()
    {
        return arrive;
    }

    public boolean allInvadersDestroyed()
    {
        return invaderSpaceShips.size() == 0;
    }

    public Sprite getShooter()
    {
        Random rnd = new Random();
        int shooterRow = 0;//rnd.nextInt(invaderSpaceShips.size());
        int shooterCol = rnd.nextInt(invaderSpaceShips.get(shooterRow).size());

        return invaderSpaceShips.get(shooterRow).get(shooterCol);
    }

    public boolean isCollision(Sprite goodSpaceShipShoot)
    {

        for (List<SovereignShip> fleetRow : invaderSpaceShips)
        {
            for (SovereignShip sprite : fleetRow)
            {

                if (sprite.isCollision(goodSpaceShipShoot))
                {
                    // Delete Sprite
                    lastDestroyedShipPoints = sprite.getPoints();
                    fleetRow.remove(sprite);

                    // If row is empty, delete row too
                    if (fleetRow.size() == 0)
                    {
                        invaderSpaceShips.remove(fleetRow);
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public int getPoints()
    {
        return lastDestroyedShipPoints;
    }
}
