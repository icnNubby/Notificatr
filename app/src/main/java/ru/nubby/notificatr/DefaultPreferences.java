package ru.nubby.notificatr;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;


public class DefaultPreferences {

    private SharedPreferences mSharedPreferences;

    public DefaultPreferences(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getStartTime() {
        return mSharedPreferences.getString("time_start", "10:00");
    }
    public String getFinishTime() {
        return mSharedPreferences.getString("time_end", "10:00");
    }

    public String getText() {
        return mSharedPreferences.getString("display_text","Сделай!");
    }


    public boolean getIsOn() {
        return mSharedPreferences.getBoolean("is_on", false);
    }

}
