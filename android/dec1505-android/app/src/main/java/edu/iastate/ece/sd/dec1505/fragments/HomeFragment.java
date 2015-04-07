package edu.iastate.ece.sd.dec1505.fragments;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import edu.iastate.ece.sd.dec1505.R;

public class HomeFragment extends ApplicationFragment {

    @Override
    public int getRootViewId() {return R.layout.fragment_home;}

    @Override
    public void onConnectViews() {
        //myImageView = (ImageView) findViewById(R.id.my_image);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_home, menu);
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
        String[] options = {"Dismiss","More Info"};
        View.OnClickListener[] listeners = {null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "MORE INFO", Toast.LENGTH_LONG).show();
            }
        }};
        showDialog("Info", "This is a blank app! Copy and paste to begin development!", options, listeners);
    }
}
