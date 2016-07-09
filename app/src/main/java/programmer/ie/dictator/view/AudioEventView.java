/**
 * This file is part of Speech Trainer.
 * Copyright (C) 2011 Jan Wrobel <wrr@mixedbit.org>
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package programmer.ie.dictator.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageButton;

import java.util.ArrayList;

import programmer.ie.dictator.R;

/**
 * Draws a plot with sound levels of recently recorded or played buffers. The
 * data to be plotted is obtained from the AudioEventHistory.
 * <p/>
 * The creator of the AudioEventView must set AudioEventHistory.
 */
public class AudioEventView extends ImageButton {
    static final int STACK_SIZE = 20;
    private static final int RECORDED_BUFFER_COLOR = 0xffd00000;
    private static final int TEXT_COLOR = 0xff0000d0;
    private final Paint recordedBufferPaint;
    private final Paint textPaint;
    Bitmap mBackgroundImage;
    ArrayList<Integer> readings = new ArrayList<Integer>();
    int max, min;


    public AudioEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        recordedBufferPaint = new Paint();
        recordedBufferPaint.setColor(RECORDED_BUFFER_COLOR);
        textPaint = new Paint();
        textPaint.setColor(TEXT_COLOR);
        mBackgroundImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.record_start_half);
    }

    public void addReading(int reading) {
        if (readings.size() > STACK_SIZE) {
            readings.remove(0);
        }
        readings.add(reading);
        invalidate();
    }

    int findMin() {
        int min = 0;
        for (int r : readings) {
            if (r < min) {
                min = r;
            }
        }
        return min;
    }

    int findMax() {
        int max = 0;
        for (int r : readings) {
            if (r > max) {
                max = r;
            }
        }
        return max;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBackgroundImage, 0, 0, null);
        final int viewWidth = getWidth();
        final int viewHeight = getHeight();
        final int usedWidth = viewWidth / 2;
        int size = readings.size();
        int barWidth = size == 0 ? 1 : usedWidth / size;
        for (int i = 0; i < usedWidth && i < size; ++i) {
            final int height = getHeightForSoundLevel(readings.get(i), viewHeight);
            final int lineStart = (viewHeight - height) / 2;
            canvas.drawText("Max:  " + max, 10, 10, textPaint);
            //public void drawRect(float left, float top, float right, float bottom, Paint paint) {

            int left = i * barWidth;
            int right = viewWidth - (i * barWidth);
            canvas.drawRect(left, lineStart, left + barWidth, lineStart + height, recordedBufferPaint);
            canvas.drawRect(right, lineStart, right + barWidth, lineStart + height, recordedBufferPaint);
        }

//        if (readings.size() > 0) {
//            int currentRmsdB = readings.get(0);
//            if(currentRmsdB != 0){
//                float halfwidth = getWidth()/2;
//                textPaint.setColor(Color.LTGRAY);
//                currentRmsdB = Math.abs(currentRmsdB);
//                textPaint.setColor(Color.GRAY);
//                canvas.drawRect(halfwidth - currentRmsdB * 2, 20, halfwidth + currentRmsdB * 2, 50, textPaint);
//
//                canvas.drawLine(halfwidth, 15, halfwidth, 55, textPaint);
//            }
//
//        }
    }

    /**
     * Calculates height of a line to represent a given soundLevel. Makes sure
     * the lowest and the smallest recorded sound levels fit in the plot.
     */
    private int getHeightForSoundLevel(double soundLevel, int canvasHeight) {
        min = findMin();
        max = findMax();
        return (int) ((soundLevel - min) * (canvasHeight - 1)
                / (max - min));
    }
}