package edu.iastate.ece.sd.dec1505.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import edu.iastate.ece.sd.dec1505.R;

public class StyledTextView extends TextView {

    public StyledTextView(Context context) {
        super(context);
    }

    public StyledTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public StyledTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs){
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.StyledTextView);

        for (int i = 0; i < a.getIndexCount(); ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.StyledTextView_font:
                    String assetName = a.getString(attr);
                    setFont(assetName);
                    break;
            }
        }
        a.recycle();
    }

    public void setFont(String assetName){
        if(isInEditMode()) return;

        Typeface face = Typeface.createFromAsset(getContext().getAssets(), "fonts/"+assetName);
        setTypeface(face);
    }

}
