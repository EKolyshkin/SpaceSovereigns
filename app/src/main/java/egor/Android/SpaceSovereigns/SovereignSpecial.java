package egor.Android.SpaceSovereigns;

import android.graphics.Bitmap;

public class SovereignSpecial extends SovereignShip
{
    public SovereignSpecial(GameView gameView, Bitmap bmp, int bmpRows, int bmpColumns,
                            int x, int y, int xSpeed, int ySpeed, int points)
    {
        super(gameView, bmp, bmpRows, bmpColumns, x, y, points);

        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.points = points;
    }

    @Override
    protected void update()
    {
        if (x >= gameView.getWidth() - width - xSpeed || x + xSpeed <= 0)
        {
            xSpeed = -xSpeed;
            points = (points == 0) ? 0 : (points - 10);
        }
        else
        {
            x = x + xSpeed;
        }

        currentFrameColumn = getNextAnimationColumn();
    }

    @Override
    protected int getNextAnimationColumn()
    {
        return ++currentFrameColumn % bmpColumns;
    }

    @Override
    public void moveDown(int stepDown)
    {
    }
}
