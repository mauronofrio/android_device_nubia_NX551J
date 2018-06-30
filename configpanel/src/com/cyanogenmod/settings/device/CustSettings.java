package com.cyanogenmod.settings.device;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.os.SystemProperties;
import android.text.TextUtils;

import com.cyanogenmod.settings.device.utils.Constants;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.cyanogenmod.internal.util.FileUtils;

/**
 * Created by chenzc on 17-7-26.
 */

public class CustSettings extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.cust_settings);
    }

    @Override
    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);
        // Initialize node preferences

        for (String pref : Constants.sBooleanNodePreferenceMap.keySet()) {
            SwitchPreference b = (SwitchPreference) findPreference(pref);
            if (b == null) continue;
            b.setOnPreferenceChangeListener(this);
            String node = Constants.sBooleanNodePreferenceMap.get(pref);
            if (new File(node).exists()) {
                String curNodeValue = FileUtils.readOneLine(node);
                b.setChecked(curNodeValue.equals("1"));
            } else {
                b.setEnabled(false);
            }
        }
        for (String pref : Constants.sBooleanPropPreferenceMap.keySet()) {
            SwitchPreference b = (SwitchPreference) findPreference(pref);
            if (b == null) continue;
            b.setOnPreferenceChangeListener(this);
            String prop = Constants.sBooleanPropPreferenceMap.get(pref);

            String value = Settings.System.getString(getContentResolver(),
                    pref);
            value = value!=null?value:"1";
            b.setChecked(value.equals("0"));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String node = Constants.sBooleanNodePreferenceMap.get(preference.getKey());
        if (!TextUtils.isEmpty(node)) {
            Boolean value = (Boolean) newValue;
            FileUtils.writeLine(node, value ? "1" : "0");
            return true;
        }
        String prop = Constants.sBooleanPropPreferenceMap.get(preference.getKey());
        if (!TextUtils.isEmpty(prop)) {
            //String value = SystemProperties.get(prop,"0");
            Boolean bool = (Boolean) newValue;
            String value = bool?"0":"1";
            Settings.System.putString(getContentResolver(), preference.getKey(), value);
            SystemProperties.set(prop,value);
            return true;
        }
        return false;
    }
}
