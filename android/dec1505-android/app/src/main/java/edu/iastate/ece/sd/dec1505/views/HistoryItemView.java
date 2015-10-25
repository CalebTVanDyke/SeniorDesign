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


public class HistoryItemView extends RelativeLayout {

    View root;
    LinearLayout itemLayoutWrapper;
    ListHeaderView headerView;

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
            itemLayoutWrapper = (LinearLayout) root.findViewById(R.id.list_item_text_wrapper);
            if (!clickable) itemLayoutWrapper.setBackgroundResource(R.color.background);
            headerView = (ListHeaderView) root.findViewById(R.id.history_item_header);
        }
    }

    public void setTimeText(String timeToShow){
        TextView tv = (TextView)findViewById(R.id.time_header_label);
        tv.setText(timeToShow);
    }

    public void setBloodOxyView(String bloodOxyView) {
        TextView tv = (TextView)findViewById(R.id.spo2_header_label);
        tv.setText(bloodOxyView);
    }


    public void setHeartView(String heartView) {
        TextView tv = (TextView)findViewById(R.id.heart_header_label);
        tv.setText(heartView);
    }

    public void setTempView(String tempView) {
        TextView tv = (TextView)findViewById(R.id.temp_header_label);
        tv.setText(tempView);
    }

    public void setHeader(String header) {
        headerView.setHeaderName(header);
        headerView.setVisibility(View.VISIBLE);
    }

    public void hideHeader() {
        headerView.setVisibility(View.GONE);
    }
}
