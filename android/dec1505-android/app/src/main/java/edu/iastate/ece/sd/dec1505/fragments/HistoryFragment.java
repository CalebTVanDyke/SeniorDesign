package edu.iastate.ece.sd.dec1505.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.iastate.ece.sd.dec1505.R;
import edu.iastate.ece.sd.dec1505.models.Reading;
import edu.iastate.ece.sd.dec1505.tools.DefaultRequestQueue;
import edu.iastate.ece.sd.dec1505.views.HistoryItemView;

public class HistoryFragment extends ApplicationFragment implements Runnable{

    final String BASE_DATA_URL =  "http://cvandyke.ddns.net/getDateRangeData?";
    String dataUrl="";
    ArrayList<Reading> readings = new ArrayList<>();
    ListView historyListView;
    HistoryDataAdapter historyDataAdapter;

    static TextView fromDate;
    static TextView toDate;
    static TextView fromTime;
    static TextView toTime;

    static SimpleDateFormat timeFormat, dateFormat, twntyFourHrFormat;


    @Override
    public int getRootViewId() {return R.layout.fragment_history;}

    @Override
    public void onConnectViews() {

        fromDate = (TextView) findViewById(R.id.date_from);
        toDate = (TextView) findViewById(R.id.date_to);
        fromDate.setOnClickListener(getDateOnClick(fromDate,true));
        toDate.setOnClickListener(getDateOnClick(toDate,false));

        fromTime = (TextView) findViewById(R.id.time_from);
        toTime = (TextView) findViewById(R.id.time_to);
        fromTime.setOnClickListener(getTimeOnClick(fromTime,true));
        toTime.setOnClickListener(getTimeOnClick(toTime, false));

        historyListView = (ListView) rootView.findViewById(R.id.history_data_list);
        historyDataAdapter = new HistoryDataAdapter();
        historyListView.setAdapter(historyDataAdapter);
    }

    @Override
    public void onInitialSetup(){
        int userId= 3;
        String startDate = "2015-08-02";
        String endDate = "2015-08-02";
        int dataGap = 1200;
        dataUrl = BASE_DATA_URL+"user_id="+userId+"&startDate="+startDate+"&endDate="+endDate+"&dataGap="+dataGap;

        timeFormat = new SimpleDateFormat("h:mm a");//12 hour format
        if(DateFormat.is24HourFormat(getActivity())) timeFormat = new SimpleDateFormat("HH:mm");//24 hour format
        dateFormat = new SimpleDateFormat("M/d/y");
        twntyFourHrFormat = new SimpleDateFormat("H:mm");

        setCurrentDateTimeValues();
    }

