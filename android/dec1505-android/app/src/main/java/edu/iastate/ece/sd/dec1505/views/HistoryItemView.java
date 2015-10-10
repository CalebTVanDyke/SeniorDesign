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
    TextView primaryText;
    TextView secondaryText;
    ImageView leftIcon;
    ImageView rightIcon;
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
                    setPrimaryText(a.getString(attr));
                    break;
                case R.styleable.HistoryItemView_secondary_text:
                    setSecondaryText(a.getString(attr));
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
            primaryText = (TextView) root.findViewById(R.id.primary);
            secondaryText = (TextView) root.findViewById(R.id.secondary);
            leftIcon = (ImageView) root.findViewById(R.id.list_item_icon);
            rightIcon = (ImageView) root.findViewById(R.id.icon_right);
            topSeparator = root.findViewById(R.id.item_top_separator);
        }
    }

    public void setPrimaryText(String name) {
        primaryText.setText(name);
    }

    public String getItemName() {
        return primaryText.getText().toString();
    }

    public void setItemBackgroundColor(int color){
        itemLayoutWrapper.setBackgroundColor(color);
    }

    public void setLeftIcon(int resource) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            leftIcon.setImageDrawable(getContext().getResources().getDrawable(resource, getContext().getTheme()));
        else
            leftIcon.setImageDrawable(resize(getContext().getResources().getDrawable(resource)));
        leftIcon.setVisibility(VISIBLE);
    }

    public void setRightIcon(int resource) {
        rightIcon.setImageResource(resource);
        rightIcon.setVisibility(VISIBLE);
    }

    public void reset() {
        secondaryText.setVisibility(GONE);
        leftIcon.setVisibility(GONE);
    }

    public void setSecondaryText(String sub) {
        secondaryText.setVisibility(VISIBLE);
        secondaryText.setText(sub);
    }

    public void setTopHeaderVisibility(int visibility) {
        topSeparator.setVisibility(visibility);
    }

    public void setPrimaryColor(int color) {
        primaryText.setTextColor(getResources().getColor(color));
    }

    public void setSecondaryColor(int color){
        secondaryText.setTextColor(getResources().getColor(color));
    }

    public void setPrimaryTint(int color) {
        leftIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public void setRightListener(OnClickListener listener) {
        rightIcon.setOnClickListener(listener);
    }

    public TextView getSecondaryText(){
        return secondaryText;
    }

    public TextView getPrimaryText(){
        return primaryText;
    }

    public ImageView getLeftIcon(){
        return leftIcon;
    }

    public ImageView getRightIcon(){
        return rightIcon;
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 50, 50, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

}
