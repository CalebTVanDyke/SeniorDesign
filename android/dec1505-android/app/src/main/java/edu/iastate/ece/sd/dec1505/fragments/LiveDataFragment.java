package edu.iastate.ece.sd.dec1505.fragments;

import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import edu.iastate.ece.sd.dec1505.R;

public class LiveDataFragment extends ApplicationFragment {

    TextView value;

    @Override
    public int getRootViewId() {return R.layout.fragment_live_data;}

    @Override
    public void onConnectViews() {
        value = (TextView)findViewById(R.id.value);
    }

    @Override
    public void onInitialSetup(){
        connectWebSocket();
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
        showDialog("Info", getResources().getString(R.string.live_data_info), null, null);
    }

    WebSocketClient webSocketClient;
    private void connectWebSocket() {
        Log.i("Websocket", "Attempting to connect websocket...");

        String webSocketAddr = "";
        URI uri;
        try {
            uri = new URI(webSocketAddr);
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
                getSupportActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        value.setText(message);
                    }
                });
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
