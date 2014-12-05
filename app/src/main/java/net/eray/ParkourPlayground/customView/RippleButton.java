package net.eray.ParkourPlayground.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import net.eray.ParkourPlayground.animator.TouchEffectAnimator_3;

/**
 * Created by Niclas on 2014-09-30.
 */
public class RippleButton extends Button {
    private TouchEffectAnimator_3 touchEffectAnimator;

    public RippleButton(Context context) {
        super(context);
        init();
    }

    public RippleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        // you should set a background to view for effect to be visible. in this sample, this
        // linear layout contains a transparent background which is set inside the XML

        // giving the view to animate on
        touchEffectAnimator = new TouchEffectAnimator_3(this);

        // enabling ripple effect. it only performs ease effect without enabling ripple effect
        touchEffectAnimator.setHasRippleEffect(true);

        // setting the effect color
        touchEffectAnimator.setEffectColor(Color.parseColor("#004400"));

        // setting the duration
        touchEffectAnimator.setAnimDuration(200);

        // setting radius to clip the effect. use it if you have a rounded background
        touchEffectAnimator.setClipRadius(100);


        // the view must contain an onClickListener to receive UP touch events. touchEffectAnimator
        // doesn't return any value in onTouchEvent for flexibility. so it is developers
        // responsibility to add a listener
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        // send the touch event to animator
        touchEffectAnimator.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // let animator show the animation by applying changes to view's canvas
        touchEffectAnimator.onDraw(canvas);
        super.onDraw(canvas);
    }
}