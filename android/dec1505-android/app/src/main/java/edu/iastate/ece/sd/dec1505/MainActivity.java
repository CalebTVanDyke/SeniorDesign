package edu.iastate.ece.sd.dec1505;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.LinkedList;

import edu.iastate.ece.sd.dec1505.fragments.ApplicationFragment;
import edu.iastate.ece.sd.dec1505.models.Navigation;
import edu.iastate.ece.sd.dec1505.tools.Prefs;
import edu.iastate.ece.sd.dec1505.views.NavigationItemView;
import edu.iastate.ece.sd.dec1505.views.base.BaseLayout;


public class MainActivity extends ActionBarActivity{

    BaseLayout baseLayout;
    DrawerLayout navigationDrawer;
    ListView navigationDrawerListView;
    NavigationListAdapter navigationListAdapter;
    FrameLayout contentFrame;
    Toolbar toolbar;
    DrawerToggle drawerToggle;
    LinkedList<Navigation.NavigationItem> navigationItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        int userId = intent.getIntExtra(AuthenticateActivity.USERID, 0);

        Log.i("Auth","MainActivity with userId:"+userId);

        // Connect base layout to call bottom sheets and popups
        baseLayout = (BaseLayout) findViewById(R.id.base_layout);

        // Setup toolbar/actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Content frame will be replaced by fragments
        contentFrame = (FrameLayout) findViewById(R.id.content_frame);

        // Setup navigation drawer
        navigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        drawerToggle = new DrawerToggle();
        navigationDrawer.setDrawerListener(drawerToggle);
        navigationDrawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);

        // Setup navigation drawer list view
        navigationItems = Navigation.getNavigationItems();
        navigationDrawerListView = (ListView) findViewById(R.id.navigation_drawer_list_view);
        navigationDrawerListView.setAdapter(navigationListAdapter = new NavigationListAdapter());

        // Set initial fragment
        setNavigationItem(navigationItems.getFirst(), false);

        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(drawerToggle.onOptionsItemSelected(item)) return true;
        //else if (id == R.id.action_settings) return true;
        return super.onOptionsItemSelected(item);
    }

    public Toolbar getToolbar(){
        return toolbar;
    }

    Navigation.NavigationItem selectedItem;

    private void setNavigationItem(Navigation.NavigationItem item){
        setNavigationItem(item,true);
    }

    private void setNavigationItem(Navigation.NavigationItem item,boolean addToBackStack){
        try {
            ApplicationFragment fragment = (ApplicationFragment) item.fragmentClass.newInstance();
            fragment.setTitle(item.title);
            goToFragment(fragment,addToBackStack);
            selectedItem = item;
            navigationDrawer.closeDrawers();
            navigationListAdapter.notifyDataSetChanged();
        }
        catch (InstantiationException e1) { } catch (IllegalAccessException e2) { }
    }

    public void goToFragment(ApplicationFragment fragment,boolean addToBackStack){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        transaction.replace(R.id.content_frame, fragment, fragment.getTitle());
        if(addToBackStack) transaction.addToBackStack(fragment.getTitle());
        transaction.commit();
    }

    public void showBottomSheet(String title, View view) {
        baseLayout.showBottomSheet(title, view);
    }

    public void showDialog(String title,String message,String[] options, View.OnClickListener[] listeners){
        baseLayout.showDialog(title, message, options, listeners);
    }

    public void showDialog(String title,View content,String[] options, View.OnClickListener[] listeners){
        baseLayout.showDialog(title,content,options, listeners);
    }

    public void nightModeOverlay(int visible) {
        RelativeLayout overlay = (RelativeLayout) findViewById(R.id.entire_app_overlay);
        overlay.setVisibility(visible);
    }

    public class DrawerToggle extends ActionBarDrawerToggle {

        public DrawerToggle() {
            super(MainActivity.this, navigationDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        }
    }

    public class OnNavigationItemClickListener implements View.OnClickListener{

        Navigation.NavigationItem navigationItem;

        public OnNavigationItemClickListener(Navigation.NavigationItem item){
            navigationItem = item;
        }

        @Override
        public void onClick(View v) {
            if(navigationItem.title.equals("Log Out")){
                Prefs.put(getApplicationContext(), Prefs.AUTO_LOGIN, false);
                Intent i = new Intent(getContext(),AuthenticateActivity.class);
                startActivity(i);
                finish();
            }
            else setNavigationItem(navigationItem);
        }
    }

    public class NavigationListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return navigationItems.size();
        }

        @Override
        public Object getItem(int position) {
            return navigationItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Obtain proper navigation item view
            NavigationItemView view = convertView==null ?
                    new NavigationItemView(MainActivity.this) : (NavigationItemView) convertView;

            Navigation.NavigationItem item = getNavItem(position);

            // Handle conditionals
            view.setAsSelectedItem(selectedItem==item);

            // Handle non-conditionals
            view.setTitle(item.title);
            view.setOnClickListener(new OnNavigationItemClickListener(item));
            return view;
        }

        public Navigation.NavigationItem getNavItem(int position) {
            return (Navigation.NavigationItem) getItem(position);
        }

    }

    private Context getContext(){
        return getApplicationContext();
    }

}
