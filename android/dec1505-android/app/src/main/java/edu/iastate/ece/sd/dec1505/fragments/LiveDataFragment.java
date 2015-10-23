package edu.iastate.ece.sd.dec1505.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;

import edu.iastate.ece.sd.dec1505.R;
import edu.iastate.ece.sd.dec1505.tools.DefaultRequestQueue;

public class LiveDataFragment extends ApplicationFragment implements Runnable{

    TextView bloodOxView;
    TextView heartRateView;
    TextView tempView;
    String WEB_SOCKET_ADDRESS = "";
    String TEST_DATA_URL =  "http://dec1505.sd.ece.iastate.edu/assets/php/rand_data_test.php";
    String ALERT_URL =      "http://arlenburroughs.com/extras/php_jobs/alert.php";

    Handler intervalTimer;
    int INTERVAL_TIME = 2000;

    boolean testingAlert=false;
    boolean alertSent = false;

    Button nightModeButton;
    boolean nightMode = false;

    Drawable btnBackground;

    @Override
    public int getRootViewId() {return R.layout.fragment_live_data;}

    @Override
    public void onConnectViews() {
        bloodOxView = (TextView) findViewById(R.id.spo2_text_view);
        heartRateView = (TextView) findViewById(R.id.heartrate_text_view);
        tempView = (TextView) findViewById(R.id.temp_text_view);

        nightModeButton = (Button) findViewById(R.id.night_mode);
        nightModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertNightMode();
            }
        });
    }

    private void convertNightMode() {
        nightMode = !nightMode;
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.live_data_wrapper);
        Window window = getSupportActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (nightMode) {
            nightModeButton.setText("Night");
            nightModeButton.setTextColor(Color.WHITE);
            btnBackground = nightModeButton.getBackground();
            nightModeButton.setBackground(getResources().getDrawable(R.drawable.selector_navdrawer_list_item));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
            rl.setBackgroundColor(getResources().getColor(R.color.black));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.black));
            getSupportActivity().nightModeOverlay(View.VISIBLE);
        }
        else{
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_color)));
            rl.setBackgroundColor(Color.TRANSPARENT);
            nightModeButton.setText("Day");
            nightModeButton.setTextColor(Color.BLACK);
            nightModeButton.setBackground(btnBackground);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.primary_dark_color));
            getSupportActivity().nightModeOverlay(View.INVISIBLE);
        }
    }

    @Override
    public void onInitialSetup(){
        //connectWebSocket();
    }

    @Override
    public void onRequestData(RequestQueue requestQueue){
        requestQueue.add(new StringRequest(TEST_DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        parseData(data);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Data", "Data Fetch Error: "+volleyError.toString());
                    }
                }
        ));
    }

    @Override
    public void run() {onRequestData(DefaultRequestQueue.getDefaultQueue(getContext()));}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_info, menu);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(intervalTimer!=null)intervalTimer.removeCallbacks(this);
        if(nightMode)convertNightMode();
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
        showDialog("Info", getResources().getString(R.string.live_data_info), null, null);
    }

    private void parseData(String data){
        Log.d("Data","Data Received: "+data);

        int heartRate = -1;
        int bloodOx = -1;
        double temp = -1;
        try {
            JSONObject jObj = new JSONObject(data);
            heartRate = jObj.getInt("heart");
            bloodOx = jObj.getInt("spo2");
            temp = jObj.getDouble("temp");
        } catch (JSONException e) {e.printStackTrace();}

        if(testingAlert){
            heartRate = 0;
            bloodOx=0;
            temp = 72.5;
        }
        detectDanger(heartRate, bloodOx, temp);
        updateViews(heartRate, bloodOx, temp);

        renewDataTimer();
    }

    private void detectDanger(int heartRate, int bloodOx, double temp){
        if(heartRate<40 || bloodOx < 60 || temp < 90)
            issueAlert();
    }

    private void updateViews(int heartRate, int bloodOx, double temp){
        String heartRateStr = ""+heartRate;
        DecimalFormat df2 = new DecimalFormat("00.0");
        while(heartRateStr.length()<3)heartRateStr="\u0020"+" "+heartRateStr;
        heartRateView.setText(heartRateStr+" bpm");
        bloodOxView.setText(bloodOx+"%");
        tempView.setText(df2.format(temp)+"°F");
    }

    private void renewDataTimer(){
        if(intervalTimer==null)intervalTimer = new Handler();
        else intervalTimer.removeCallbacks(this);
        intervalTimer.postDelayed(this, INTERVAL_TIME);
    }

    private void issueAlert(){
        if(alertSent)return;
        String contact = "5152973801@mms.uscc.net";

        RequestQueue requestQueue = DefaultRequestQueue.getDefaultQueue(getContext());
        requestQueue.add(new StringRequest(ALERT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        Log.d("Data", "Alert data back: " + data);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Data", "Data Fetch Error: "+volleyError.toString());
                    }
                }
        ));
        alertSent = true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //    WEB SOCKET
    WebSocketClient webSocketClient;
    private void connectWebSocket() {
        Log.i("Websocket", "Attempting to connect websocket...");

        URI uri;
        try {
            uri = new URI(WEB_SOCKET_ADDRESS);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                //parseData(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.e("Websocket", "Error " + e.getMessage());
            }
        };
        webSocketClient.connect();
    }
}
