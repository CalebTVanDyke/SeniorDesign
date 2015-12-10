package edu.iastate.ece.sd.dec1505.fragments;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.iastate.ece.sd.dec1505.R;
import edu.iastate.ece.sd.dec1505.tools.Prefs;

public class SettingsFragment extends ApplicationFragment{

    String BASE_DATA_URL = "http://databasesupport.arlenburroughs.com/492_db_query.php?query=";
    String SETTINGS_SQL =  "Select * from `users` where `id` = ";

    final int REQUEST_TIMEOUT = 10000;

    String settingsUrl;

    @Override
    public int getRootViewId() {//TODO
        return R.layout.fragment_settings;
    }

    @Override
    public void onConnectViews() {
        //TODO
    }

    @Override
    public void onInitialSetup(){
        //TODO
        settingsUrl = BASE_DATA_URL+SETTINGS_SQL.replace(' ','+')+ Prefs.getUserID(getContext());
    }

    @Override
    public void onRequestData(RequestQueue requestQueue) {

        //for debugging use parseData with sampleData
        //String sampleData = "\"[{\"id\":\"3\",\"username\":\"root\",\"password_hash\":\"2cbb71e8d423aa2f5bb9923fd1098838a384ca8a6d323ce764698af5249bf721,pmPga\",\"email\":\"root@root.com\",\"phone\":\"5153217935\",\"avg_blood_ox\":\"0\",\"avg_heart_rate\":\"0\",\"avg_temp\":\"0\",\"sensitivity\":\"m\"}]\"";
        //parseData(sampleData);

        requestQueue.add(getDataRequest());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_info, menu);
    }

    private StringRequest getDataRequest(){
        //progBar.setVisibility(View.VISIBLE);
        StringRequest sr = new StringRequest(settingsUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        parseData(data);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Data", "Data Fetch Error: " + volleyError.toString());
                        //progBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to load settings info.", Toast.LENGTH_LONG).show();
                    }
                }
        );
        sr.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return sr;
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

    private void showInfo() {
        showDialog("Info", getResources().getString(R.string.settings), null, null);
    }

    private void parseData(String data){
        Log.i("Settings data",data);

        String usrname = "", phone = "", receivers = "",sensativity = "";
        try {
            JSONArray jArr = new JSONArray(data);
            JSONObject jObj = jArr.getJSONObject(0);
            usrname = jObj.getString("username");
            phone = jObj.getString("phone");
            sensativity = jObj.getString("sensitivity");
            receivers = jObj.getString("receivers");
        } catch (JSONException e) {e.printStackTrace();}

        EditText tv = (EditText) findViewById(R.id.username_field);
        tv.setText(usrname);
        tv = (EditText) findViewById(R.id.phone_field);
        tv.setText(phone);
        tv = (EditText) findViewById(R.id.receivers_field);
        tv.setText(receivers);
        tv = (EditText) findViewById(R.id.sensativity_field);
        tv.setText(sensativity);
    }
}