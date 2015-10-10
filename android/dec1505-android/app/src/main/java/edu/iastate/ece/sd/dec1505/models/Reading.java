package edu.iastate.ece.sd.dec1505.models;

/**
 * Created by arlenb on 10/10/15.
 */
public class Reading {

    int blooxOx;
    int heart;
    double temp;
    String time;

    public Reading(ReadingType type, String timeVal) {
        time = timeVal;
    }

    public String getTime(){
        return time;
    }
    public int getBlooxOxygen(){
        return blooxOx;
    }
    public int getHeartRate(){
        return heart;
    }
    public double getTemp(){
        return temp;
    }

    public void setHeartRate(int heartRateVal) {
        heart = heartRateVal;
    }
    public void setBloodOxygen(int bloodOxygen) {
        blooxOx = bloodOxygen;
    }
    public void setBodyTemp(double bodyTemp){
        temp = bodyTemp;
    }
}