    @Override
    public void onRequestData(RequestQueue requestQueue){

        //quick debugging
        parseData("{ \"blood_oxygen\": [ { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 00:00:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 00:10:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 00:20:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 00:30:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 00:40:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 00:50:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 01:00:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 01:10:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 01:20:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 01:30:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 01:40:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 01:50:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 02:00:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 02:10:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 02:20:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 02:30:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 02:40:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 02:50:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 03:00:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 03:10:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 03:20:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 03:30:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 03:40:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 03:50:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 04:00:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 04:10:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 04:20:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 04:30:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 04:40:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 04:50:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 05:00:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 05:10:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 05:20:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 05:30:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 05:40:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 05:50:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 06:00:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 06:10:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 06:20:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 06:30:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 06:40:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 06:50:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 07:00:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 07:10:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 07:20:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 07:30:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 07:40:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 07:50:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 08:00:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 08:10:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 08:20:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 08:30:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 08:40:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 08:50:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 09:00:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 09:10:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 09:20:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 09:30:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 09:40:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 09:50:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 10:00:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 10:10:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 10:20:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 10:30:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 10:40:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 10:50:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 11:00:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 11:10:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 11:20:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 11:30:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 11:40:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 11:50:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 12:00:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 12:10:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 12:20:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 12:30:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 12:40:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 12:50:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 13:00:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 13:10:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 13:20:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 13:30:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 13:40:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 13:50:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 14:00:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 14:10:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 14:20:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 14:30:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 14:40:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 14:50:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 15:00:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 15:10:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 15:20:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 15:30:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 15:40:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 15:50:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 16:00:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 16:10:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 16:20:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 16:30:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 16:40:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 16:50:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 17:00:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 17:10:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 17:20:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 17:30:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 17:40:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 17:50:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 18:00:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 18:10:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 18:20:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 18:30:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 18:40:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 18:50:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 19:00:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 19:10:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 19:20:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 19:30:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 19:40:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 19:50:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 20:00:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 20:10:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 20:20:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 20:30:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 20:40:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 20:50:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 21:00:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 21:10:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 21:20:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 21:30:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 21:40:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 21:50:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 22:00:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 22:10:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 22:20:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 22:30:00\" }, { \"blood_oxygen\": \"98\", \"time\": \"2015-08-02 22:40:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 22:50:00\" }, { \"blood_oxygen\": \"95\", \"time\": \"2015-08-02 23:00:00\" }, { \"blood_oxygen\": \"100\", \"time\": \"2015-08-02 23:10:00\" }, { \"blood_oxygen\": \"99\", \"time\": \"2015-08-02 23:20:00\" }, { \"blood_oxygen\": \"96\", \"time\": \"2015-08-02 23:30:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 23:40:00\" }, { \"blood_oxygen\": \"97\", \"time\": \"2015-08-02 23:50:00\" } ], \"heart_rate\": [ { \"heart_rate\": \"94\", \"time\": \"2015-08-02 00:00:00\" }, { \"heart_rate\": \"93\", \"time\": \"2015-08-02 00:10:00\" }, { \"heart_rate\": \"81\", \"time\": \"2015-08-02 00:20:00\" }, { \"heart_rate\": \"107\", \"time\": \"2015-08-02 00:30:00\" }, { \"heart_rate\": \"110\", \"time\": \"2015-08-02 00:40:00\" }, { \"heart_rate\": \"90\", \"time\": \"2015-08-02 00:50:00\" }, { \"heart_rate\": \"106\", \"time\": \"2015-08-02 01:00:00\" }, { \"heart_rate\": \"86\", \"time\": \"2015-08-02 01:10:00\" }, { \"heart_rate\": \"102\", \"time\": \"2015-08-02 01:20:00\" }, { \"heart_rate\": \"89\", \"time\": \"2015-08-02 01:30:00\" }, { \"heart_rate\": \"95\", \"time\": \"2015-08-02 01:40:00\" }, { \"heart_rate\": \"89\", \"time\": \"2015-08-02 01:50:00\" }, { \"heart_rate\": \"88\", \"time\": \"2015-08-02 02:00:00\" }, { \"heart_rate\": \"89\", \"time\": \"2015-08-02 02:10:00\" }, { \"heart_rate\": \"86\", \"time\": \"2015-08-02 02:20:00\" }, { \"heart_rate\": \"93\", \"time\": \"2015-08-02 02:30:00\" }, { \"heart_rate\": \"84\", \"time\": \"2015-08-02 02:40:00\" }, { \"heart_rate\": \"99\", \"time\": \"2015-08-02 02:50:00\" }, { \"heart_rate\": \"110\", \"time\": \"2015-08-02 03:00:00\" }, { \"heart_rate\": \"82\", \"time\": \"2015-08-02 03:10:00\" }, { \"heart_rate\": \"83\", \"time\": \"2015-08-02 03:20:00\" }, { \"heart_rate\": \"89\", \"time\": \"2015-08-02 03:30:00\" }, { \"heart_rate\": \"101\", \"time\": \"2015-08-02 03:40:00\" }, { \"heart_rate\": \"81\", \"time\": \"2015-08-02 03:50:00\" }, { \"heart_rate\": \"109\", \"time\": \"2015-08-02 04:00:00\" }, { \"heart_rate\": \"107\", \"time\": \"2015-08-02 04:10:00\" }, { \"heart_rate\": \"110\", \"time\": \"2015-08-02 04:20:00\" }, { \"heart_rate\": \"88\", \"time\": \"2015-08-02 04:30:00\" }, { \"heart_rate\": \"80\", \"time\": \"2015-08-02 04:40:00\" }, { \"heart_rate\": \"110\", \"time\": \"2015-08-02 04:50:00\" }, { \"heart_rate\": \"105\", \"time\": \"2015-08-02 05:00:00\" }, { \"heart_rate\": \"80\", \"time\": \"2015-08-02 05:10:00\" }, { \"heart_rate\": \"81\", \"time\": \"2015-08-02 05:20:00\" }, { \"heart_rate\": \"108\", \"time\": \"2015-08-02 05:30:00\" }, { \"heart_rate\": \"84\", \"time\": \"2015-08-02 05:40:00\" }, { \"heart_rate\": \"97\", \"time\": \"2015-08-02 05:50:00\" }, { \"heart_rate\": \"107\", \"time\": \"2015-08-02 06:00:00\" }, { \"heart_rate\": \"90\", \"time\": \"2015-08-02 06:10:00\" }, { \"heart_rate\": \"93\", \"time\": \"2015-08-02 06:20:00\" }, { \"heart_rate\": \"90\", \"time\": \"2015-08-02 06:30:00\" }, { \"heart_rate\": \"82\", \"time\": \"2015-08-02 06:40:00\" }, { \"heart_rate\": \"91\", \"time\": \"2015-08-02 06:50:00\" }, { \"heart_rate\": \"99\", \"time\": \"2015-08-02 07:00:00\" }, { \"heart_rate\": \"87\", \"time\": \"2015-08-02 07:10:00\" }, { \"heart_rate\": \"85\", \"time\": \"2015-08-02 07:20:00\" }, { \"heart_rate\": \"86\", \"time\": \"2015-08-02 07:30:00\" }, { \"heart_rate\": \"98\", \"time\": \"2015-08-02 07:40:00\" }, { \"heart_rate\": \"96\", \"time\": \"2015-08-02 07:50:00\" }, { \"heart_rate\": \"108\", \"time\": \"2015-08-02 08:00:00\" }, { \"heart_rate\": \"92\", \"time\": \"2015-08-02 08:10:00\" }, { \"heart_rate\": \"88\", \"time\": \"2015-08-02 08:20:00\" }, { \"heart_rate\": \"84\", \"time\": \"2015-08-02 08:30:00\" }, { \"heart_rate\": \"85\", \"time\": \"2015-08-02 08:40:00\" }, { \"heart_rate\": \"84\", \"time\": \"2015-08-02 08:50:00\" }, { \"heart_rate\": \"90\", \"time\": \"2015-08-02 09:00:00\" }, { \"heart_rate\": \"105\", \"time\": \"2015-08-02 09:10:00\" }, { \"heart_rate\": \"85\", \"time\": \"2015-08-02 09:20:00\" }, { \"heart_rate\": \"105\", \"time\": \"2015-08-02 09:30:00\" }, { \"heart_rate\": \"99\", \"time\": \"2015-08-02 09:40:00\" }, { \"heart_rate\": \"82\", \"time\": \"2015-08-02 09:50:00\" }, { \"heart_rate\": \"91\", \"time\": \"2015-08-02 10:00:00\" }, { \"heart_rate\": \"83\", \"time\": \"2015-08-02 10:10:00\" }, { \"heart_rate\": \"86\", \"time\": \"2015-08-02 10:20:00\" }, { \"heart_rate\": \"85\", \"time\": \"2015-08-02 10:30:00\" }, { \"heart_rate\": \"105\", \"time\": \"2015-08-02 10:40:00\" }, { \"heart_rate\": \"82\", \"time\": \"2015-08-02 10:50:00\" }, { \"heart_rate\": \"94\", \"time\": \"2015-08-02 11:00:00\" }, { \"heart_rate\": \"106\", \"time\": \"2015-08-02 11:10:00\" }, { \"heart_rate\": \"86\", \"time\": \"2015-08-02 11:20:00\" }, { \"heart_rate\": \"82\", \"time\": \"2015-08-02 11:30:00\" }, { \"heart_rate\": \"87\", \"time\": \"2015-08-02 11:40:00\" }, { \"heart_rate\": \"102\", \"time\": \"2015-08-02 11:50:00\" }, { \"heart_rate\": \"89\", \"time\": \"2015-08-02 12:00:00\" }, { \"heart_rate\": \"92\", \"time\": \"2015-08-02 12:10:00\" }, { \"heart_rate\": \"100\", \"time\": \"2015-08-02 12:20:00\" }, { \"heart_rate\": \"109\", \"time\": \"2015-08-02 12:30:00\" }, { \"heart_rate\": \"110\", \"time\": \"2015-08-02 12:40:00\" }, { \"heart_rate\": \"94\", \"time\": \"2015-08-02 12:50:00\" }, { \"heart_rate\": \"109\", \"time\": \"2015-08-02 13:00:00\" }, { \"heart_rate\": \"97\", \"time\": \"2015-08-02 13:10:00\" }, { \"heart_rate\": \"91\", \"time\": \"2015-08-02 13:20:00\" }, { \"heart_rate\": \"95\", \"time\": \"2015-08-02 13:30:00\" }, { \"heart_rate\": \"86\", \"time\": \"2015-08-02 13:40:00\" }, { \"heart_rate\": \"84\", \"time\": \"2015-08-02 13:50:00\" }, { \"heart_rate\": \"108\", \"time\": \"2015-08-02 14:00:00\" }, { \"heart_rate\": \"84\", \"time\": \"2015-08-02 14:10:00\" }, { \"heart_rate\": \"82\", \"time\": \"2015-08-02 14:20:00\" }, { \"heart_rate\": \"102\", \"time\": \"2015-08-02 14:30:00\" }, { \"heart_rate\": \"104\", \"time\": \"2015-08-02 14:40:00\" }, { \"heart_rate\": \"90\", \"time\": \"2015-08-02 14:50:00\" }, { \"heart_rate\": \"92\", \"time\": \"2015-08-02 15:00:00\" }, { \"heart_rate\": \"98\", \"time\": \"2015-08-02 15:10:00\" }, { \"heart_rate\": \"93\", \"time\": \"2015-08-02 15:20:00\" }, { \"heart_rate\": \"90\", \"time\": \"2015-08-02 15:30:00\" }, { \"heart_rate\": \"96\", \"time\": \"2015-08-02 15:40:00\" }, { \"heart_rate\": \"86\", \"time\": \"2015-08-02 15:50:00\" }, { \"heart_rate\": \"100\", \"time\": \"2015-08-02 16:00:00\" }, { \"heart_rate\": \"85\", \"time\": \"2015-08-02 16:10:00\" }, { \"heart_rate\": \"85\", \"time\": \"2015-08-02 16:20:00\" }, { \"heart_rate\": \"102\", \"time\": \"2015-08-02 16:30:00\" }, { \"heart_rate\": \"82\", \"time\": \"2015-08-02 16:40:00\" }, { \"heart_rate\": \"84\", \"time\": \"2015-08-02 16:50:00\" }, { \"heart_rate\": \"98\", \"time\": \"2015-08-02 17:00:00\" }, { \"heart_rate\": \"85\", \"time\": \"2015-08-02 17:10:00\" }, { \"heart_rate\": \"110\", \"time\": \"2015-08-02 17:20:00\" }, { \"heart_rate\": \"105\", \"time\": \"2015-08-02 17:30:00\" }, { \"heart_rate\": \"89\", \"time\": \"2015-08-02 17:40:00\" }, { \"heart_rate\": \"98\", \"time\": \"2015-08-02 17:50:00\" }, { \"heart_rate\": \"80\", \"time\": \"2015-08-02 18:00:00\" }, { \"heart_rate\": \"88\", \"time\": \"2015-08-02 18:10:00\" }, { \"heart_rate\": \"106\", \"time\": \"2015-08-02 18:20:00\" }, { \"heart_rate\": \"83\", \"time\": \"2015-08-02 18:30:00\" }, { \"heart_rate\": \"90\", \"time\": \"2015-08-02 18:40:00\" }, { \"heart_rate\": \"106\", \"time\": \"2015-08-02 18:50:00\" }, { \"heart_rate\": \"92\", \"time\": \"2015-08-02 19:00:00\" }, { \"heart_rate\": \"105\", \"time\": \"2015-08-02 19:10:00\" }, { \"heart_rate\": \"91\", \"time\": \"2015-08-02 19:20:00\" }, { \"heart_rate\": \"82\", \"time\": \"2015-08-02 19:30:00\" }, { \"heart_rate\": \"107\", \"time\": \"2015-08-02 19:40:00\" }, { \"heart_rate\": \"83\", \"time\": \"2015-08-02 19:50:00\" }, { \"heart_rate\": \"94\", \"time\": \"2015-08-02 20:00:00\" }, { \"heart_rate\": \"88\", \"time\": \"2015-08-02 20:10:00\" }, { \"heart_rate\": \"107\", \"time\": \"2015-08-02 20:20:00\" }, { \"heart_rate\": \"98\", \"time\": \"2015-08-02 20:30:00\" }, { \"heart_rate\": \"100\", \"time\": \"2015-08-02 20:40:00\" }, { \"heart_rate\": \"110\", \"time\": \"2015-08-02 20:50:00\" }, { \"heart_rate\": \"105\", \"time\": \"2015-08-02 21:00:00\" }, { \"heart_rate\": \"102\", \"time\": \"2015-08-02 21:10:00\" }, { \"heart_rate\": \"105\", \"time\": \"2015-08-02 21:20:00\" }, { \"heart_rate\": \"93\", \"time\": \"2015-08-02 21:30:00\" }, { \"heart_rate\": \"89\", \"time\": \"2015-08-02 21:40:00\" }, { \"heart_rate\": \"84\", \"time\": \"2015-08-02 21:50:00\" }, { \"heart_rate\": \"108\", \"time\": \"2015-08-02 22:00:00\" }, { \"heart_rate\": \"85\", \"time\": \"2015-08-02 22:10:00\" }, { \"heart_rate\": \"109\", \"time\": \"2015-08-02 22:20:00\" }, { \"heart_rate\": \"94\", \"time\": \"2015-08-02 22:30:00\" }, { \"heart_rate\": \"91\", \"time\": \"2015-08-02 22:40:00\" }, { \"heart_rate\": \"108\", \"time\": \"2015-08-02 22:50:00\" }, { \"heart_rate\": \"110\", \"time\": \"2015-08-02 23:00:00\" }, { \"heart_rate\": \"107\", \"time\": \"2015-08-02 23:10:00\" }, { \"heart_rate\": \"92\", \"time\": \"2015-08-02 23:20:00\" }, { \"heart_rate\": \"86\", \"time\": \"2015-08-02 23:30:00\" }, { \"heart_rate\": \"81\", \"time\": \"2015-08-02 23:40:00\" }, { \"heart_rate\": \"86\", \"time\": \"2015-08-02 23:50:00\" } ], \"temp\": [ { \"temp\": \"98.7338306449\", \"time\": \"2015-08-02 00:00:00\" }, { \"temp\": \"98.1006553805\", \"time\": \"2015-08-02 00:10:00\" }, { \"temp\": \"97.7756884079\", \"time\": \"2015-08-02 00:20:00\" }, { \"temp\": \"98.2387520817\", \"time\": \"2015-08-02 00:30:00\" }, { \"temp\": \"97.76852418\", \"time\": \"2015-08-02 00:40:00\" }, { \"temp\": \"98.5219206352\", \"time\": \"2015-08-02 00:50:00\" }, { \"temp\": \"98.0502361432\", \"time\": \"2015-08-02 01:00:00\" }, { \"temp\": \"97.6161038844\", \"time\": \"2015-08-02 01:10:00\" }, { \"temp\": \"98.9119246101\", \"time\": \"2015-08-02 01:20:00\" }, { \"temp\": \"99.1811666904\", \"time\": \"2015-08-02 01:30:00\" }, { \"temp\": \"99.796134993\", \"time\": \"2015-08-02 01:40:00\" }, { \"temp\": \"99.0005775095\", \"time\": \"2015-08-02 01:50:00\" }, { \"temp\": \"99.6862776764\", \"time\": \"2015-08-02 02:00:00\" }, { \"temp\": \"99.9578767525\", \"time\": \"2015-08-02 02:10:00\" }, { \"temp\": \"98.7905058466\", \"time\": \"2015-08-02 02:20:00\" }, { \"temp\": \"99.9107404598\", \"time\": \"2015-08-02 02:30:00\" }, { \"temp\": \"98.0966568883\", \"time\": \"2015-08-02 02:40:00\" }, { \"temp\": \"99.4456351942\", \"time\": \"2015-08-02 02:50:00\" }, { \"temp\": \"99.5842903457\", \"time\": \"2015-08-02 03:00:00\" }, { \"temp\": \"99.4319843559\", \"time\": \"2015-08-02 03:10:00\" }, { \"temp\": \"97.3607930246\", \"time\": \"2015-08-02 03:20:00\" }, { \"temp\": \"99.0210612689\", \"time\": \"2015-08-02 03:30:00\" }, { \"temp\": \"98.4762772731\", \"time\": \"2015-08-02 03:40:00\" }, { \"temp\": \"99.5562104331\", \"time\": \"2015-08-02 03:50:00\" }, { \"temp\": \"98.4374849706\", \"time\": \"2015-08-02 04:00:00\" }, { \"temp\": \"98.2997549555\", \"time\": \"2015-08-02 04:10:00\" }, { \"temp\": \"99.0476928955\", \"time\": \"2015-08-02 04:20:00\" }, { \"temp\": \"97.0747486363\", \"time\": \"2015-08-02 04:30:00\" }, { \"temp\": \"98.1552712697\", \"time\": \"2015-08-02 04:40:00\" }, { \"temp\": \"99.8047173516\", \"time\": \"2015-08-02 04:50:00\" }, { \"temp\": \"97.9099304477\", \"time\": \"2015-08-02 05:00:00\" }, { \"temp\": \"99.9731677423\", \"time\": \"2015-08-02 05:10:00\" }, { \"temp\": \"98.1837466305\", \"time\": \"2015-08-02 05:20:00\" }, { \"temp\": \"97.2943801801\", \"time\": \"2015-08-02 05:30:00\" }, { \"temp\": \"98.5298871703\", \"time\": \"2015-08-02 05:40:00\" }, { \"temp\": \"99.348647921\", \"time\": \"2015-08-02 05:50:00\" }, { \"temp\": \"98.1052513745\", \"time\": \"2015-08-02 06:00:00\" }, { \"temp\": \"98.6659030396\", \"time\": \"2015-08-02 06:10:00\" }, { \"temp\": \"97.884968215\", \"time\": \"2015-08-02 06:20:00\" }, { \"temp\": \"98.2969217211\", \"time\": \"2015-08-02 06:30:00\" }, { \"temp\": \"99.1659144179\", \"time\": \"2015-08-02 06:40:00\" }, { \"temp\": \"97.6564240673\", \"time\": \"2015-08-02 06:50:00\" }, { \"temp\": \"99.3546139132\", \"time\": \"2015-08-02 07:00:00\" }, { \"temp\": \"99.8587221164\", \"time\": \"2015-08-02 07:10:00\" }, { \"temp\": \"97.47067179\", \"time\": \"2015-08-02 07:20:00\" }, { \"temp\": \"99.8420305531\", \"time\": \"2015-08-02 07:30:00\" }, { \"temp\": \"97.6300731525\", \"time\": \"2015-08-02 07:40:00\" }, { \"temp\": \"99.2740708139\", \"time\": \"2015-08-02 07:50:00\" }, { \"temp\": \"97.003550656\", \"time\": \"2015-08-02 08:00:00\" }, { \"temp\": \"97.0508537617\", \"time\": \"2015-08-02 08:10:00\" }, { \"temp\": \"97.7065821945\", \"time\": \"2015-08-02 08:20:00\" }, { \"temp\": \"97.5773400763\", \"time\": \"2015-08-02 08:30:00\" }, { \"temp\": \"99.7781096151\", \"time\": \"2015-08-02 08:40:00\" }, { \"temp\": \"99.683184724\", \"time\": \"2015-08-02 08:50:00\" }, { \"temp\": \"99.0017492282\", \"time\": \"2015-08-02 09:00:00\" }, { \"temp\": \"99.9363224886\", \"time\": \"2015-08-02 09:10:00\" }, { \"temp\": \"97.9918290109\", \"time\": \"2015-08-02 09:20:00\" }, { \"temp\": \"98.2870621745\", \"time\": \"2015-08-02 09:30:00\" }, { \"temp\": \"98.4974945035\", \"time\": \"2015-08-02 09:40:00\" }, { \"temp\": \"99.9890789007\", \"time\": \"2015-08-02 09:50:00\" }, { \"temp\": \"97.6296463413\", \"time\": \"2015-08-02 10:00:00\" }, { \"temp\": \"99.2530309716\", \"time\": \"2015-08-02 10:10:00\" }, { \"temp\": \"99.0745456364\", \"time\": \"2015-08-02 10:20:00\" }, { \"temp\": \"98.6701335275\", \"time\": \"2015-08-02 10:30:00\" }, { \"temp\": \"98.896164064\", \"time\": \"2015-08-02 10:40:00\" }, { \"temp\": \"98.8690496987\", \"time\": \"2015-08-02 10:50:00\" }, { \"temp\": \"98.2248235565\", \"time\": \"2015-08-02 11:00:00\" }, { \"temp\": \"98.7608189545\", \"time\": \"2015-08-02 11:10:00\" }, { \"temp\": \"98.0979962626\", \"time\": \"2015-08-02 11:20:00\" }, { \"temp\": \"99.6981121704\", \"time\": \"2015-08-02 11:30:00\" }, { \"temp\": \"97.9629177295\", \"time\": \"2015-08-02 11:40:00\" }, { \"temp\": \"97.8302125962\", \"time\": \"2015-08-02 11:50:00\" }, { \"temp\": \"98.1239117478\", \"time\": \"2015-08-02 12:00:00\" }, { \"temp\": \"98.1340303945\", \"time\": \"2015-08-02 12:10:00\" }, { \"temp\": \"98.7693405932\", \"time\": \"2015-08-02 12:20:00\" }, { \"temp\": \"97.6108327902\", \"time\": \"2015-08-02 12:30:00\" }, { \"temp\": \"97.871933102\", \"time\": \"2015-08-02 12:40:00\" }, { \"temp\": \"99.9634107847\", \"time\": \"2015-08-02 12:50:00\" }, { \"temp\": \"98.0915631709\", \"time\": \"2015-08-02 13:00:00\" }, { \"temp\": \"97.9552312239\", \"time\": \"2015-08-02 13:10:00\" }, { \"temp\": \"97.4828105544\", \"time\": \"2015-08-02 13:20:00\" }, { \"temp\": \"99.0553611538\", \"time\": \"2015-08-02 13:30:00\" }, { \"temp\": \"98.0595140594\", \"time\": \"2015-08-02 13:40:00\" }, { \"temp\": \"98.1088632821\", \"time\": \"2015-08-02 13:50:00\" }, { \"temp\": \"98.3778829087\", \"time\": \"2015-08-02 14:00:00\" }, { \"temp\": \"99.3896203146\", \"time\": \"2015-08-02 14:10:00\" }, { \"temp\": \"99.6757291544\", \"time\": \"2015-08-02 14:20:00\" }, { \"temp\": \"99.2184264791\", \"time\": \"2015-08-02 14:30:00\" }, { \"temp\": \"98.6246554878\", \"time\": \"2015-08-02 14:40:00\" }, { \"temp\": \"98.2688668504\", \"time\": \"2015-08-02 14:50:00\" }, { \"temp\": \"97.7367359227\", \"time\": \"2015-08-02 15:00:00\" }, { \"temp\": \"99.9836757789\", \"time\": \"2015-08-02 15:10:00\" }, { \"temp\": \"98.9249127135\", \"time\": \"2015-08-02 15:20:00\" }, { \"temp\": \"98.6280083825\", \"time\": \"2015-08-02 15:30:00\" }, { \"temp\": \"99.7657282318\", \"time\": \"2015-08-02 15:40:00\" }, { \"temp\": \"99.8203186987\", \"time\": \"2015-08-02 15:50:00\" }, { \"temp\": \"99.2650631523\", \"time\": \"2015-08-02 16:00:00\" }, { \"temp\": \"98.6334042359\", \"time\": \"2015-08-02 16:10:00\" }, { \"temp\": \"98.5426381819\", \"time\": \"2015-08-02 16:20:00\" }, { \"temp\": \"97.8027014223\", \"time\": \"2015-08-02 16:30:00\" }, { \"temp\": \"98.174284611\", \"time\": \"2015-08-02 16:40:00\" }, { \"temp\": \"97.5566451246\", \"time\": \"2015-08-02 16:50:00\" }, { \"temp\": \"98.2062764853\", \"time\": \"2015-08-02 17:00:00\" }, { \"temp\": \"98.6904679923\", \"time\": \"2015-08-02 17:10:00\" }, { \"temp\": \"98.2575624424\", \"time\": \"2015-08-02 17:20:00\" }, { \"temp\": \"98.8619340858\", \"time\": \"2015-08-02 17:30:00\" }, { \"temp\": \"99.0868382699\", \"time\": \"2015-08-02 17:40:00\" }, { \"temp\": \"98.3876445276\", \"time\": \"2015-08-02 17:50:00\" }, { \"temp\": \"98.1708373818\", \"time\": \"2015-08-02 18:00:00\" }, { \"temp\": \"99.9206112204\", \"time\": \"2015-08-02 18:10:00\" }, { \"temp\": \"97.067494908\", \"time\": \"2015-08-02 18:20:00\" }, { \"temp\": \"98.2549345718\", \"time\": \"2015-08-02 18:30:00\" }, { \"temp\": \"97.1743743291\", \"time\": \"2015-08-02 18:40:00\" }, { \"temp\": \"99.3349926751\", \"time\": \"2015-08-02 18:50:00\" }, { \"temp\": \"98.2194835793\", \"time\": \"2015-08-02 19:00:00\" }, { \"temp\": \"99.8427520491\", \"time\": \"2015-08-02 19:10:00\" }, { \"temp\": \"97.6475479338\", \"time\": \"2015-08-02 19:20:00\" }, { \"temp\": \"98.0223554248\", \"time\": \"2015-08-02 19:30:00\" }, { \"temp\": \"98.6195212366\", \"time\": \"2015-08-02 19:40:00\" }, { \"temp\": \"98.4025216666\", \"time\": \"2015-08-02 19:50:00\" }, { \"temp\": \"98.0496721612\", \"time\": \"2015-08-02 20:00:00\" }, { \"temp\": \"98.9313691091\", \"time\": \"2015-08-02 20:10:00\" }, { \"temp\": \"98.0607708875\", \"time\": \"2015-08-02 20:20:00\" }, { \"temp\": \"97.9126558484\", \"time\": \"2015-08-02 20:30:00\" }, { \"temp\": \"97.4369727793\", \"time\": \"2015-08-02 20:40:00\" }, { \"temp\": \"99.7356628871\", \"time\": \"2015-08-02 20:50:00\" }, { \"temp\": \"98.8375237993\", \"time\": \"2015-08-02 21:00:00\" }, { \"temp\": \"98.7192905651\", \"time\": \"2015-08-02 21:10:00\" }, { \"temp\": \"98.381670442\", \"time\": \"2015-08-02 21:20:00\" }, { \"temp\": \"98.8523080776\", \"time\": \"2015-08-02 21:30:00\" }, { \"temp\": \"99.7084231828\", \"time\": \"2015-08-02 21:40:00\" }, { \"temp\": \"97.7999238332\", \"time\": \"2015-08-02 21:50:00\" }, { \"temp\": \"97.9689144278\", \"time\": \"2015-08-02 22:00:00\" }, { \"temp\": \"99.4004475592\", \"time\": \"2015-08-02 22:10:00\" }, { \"temp\": \"99.7908013825\", \"time\": \"2015-08-02 22:20:00\" }, { \"temp\": \"97.9083068999\", \"time\": \"2015-08-02 22:30:00\" }, { \"temp\": \"99.3148173742\", \"time\": \"2015-08-02 22:40:00\" }, { \"temp\": \"99.9887498752\", \"time\": \"2015-08-02 22:50:00\" }, { \"temp\": \"97.776573202\", \"time\": \"2015-08-02 23:00:00\" }, { \"temp\": \"98.6360408194\", \"time\": \"2015-08-02 23:10:00\" }, { \"temp\": \"99.7946800177\", \"time\": \"2015-08-02 23:20:00\" }, { \"temp\": \"98.3482074773\", \"time\": \"2015-08-02 23:30:00\" }, { \"temp\": \"97.4398538452\", \"time\": \"2015-08-02 23:40:00\" }, { \"temp\": \"99.9628214345\", \"time\": \"2015-08-02 23:50:00\" } ] }");

        //requestQueue.add(getDataRequest());//comment this out, and uncomment above line for UI changes. (gets rid of wait)
    }

