package com.example.dongjianye.gesturestroketest;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.dongjianye.gesturestroketest.gesture.BatchInputArbiter;
import com.example.dongjianye.gesturestroketest.gesture.CoordinateUtils;
import com.example.dongjianye.gesturestroketest.gesture.DrawingPreviewPlacerView;
import com.example.dongjianye.gesturestroketest.gesture.GestureEnabler;
import com.example.dongjianye.gesturestroketest.gesture.GestureStrokeDrawingParams;
import com.example.dongjianye.gesturestroketest.gesture.GestureStrokeDrawingPoints;
import com.example.dongjianye.gesturestroketest.gesture.GestureStrokeRecognitionParams;
import com.example.dongjianye.gesturestroketest.gesture.GestureTrailDrawingParams;
import com.example.dongjianye.gesturestroketest.gesture.GestureTrailsDrawingPreview;
import com.example.dongjianye.gesturestroketest.gesture.InputPointers;
import com.example.dongjianye.gesturestroketest.gesture.TypingTimeRecorder;

public class MainKeyboardView extends View implements BatchInputArbiter.BatchInputArbiterListener{
    private final static String TAG = MainKeyboardView.class.getSimpleName();

    private final DrawingPreviewPlacerView mDrawingPreviewPlacerView;
    private final GestureTrailsDrawingPreview mGestureTrailsDrawingPreview;

    private final int[] mOriginCoords = CoordinateUtils.newInstance();


    // 以下内容，放到TouchMainImpBase里面，在里面增加一个状态BATCH_INPUT，代表滑行输入
    private long mDownTime;

    // 输入逻辑的滑动轨迹
    private final BatchInputArbiter mBatchInputArbiter;
    // 绘制痕迹的滑动轨迹
    private final GestureStrokeDrawingPoints mGestureStrokeDrawingPoints;


    private boolean mIsDetectingGesture = false; // per PointerTracker.
    private static GestureEnabler sGestureEnabler = new GestureEnabler();
    private static TypingTimeRecorder sTypingTimeRecorder;

    private static GestureStrokeRecognitionParams sGestureStrokeRecognitionParams;
    private static GestureStrokeDrawingParams sGestureStrokeDrawingParams;

    private static boolean sInGesture = false;

    public MainKeyboardView(Context context) {
        this(context, null, 0);
    }

    public MainKeyboardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainKeyboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final DrawingPreviewPlacerView drawingPreviewPlacerView =
                new DrawingPreviewPlacerView(context);
        drawingPreviewPlacerView.setHardwareAcceleratedDrawingEnabled(true);

        mGestureTrailsDrawingPreview = new GestureTrailsDrawingPreview(new GestureTrailDrawingParams());
        mGestureTrailsDrawingPreview.setDrawingView(drawingPreviewPlacerView);

        mDrawingPreviewPlacerView = drawingPreviewPlacerView;

        // pointerId现在没啥用
        mBatchInputArbiter = new BatchInputArbiter(0, new GestureStrokeRecognitionParams());
        mGestureStrokeDrawingPoints = new GestureStrokeDrawingPoints(new GestureStrokeDrawingParams());

