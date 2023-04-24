package egor.Android.SpaceSovereigns;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Sprite
{
    protected int bmpRows;
    protected int bmpColumns;
    protected Rect src;
    protected Rect dst;

    protected GameView gameView;
    protected Bitmap bmp;
    protected int x = 0;
    protected int y = 0;
    protected int xSpeed;
    protected int ySpeed;
    protected int currentFrameColumn = 0;
    protected int currentFrameRow = 0;
    protected int width;
    protected int height;
    protected boolean alive;

    public Sprite(GameView gameView, Bitmap bmp, int bmpRows, int bmpColumns)
    {
        //Depends on each type of sprite
        this.bmpRows = bmpRows;
        this.bmpColumns = bmpColumns;

        //The same for all sprites
        this.width = bmp.getWidth() / bmpColumns;
        this.height = bmp.getHeight() / bmpRows;
        this.gameView = gameView;
        this.bmp = bmp;

        // Initialize Rect
        int srcX = currentFrameColumn * width;
        int srcY = currentFrameRow * height;
        src = new Rect(srcX, srcY, srcX + width, srcY + height);
        dst = new Rect(x, y, x + width, y + height);

        //Depends on each type of sprite
        x = 30;
        y = 30;
        xSpeed = 0;
        ySpeed = 0;
        alive = true;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public boolean isAlive()
    {
        return alive;
    }

    public void setAlive(boolean alive)
    {
        this.alive = alive;
    }

    protected void update()
    {
        if (x >= gameView.getWidth() - width - xSpeed || x + xSpeed <= 0)
        {
            xSpeed -= xSpeed;
        }
        x += xSpeed;

        if (y >= gameView.getHeight() - height - ySpeed || y + ySpeed <= 0)
        {
            ySpeed -= ySpeed;
        }
        y += ySpeed;

        currentFrameColumn = ++currentFrameColumn % bmpColumns;
    }

    public void onDraw(Canvas canvas)
    {
        update();
        int srcX = currentFrameColumn * width;
        int srcY = currentFrameRow * height;

        src.left = srcX;
        src.top = srcY;
        src.right = srcX + width;
        src.bottom = srcY + height;
        dst.left = x;
        dst.top = y;
        dst.right = x + width;
        dst.bottom = y + height;

        canvas.drawBitmap(bmp, src, dst, null);
    }

    protected int getNextAnimationColumn()
    {
        return 0;
    }

    public boolean isCollision(Sprite sprite)
    {
        // Detects collision between two rectangles (sprites)
        return this.x < sprite.getX() + sprite.getWidth() &&
                this.x + this.width > sprite.getX() &&
                this.y + this.height > sprite.getY() &&
                this.y < sprite.getY() + sprite.getHeight();
    }
}
