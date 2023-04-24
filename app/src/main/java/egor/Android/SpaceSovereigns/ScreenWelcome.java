package egor.Android.SpaceSovereigns;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;

public class ScreenWelcome
{
    private final Paint welcomeMessage = new Paint();
    private final Paint detailsMessage = new Paint();
    private final int x;
    private final int y;
    private final Sprite sprite;

    public ScreenWelcome(GameView gameView)
    {
        Bitmap bmpShip = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.sovereign_welcome_screen);
        this.sprite = new Sprite(gameView, bmpShip, 1, 36);
        this.sprite.setX(gameView.getWidth() / 2 - 350);
        this.sprite.setY(gameView.getHeight() / 2 - 300);

        Typeface welcomeTypeface = Typeface.createFromAsset(gameView.getContext().getAssets(), "font/karma_future.ttf");
        Typeface detailsTypeface = Typeface.createFromAsset(gameView.getContext().getAssets(), "font/arcade_classic.ttf");

        welcomeMessage.setTypeface(welcomeTypeface);
        welcomeMessage.setColor(Color.YELLOW);
        welcomeMessage.setTextSize(150);
        welcomeMessage.setTextAlign(Align.CENTER);

        x = gameView.getWidth() / 2;
        y = gameView.getHeight() / 6;

        detailsMessage.setTypeface(detailsTypeface);
        detailsMessage.setColor(Color.LTGRAY);
        detailsMessage.setTextAlign(Align.CENTER);
        detailsMessage.setTextSize(90);
    }

    @SuppressLint("WrongCall")
    public void draw(Canvas canvas)
    {
        canvas.drawText("SPACE", x, y, welcomeMessage);
        canvas.drawText("SOVEREIGNS", x, y + 175, welcomeMessage);
        sprite.onDraw(canvas);
        canvas.drawText("TAP TO START", x, (int) (5.5 * y), detailsMessage);
    }
}
