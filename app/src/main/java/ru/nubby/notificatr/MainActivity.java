package ru.nubby.notificatr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private DefaultPreferences mDefaultPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainFragment fragmentPreferences = (MainFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragmentPreferences == null) {
            fragmentPreferences = MainFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragmentPreferences)
                    .commit();

            mContext = getApplicationContext();

        }

        mDefaultPreferences = new DefaultPreferences(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NotificationHelper.disableBootReceiver(mContext);
        NotificationHelper.cancelAlarmRTC();
        if (mDefaultPreferences.getIsOn()) {
            NotificationHelper.enableBootReceiver(mContext);
            NotificationHelper.scheduleRepeatingRTCNotification(mContext);
        }
    }
}
