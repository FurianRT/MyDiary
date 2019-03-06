package com.furianrt.mydiary.general;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.furianrt.mydiary.R;

public class FlikerProgressBar extends View implements Runnable {
    private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    private int DEFAULT_HEIGHT_DP = 35;
    private int borderWidth;
    private float maxProgress = 100f;
    private Paint textPaint;
    private Paint bgPaint;
    private Paint pgPaint;
    private String progressText;
    private Rect textRect;
    private RectF bgRectf;
    private Bitmap flikerBitmap;
    private float flickerLeft;
    private Bitmap pgBitmap;
    private Canvas pgCanvas;
    private float progress;
    private boolean isFinish;
    private boolean isStop;
    private int loadingColor;
    private int stopColor;
    private int progressColor;
    private int textSize;
    private int radius;
    private Thread thread;

    BitmapShader bitmapShader;

    public FlikerProgressBar(Context context) {
        this(context, null, 0);
    }

    public FlikerProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlikerProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.FlikerProgressBar);
        try {
            textSize = (int) ta.getDimension(R.styleable.FlikerProgressBar_textSize, 12);
            loadingColor = ta.getColor(R.styleable.FlikerProgressBar_loadingColor, Color.parseColor("#40c4ff"));
            stopColor = ta.getColor(R.styleable.FlikerProgressBar_stopColor, Color.parseColor("#ff9800"));
            radius = (int) ta.getDimension(R.styleable.FlikerProgressBar_radius, 0);
            borderWidth = (int) ta.getDimension(R.styleable.FlikerProgressBar_borderWidth, 1);
        } finally {
            ta.recycle();
        }
    }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(borderWidth);

        pgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pgPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);

        textRect = new Rect();
        bgRectf = new RectF(borderWidth, borderWidth, getMeasuredWidth() - borderWidth, getMeasuredHeight() - borderWidth);

        if (isStop) {
            progressColor = stopColor;
        } else {
            progressColor = loadingColor;
        }

        flikerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flicker);
        flickerLeft = -flikerBitmap.getWidth();

        initPgBimap();
    }

    private void initPgBimap() {
        pgBitmap = Bitmap.createBitmap(getMeasuredWidth() - borderWidth, getMeasuredHeight() - borderWidth, Bitmap.Config.ARGB_8888);

        pgCanvas = new Canvas(pgBitmap);
        thread = new Thread(this);
        thread.start();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0;
        switch (heightSpecMode) {
            case MeasureSpec.AT_MOST:
                height = dp2px(DEFAULT_HEIGHT_DP);
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                height = heightSpecSize;
                break;
        }
        setMeasuredDimension(widthSpecSize, height);

        if (pgBitmap == null) {
            init();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackGround(canvas);
        drawProgress(canvas);
        drawProgressText(canvas);
        drawColorProgressText(canvas);
    }

    private void drawBackGround(Canvas canvas) {
        bgPaint.setColor(progressColor);
        canvas.drawRoundRect(bgRectf, radius, radius, bgPaint);
    }

    private void drawProgress(Canvas canvas) {
        pgPaint.setColorFilter(new PorterDuffColorFilter(progressColor, PorterDuff.Mode.SRC_IN));

        float right = (progress / maxProgress) * getMeasuredWidth();
        pgCanvas.save();
        pgCanvas.clipRect(0, 0, right, getMeasuredHeight());
        pgCanvas.drawColor(progressColor);
        pgCanvas.restore();

        if (!isStop) {
            pgPaint.setXfermode(xfermode);

            pgCanvas.drawBitmap(flikerBitmap, flickerLeft, 0, pgPaint);
            pgPaint.setXfermode(null);
        }

        bitmapShader = new BitmapShader(pgBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        pgPaint.setShader(bitmapShader);
        canvas.drawRoundRect(bgRectf, radius, radius, pgPaint);
    }

    private void drawProgressText(Canvas canvas) {
        textPaint.setColor(progressColor);
        progressText = getProgressText();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textRect);
        int tWidth = textRect.width();
        int tHeight = textRect.height();
        float xCoordinate = (getMeasuredWidth() - tWidth) / 2;
        float yCoordinate = (getMeasuredHeight() + tHeight) / 2;
        canvas.drawText(progressText, xCoordinate, yCoordinate, textPaint);
    }

    private void drawColorProgressText(Canvas canvas) {
        textPaint.setColor(Color.WHITE);
        int tWidth = textRect.width();
        int tHeight = textRect.height();
        float xCoordinate = (getMeasuredWidth() - tWidth) / 2;
        float yCoordinate = (getMeasuredHeight() + tHeight) / 2;
        float progressWidth = (progress / maxProgress) * getMeasuredWidth();
        if (progressWidth > xCoordinate) {
            canvas.save();
            float right = Math.min(progressWidth, xCoordinate + tWidth * 1.1f);
            canvas.clipRect(xCoordinate, 0, right, getMeasuredHeight());
            canvas.drawText(progressText, xCoordinate, yCoordinate, textPaint);
            canvas.restore();
        }
    }

    public void setProgress(float progress) {
        if (!isStop) {
            if (progress < maxProgress) {
                this.progress = progress;
            } else {
                this.progress = maxProgress;
                finishLoad();
            }
            invalidate();
        }
    }

    public void setStop(boolean stop) {
        isStop = stop;
        if (isStop) {
            progressColor = stopColor;
            thread.interrupt();
        } else {
            progressColor = loadingColor;
            thread = new Thread(this);
            thread.start();
        }
        invalidate();
    }

    public void finishLoad() {
        isFinish = true;
        setStop(true);
    }

    public void toggle() {
        if (!isFinish) {
            if (isStop) {
                setStop(false);
            } else {
                setStop(true);
            }
        }
    }

    @Override
    public void run() {
        int width = flikerBitmap.getWidth();
        try {
            while (!isStop && !thread.isInterrupted()) {
                flickerLeft += dp2px(5);
                float progressWidth = (progress / maxProgress) * getMeasuredWidth();
                if (flickerLeft >= progressWidth) {
                    flickerLeft = -width;
                }
                postInvalidate();
                Thread.sleep(20);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        setStop(true);
        progress = 0;
        isFinish = false;
        isStop = false;
        progressColor = loadingColor;
        progressText = "";
        flickerLeft = -flikerBitmap.getWidth();
        initPgBimap();
    }

    public float getProgress() {
        return progress;
    }

    public boolean isStop() {
        return isStop;
    }

    public boolean isFinish() {
        return isFinish;
    }

    private String getProgressText() {
        String text = "";
        if (!isFinish) {
            if (!isStop) {
                //text = progress + "%";
            } else {
                //text = "";
            }
        } else {
            //text = getContext().getString(R.string.sync_done);
        }

        return text;
    }

    private int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}
