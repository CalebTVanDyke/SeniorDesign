package edu.iastate.ece.sd.dec1505.tools;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class DefaultRequestQueue {

    private static RequestQueue defaultQueue;

    public synchronized static RequestQueue getDefaultQueue(Context context) {
        return defaultQueue==null ? defaultQueue=Volley.newRequestQueue(context) : defaultQueue;
    }

}
