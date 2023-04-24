package egor.Android.SpaceSovereigns;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Cannon extends Sprite implements SensorEventListener
{
    float sensorSensitivity;

    public Cannon(GameView gameView, Bitmap bmp, int bmpRows, int bmpColumns, Context context)
    {
        super(gameView, bmp, bmpRows, bmpColumns);

        //Depends on each type of sprite
        x = (gameView.getWidth() - width) / 2;
        y = gameView.getHeight() - height;
        xSpeed = 0;
        ySpeed = 0;

        //Sensor stuff
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null)
        {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        }
        sensorSensitivity = (float) 1.75;
    }

    @Override
    protected void update()
    {
        if (!(x >= gameView.getWidth() - width / 2 - xSpeed || x + xSpeed <= -width / 2))
        {
            x += xSpeed;
        }

        currentFrameColumn = getNextAnimationColumn();
    }

    @Override
    protected int getNextAnimationColumn()
    {
        float y1, y2, x1, x2;
        x1 = -sensorSensitivity * (float) 9.8;
        x2 = sensorSensitivity * (float) 9.8;
        y1 = 0;
        y2 = 12;
        //Linear mapping x1 x2 to y1 y2
        return (int) ((y2 - y1) / (x2 - x1) * ((float) xSpeed - x1) + y1);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        float x;
        x = event.values[0];
        // Check so max acceleration value is 9.82
        if (x > 9.82)
        {
            x = (float) 9.82;
        }
        else if (x < -9.82)
        {
            x = (float) -9.82;
        }

        //event.sensor.getMaximumRange();
        this.xSpeed = (int) (-x * sensorSensitivity);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }

    public int getXSpeed()
    {
        return xSpeed;
    }

    @Override
    public boolean isCollision(Sprite sprite)
    {
        int special_x = x + 20;
        int special_width = width - 40;

        // Detects collision between two rectangles (sprites)
        return special_x < sprite.getX() + sprite.getWidth() &&
                special_x + special_width > sprite.getX() &&
                this.y + this.height > sprite.getY() &&
                this.y < sprite.getY() + sprite.getHeight();
    }
}