    @Override
    public void run() {onRequestData(DefaultRequestQueue.getDefaultQueue(getContext()));}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_info, menu);
    }

    private void setCurrentDateTimeValues(){
        final Calendar c = Calendar.getInstance();
        Date currTime = c.getTime();
        c.add(Calendar.HOUR,-1);
        Date hourBack = c.getTime();

        fromTime.setText(timeFormat.format(hourBack));
        toTime.setText(timeFormat.format(currTime));
        fromDate.setText(dateFormat.format(hourBack));
        toDate.setText(dateFormat.format(currTime));
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
        showDialog("Info", getResources().getString(R.string.history_info), null, null);
    }

    private StringRequest getDataRequest(){
        return new StringRequest(dataUrl,
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
        );
    }

    private void parseData(String data){
        //Log.d("Data","Data Received: "+data);
        readings = Reading.parseData(data);
        ProgressBar progBar = (ProgressBar) rootView.findViewById(R.id.history_loading);
        progBar.setVisibility(View.GONE);
        historyDataAdapter.notifyDataSetChanged();
    }

    public View.OnClickListener getDateOnClick(final TextView dateTextView, final boolean isFromDate) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dpf = new DatePickerFragment();
                dpf.setTextView(dateTextView, isFromDate);
                DialogFragment newFragment = dpf;
                newFragment.show(getFragmentManager(), "datePicker");
            }
        };
    }

    private View.OnClickListener getTimeOnClick(final TextView timeTextView, final boolean isFromTime) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment tpf = new TimePickerFragment();
                tpf.setTextView(timeTextView, isFromTime);
                DialogFragment newFragment = tpf;
                newFragment.show(getFragmentManager(), "timePicker");
            }
        };
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
            else itemView = new HistoryItemView(viewGroup.getContext(),false);

            Reading toView = readings.get(i);
            itemView.setTimeText(toView.getTimeOfDay());
            itemView.setBloodOxyView("" + toView.getBlooxOxygenForUI());
            itemView.setHeartView("" + toView.getHeartRateForUI());
            itemView.setTempView(""+toView.getTempForUI());

            return itemView;
        }
    }

    ///////////////////////
    // Time Picker Dialog
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        TextView toUpdate;
        boolean isFrom;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            Date toDisplay=null;
            try{
                toDisplay = timeFormat.parse(toTime.getText().toString());
                if(isFrom) toDisplay = timeFormat.parse(fromTime.getText().toString());
            }catch(ParseException e){}
            final Calendar c = Calendar.getInstance();
            c.setTime(toDisplay);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            try {
                Date thisTime = twntyFourHrFormat.parse(hourOfDay+":"+minute);
                Log.e("", "");
                toUpdate.setText(timeFormat.format(thisTime));
            }catch(ParseException e){e.printStackTrace();}
            validateTimeWindow(isFrom);
        }

        public void setTextView(TextView textView, boolean isOnLeft) {
            toUpdate = textView;
            isFrom = isOnLeft;
        }
    }

    ///////////////////////
    // Date Picker Dialog
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        TextView toUpdate;
        boolean isOnLeft;

        public void setTextView(TextView tv, boolean isLeft){
            toUpdate = tv;
            isOnLeft = isLeft;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            Date toDisplay=null;
            try{
                toDisplay = dateFormat.parse(toDate.getText().toString());
                if(isOnLeft) toDisplay = dateFormat.parse(fromDate.getText().toString());
            }catch(ParseException e){}
            final Calendar c = Calendar.getInstance();
            c.setTime(toDisplay);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String date = month+1+"/"+day+"/"+year;
            toUpdate.setText(date);
            validateTimeWindow(isOnLeft);
        }
    }

    private static void validateTimeWindow(boolean isFromSide){
        try{

            //validate dates
            Date thisDate,otherDate;
            if(isFromSide){
                thisDate = dateFormat.parse(fromDate.getText().toString());
                otherDate = dateFormat.parse(toDate.getText().toString());
                if(thisDate.getTime()>otherDate.getTime())toDate.setText(dateFormat.format(thisDate));
            }
            else {
                thisDate = dateFormat.parse(toDate.getText().toString());
                otherDate = dateFormat.parse(fromDate.getText().toString());
                if(thisDate.getTime()<otherDate.getTime())fromDate.setText(dateFormat.format(thisDate));
            }

            //validate times
            Date leftDate = dateFormat.parse(fromDate.getText().toString());
            Date rightDate = dateFormat.parse(toDate.getText().toString());
            Date otherTime;
            if(leftDate.getTime()>=rightDate.getTime()){
                Date thisTime;
                if(isFromSide){
                    thisTime = timeFormat.parse(fromTime.getText().toString());
                    otherTime = timeFormat.parse(toTime.getText().toString());
                    if(thisTime.getTime()>otherTime.getTime()){
                        toTime.setText(timeFormat.format(thisTime));
                    }
                }
                else{
                    thisTime = timeFormat.parse(toTime.getText().toString());
                    otherTime = timeFormat.parse(fromTime.getText().toString());
                    if(thisTime.getTime()<otherTime.getTime()){
                        fromTime.setText(timeFormat.format(thisTime));
                    }
                }
            }
        }catch (ParseException e){e.printStackTrace();}
    }
}
