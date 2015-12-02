package vandy.mooc.view.ui;

import vandy.mooc.R;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * CustomView that shows how to create a Floating Action Button, as
 * per Google's Material Design principles.
 */
public class FloatingActionButton extends View {
    /**
     * An interpolator where the change flings forward and overshoots
     * the last value then comes back.
     */
    final static OvershootInterpolator overshootInterpolator =
        new OvershootInterpolator();
    
    /**
     * An interpolator where the rate of change starts out slowly and
     * and then accelerates.
     */
    final static AccelerateInterpolator accelerateInterpolator =
        new AccelerateInterpolator();
    
    /**
     * Paints used to draw the Button in Canvas.
     */
    Paint mButtonPaint;
    Paint mDrawablePaint;
    
    /**
     * Bitmap of the icon present in Floating Action Button
     */
    Bitmap mBitmap;
    Drawable drawable;
    int color = Color.WHITE;
    
    /**
     * Boolean to indicate if the Button is hidden or not. 
     */
    boolean mHidden = false;
 
    /**
     * Constructor that initializes the Floating
     * Action Button.
     * 
     * @param context
     */
	public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        TypedArray a = context.obtainStyledAttributes(attrs,
                          R.styleable.FabView);

        this.color = a.getColor(R.styleable.FabView_fabColor, Color.WHITE);
        this.drawable = a.getDrawable(R.styleable.FabView_fabDrawable);
        this.mBitmap = ((BitmapDrawable)drawable).getBitmap();
        
        a.recycle();
        
        init(color);
    }
    
    /**
     * Sets the Color of FloatingActionButton.
     * 
     * @param FloatingActionButtonColor
     */
    public void setFloatingActionButtonColor(int color) {
        this.color = color;
        invalidate();
    }

    /**
     * Sets the Icon of FloatingActionButton.
     * 
     * @param FloatingActionButtonDrawable
     */
    public void setFloatingActionButtonDrawable(Drawable drawable) {
        this.mBitmap = ((BitmapDrawable) drawable).getBitmap();
        invalidate();
    }

    /**
     * Initialize all the Resources needed before drawing.
     * 
     * @param FloatingActionButtonColor
     */
	public void init(int FloatingActionButtonColor) {
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(FloatingActionButtonColor);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setShadowLayer(10.0f,
                                    0.0f,
                                    3.5f,
                                    Color.argb(100,
                                               0,
                                               0,
                                               0));
        mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        invalidate();
    }

    /**
     * Hook method called to draw the View on the Canvas.
     * 
     *@param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        setClickable(true);
        float cx = getWidth() / 2;
        float cy = getHeight() / 2;
        float radius = getWidth() / 2.6f;
        float left = (float) (cx - (0.5*mBitmap.getWidth()));
        float top = (getHeight() - mBitmap.getHeight()) / 2;
        
        canvas.drawCircle(cx,
                          cy,
                          radius,
                    mButtonPaint);
        
        canvas.drawBitmap(mBitmap,
                          left,
                          top,
                          mDrawablePaint);
    }

    /**
     * Hook method called when View is Touched.
     * 
     * @param event
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) 
            setAlpha(1.0f);
        else if (event.getAction() == MotionEvent.ACTION_DOWN)
            setAlpha(0.6f);

        return super.onTouchEvent(event);
    }

    /**
     * Hides the Floating Action Button with some Animation.
     */
    public void hideFloatingActionButton() {
        if (!mHidden) {
            ObjectAnimator scaleX =
                ObjectAnimator.ofFloat(this,
                                       "scaleX",
                                       1,
                                       0);
            ObjectAnimator scaleY =
                ObjectAnimator.ofFloat(this,
                                       "scaleY",
                                       1,
                                       0);
            AnimatorSet animSetXY = 
                new AnimatorSet();
            animSetXY.playTogether(scaleX,
                                   scaleY);
            animSetXY.setInterpolator(accelerateInterpolator);
            animSetXY.setDuration(100);
            animSetXY.start();
            mHidden = true;
        }
    }

    /**
     * Shows the Floating Action Button with some Animation.
     */
    public void showFloatingActionButton() {
        if (mHidden) {
            ObjectAnimator scaleX =
                ObjectAnimator.ofFloat(this,
                                       "scaleX",
                                       0,
                                       1);
            ObjectAnimator scaleY =
                ObjectAnimator.ofFloat(this,
                                       "scaleY",
                                       0,
                                       1);
            AnimatorSet animSetXY =
                new AnimatorSet();
            animSetXY.playTogether(scaleX,
                                   scaleY);
            animSetXY.setInterpolator(overshootInterpolator);
            animSetXY.setDuration(200);
            animSetXY.start();
            mHidden = false;
        }
    }

    /**
     * @return True if the View is hidden.
     */
    public boolean isHidden() {
        return mHidden;
    }
}
