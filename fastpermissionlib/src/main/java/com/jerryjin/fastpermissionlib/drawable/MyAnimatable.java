package com.jerryjin.fastpermissionlib.drawable;

import android.graphics.drawable.Animatable;

@SuppressWarnings("SpellCheckingInspection")
public interface MyAnimatable extends Animatable {

    /**
     * This is drawable animation frame duration.
     */
    int FPS = 60;

    int FRAME_DURATION = (int) (1.0f / 60 * 1000);

    /**
     * This is drawable animation duration.
     */
    int ANIMATION_DURATION = 250;
}
