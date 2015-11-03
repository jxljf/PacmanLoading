package com.kaming.pacmanloading;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kaming on 2015/10/26.
 */
public class PacmanLoading extends View {

    public static final int DEFAULT_PADDING = 5;
    public static final int DEFAULT_SIZE = 45;
    public static final int PACMAN_COLOR = 0xFFFF00;
    public static final int BEAN_COLOR = 0xFFB8AE;
    public static final int BEAN_NUMBER = 17;
    public static final float PACMAN_SIZE_MULTIPLE = 8.7f;

    private List<Integer> mDegrees;
    private int mPadding;
    private int mPacmanColor;
    private int mBeanColor;
    private Paint mPacmanPaint;
    private Paint mBeanPaint;
    private boolean mHasAnimation;
    private float degrees1, degrees2;
    private int secDegrees;
    private int widthHalf, heightHalf;
    private boolean isDrawAllBean = true;

    public PacmanLoading(Context context) {
        super(context);
        init(null, 0);
    }

    public PacmanLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PacmanLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        mDegrees = new LinkedList<>();
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PacmanLoading, defStyleAttr, 0);
        mPacmanColor = a.getColor(R.styleable.PacmanLoading_pacman_color, PACMAN_COLOR);
        mBeanColor = a.getColor(R.styleable.PacmanLoading_bean_color, BEAN_COLOR);
        a.recycle();
        mPacmanPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPacmanPaint.setColor(mPacmanColor);
        mPacmanPaint.setStyle(Paint.Style.FILL);
        mPacmanPaint.setStrokeWidth(10.0f);
        mBeanPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBeanPaint.setColor(mBeanColor);
        mBeanPaint.setStyle(Paint.Style.FILL);
        mHandler.sendEmptyMessage(10);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
        int horPadding = Math.max(getPaddingLeft(), getPaddingRight());
        int verPadding = Math.max(getPaddingTop(), getPaddingBottom());
        mPadding = Math.max(horPadding, verPadding);
        if (mPadding < DEFAULT_PADDING) {
            mPadding = dp2px(DEFAULT_PADDING);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!mHasAnimation) {
            mHasAnimation = true;
            applyAnimation();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        widthHalf = getWidth() / 2;
        heightHalf = getHeight() / 2;
        if (isDrawAllBean){
            drawTimeCircle(canvas);
            isDrawAllBean = false;
        }else{
            drawLeftOver(canvas);
        }
        canvas.save();
        for (int i = 0; i < mDegrees.size(); i++) {
            int degree = mDegrees.get(i);
            if (degree == secDegrees){
                mDegrees.remove(i);
                Log.d("FUCK","szie : " + mDegrees.size());
            }
        }
        if (Math.floor(secDegrees / 360) > 1 ){
            isDrawAllBean = true;
        }
        canvas.rotate(secDegrees, widthHalf, widthHalf);
        drawPacman(canvas);
        canvas.restore();
    }

    private void drawLeftOver(Canvas canvas) {
        float radius = getWidth() / 50;
        canvas.save();
        canvas.rotate(0);
        int count = mDegrees.size();
        for (int i = 0; i < count; i++) {
            mBeanPaint.setAlpha(255);
            canvas.drawCircle(widthHalf, heightHalf / PACMAN_SIZE_MULTIPLE, radius, mBeanPaint);
            int degrees = mDegrees.get(i);
            canvas.rotate(degrees, widthHalf, heightHalf);
        }
        canvas.restore();
    }

    private void drawTimeCircle(Canvas canvas) {
        float radius = getWidth() / 50;
        canvas.save();
        canvas.rotate(0);
        for (int i = 0; i < BEAN_NUMBER; i++) {
            mBeanPaint.setAlpha(255);
            canvas.drawCircle(widthHalf, heightHalf / PACMAN_SIZE_MULTIPLE, radius, mBeanPaint);
            int degrees = 360 / BEAN_NUMBER;
            canvas.rotate(degrees, widthHalf, heightHalf);
            mDegrees.add(degrees * i);
        }
        canvas.restore();
    }

    private void drawPacman(Canvas canvas) {
        canvas.save();

        canvas.translate(widthHalf, heightHalf / PACMAN_SIZE_MULTIPLE);
        canvas.rotate(degrees1);
        mPacmanPaint.setAlpha(255);
        RectF rectF1 = new RectF(-widthHalf / PACMAN_SIZE_MULTIPLE, -heightHalf / PACMAN_SIZE_MULTIPLE, widthHalf / PACMAN_SIZE_MULTIPLE, heightHalf / PACMAN_SIZE_MULTIPLE);
        canvas.drawArc(rectF1, 0, 270, true, mPacmanPaint);

        canvas.restore();

        canvas.save();
        canvas.translate(widthHalf, heightHalf / PACMAN_SIZE_MULTIPLE);
        canvas.rotate(degrees2);
        mPacmanPaint.setAlpha(255);
        RectF rectF2 = new RectF(-widthHalf / PACMAN_SIZE_MULTIPLE, -heightHalf / PACMAN_SIZE_MULTIPLE, widthHalf / PACMAN_SIZE_MULTIPLE, heightHalf / PACMAN_SIZE_MULTIPLE);

        canvas.drawArc(rectF2, 90, 270, true, mPacmanPaint);
        canvas.restore();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void applyAnimation() {
        ValueAnimator rotateAnim1 = ValueAnimator.ofFloat(0, 45, 0);
        rotateAnim1.setDuration(650);
        rotateAnim1.setRepeatCount(-1);
        rotateAnim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degrees1 = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        rotateAnim1.start();

        ValueAnimator rotateAnim2 = ValueAnimator.ofFloat(0, -45, 0);
        rotateAnim2.setDuration(650);
        rotateAnim2.setRepeatCount(-1);
        rotateAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degrees2 = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        rotateAnim2.start();
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    private int dp2px(int dpValue) {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 10:
                    secDegrees += 1;
                    invalidate();
                    this.sendEmptyMessageDelayed(10, 25);
                    break;
                default:
                    break;
            }
        }

        ;
    };

}
