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
 * This class holds parameters to control how a gesture stroke is sampled and recognized.
 * This class also has parameters to distinguish gesture input events from fast typing events.
 *
 * @attr ref R.styleable#MainKeyboardView_gestureStaticTimeThresholdAfterFastTyping
 * @attr ref R.styleable#MainKeyboardView_gestureDetectFastMoveSpeedThreshold
 * @attr ref R.styleable#MainKeyboardView_gestureDynamicThresholdDecayDuration
 * @attr ref R.styleable#MainKeyboardView_gestureDynamicTimeThresholdFrom
 * @attr ref R.styleable#MainKeyboardView_gestureDynamicTimeThresholdTo
 * @attr ref R.styleable#MainKeyboardView_gestureDynamicDistanceThresholdFrom
 * @attr ref R.styleable#MainKeyboardView_gestureDynamicDistanceThresholdTo
 * @attr ref R.styleable#MainKeyboardView_gestureSamplingMinimumDistance
 * @attr ref R.styleable#MainKeyboardView_gestureRecognitionMinimumTime
 * @attr ref R.styleable#MainKeyboardView_gestureRecognitionSpeedThreshold
 */
public final class GestureStrokeRecognitionParams {
    // Static threshold for gesture after fast typing
    public final int
            mStaticTimeThresholdAfterFastTyping; // msec
    // Static threshold for starting gesture detection
    public final float mDetectFastMoveSpeedThreshold; // keyWidth/sec
    // Dynamic threshold for gesture after fast typing
    public final int mDynamicThresholdDecayDuration; // msec
    // Time based threshold values
    public final int mDynamicTimeThresholdFrom; // msec
    public final int mDynamicTimeThresholdTo; // msec
    // Distance based threshold values
    public final float mDynamicDistanceThresholdFrom; // keyWidth
    public final float mDynamicDistanceThresholdTo; // keyWidth
    // Parameters for gesture sampling
    public final float mSamplingMinimumDistance; // keyWidth
    // Parameters for gesture recognition
    public final int mRecognitionMinimumTime; // msec
    public final float mRecognitionSpeedThreshold; // keyWidth/sec

    // Default GestureStrokeRecognitionPoints parameters.
    public static final GestureStrokeRecognitionParams DEFAULT =
            new GestureStrokeRecognitionParams();

    public GestureStrokeRecognitionParams() {
        // These parameter values are default and intended for testing.
        mStaticTimeThresholdAfterFastTyping = 350; // msec
        // 检测快速滑动，除非之前没有轨迹的点，否则每次都会检测，直到有一次快速滑动为止，
        // 当检测到快速滑动，下一个点的距离和时间如果满足条件，就开始滑动输入了
        mDetectFastMoveSpeedThreshold = 1.5f; // keyWidth/sec
        mDynamicThresholdDecayDuration = 450; // msec
        mDynamicTimeThresholdFrom = 300; // msec
        mDynamicTimeThresholdTo = 20; // msec
        mDynamicDistanceThresholdFrom = 6.0f; // keyWidth
        mDynamicDistanceThresholdTo = 0.35f; // keyWidth
        // The following parameters' change will affect the result of regression test.
        mSamplingMinimumDistance = 1.0f / 6.0f; // keyWidth
        mRecognitionMinimumTime = 100; // msec
        mRecognitionSpeedThreshold = 5.5f; // keyWidth/sec
    }
}
