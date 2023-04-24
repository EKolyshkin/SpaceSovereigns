package egor.Android.SpaceSovereigns;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BrickWall
{
    private final List<Brick> wall = new ArrayList<>();

    public BrickWall(GameView gameView, Bitmap brickBmp, int spriteColumns, int horizontalNumOfBrick, int verticalNumOfBrick, int x, int y)
    {
        // creates a BrickWall
        int xBrick, yBrick;
        int spriteRows = 1;

        for (int i = 0; i < horizontalNumOfBrick; i++)
        {
            xBrick = i * brickBmp.getWidth() / spriteColumns;

            for (int j = 0; j < verticalNumOfBrick; j++)
            {
                yBrick = j * brickBmp.getHeight() / spriteRows;
                wall.add(new Brick(gameView, brickBmp, spriteRows, spriteColumns, x + xBrick, y + yBrick));
            }
        }
    }

    public boolean isCollision(Sprite sprite)
    {
        boolean collision = false;

        for (Iterator<Brick> iterator = wall.iterator(); iterator.hasNext(); )
        {
            Brick brick = iterator.next();
            if (brick.isCollision(sprite))
            {
                brick.degrade();
                if (!brick.isAlive())
                {
                    iterator.remove(); // Remove the current element from the iterator and the list.
                }
                collision = true;
            }
        }

        return collision;
    }

    public int getSize()
    {
        return wall.size();
    }

    @SuppressLint("WrongCall")
    public void onDraw(Canvas canvas)
    {
        for (Brick brick : wall)
        {
            brick.onDraw(canvas);
        }
    }
}
