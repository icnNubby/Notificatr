package ru.nubby.notificatr.app.ui.preferences;

import android.os.Bundle;

import ru.nubby.notificatr.R;
import ru.nubby.notificatr.app.prefsutils.PreferencesFragment;

public class MainFragment extends PreferencesFragment {

    public static MainFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }
}
