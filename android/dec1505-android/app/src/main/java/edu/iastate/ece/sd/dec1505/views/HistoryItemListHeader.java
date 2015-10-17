package edu.iastate.ece.sd.dec1505.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import edu.iastate.ece.sd.dec1505.R;


public class HistoryItemListHeader extends RelativeLayout {

    View root;
    LinearLayout itemLayoutWrapper;

    public HistoryItemListHeader(Context context, boolean clickable) {
        super(context);
        sharedConstructor(context, clickable);
    }

    public HistoryItemListHeader(Context context) {
        super(context);
        sharedConstructor(context, true);
    }

    public HistoryItemListHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor(context, true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HistoryItemListHeader);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {

                case R.styleable.HistoryItemListHeader_time_text:
                    TextView tv1 = (TextView)findViewById(R.id.time_header_label);
                    tv1.setText(a.getString(attr));
                    break;
                case R.styleable.HistoryItemListHeader_text1:
                    TextView tv2 = (TextView)findViewById(R.id.spo2_header_label);
                    tv2.setText(a.getString(attr));
                    break;
                case R.styleable.HistoryItemListHeader_text2:
                    TextView tv3 = (TextView)findViewById(R.id.heart_header_label);
                    tv3.setText(a.getString(attr));
                    break;
                case R.styleable.HistoryItemListHeader_text3:
                    TextView tv4 = (TextView)findViewById(R.id.temp_header_label);
                    tv4.setText(a.getString(attr));
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
        root = LayoutInflater.from(context).inflate(R.layout.view_history_list_header, this);
        if (root != null) {
            itemLayoutWrapper = (LinearLayout) root.findViewById(R.id.list_item_text_wrapper);
            if (!clickable) itemLayoutWrapper.setBackgroundResource(R.color.white);
        }
    }
}
