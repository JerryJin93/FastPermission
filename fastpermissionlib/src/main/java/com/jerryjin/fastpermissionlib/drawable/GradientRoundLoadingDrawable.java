package com.jerryjin.fastpermissionlib.drawable;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;

public class GradientRoundLoadingDrawable extends RoundLoadingDrawable {

    public GradientRoundLoadingDrawable() {
        super();
        initShader();
    }

    public GradientRoundLoadingDrawable(int minSize, int maxSize) {
        super();
        initShader();
    }

    private void initShader() {
        mForegroundPaint.setShader(new LinearGradient(0, 0, 100, 0,
                Color.parseColor("#fffe7865"),
                Color.parseColor("#ff842398"), Shader.TileMode.CLAMP));
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
        }
        mSweepAngle += mAngleIncrement;
    }

    public void setForegroundGradient(LinearGradient gradient) {
        mForegroundPaint.setShader(gradient);
        invalidateSelf();
    }
}
