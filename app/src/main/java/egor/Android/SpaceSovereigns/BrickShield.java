package egor.Android.SpaceSovereigns;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BrickShield
{
    List<BrickWall> shield = new ArrayList<>();
    int y;

    public BrickShield(GameView gameView, Bitmap brickBmp, int spriteColumns, int horizontalNumOfBrick, int verticalNumOfBrick, int x, int y, int numberOfWalls, int separationX)
    {
        this.y = y;
        int x_shield;
        for (int i = 0; i < numberOfWalls; i++)
        {
            x_shield = i * separationX;
            shield.add(new BrickWall(gameView, brickBmp, spriteColumns, horizontalNumOfBrick, verticalNumOfBrick, x + x_shield, y));
        }
    }

    public boolean isCollision(Sprite sprite)
    {
        for (Iterator<BrickWall> iterator = shield.iterator(); iterator.hasNext(); )
        {
            BrickWall brickWall = iterator.next();
            if (brickWall.isCollision(sprite))
            {
                if (brickWall.getSize() == 0)
                {
                    iterator.remove();
                }
                return true;
            }
        }
        return false;
    }

    @SuppressLint("WrongCall")
    public void onDraw(Canvas canvas)
    {
        for (BrickWall brickWall : shield)
        {
            brickWall.onDraw(canvas);
        }
    }

    public boolean isAlive()
    {
        return shield.size() != 0;
    }

    public int getY()
    {
        return y;
    }

    public boolean isCollisionFleet(SovereignFleet sovereignFleet)
    {
        boolean collision = false;
        for (List<SovereignShip> fleetRow : sovereignFleet.getInvaderSpaceShips())
        {
            for (SovereignShip sprite : fleetRow)
            {
                collision = this.isCollision(sprite);
            }
        }
        return collision;
    }
}
