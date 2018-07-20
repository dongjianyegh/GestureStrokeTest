/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dongjianye.gesturestroketest.gesture;

/**
 * This class holds parameters to control how a gesture trail is drawn and animated on the screen.
 *
 * On the other hand, {@link GestureStrokeDrawingParams} class controls how each gesture stroke is
 * sampled and interpolated. This class controls how those gesture strokes are displayed as a
 * gesture trail and animated on the screen.
 *
 * @attr ref R.styleable#MainKeyboardView_gestureTrailFadeoutStartDelay
 * @attr ref R.styleable#MainKeyboardView_gestureTrailFadeoutDuration
 * @attr ref R.styleable#MainKeyboardView_gestureTrailUpdateInterval
 * @attr ref R.styleable#MainKeyboardView_gestureTrailColor
 * @attr ref R.styleable#MainKeyboardView_gestureTrailWidth
 */
public final class GestureTrailDrawingParams {
    private static final int FADEOUT_START_DELAY_FOR_DEBUG = 2000; // millisecond
    private static final int FADEOUT_DURATION_FOR_DEBUG = 200; // millisecond

    public final int mTrailColor;
    public final float mTrailStartWidth;
    public final float mTrailEndWidth;
    public final float mTrailBodyRatio;
    public boolean mTrailShadowEnabled;
    public final float mTrailShadowRatio;
    public final int mFadeoutStartDelay;
    public final int mFadeoutDuration;
    public final int mUpdateInterval;

    public final int mTrailLingerDuration;

    public GestureTrailDrawingParams() {
        mTrailColor = 0xFF33B5E5;
        mTrailStartWidth = 30f;
        mTrailEndWidth = 7.5f;
        mTrailBodyRatio = 1;
        mTrailShadowEnabled = false;
        mTrailShadowRatio = 1;
        mFadeoutStartDelay = GestureTrailDrawingPoints.DEBUG_SHOW_POINTS
                ? FADEOUT_START_DELAY_FOR_DEBUG
                : 100;
        mFadeoutDuration = GestureTrailDrawingPoints.DEBUG_SHOW_POINTS
                ? FADEOUT_DURATION_FOR_DEBUG
                : 800;
        mTrailLingerDuration = mFadeoutStartDelay + mFadeoutDuration;
        mUpdateInterval = 20;
    }
}
