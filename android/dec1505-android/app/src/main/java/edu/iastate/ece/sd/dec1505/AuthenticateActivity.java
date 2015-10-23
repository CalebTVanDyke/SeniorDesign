package edu.iastate.ece.sd.dec1505;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.iastate.ece.sd.dec1505.tools.Prefs;


public class AuthenticateActivity extends ActionBarActivity{

    Toolbar toolbar;
    EditText user,pass;
    CheckBox stayLoggedInBox;

    public final static String USERID = "current user";
    final String AUTH_URL = "http://cvandyke.ddns.net/apiLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        setUpView();

        boolean autoLogin = Prefs.get(getApplicationContext(),Prefs.AUTO_LOGIN);
        if(autoLogin){
            user.setText(Prefs.getLastUser(getApplicationContext()));
            pass.setText(Prefs.getLastPass(getApplicationContext()));
            attemptLogin(Prefs.getLastUser(getApplicationContext()), Prefs.getLastPass(getApplicationContext()));
        }
        //Bypass login
        //goToMainActivity("3");//TODO comment out this line to actually use log in.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void setUpView(){

        user = (EditText) findViewById(R.id.username_field);
        pass = (EditText) findViewById(R.id.password_field);
        stayLoggedInBox = (CheckBox) findViewById(R.id.stay_logged_in_box);

        // Setup toolbar/actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.put(getApplicationContext(), Prefs.LAST_USER, user.getText().toString());
                Prefs.put(getApplicationContext(),Prefs.LAST_PASS,pass.getText().toString());
                attemptLogin(user.getText().toString(), pass.getText().toString());
            }
        });
        stayLoggedInBox.setChecked(Prefs.get(getApplicationContext(),Prefs.AUTO_LOGIN));
    }

    private void attemptLogin(String attemptedUser, String attemptedPass){

        Log.i("Auth", "U:"+attemptedUser+", P:"+attemptedPass);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus()!=null)imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

        ProgressBar spinner = (ProgressBar) findViewById(R.id.auth_loading);
        spinner.setVisibility(View.VISIBLE);

        new HttpPostTask().execute(attemptedUser, attemptedPass);
    }

    private void finishAuthenticationAttempt(HttpResponse response){
        if(response == null){
            authFail();
            return;
        }

        HttpEntity entity = response.getEntity();

        boolean success = false;
        int userId = -1;
        JSONObject jObj = null;
        try{
            jObj = new JSONObject(EntityUtils.toString(entity, "UTF-8"));
            success = !jObj.getBoolean("error");
            userId = jObj.getInt("user_id");
        }catch(Exception e){e.printStackTrace();}

        Log.i("Auth", "Auth attempt successful?:" + success+", response:"+jObj);

        if(success){
            Prefs.put(getApplicationContext(),Prefs.AUTO_LOGIN,stayLoggedInBox.isChecked());
            goToMainActivity(userId);
        }
        else authFail();
    }

    private void authFail(){
        ProgressBar spinner = (ProgressBar) findViewById(R.id.auth_loading);
        spinner.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(),"Incorrect username/password. Please try again.",Toast.LENGTH_SHORT).show();
    }

    private void goToMainActivity(int authenticatedUser){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(USERID, authenticatedUser);
        startActivity(intent);
        finish();
    }

    class HttpPostTask extends AsyncTask<String, Void, HttpResponse> {

        protected HttpResponse doInBackground(String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(AUTH_URL);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("username", args[0]));
                nameValuePairs.add(new BasicNameValuePair("password", args[1]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                return httpclient.execute(httppost);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(HttpResponse response) {
            finishAuthenticationAttempt(response);
        }
    }
}