package egor.Android.SpaceSovereigns;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;

public class ScreenHighScore
{
    private final GameView gameView;
    private final Context context;
    private final Typeface typeface;
    private final Paint titlePaint = new Paint();
    private final Paint descriptionPaint = new Paint();
    String nameBest1, nameBest2, nameBest3;
    int scoreBest1, scoreBest2, scoreBest3;
    AlertDialog.Builder builder;
    private final int x;
    private int y;
    private int actualScore;
    private boolean bestScore, scoreSet = false;
    private String playerName;

    public ScreenHighScore(GameView gameView, Context context)
    {
        this.context = context;
        this.gameView = gameView;
        this.typeface = Typeface.createFromAsset(gameView.getContext().getAssets(), "font/arcade_classic.ttf");
        this.x = gameView.getWidth() / 2;

        // get reference to preferences file
        getScoreValues();
        createInput();

        titlePaint.setTypeface(typeface);
        titlePaint.setColor(Color.YELLOW);
        titlePaint.setTextSize(90);
        titlePaint.setTextAlign(Align.CENTER);

        descriptionPaint.setTypeface(typeface);
        descriptionPaint.setColor(Color.WHITE);
        descriptionPaint.setTextAlign(Align.CENTER);
        descriptionPaint.setTextSize(60);
    }

    @SuppressLint("WrongCall")
    public void draw(Canvas canvas)
    {
        if (bestScore)
        {
            if (!scoreSet && playerName != null)
            {
                setScoreValues();
                scoreSet = true;
            }

            y = gameView.getHeight() / 6;
            canvas.drawText("WELL DONE!", x, y, titlePaint);
            y = gameView.getHeight() / 3;
            canvas.drawText(nameBest1 + "         SCORE " + scoreBest1, x, y, descriptionPaint);
            y += gameView.getHeight() / 8;
            canvas.drawText(nameBest2 + "         SCORE " + scoreBest2, x, y, descriptionPaint);
            y += gameView.getHeight() / 8;
            canvas.drawText(nameBest3 + "         SCORE " + scoreBest3, x, y, descriptionPaint);
        }
        else
        {
            y = gameView.getHeight() / 2;
            canvas.drawText("GAME OVER!", x, y, titlePaint);
        }
    }

    public void showInput(int actualScore)
    {
        this.actualScore = actualScore;
        bestScore = actualScore > scoreBest3;
        builder.show();
    }

    private void createInput()
    {
        // input field customization
        final EditText input = new EditText(context);
        input.setHint("ENTER YOUR NAME");
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        input.setPadding(0, 50, 0, 0);
        input.setBackgroundResource(android.R.color.transparent);
        input.setTypeface(typeface);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        this.builder = new AlertDialog.Builder(context);
        builder.setView(input);

        // Set up buttons
        builder.setPositiveButton("ENTER", (dialog, which) -> playerName = input.getText().toString());
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
    }

    private void getScoreValues()
    {
        SharedPreferences bestScores = context.getSharedPreferences("BestScores", Context.MODE_PRIVATE);
        nameBest1 = bestScores.getString("bestPlayer1", "Player 1");
        nameBest2 = bestScores.getString("bestPlayer2", "Player 2");
        nameBest3 = bestScores.getString("bestPlayer3", "Player 3");
        scoreBest1 = bestScores.getInt("bestScore1", 0);
        scoreBest2 = bestScores.getInt("bestScore2", 0);
        scoreBest3 = bestScores.getInt("bestScore3", 0);
    }

    public void setScoreValues()
    {
        bestScore = true;
        SharedPreferences bestScores = context.getSharedPreferences("BestScores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = bestScores.edit();

        if (actualScore > scoreBest1)
        {
            editor.putInt("bestScore3", scoreBest2);
            editor.putString("bestPlayer3", nameBest2);

            editor.putInt("bestScore2", scoreBest1);
            editor.putString("bestPlayer2", nameBest1);

            editor.putInt("bestScore1", actualScore);
            editor.putString("bestPlayer1", playerName);
        }
        else if (actualScore > scoreBest2)
        {
            editor.putInt("bestScore3", scoreBest2);
            editor.putString("bestPlayer3", nameBest2);

            editor.putInt("bestScore2", actualScore);
            editor.putString("bestPlayer2", playerName);
        }
        else
        {
            editor.putInt("bestScore3", actualScore);
            editor.putString("bestPlayer3", playerName);
        }

        editor.apply(); // apply changes to high scores
        getScoreValues(); // update changes in class and screen
    }
}
