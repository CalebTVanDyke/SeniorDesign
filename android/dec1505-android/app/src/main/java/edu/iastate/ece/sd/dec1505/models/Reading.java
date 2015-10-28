package edu.iastate.ece.sd.dec1505.models;

import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.iastate.ece.sd.dec1505.fragments.HistoryFragment;

/**
 * A reading consists of a timestamp and 3 data types recorded at that exact time.
 * Created by arlenb on 9/10/15.
 */
public class Reading {

    int blooxOx;
    int heart;
    double temp;
    String timeStr;
    Date time;

    public Reading(String timeString) {
        timeStr = timeString;
        time = parseTime(timeString);
    }

    public String getTimeStr(){return timeStr;}
    public int getBlooxOxygen(){return blooxOx;}
    public int getHeartRate(){
        return heart;
    }
    public double getTemp(){return temp;}

    public void setHeartRate(int heartRateVal) {heart = heartRateVal;}
    public void setBloodOxygen(int bloodOxygen) {
        blooxOx = bloodOxygen;
    }
    public void setBodyTemp(double bodyTemp){temp = bodyTemp;}

    private Date parseTime(String timeString) {
        SimpleDateFormat dateInFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date toReturn = null;
        try{
            toReturn = dateInFormat.parse(timeString);
        }catch (ParseException e){Log.e("Reading","Failed to parse time. "+e.toString());}
        return toReturn;
    }

    public String getTempForUI(){
        double toPrint = temp;
        DecimalFormat df = new DecimalFormat("#.00");

        //TODO
        //if preference is Celcius, toPrint = (toPrint-32)*(5/9);

        return df.format(toPrint);
    }

    public String getBlooxOxygenForUI(){
        return padBeginningOfString("" + blooxOx, 3);
    }

    public String getHeartRateForUI(){
        return padBeginningOfString(heart+"",3);
    }

    private String padBeginningOfString(String string, int desiredLength) {
        while(string.length()<desiredLength)string="\u0020"+"\u0020"+string;
        return string;
    }

    /**
     * Assembles ArrayList of Reading objects using passed in JSON data string.
     * @param dataStr JSON data to parse and assemble objects off of.
     * @return The list of Reading objects.
     */
    public static ArrayList<Reading> parseData(String dataStr) {
        ArrayList<Reading> readingList = new ArrayList<>();
        try {
            JSONObject dataObj = new JSONObject(dataStr);

            //Blood Oxygen
            JSONArray jArr = dataObj.getJSONArray("blood_oxygen");
            int bloodOx = -1;
            String time = "";
            Reading tmpReading;
            for(int i=0; i<jArr.length(); i++){
                JSONObject readJSONObj = jArr.getJSONObject(i);
                bloodOx = readJSONObj.getInt("blood_oxygen");
                time = readJSONObj.getString("time");
                //Log.d("History", "At " + time + ", SPO2: " + bloodOx);
                tmpReading = new Reading(time);
                tmpReading.setBloodOxygen(bloodOx);
                readingList.add(tmpReading);
            }

            //Heart Rate
            jArr = dataObj.getJSONArray("heart_rate");
            int heartRate = -1;
            for(int i=0; i<jArr.length(); i++){
                JSONObject readJSONObj = jArr.getJSONObject(i);
                heartRate = readJSONObj.getInt("heart_rate");
                time = readJSONObj.getString("time");
                if(!readingList.get(i).getTimeStr().equals(time))Log.e("History","Time Mismatch");
                readingList.get(i).setHeartRate(heartRate);
            }

            //Body Temp
            jArr = dataObj.getJSONArray("temp");
            double temp = -1;
            for(int i=0; i<jArr.length(); i++){
                JSONObject readJSONObj = jArr.getJSONObject(i);
                temp = readJSONObj.getDouble("temp");
                time = readJSONObj.getString("time");
                if(!readingList.get(i).getTimeStr().equals(time))Log.e("History","Time Mismatch");
                readingList.get(i).setBodyTemp(temp);
            }

        } catch (Exception e) {e.printStackTrace();}
        return readingList;
    }

    public Date getDateObject() {return time;}
}