        // TODO 在TouchMainImpBase的setKeyboard里面去设置
        final int keyWidth = 50;
        final int keyHeight = 700;
        mBatchInputArbiter.setKeyboardGeometry(50, 700);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        installPreviewPlacerView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDrawingPreviewPlacerView.removeAllViews();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int index = event.getActionIndex();
        final int x = (int)event.getX(index);
        final int y = (int)event.getY(index);
        final long eventTime = event.getEventTime();
        final int id = event.getPointerId(index);
        Log.w(TAG, "getPointerId is " + id);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mDownTime = eventTime;
                onGestureDownEvent(x, y, eventTime);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                onGestureHistoricalMoveEvent(id, event);
                onGestureMoveEvent(id, x, y, eventTime, true);
                break;
            }
            case MotionEvent.ACTION_UP: {
                onGestureEnd(id, x, y, eventTime);
                break;
            }
        }
        return true;
    }

    private void installPreviewPlacerView() {
        final View rootView = getRootView();
        if (rootView == null) {
            Log.w(TAG, "Cannot find root view");
            return;
        }
        final ViewGroup windowContentView = (ViewGroup)rootView.findViewById(android.R.id.content);
        // Note: It'd be very weird if we get null by android.R.id.content.
        if (windowContentView == null) {
            Log.w(TAG, "Cannot find android.R.id.content view to add DrawingPreviewPlacerView");
            return;
        }
        windowContentView.addView(mDrawingPreviewPlacerView);
        Drawable drawable = mDrawingPreviewPlacerView.getBackground();
        Log.w(TAG, "drawable is " + drawable);
    }

    public void showGestureTrail(final GestureStrokeDrawingPoints points,
                                 final int pointerId, final long downTime) {
        locatePreviewPlacerView();
        mGestureTrailsDrawingPreview.setPreviewPosition(points, pointerId, downTime);
    }

    private void locatePreviewPlacerView() {
        getLocationInWindow(mOriginCoords);
        Log.w(TAG, "width is " + getWidth() + ", height is " + getHeight());
        mDrawingPreviewPlacerView.setKeyboardViewGeometry(mOriginCoords, getWidth(), getHeight());
    }


    // 以下函数，放在TouchMainImpBase里面，
    static {
        sGestureStrokeRecognitionParams = new GestureStrokeRecognitionParams();
        sGestureStrokeDrawingParams = new GestureStrokeDrawingParams();
        sTypingTimeRecorder = new TypingTimeRecorder(
                sGestureStrokeRecognitionParams.mStaticTimeThresholdAfterFastTyping,
                1000);
    }

    // 在down里面，判断是否能够开始检测滑行输入
    // 在processNormal里面，ACTION_DOWN和ACTION_POINTER_DOWN里面
    // 在singlePressed里面，ACTION_POINTER_DOWN里面
    private void onGestureDownEvent(final int x, final int y, final long eventTime) {
        if (!sGestureEnabler.shouldHandleGesture()) {
            return;
        }
        // A gesture should start only from a non-modifier key. Note that the gesture detection is
        // disabled when the key is repeating.
        // TODO Keyboard不为null，并且是Alphabet类型，并且key不能是null且modifier
        /*mIsDetectingGesture = (mKeyboard != null) && mKeyboard.mId.isAlphabetKeyboard()
                && key != null && !key.isModifier();*/
        mIsDetectingGesture = true;

        if (mIsDetectingGesture) {
            mBatchInputArbiter.addDownEventPoint(x, y, eventTime,
                    sTypingTimeRecorder.getLastLetterTypingTime(), 1);
            mGestureStrokeDrawingPoints.onDownEvent(
                    x, y, mBatchInputArbiter.getElapsedTimeSinceFirstDown(eventTime));
        }
    }

    // 调用位置
    // 在processSinglePressed的MOVE里面调用
    // 在processSecondary的MOVE里面
    // 在processGestureInput的MOVE里面
    private void onGestureHistoricalMoveEvent(final int id, final MotionEvent me) {
        if (sGestureEnabler.shouldHandleGesture() && me != null) {
            // Add historical points to gesture path.
            final int historicalSize = me.getHistorySize();
            for (int h = 0; h < historicalSize; h++) {
                final int historicalX = (int)me.getHistoricalX(h);
                final int historicalY = (int)me.getHistoricalY(h);
                final long historicalTime = me.getHistoricalEventTime(h);
                onGestureMoveEvent(id, historicalX, historicalY, historicalTime,
                        false /* isMajorEvent *//*, null*/);
            }
        }
    }

    // 调用位置
    // 在processSinglePressed的MOVE里面调用
    // 在processSecondary的MOVE里面
    // 在processGestureInput的MOVE里面
    private void onGestureMoveEvent(final int id, final int x, final int y, final long eventTime,
                                    final boolean isMajorEvent/*, final Key key*/) {
        if (!mIsDetectingGesture) {
            return;
        }
        final boolean onValidArea = mBatchInputArbiter.addMoveEventPoint(
                x, y, eventTime, isMajorEvent, this);
        // If the move event goes out from valid batch input area, cancel batch input.
        // TODO 这地方可以去掉了，
        /*if (!onValidArea) {
            cancelBatchInput();
            return;
        }*/
        mGestureStrokeDrawingPoints.onMoveEvent(
                x, y, mBatchInputArbiter.getElapsedTimeSinceFirstDown(eventTime));
        // If the MoreKeysPanel is showing then do not attempt to enter gesture mode. However,
        // the gestured touch points are still being recorded in case the panel is dismissed.
        // TODO 当显示Secondary的时候，记录需要绘制的滑行轨迹，但是不开始记录需要处理的滑动轨迹
        /*if (isShowingMoreKeysPanel()) {
            return;
        }*/
        if (!sInGesture /*&& key != null && Character.isLetter(key.getCode()*/
                && mBatchInputArbiter.mayStartBatchInput(this)) {
            sInGesture = true;

            // TODO 将当前的状态改为滑动输入状态
        }
        if (sInGesture) {
            // TODO
            if (true/*key != null*/) {
                mBatchInputArbiter.updateBatchInput(eventTime, this);
            }

            showGestureTrail(mGestureStrokeDrawingPoints, id, getDownTime());
        }
    }

    // 调用为止
    // 在processBatchInput里面的up，pointer_down，cancel里面调用
    private void onGestureEnd(final int id, final int x, final int y, final long eventTime) {
        if (sInGesture) {
            /*if (currentKey != null) {
                callListenerOnRelease(currentKey, currentKey.getCode(), true *//* withSliding *//*);
            }*/
            if (mBatchInputArbiter.mayEndBatchInput(
                    eventTime, 1, this)) {
                sInGesture = false;
            }
            showGestureTrail(mGestureStrokeDrawingPoints, id, getDownTime());
        }
    }

    private long getDownTime() {
        return mDownTime;
    }

    // TODO 以下可以根据LatinIme的代码进行适当的处理
    @Override
    public void onStartBatchInput() {

    }

    @Override
    public void onUpdateBatchInput(InputPointers aggregatedPointers, long moveEventTime) {

    }

    @Override
    public void onStartUpdateBatchInputTimer() {

    }

    @Override
    public void onEndBatchInput(InputPointers aggregatedPointers, long upEventTime) {

    }
}
