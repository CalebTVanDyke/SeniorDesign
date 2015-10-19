package edu.iastate.ece.sd.dec1505.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import edu.iastate.ece.sd.dec1505.R;

/**
 * Created by Arlen on 15/10/18.
 */
public class Prefs {

    public static final String loggedIn = "is the user logged in?";
    private Prefs(){

    }

    public static void put(Context context, String key, boolean value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(key,value).apply();
    }
}
