package edu.iastate.ece.sd.dec1505.fragments;

import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.iastate.ece.sd.dec1505.R;
import edu.iastate.ece.sd.dec1505.models.Navigation;
import edu.iastate.ece.sd.dec1505.tools.DefaultRequestQueue;
import edu.iastate.ece.sd.dec1505.views.HistoryItemView;

public class HistoryFragment extends ApplicationFragment implements Runnable{

    final String BASE_DATA_URL =  "http://cvandyke.ddns.net/getDateRangeData?";
    String dataUrl="";
    ArrayList<Reading> readings = new ArrayList<>();
    ListView historyListView;
    HistoryDataAdapter historyDataAdapter;


    @Override
    public int getRootViewId() {return R.layout.fragment_history;}

    @Override
    public void onConnectViews() {
        historyListView = (ListView) rootView.findViewById(R.id.history_data_list);
        historyDataAdapter = new HistoryDataAdapter();
        historyListView.setAdapter(historyDataAdapter);
    }

    @Override
    public void onInitialSetup(){
        int userId= 3;
        String startDate = "2015-08-02";
        String endDate = "2015-08-02";
        int dataGap = 600;
        dataUrl = BASE_DATA_URL+"user_id="+userId+"&startDate="+startDate+"&endDate="+endDate+"&dataGap="+dataGap;
    }

    @Override
    public void onRequestData(RequestQueue requestQueue){
        requestQueue.add(new StringRequest(dataUrl,
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
                        ProgressBar progBar = (ProgressBar) rootView.findViewById(R.id.history_loading);
                        progBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(),"Failed to load data", Toast.LENGTH_LONG).show();
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
        //Log.d("Data","Data Received: "+data);

        try {
            JSONObject jObj = new JSONObject(data);
            JSONArray jArr = jObj.getJSONArray("blood_oxygen");

            int bloodOx = -1;
            String time = "";
            Reading tmpReading;
            for(int i=0; i<jArr.length(); i++){
                JSONObject readJSONObj = jArr.getJSONObject(i);
                bloodOx = readJSONObj.getInt("blood_oxygen");
                time = readJSONObj.getString("time");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Log.d("History", "At "+time+", SPO2: "+bloodOx);
                tmpReading = new Reading(ReadingType.BLOOD_OX, bloodOx, time);
                readings.add(tmpReading);
            }

        } catch (Exception e) {e.printStackTrace();}
        ProgressBar progBar = (ProgressBar) rootView.findViewById(R.id.history_loading);
        progBar.setVisibility(View.GONE);
        historyDataAdapter.notifyDataSetChanged();

    }

    class HistoryDataAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return readings.size();
        }

        @Override
        public Object getItem(int i) {
            return readings.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View recycleView, ViewGroup viewGroup) {

            HistoryItemView itemView;
            if(recycleView!=null && recycleView instanceof HistoryItemView) itemView = (HistoryItemView) recycleView;
            else itemView = new HistoryItemView(viewGroup.getContext());

            itemView.reset();
            Reading toView = readings.get(i);
            itemView.setPrimaryText(toView.time);
            itemView.setSecondaryText(""+toView.blooxOx);

            return itemView;
        }
    }





    //////////////////////
    // Modeling

    enum ReadingType{
        BLOOD_OX,HEART_RATE,TEMP;
    }

    class Reading{
        int blooxOx;
        int heart;
        int temp;
        String time;
        public Reading(ReadingType type, int value, String timeVal) {
            if(type==ReadingType.BLOOD_OX)blooxOx=value;
            else if(type==ReadingType.HEART_RATE)heart=value;
            else if(type==ReadingType.TEMP)temp=value;
            time = timeVal;
        }
    }
}
