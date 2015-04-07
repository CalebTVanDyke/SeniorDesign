package edu.iastate.ece.sd.dec1505.views.base;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import edu.iastate.ece.sd.dec1505.R;

public class BaseLayout extends FrameLayout{

    View overlay;

    public BaseLayout(Context context) {
        super(context);
        init();
    }

    public BaseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Creates filling overlay that handles cancelling of popup/sheet
        overlay = new View(getContext());
        overlay.setVisibility(GONE);
        overlay.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        );
        overlay.setBackgroundColor(getResources().getColor(R.color.overlay));
        overlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("1","1");
                hideBottomSheet();
                hideDialog();
            }
        });
        addView(overlay);
    }

    private void hideOverlay(){
        overlay.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.abc_fade_out));
        overlay.setVisibility(GONE);
    }

    private void showOverlay(){
        overlay.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.abc_fade_in));
        overlay.setVisibility(VISIBLE);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        overlay.bringToFront();
    }

    /** Bottom sheet **/

    View bottomSheetView;
    TextView bottomSheetTitle;

    public void showBottomSheet(String title,View bottomSheetContent) {
        if(bottomSheetView!=null) hideBottomSheet();

        bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.view_base_bottom_sheet,this,false);
        bottomSheetView.setVisibility(INVISIBLE);

        // Title
        bottomSheetTitle = (TextView) bottomSheetView.findViewById(R.id.sheet_title);
        if(title==null) bottomSheetTitle.setVisibility(GONE);
        else bottomSheetTitle.setText(title);

        // Content
        ScrollView content = (ScrollView) bottomSheetView.findViewById(R.id.sheet_content);
        content.addView(bottomSheetContent);

        // Add sheet on top of everything else
        super.addView(bottomSheetView, getChildCount());
        bottomSheetView.setOnTouchListener(new BottomSheetTouchListener());

        // Start animation and show
        Animation animation = new SheetInAnimation();
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(300);
        bottomSheetView.startAnimation(animation);
        bottomSheetView.setVisibility(VISIBLE);

        showOverlay();
    }

    class SheetInAnimation extends Animation {

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int temp = (int) (getSheetMinHeight()*interpolatedTime);
            setSheetOffset(getHeight() - temp);
        }

    }

    class SheetMaxAnimation extends Animation {

        int originalOffset = 0;
        int threshold = 0;

        @Override
        public void start() {
            originalOffset = getSheetOffset();
            threshold = getSheetThreshold();
            super.start();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int temp = originalOffset - threshold;
            temp = (int) (temp * interpolatedTime);
            setSheetOffset(originalOffset - temp);
        }

    }

    class BottomSheetTouchListener implements OnTouchListener {

        float last = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(bottomSheetView==null) return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    last = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveSheet(event.getRawY()-last);
                    last = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    if(sheetBottom() < getSheetMinHeight()) hideBottomSheet();
                    break;
            }
            return true;
        }

    }

    private int getSheetMinHeight(){
        return Math.min(bottomSheetView.getHeight(), 400);
    }

    private int sheetTop(){
        int[] sheetLocation = new int[2];
        bottomSheetView.getLocationOnScreen(sheetLocation);

        int[] parentLocation = new int[2];
        getLocationOnScreen(parentLocation);

        return sheetLocation[1] - parentLocation[1];
    }

    private int sheetBottom(){
        int[] sheetLocation = new int[2];
        bottomSheetView.getLocationOnScreen(sheetLocation);

        int[] parentLocation = new int[2];
        getLocationOnScreen(parentLocation);
        parentLocation[1] = parentLocation[1]+getHeight();

        return parentLocation[1] - sheetLocation[1];
    }

    private int offset = 0;

    private synchronized void moveSheetOffset(int delta){
        offset += delta;
        bottomSheetView.offsetTopAndBottom(delta);
    }

    private synchronized int getSheetOffset(){
        return offset;
    }

    private int getSheetThreshold(){
        return Math.max(0, getHeight() - bottomSheetView.getHeight());
    }

    private boolean moveSheet(float delta){
        int offset = (int) (sheetTop() + delta);
        int difference = offset - getSheetThreshold();
        if(difference < 0){
            moveSheetOffset(getSheetThreshold()-getSheetOffset());
            return false;
        }
        else{
            moveSheetOffset((int) delta);
            return true;
        }
    }

    private void setSheetOffset(int position){
        moveSheet(position - getSheetOffset());
    }

    public void hideBottomSheet() {
        if(bottomSheetView!=null){
            Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.abc_slide_out_bottom);
            bottomSheetView.startAnimation(animation);
            bottomSheetView.setVisibility(INVISIBLE);

            hideOverlay();

            removeView(bottomSheetView);
            bottomSheetView = null;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(bottomSheetView!=null) bottomSheetView.offsetTopAndBottom(getSheetOffset());
    }






    /** Dialog **/

    View dialogView;
    FrameLayout dialogContent;
    TextView dialogTitle;

    public void showDialog(String title, View content, String[] items, OnClickListener[] listeners){
        dialogView = LayoutInflater.from(getContext()).inflate(R.layout.view_base_dialog,this,false);
        dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText(title);

        dialogContent = (FrameLayout) dialogView.findViewById(R.id.dialog_content);
        dialogContent.addView(content);

        if(items==null)setNeutralDialogButton("Dismiss", null);
        else {
            if (items.length >= 1) setNeutralDialogButton(items[0], listeners[0]);
            if (items.length >= 2) setPositiveDialogButton(items[1], listeners[1]);
            if (items.length >= 3) setNegativeDialogButton(items[2], listeners[2]);
        }

        dialogView.setVisibility(INVISIBLE);
        addView(dialogView);
        dialogView.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.abc_fade_in));
        dialogView.setVisibility(VISIBLE);
        dialogView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        showOverlay();
    }

    public void showDialog(String title,String message,String[] items, OnClickListener[] listeners){
        TextView textContent = new TextView(getContext());
        textContent.setText(message);
        textContent.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        showDialog(title,textContent,items, listeners);
    }

    public void setNeutralDialogButton(String text, OnClickListener onClickListener) {
        LinearLayout optionsList = (LinearLayout) dialogView.findViewById(R.id.dialog_options_list);
        TextView tv = (TextView) optionsList.findViewById(R.id.dialog_neutral);
        tv.setVisibility(View.VISIBLE);
        tv.setText(text);
        FrameLayout fl = (FrameLayout) optionsList.findViewById(R.id.dialog_separator_1);
        fl.setVisibility(View.VISIBLE);

        if(onClickListener == null){
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideDialog();
                }
            });
        }
        else tv.setOnClickListener(onClickListener);
    }

    public void setPositiveDialogButton(String text, OnClickListener onClickListener) {
        LinearLayout optionsList = (LinearLayout) dialogView.findViewById(R.id.dialog_options_list);
        TextView tv = (TextView) optionsList.findViewById(R.id.dialog_positive);
        tv.setVisibility(View.VISIBLE);
        tv.setText(text);
        FrameLayout fl = (FrameLayout) optionsList.findViewById(R.id.dialog_separator_2);
        fl.setVisibility(View.VISIBLE);

        if(onClickListener == null){
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideDialog();
                }
            });
        }
        else tv.setOnClickListener(onClickListener);
    }

    public void setNegativeDialogButton(String text, OnClickListener onClickListener) {
        LinearLayout optionsList = (LinearLayout) dialogView.findViewById(R.id.dialog_options_list);
        TextView tv = (TextView) optionsList.findViewById(R.id.dialog_negative);
        tv.setVisibility(View.VISIBLE);
        tv.setText(text);

        if(onClickListener == null){
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideDialog();
                }
            });
        }
        else tv.setOnClickListener(onClickListener);
    }

    public void hideDialog(){
        if(dialogView!=null){
            Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.abc_fade_out);
            dialogView.startAnimation(animation);
            dialogView.setVisibility(INVISIBLE);

            hideOverlay();

            removeView(dialogView);
            dialogView = null;
        }
    }

}
