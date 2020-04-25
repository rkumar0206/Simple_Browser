package com.example.simple_browser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import java.util.Set;

public class Setting_fragment extends PreferenceFragmentCompat {


    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    int brightness;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {


        brightness = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);

        addPreferencesFromResource(R.xml.setting_preference);

        if (getPreferenceScreen().getSharedPreferences().getString(getString(R.string.pref_themes_key), "").equals("Dark")) {

            findPreference(getString(R.string.pref_dark_brightness_key)).setVisible(true);
        } else {
            findPreference(getString(R.string.pref_dark_brightness_key)).setVisible(false);

        }


        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                if (key.equals(getString(R.string.pref_choose_engine_key))) {

                    Preference choose_engine_pref = findPreference(key);
                    choose_engine_pref.setSummary(sharedPreferences.getString(key, ""));

                }

                if (key.equals(getString(R.string.pref_home_page_key))) {

                    String summary = sharedPreferences.getString(key, "").trim();

                    if (!summary.isEmpty()) {
                        if (summary.startsWith("https://") || summary.startsWith("http://")) {

                            findPreference(key).setSummary(summary);
                        } else {

                            findPreference(key).setSummary("https://www." + summary);
                        }


                    } else if (summary.trim().isEmpty()) {

                        findPreference(key).setSummary("Home");

                    }

                }

                if (key.equals(getString(R.string.pref_themes_key))) {

                    findPreference(key).setSummary(sharedPreferences.getString(key, ""));

                    SeekBarPreference seekbar = (SeekBarPreference) findPreference(getString(R.string.pref_dark_brightness_key));


                    if (sharedPreferences.getString(key, "").equals("Dark")) {

                        //bright = 0.3f;

                        findPreference(getString(R.string.pref_dark_brightness_key)).setVisible(true);
                        //findPreference(getString(R.string.pref_dark_brightness_key)).setEnabled(true);
                        seekbar.setValue(21);

                        changescreenBrigtness(seekbar);


                    } else {

                        findPreference(getString(R.string.pref_dark_brightness_key)).setVisible(false);
                        boolean canWrite = Settings.System.canWrite(getActivity());
                        if (canWrite) {

                            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (brightness));

                        } else {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            getActivity().startActivity(intent);

                        }


                        // int brightness = Settings.System.getInt(Objects.requireNonNull(getActivity()).getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
                        //bright = (((brightness * 255) / 100)) / 100;
                        // bright=0.6f;
                        //findPreference(getString(R.string.pref_dark_brightness_key)).setEnabled(false);

                        //seekbar.setValue(47);
                    }


                    //to change the app brigtness
                    //WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
                    //layoutParams.screenBrightness = bright;
                    //getActivity().getWindow().setAttributes(layoutParams);


                }

                /*if (key.equals(getString(R.string.pref_clear_browsing_data_key))) {

                    Set<String> selections = sharedPreferences.getStringSet(getString(R.string.pref_clear_browsing_data_key), null);

                    if (selections != null) {
                        findPreference(key).setSummary(selections.toString());
                    }



                }

                 */

                if (key.equals(getString(R.string.pref_select_what_to_delete_after_app_is_closed_key))) {


                    Set<String> select = sharedPreferences.getStringSet(getString(R.string.pref_select_what_to_delete_after_app_is_closed_key), null);

                    if (select != null) {
                        findPreference(key).setSummary(select.toString());
                    }

                }


                if (key.equals(getString(R.string.pref_dark_brightness_key))) {


                    SeekBarPreference seekbar = (SeekBarPreference) findPreference(getString(R.string.pref_dark_brightness_key));

                    changescreenBrigtness(seekbar);
                    //WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
                    //layoutParams.screenBrightness = (float) seekbar.getValue() / 100;
                    //getActivity().getWindow().setAttributes(layoutParams);


                    //To change the system brightness


                }


            }


        };

    }


    public void changescreenBrigtness(SeekBarPreference seekbar) {


        boolean canWrite = Settings.System.canWrite(getActivity());
        if (canWrite) {

            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (seekbar.getValue() * 255) / 100);

        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            getActivity().startActivity(intent);

        }


    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        Preference choose_engine_pref = findPreference(getString(R.string.pref_choose_engine_key));
        choose_engine_pref.setSummary(getPreferenceScreen().getSharedPreferences().getString(getString(R.string.pref_choose_engine_key), ""));

        findPreference(getString(R.string.pref_home_page_key)).setSummary(getPreferenceScreen().getSharedPreferences().getString(getString(R.string.pref_home_page_key), ""));

        findPreference(getString(R.string.pref_themes_key)).setSummary(getPreferenceScreen().getSharedPreferences().getString(getString(R.string.pref_themes_key), ""));

        /*Set<String> selections_clear_browsing_data = getPreferenceScreen().getSharedPreferences().getStringSet(getString(R.string.pref_clear_browsing_data_key), null);

        if (selections_clear_browsing_data != null) {
            findPreference(getString(R.string.pref_clear_browsing_data_key)).setSummary(selections_clear_browsing_data.toString());

        }

         */

        Set<String> select_clear_data_after_app_is_closed = getPreferenceScreen().getSharedPreferences().getStringSet(getString(R.string.pref_select_what_to_delete_after_app_is_closed_key), null);

        if (select_clear_data_after_app_is_closed != null) {
            findPreference(getString(R.string.pref_select_what_to_delete_after_app_is_closed_key)).setSummary(select_clear_data_after_app_is_closed.toString());
        }


        if (findPreference(getString(R.string.pref_themes_key)).equals("Dark")) {
            SeekBarPreference seekBarPreference = (SeekBarPreference) findPreference(getString(R.string.pref_dark_brightness_key));
            seekBarPreference.setValue(getPreferenceScreen().getSharedPreferences().getInt(getString(R.string.pref_dark_brightness_key), 47));
            changescreenBrigtness(seekBarPreference);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);


    }


}
