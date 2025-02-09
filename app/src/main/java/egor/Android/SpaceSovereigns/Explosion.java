package egor.Android.SpaceSovereigns;

import android.graphics.Bitmap;

public class Explosion extends Sprite
{
    private int life = bmpRows;

    public Explosion(GameView gameView, Bitmap bmp, int bmpRows, int bmpColumns, int x, int y, boolean alive)
    {
        super(gameView, bmp, bmpRows, bmpColumns);
        this.alive = alive;
        this.x = x;
        this.y = y - 150;
    }

    @Override
    protected void update()
    {
        if (--life < 1)
        {
            alive = false;
        }
        else
        {
            currentFrameColumn = getNextAnimationColumn();
        }
    }

    @Override
    protected int getNextAnimationColumn()
    {
        return ++currentFrameColumn % bmpColumns;
    }
}
