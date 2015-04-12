package edu.iastate.ece.sd.dec1505.fragments;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import edu.iastate.ece.sd.dec1505.R;

public class Fragment extends ApplicationFragment {

    @Override
    public int getRootViewId() {return R.layout.fragment_home;}

    @Override
    public void onConnectViews() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_info, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                showInfo();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showInfo(){
        showDialog("Info", getResources().getString(R.string.app_descrip), null, null);
    }
}
