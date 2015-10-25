package edu.iastate.ece.sd.dec1505.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import edu.iastate.ece.sd.dec1505.R;

public class ListHeaderView extends RelativeLayout {

    View root;
    TextView textView;
    TextView rightTextView;
    View topHeader;
    View container;
    ImageView rightTextViewIcon;
    LinearLayout rightHolder;

    public ListHeaderView(Context context) {
        super(context);
        sharedConstructor(context);
    }

    public ListHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ListHeaderView);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ListHeaderView_headerText:
                    String text = a.getString(attr);
                    textView.setText(text);
                    break;
                case R.styleable.ListHeaderView_text_color:
                    int textColor = a.getColor(R.styleable.ListHeaderView_text_color, 0);
                    setTextColor(textColor);
                    break;
                case R.styleable.ListHeaderView_headerBgColor:
                    int backColor = a.getColor(R.styleable.ListHeaderView_headerBgColor, 0);
                    setBackgroundColor(backColor);
                    break;
            }
        }
        a.recycle();
    }

    public ListHeaderView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        sharedConstructor(context);
    }

    private void sharedConstructor(Context context) {
        root = LayoutInflater.from(context).inflate(R.layout.view_list_header, this);
        textView = (TextView) root.findViewById(R.id.header_name);
        topHeader = root.findViewById(R.id.header_top_separator);
        container = root.findViewById(R.id.header_view_container);
        rightTextView = (TextView) root.findViewById(R.id.header_right_action);
        rightTextViewIcon = (ImageView) root.findViewById(R.id.header_right_icon);
        rightHolder = (LinearLayout) root.findViewById(R.id.header_right_action_holder);
        setClickable(false);
    }

    public void setHeaderName(String name) {
        textView.setText(name);
    }

    public void setTopHeaderVisibility(int visibility) {
        topHeader.setVisibility(visibility);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setBackgroundColor(int color) {
        container.setBackgroundColor(color);
    }

    public void setTextPadding(int left, int top, int right, int bottom) {
        textView.setPadding(left, top, right, bottom);
    }

    public void setTextGravity(int gravity) {
        textView.setGravity(gravity);
    }

    public void setRightVisibility(int visibility) {
        rightHolder.setVisibility(visibility);
    }

    public void setRightIconResource(int resource) {
//        rightTextViewIcon.setBackground(background);
        rightTextViewIcon.setImageResource(resource);
    }

    public void setRightIconColor(int color) {
        rightTextViewIcon.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public void setRightText(String text) {
        rightTextView.setText(text);
    }

    public TextView getRightText() {
        return rightTextView;
    }

    public void setRightClickListener(OnClickListener listener) {
        rightHolder.setOnClickListener(listener);
    }
}
