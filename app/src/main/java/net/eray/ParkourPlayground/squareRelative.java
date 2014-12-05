package net.eray.ParkourPlayground;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Niclas on 2014-11-11.
 */
public class squareRelative extends RelativeLayout{
    public squareRelative(Context context) {
        super(context);
    }

    public squareRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public squareRelative(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}