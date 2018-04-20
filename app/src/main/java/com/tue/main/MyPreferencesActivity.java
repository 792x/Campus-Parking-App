package com.tue.main;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.mert.testproj2.R;

import java.util.Locale;


/**
 * Created by Administrator on 3/12/2016.
 */

public class MyPreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);


       SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        boolean subscriptionOwner = sharedPrefs.getBoolean("subscriptionOwner", false);
//        boolean bAppUpdates = sharedPrefs.getBoolean("applicationUpdates",false);
//        boolean proximityNotification = sharedPrefs.getBoolean("proximityNotification", true);
//        String theme = sharedPrefs.getString("theme", "Light");
        String language = sharedPrefs.getString("language", "English");
//
        if(language.equals("English")){
            Log.v(String.valueOf(this), "starting with english");
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources()
                    .updateConfiguration(
                            config,
                            getBaseContext().getResources()
                                    .getDisplayMetrics());
        }
        else if(language.equals("Dutch")){
            Log.v(String.valueOf(this), "starting with dutch");
            Locale locale = new Locale("nl");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources()
                    .updateConfiguration(
                            config,
                            getBaseContext().getResources()
                                    .getDisplayMetrics());
        }


        checkValues();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("language")) {
            if (sharedPreferences.getString("language", "English").equals("English")) {
                Log.v(String.valueOf(this), "sharedPreferences is set to english");


                Locale locale = new Locale("en");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources()
                        .updateConfiguration(
                                config,
                                getBaseContext().getResources()
                                        .getDisplayMetrics());
            }
            else if (sharedPreferences.getString("language", "English").equals("Dutch")) {
                Log.v(String.valueOf(this), "sharedPreferences is set to dutch");
                Locale locale = new Locale("nl");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources()
                        .updateConfiguration(
                                config,
                                getBaseContext().getResources()
                                        .getDisplayMetrics());
            }
        }


        if (key.equals("theme")) {
        }
    }



    public static class MyPreferenceFragment extends PreferenceFragment    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

    }






    private void checkValues()
    {

        //hier haal je de instellingen op uit SharedPreferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean subscriptionOwner = sharedPrefs.getBoolean("subscriptionOwner", false);
        boolean bAppUpdates = sharedPrefs.getBoolean("applicationUpdates",false);
        boolean proximityNotification = sharedPrefs.getBoolean("proximityNotification", true);
        String theme = sharedPrefs.getString("theme", "Light");
        String language = sharedPrefs.getString("language","English");
        //logging
        String msg = "Cur Values: ";
        msg += "\n subscriptionOwner = " + subscriptionOwner;
        msg += "\n bAppUpdates = " + bAppUpdates;
        msg += "\n proximityNotification = " + proximityNotification;
        msg += "\n theme = " + theme;
        msg += "\n language = " + language;

        Log.v(String.valueOf(this), msg);
    }

}