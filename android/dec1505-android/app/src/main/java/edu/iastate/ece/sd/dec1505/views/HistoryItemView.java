package edu.iastate.ece.sd.dec1505.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import edu.iastate.ece.sd.dec1505.R;


public class HistoryItemView extends RelativeLayout {

    View root;
    LinearLayout itemLayoutWrapper;
    View topSeparator;

    public HistoryItemView(Context context, boolean clickable) {
        super(context);
        sharedConstructor(context, clickable);
    }

    public HistoryItemView(Context context) {
        super(context);
        sharedConstructor(context, true);
    }

    public HistoryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor(context, true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HistoryItemView);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.HistoryItemView_primary_text:
                    break;
                case R.styleable.HistoryItemView_secondary_text:
                    break;
            }
        }
        a.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void sharedConstructor(Context context, boolean clickable) {
        root = LayoutInflater.from(context).inflate(R.layout.view_history_item, this);
        if (root != null) {
            itemLayoutWrapper = (LinearLayout) root.findViewById(R.id.list_item_layout_wrapper);
            if (!clickable) itemLayoutWrapper.setBackgroundResource(R.color.white);
        }
    }

    public void setTimeText(String timeToShow){
        TextView tv = (TextView)findViewById(R.id.time_hist_view);
        tv.setText(timeToShow);
    }

    public void setBloodOxyView(String bloodOxyView) {
        TextView tv = (TextView)findViewById(R.id.spo2_hist_value);
        tv.setText(bloodOxyView);
    }


    public void setHeartView(String heartView) {
        TextView tv = (TextView)findViewById(R.id.heart_hist_value);
        tv.setText(heartView);
    }

    public void setTempView(String tempView) {
        TextView tv = (TextView)findViewById(R.id.temp_hist_value);
        tv.setText(tempView);
    }


    public void setItemBackgroundColor(int color){
        itemLayoutWrapper.setBackgroundColor(color);
    }

    public void setTopHeaderVisibility(int visibility) {
        topSeparator.setVisibility(visibility);
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 50, 50, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}
