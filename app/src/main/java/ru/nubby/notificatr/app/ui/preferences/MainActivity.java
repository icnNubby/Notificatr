package ru.nubby.notificatr.app.ui.preferences;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import ru.nubby.notificatr.R;

public class MainActivity extends AppCompatActivity {

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
        }
    }
}
