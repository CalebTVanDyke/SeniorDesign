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
import android.widget.EditText;
import android.widget.ProgressBar;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AuthenticateActivity extends ActionBarActivity{

    Toolbar toolbar;

    public final static String USERID = "current user";
    final String AUTH_URL = "http://cvandyke.ddns.net/apiLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        setUpView();

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
                attemptLogin(userName.getText().toString(), pass.getText().toString());
            }
        });
    }

    private void attemptLogin(String attemptedUser, String attemptedPass){

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

        ProgressBar spinner = (ProgressBar) findViewById(R.id.auth_loading);
        spinner.setVisibility(View.VISIBLE);

        //TODO attempt auth
        EditText usrNameField = (EditText) findViewById(R.id.username_field);
        String username = usrNameField.getText().toString();
        EditText psswrdField = (EditText) findViewById(R.id.password_field);
        String password = psswrdField.getText().toString();

        new RetrieveFeedTask().execute(username, password);
    }

    private void finishAuthenticationAttempt(HttpResponse response){

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

        if(success)goToMainActivity(userId);
    }

    private void goToMainActivity(int authenticatedUser){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(USERID, authenticatedUser);
        startActivity(intent);
        finish();
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, HttpResponse> {

        private Exception exception;

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
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return null;
        }

        protected void onPostExecute(HttpResponse response) {
            finishAuthenticationAttempt(response);
        }
    }
}