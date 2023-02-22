package com.ps.tokky.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ps.tokky.R;

public class CustomProgressBar extends View {
    private final float START_ANGLE = 270;
    private final Paint paintBackground;
    private final Paint paintProgress;
    private final RectF rectProgress;
    private int max = 100;
    private int centerX, centerY, radius;
    private float swipeAngle = 0f;


    public CustomProgressBar(Context context) {
        this(context, null);
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        paintBackground = new Paint();
        paintBackground.setAntiAlias(true);
        paintBackground.setStyle(Paint.Style.FILL);
        paintBackground.setColor(getThemeColor(context, com.google.android.material.R.attr.colorPrimary));

        paintProgress = new Paint(paintBackground);
        paintProgress.setColor(getThemeColor(context, com.google.android.material.R.attr.colorSurface));

        rectProgress = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        centerX = viewWidth / 2;
        centerY = viewHeight / 2;
        radius = Math.min(viewWidth, viewHeight) / 2;

        rectProgress.left = centerX - radius;
        rectProgress.top = centerY - radius;
        rectProgress.right = centerX + radius;
        rectProgress.bottom = centerY + radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, radius - 1, paintBackground);
        canvas.drawArc(rectProgress, START_ANGLE, swipeAngle, true, paintProgress);
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setProgress(int progress) {
        float percentage = (float) progress * 100 / max;
        swipeAngle = percentage * 360 / 100;

        invalidate();
    }

    public int getThemeColor(Context context, int colorAttr) {
        Resources.Theme theme = context.getTheme();
        TypedArray arr = theme.obtainStyledAttributes(new int[]{colorAttr});

        int colorValue = arr.getColor(0, -1);
        arr.recycle();

        return colorValue;
    }
}
