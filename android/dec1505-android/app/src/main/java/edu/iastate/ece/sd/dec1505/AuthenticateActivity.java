package edu.iastate.ece.sd.dec1505;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.LinkedList;

import edu.iastate.ece.sd.dec1505.fragments.ApplicationFragment;
import edu.iastate.ece.sd.dec1505.models.Navigation;
import edu.iastate.ece.sd.dec1505.views.NavigationItemView;
import edu.iastate.ece.sd.dec1505.views.base.BaseLayout;


public class AuthenticateActivity extends ActionBarActivity{

    Toolbar toolbar;

    public final static String USERID = "current user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        setUpView();

        //Bypass login
        goToMainActivity("3");//TODO comment out this line to actually use log in.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void setUpView(){

        // Setup toolbar/actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userName = (EditText) findViewById(R.id.username_field);
                EditText pass = (EditText) findViewById(R.id.password_field);
                attemptAuthenticate(userName.getText().toString(),pass.getText().toString());
            }
        });
    }

    private void attemptAuthenticate(String attemptedUser, String attemptedPass){

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

        ProgressBar spinner = (ProgressBar) findViewById(R.id.auth_loading);
        spinner.setVisibility(View.VISIBLE);
        //TODO attempt auth

        //if successful
        goToMainActivity(attemptedUser);
    }

    private void goToMainActivity(String authenticatedUser){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(USERID, authenticatedUser);
        startActivity(intent);
        finish();
    }
}