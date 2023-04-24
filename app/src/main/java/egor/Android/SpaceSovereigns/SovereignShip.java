package egor.Android.SpaceSovereigns;

import android.graphics.Bitmap;

public class SovereignShip extends Sprite
{
    protected int points;

    public SovereignShip(GameView gameView, Bitmap bmp, int spriteRows, int spriteColumns, int x, int y, int points)
    {
        super(gameView, bmp, spriteRows, spriteColumns);

        //Depends on each type of sprite
        this.x = x;//(gameView.getWidth() - width)/2;
        this.y = y;//10;//gameView.getHeight() - height;
        this.xSpeed = 0;
        this.ySpeed = 0;
        this.points = points;
    }

    @Override
    protected void update()
    {
        if (!(x >= gameView.getWidth() - width - xSpeed || x + xSpeed <= 0))
        {
            x += xSpeed;
        }

        currentFrameColumn = getNextAnimationColumn();
    }

    @Override
    protected int getNextAnimationColumn()
    {
        return ++currentFrameColumn % bmpColumns;
    }

    public int getPoints()
    {
        return points;
    }

    public void moveDown(int stepDown)
    {
        this.y = y + stepDown;
    }
}
