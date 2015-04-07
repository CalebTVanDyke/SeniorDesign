package edu.iastate.ece.sd.dec1505.fragments;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import edu.iastate.ece.sd.dec1505.R;

import edu.iastate.ece.sd.dec1505.MainActivity;
import edu.iastate.ece.sd.dec1505.tools.DefaultRequestQueue;

public abstract class ApplicationFragment extends Fragment {

    private String fragmentTitle;
    public int PRIMARY_COLOR;

    public ApplicationFragment setTitle(String title){
        fragmentTitle = title;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        PRIMARY_COLOR = getColor(R.color.primary_color);
    }



    @Override
    public void onResume() {
        super.onResume();
        getSupportActivity().getSupportActionBar().setTitle(fragmentTitle != null ? fragmentTitle : "");
    }



    /**
     * FRAMEWORK FLOW
     *
     * Below the callbacks and process for the standard framework of the application
     *
     * The flow is as such:
     *  1. getRootViewId() return the layout id for the fragment
     *  2. onConnectViews() connect all your views with findViewById if needed
     *  3. onInitialSetup() do any setting up for the fragment (create models, set adapters, etc)
     *  4. onRequestData() do any volley request now with the given default queue
     *
     */

    public View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int rootViewId = getRootViewId();
        if(rootViewId!=-1){
            rootView = inflater.inflate(rootViewId,container,false);
            onConnectViews();
            onInitialSetup();
            onRequestData(DefaultRequestQueue.getDefaultQueue(getContext()));
            return rootView;
        }
        return super.onCreateView(inflater,container,savedInstanceState);
    }

    public int getRootViewId(){
        return -1;
    }

    public void onConnectViews(){ }

    public void onInitialSetup(){ }

    public void onRequestData(RequestQueue requestQueue){

    }



    /**
     * STANDARD TOOLS
     *
     * Below are standard tools and methods that will probably be used often in multiple fragments
     */

    public String getTitle(){
        return fragmentTitle;
    }

    public MainActivity getSupportActivity(){
        return (MainActivity) getActivity();
    }

    public ActionBar getSupportActionBar(){
        return getSupportActivity().getSupportActionBar();
    }

    public void setActionBarColor(int color){
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    public Toolbar getToolbar(){
        return getSupportActivity().getToolbar();
    }

    public View findViewById(int id){
        return rootView.findViewById(id);
    }

    public float getDimen(int id){
        return getResources().getDimension(id);
    }

    public int getColor(int id){
        return getResources().getColor(id);
    }

    public void goToFragment(ApplicationFragment fragment){
        getSupportActivity().goToFragment(fragment,true);
    }

    public void goToFragment(ApplicationFragment fragment,boolean addToBackStack){
        getSupportActivity().goToFragment(fragment,addToBackStack);
    }

    public Context getContext(){
        return getSupportActivity();
    }

    public void showBottomSheet(String title,View view){
        if(isAdded()){
            getSupportActivity().showBottomSheet(title,view);
        }
    }

    /**
     * Calls Support Activity's showDialog.
     * @param title Title for the dialog box.
     * @param message Text to display in the body of the dialog.
     * @param options The options to display at the bottom of the dialog (max of 3).
     * @param listeners The listeners to use for each option. Null values default to closing the dialog.
     */
    public void showDialog(String title,String message,String[] options, View.OnClickListener[] listeners){
        if(isAdded()){
            getSupportActivity().showDialog(title,message,options, listeners);
        }
    }

    public void showDialog(String title,View content,String[] options, View.OnClickListener[] listeners){
        if(isAdded()){
            getSupportActivity().showDialog(title,content,options, listeners);
        }
    }

    public View inflate(int id,ViewGroup container,boolean attach){
        return LayoutInflater.from(getContext()).inflate(id,container,attach);
    }
}
