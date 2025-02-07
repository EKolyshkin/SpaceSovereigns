package egor.Android.SpaceSovereigns;

import android.graphics.Bitmap;

public class Brick extends Sprite
{
    public Brick(GameView gameView, Bitmap bmp, int bmpRows, int bmpColumns, int x, int y)
    {
        super(gameView, bmp, bmpRows, bmpColumns);
        this.x = x;
        this.y = y;
        this.alive = true;
    }

    @Override
    protected void update()
    {
    }

    @Override
    protected int getNextAnimationColumn()
    {
        return 0;
    }

    public void degrade()
    {
        currentFrameColumn += 1;

        if (currentFrameColumn == bmpColumns)
        {
            this.alive = false;
        }
    }
}
