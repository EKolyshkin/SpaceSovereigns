package egor.Android.SpaceSovereigns;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;

public class ScreenNextLevel
{
    private final GameView gameView;
    private final Paint nextLevelMessage = new Paint();
    private final int x;

    public ScreenNextLevel(GameView gameView)
    {
        this.gameView = gameView;
        Typeface typeface = Typeface.createFromAsset(gameView.getContext().getAssets(), "font/karma_future.ttf");
        nextLevelMessage.setTypeface(typeface);
        nextLevelMessage.setColor(Color.YELLOW);
        nextLevelMessage.setTextSize(90);
        nextLevelMessage.setTextAlign(Align.CENTER);
        x = gameView.getWidth() / 2;
    }

    @SuppressLint("WrongCall")
    public void draw(Canvas canvas, int level)
    {
        canvas.drawText("LEVEL " + level, x, (float) gameView.getWidth() / 2, nextLevelMessage);
    }
}
