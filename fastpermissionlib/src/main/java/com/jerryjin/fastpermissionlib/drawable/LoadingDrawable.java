package com.jerryjin.fastpermissionlib.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public abstract class LoadingDrawable extends Drawable implements Animatable, MyAnimatable {

    private static final int LINE_SIZE = 4;
    private static final int BACKGROUND_COLOR = 0x32000000;

    protected Paint mForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    protected Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    protected float mProgress;
    private boolean mIsRunning;
    private final Runnable mAnim = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                onRefresh();
                invalidateSelf();
            } else {
                unscheduleSelf(this);
            }
        }
    };
    private int[] mForegroundColor = new int[]{0xcc000000, 0xfffe7865, 0xff842398};
    private int mForegroundColorIndex = 0;

    public LoadingDrawable() {
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setDither(true);
        mBackgroundPaint.setStrokeWidth(LINE_SIZE);
        mBackgroundPaint.setColor(BACKGROUND_COLOR);

        mForegroundPaint.setStyle(Paint.Style.STROKE);
        mForegroundPaint.setAntiAlias(true);
        mForegroundPaint.setDither(true);
        mForegroundPaint.setStrokeWidth(LINE_SIZE);
        mForegroundPaint.setColor(mForegroundColor[0]);
    }

    @Override
    public int getIntrinsicHeight() {
        float maxLine = Math.max(mBackgroundPaint.getStrokeWidth(), mForegroundPaint.getStrokeWidth());
        return (int) (maxLine * 2);
    }

    @Override
    public int getIntrinsicWidth() {
        float maxLine = Math.max(mBackgroundPaint.getStrokeWidth(), mForegroundPaint.getStrokeWidth());
        return (int) (maxLine * 2);
    }

    public float getBackgroundLineSize() {
        return mBackgroundPaint.getStrokeWidth();
    }

    public void setBackgroundLineSize(float size) {
        mBackgroundPaint.setStrokeWidth(size);
        onBoundsChange(getBounds());
    }

    public float getForegroundLineSize() {
        return mForegroundPaint.getStrokeWidth();
    }

    public void setForegroundLineSize(float size) {
        mForegroundPaint.setStrokeWidth(size);
        onBoundsChange(getBounds());
    }

    public int getBackgroundColor() {
        return mBackgroundPaint.getColor();
    }

    public void setBackgroundColor(int color) {
        mBackgroundPaint.setColor(color);
    }

    public int[] getForegroundColor() {
        return mForegroundColor;
    }

    public void setForegroundColor(int[] colors) {
        if (colors == null)
            return;
        this.mForegroundColor = colors;
        this.mForegroundColorIndex = -1;
        getNextForegroundColor();
    }

    /**
     * Get the cyclic foreground color.
     *
     * @return Settled foreground color.
     */
    int getNextForegroundColor() {
        final int[] colors = mForegroundColor;
        if (colors.length > 1) {
            int index = mForegroundColorIndex + 1;
            if (index >= colors.length)
                index = 0;

            mForegroundPaint.setColor(colors[index]);
            mForegroundColorIndex = index;
        } else {
            mForegroundPaint.setColor(colors[0]);
        }
        return mForegroundPaint.getColor();
    }

    /**
     * Get the loading progress.
     *
     * @return Current loading progress.
     */
    public float getProgress() {
        return mProgress;
    }

    /**
     * Set the loading progress.
     * <p>
     * The progress is a float type which between 0 to 1.
     * On changed, stop animation draw
     *
     * @param progress Loading progress
     */
    public void setProgress(float progress) {
        if (progress < 0)
            mProgress = 0;
        else if (mProgress > 1)
            mProgress = 1;
        else
            mProgress = progress;
        stop();
        onProgressChange(mProgress);
        invalidateSelf();
    }

    /**
     * Get running status.
     *
     * @return True if it's running, false otherwise.
     */
    public boolean isRunning() {
        return mIsRunning;
    }

    /**
     * Start the loading animation.
     */
    public void start() {
        if (!mIsRunning) {
            mIsRunning = true;
            scheduleSelf(mAnim, SystemClock.uptimeMillis() + FRAME_DURATION);
        }
    }

    /**
     * Stop the loading animation.
     */
    public void stop() {
        if (mIsRunning) {
            mIsRunning = false;
            unscheduleSelf(mAnim);
            invalidateSelf();
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int count = canvas.save();

        if (mBackgroundPaint.getColor() != 0 && mBackgroundPaint.getStrokeWidth() > 0)
            drawBackground(canvas, mBackgroundPaint);

        if (mIsRunning) {
            if (mForegroundPaint.getColor() != 0 && mForegroundPaint.getStrokeWidth() > 0)
                drawForeground(canvas, mForegroundPaint);
            // invalidate next call in this
            scheduleSelf(mAnim, SystemClock.uptimeMillis() + FRAME_DURATION);
        } else if (mProgress > 0) {
            if (mForegroundPaint.getColor() != 0 && mForegroundPaint.getStrokeWidth() > 0)
                drawForeground(canvas, mForegroundPaint);
        }

        canvas.restoreToCount(count);
    }

    @Override
    public void setAlpha(int alpha) {
        mForegroundPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        boolean needRefresh = false;
        if (mBackgroundPaint.getColorFilter() != cf) {
            mBackgroundPaint.setColorFilter(cf);
            needRefresh = true;
        }
        if (mForegroundPaint.getColorFilter() != cf) {
            mForegroundPaint.setColorFilter(cf);
            needRefresh = true;
        }
        if (needRefresh)
            invalidateSelf();
    }

    @Override
    public int getOpacity() {
        if (mBackgroundPaint.getXfermode() == null && mForegroundPaint.getXfermode() == null) {
            final int alpha = Color.alpha(mForegroundPaint.getColor());
            if (alpha == 0) {
                return PixelFormat.TRANSPARENT;
            }
            if (alpha == 255) {
                return PixelFormat.OPAQUE;
            }
        }
        // not sure, so be safe
        return PixelFormat.TRANSLUCENT;
    }


    protected abstract void onRefresh();

    protected abstract void drawBackground(Canvas canvas, Paint backgroundPaint);

    protected abstract void drawForeground(Canvas canvas, Paint foregroundPaint);

    protected abstract void onProgressChange(float progress);
}
