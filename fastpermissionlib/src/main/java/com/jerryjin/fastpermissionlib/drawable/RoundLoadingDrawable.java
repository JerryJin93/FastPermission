package com.jerryjin.fastpermissionlib.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

@SuppressWarnings("WeakerAccess")
public class RoundLoadingDrawable extends LoadingDrawable {
    protected static final int ANGLE_ADD = 5;
    protected static final int MIN_ANGLE_SWEEP = 3;
    protected static final int MAX_ANGLE_SWEEP = 255;
    protected static int DEFAULT_SIZE = 56;

    protected int mMinSize = DEFAULT_SIZE;
    protected int mMaxSize = DEFAULT_SIZE;
    protected RectF mOval = new RectF();

    protected float mStartAngle = 0;
    protected float mSweepAngle = 0;
    protected int mAngleIncrement = -3;

    public RoundLoadingDrawable() {
        super();
        mForegroundPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public RoundLoadingDrawable(int minSize, int maxSize) {
        super();
        mMinSize = minSize;
        mMaxSize = maxSize;
    }

    @Override
    public int getIntrinsicHeight() {
        float maxLine = Math.max(mBackgroundPaint.getStrokeWidth(), mForegroundPaint.getStrokeWidth());
        int size = (int) (maxLine * 2 + 10);
        return Math.min(mMaxSize, Math.max(size, mMinSize));
    }

    @Override
    public int getIntrinsicWidth() {
        float maxLine = Math.max(mBackgroundPaint.getStrokeWidth(), mForegroundPaint.getStrokeWidth());
        int size = (int) (maxLine * 2 + 10);
        return Math.min(mMaxSize, Math.max(size, mMinSize));
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (bounds.left == 0 && bounds.top == 0 && bounds.right == 0 && bounds.bottom == 0) {
            return;
        }

        final int centerX = bounds.centerX();
        final int centerY = bounds.centerY();

        // The radius is 1/2 height or width.
        final int radius = Math.min(bounds.height(), bounds.width()) >> 1;
        final int maxStrokeRadius = ((int) Math.max(getForegroundLineSize(), getBackgroundLineSize()) >> 1) + 1;
        final int areRadius = radius - maxStrokeRadius;

        mOval.set(centerX - areRadius, centerY - areRadius, centerX + areRadius, centerY + areRadius);
    }

    @Override
    protected void onProgressChange(float progress) {
        mStartAngle = 0;
        mSweepAngle = 360 * progress;
    }

    @Override
    protected void onRefresh() {
        mStartAngle += ANGLE_ADD;

        if (mStartAngle > 360) {
            mStartAngle -= 360;
        }

        if (mSweepAngle > MAX_ANGLE_SWEEP) {
            mAngleIncrement = -mAngleIncrement;
        } else if (mSweepAngle < MIN_ANGLE_SWEEP) {
            mSweepAngle = MIN_ANGLE_SWEEP;
            return;
        } else if (mSweepAngle == MIN_ANGLE_SWEEP) {
            mAngleIncrement = -mAngleIncrement;
            getNextForegroundColor();
        }
        mSweepAngle += mAngleIncrement;
    }

    @Override
    protected void drawBackground(Canvas canvas, Paint backgroundPaint) {
        canvas.drawArc(mOval, 0, 360, false, backgroundPaint);
    }

    @Override
    protected void drawForeground(Canvas canvas, Paint foregroundPaint) {
        canvas.drawArc(mOval, mStartAngle, -mSweepAngle, false, foregroundPaint);
    }
}
