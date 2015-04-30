/*
* This file is part of the Kernel Tuner.
*
* Copyright Predrag Čokulov <predragcokulov@gmail.com>
*
* Kernel Tuner is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Kernel Tuner is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Kernel Tuner. If not, see <http://www.gnu.org/licenses/>.
*/
package rs.pedjaapps.kerneltuner.ui;

import android.app.ActivityManager.*;
import android.app.AlertDialog;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.preference.Preference.*;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import rs.pedjaapps.kerneltuner.*;

public class PreferencesFragment extends PreferenceFragment
{

    private ListPreference bootPrefList;
    private EditTextPreference widgetPref;
    private ListPreference tempPrefList;
    private ListPreference widgetPrefList;
    private ListPreference unsupportedPrefList;
    private CheckBoxPreference mainCpuPref;
    private CheckBoxPreference mainTempPref;
    private CheckBoxPreference mainTogglesPref;
    private CheckBoxPreference mainButtonsPref;
    private EditTextPreference refreshPref;
    private ListPreference levelPrefList;
    private ListPreference formatPrefList;
    private ListPreference bufferPrefList;
    private ListPreference textsizePrefList;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //TODO ab
        //ActionBar ab = ((AbsActivity)getActivity()).getSupportActionBar();
        String settings = getArguments().getString("settings");
        if ("application".equals(settings))
        {
            addPreferencesFromResource(R.xml.settings_application);
            app();
            //ab.setSubtitle(getResources().getString(R.string.preferences_subtitle_genereal));
        }
        else if ("widget".equals(settings))
        {
            addPreferencesFromResource(R.xml.settings_widget);
            widget();
            //ab.setSubtitle(getResources().getString(R.string.preferences_subtitle_widget));
        }
        else if ("logcat".equals(settings))
        {
            addPreferencesFromResource(R.xml.settings_logcat);
            logcat();
            //ab.setSubtitle(getResources().getString(R.string.preferences_subtitle_logcat));
        }
        else if ("main".equals(settings))
        {
            addPreferencesFromResource(R.xml.settings_main);
            main();
            //ab.setSubtitle(getResources().getString(R.string.preferences_subtitle_main));
        }
    }


    private void main()
    {
        mainCpuPref = (CheckBoxPreference) findPreference("main_cpu");
        mainTempPref = (CheckBoxPreference) findPreference("main_temp");
        mainTogglesPref = (CheckBoxPreference) findPreference("main_toggles");
        mainButtonsPref = (CheckBoxPreference) findPreference("main_buttons");
        CheckBoxPreference mainStylePref = (CheckBoxPreference) findPreference("main_style");

        mainStylePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {

            public boolean onPreferenceChange(Preference preferenxe, Object newValue)
            {
                if (newValue.toString().equals("true"))
                {
                    mainCpuPref.setEnabled(false);
                    mainTogglesPref.setEnabled(false);
                    mainButtonsPref.setEnabled(false);
                    mainTempPref.setEnabled(false);
                }
                else
                {
                    mainCpuPref.setEnabled(true);
                    mainTogglesPref.setEnabled(true);
                    mainButtonsPref.setEnabled(true);
                    mainTempPref.setEnabled(true);
                }
                return true;
            }
        });
        if (mainStylePref.isChecked())
        {
            mainCpuPref.setEnabled(false);
            mainTogglesPref.setEnabled(false);
            mainButtonsPref.setEnabled(false);
            mainTempPref.setEnabled(false);
        }
        else
        {
            mainCpuPref.setEnabled(true);
            mainTogglesPref.setEnabled(true);
            mainButtonsPref.setEnabled(true);
            mainTempPref.setEnabled(true);
        }
        unsupportedPrefList = (ListPreference) findPreference("unsupported_items_display");
        unsupportedPrefList.setDefaultValue(unsupportedPrefList.getEntryValues()[0]);
        String unsupported = unsupportedPrefList.getValue();
        if (unsupported == null)
        {
            unsupportedPrefList.setValue((String) unsupportedPrefList.getEntryValues()[0]);
            unsupported = unsupportedPrefList.getValue();
        }
        unsupportedPrefList.setSummary(unsupportedPrefList.getEntries()[unsupportedPrefList.findIndexOfValue(unsupported)]);


        unsupportedPrefList.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                unsupportedPrefList.setSummary(unsupportedPrefList.getEntries()[unsupportedPrefList.findIndexOfValue(newValue.toString())]);
                return true;
            }
        });
    }

    private void widget()
    {
        widgetPrefList = (ListPreference) findPreference("widget_bg");
        widgetPrefList.setDefaultValue(widgetPrefList.getEntryValues()[0]);
        String widgetBg = widgetPrefList.getValue();
        if (widgetBg == null)
        {
            widgetPrefList.setValue((String) widgetPrefList.getEntryValues()[0]);
            widgetBg = widgetPrefList.getValue();
        }
        widgetPrefList.setSummary(widgetPrefList.getEntries()[widgetPrefList.findIndexOfValue(widgetBg)]);


        widgetPrefList.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                widgetPrefList.setSummary(widgetPrefList.getEntries()[widgetPrefList.findIndexOfValue(newValue.toString())]);
                return true;
            }
        });
        widgetPref = (EditTextPreference) findPreference("widget_time");
        widgetPref.setDefaultValue(widgetPref.getText());
        String widget = widgetPref.getText();
        if (widget == null)
        {
            widgetPref.setText((String) widgetPref.getText());
            widget = widgetPref.getText();
        }
        widgetPref.setSummary(widget + "min");


        widgetPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                widgetPref.setSummary(newValue.toString() + "min");
                return true;
            }
        });

    }

    private void logcat()
    {
        levelPrefList = (ListPreference) findPreference("level");
        levelPrefList.setDefaultValue(levelPrefList.getEntryValues()[0]);
        String level = levelPrefList.getValue();
        if (level == null)
        {
            levelPrefList.setValue((String) levelPrefList.getEntryValues()[0]);
            level = levelPrefList.getValue();
        }
        levelPrefList.setSummary(levelPrefList.getEntries()[levelPrefList.findIndexOfValue(level)]);


        levelPrefList.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                levelPrefList.setSummary(levelPrefList.getEntries()[levelPrefList.findIndexOfValue(newValue.toString())]);
                return true;
            }
        });

        bufferPrefList = (ListPreference) findPreference("buffer");
        bufferPrefList.setDefaultValue(bufferPrefList.getEntryValues()[0]);
        String buffer = bufferPrefList.getValue();
        if (buffer == null)
        {
            bufferPrefList.setValue((String) bufferPrefList.getEntryValues()[0]);
            buffer = bufferPrefList.getValue();
        }
        bufferPrefList.setSummary(bufferPrefList.getEntries()[bufferPrefList.findIndexOfValue(buffer)]);


        bufferPrefList.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                bufferPrefList.setSummary(bufferPrefList.getEntries()[bufferPrefList.findIndexOfValue(newValue.toString())]);
                return true;
            }
        });

        formatPrefList = (ListPreference) findPreference("format");
        formatPrefList.setDefaultValue(formatPrefList.getEntryValues()[0]);
        String format = formatPrefList.getValue();
        if (format == null)
        {
            formatPrefList.setValue((String) formatPrefList.getEntryValues()[0]);
            format = formatPrefList.getValue();
        }
        formatPrefList.setSummary(formatPrefList.getEntries()[formatPrefList.findIndexOfValue(format)]);


        formatPrefList.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                formatPrefList.setSummary(formatPrefList.getEntries()[formatPrefList.findIndexOfValue(newValue.toString())]);
                return true;
            }
        });

        textsizePrefList = (ListPreference) findPreference("textsize");
        textsizePrefList.setDefaultValue(textsizePrefList.getEntryValues()[0]);
        String textsize = textsizePrefList.getValue();
        if (textsize == null)
        {
            textsizePrefList.setValue((String) textsizePrefList.getEntryValues()[0]);
            textsize = textsizePrefList.getValue();
        }
        textsizePrefList.setSummary(textsizePrefList.getEntries()[textsizePrefList.findIndexOfValue(textsize)]);


        textsizePrefList.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                textsizePrefList.setSummary(textsizePrefList.getEntries()[textsizePrefList.findIndexOfValue(newValue.toString())]);
                return true;
            }
        });
    }

    private void app()
    {
        tempPrefList = (ListPreference) findPreference("temp_unit");
        tempPrefList.setDefaultValue(tempPrefList.getEntryValues()[0]);
        String temp = tempPrefList.getValue();
        if (temp == null)
        {
            tempPrefList.setValue((String) tempPrefList.getEntryValues()[0]);
            temp = tempPrefList.getValue();
        }
        tempPrefList.setSummary(tempPrefList.getEntries()[tempPrefList.findIndexOfValue(temp)]);
        tempPrefList.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                tempPrefList.setSummary(tempPrefList.getEntries()[tempPrefList.findIndexOfValue(newValue.toString())]);
                return true;
            }
        });

        bootPrefList = (ListPreference) findPreference("boot");
        bootPrefList.setDefaultValue(bootPrefList.getEntryValues()[0]);
        String boot = bootPrefList.getValue();
        if (boot == null)
        {
            bootPrefList.setValue((String) bootPrefList.getEntryValues()[0]);
            boot = bootPrefList.getValue();
        }
        bootPrefList.setSummary(bootPrefList.getEntries()[bootPrefList.findIndexOfValue(boot)]);
        bootPrefList.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                bootPrefList.setSummary(bootPrefList.getEntries()[bootPrefList.findIndexOfValue(newValue.toString())]);
                return true;
            }
        });


        refreshPref = (EditTextPreference) findPreference("cpu_refresh_interval");
        refreshPref.setDefaultValue(refreshPref.getText());
        String refresh = refreshPref.getText();
        if (refresh == null)
        {
            refreshPref.setText(refreshPref.getText().toString());
            refresh = refreshPref.getText();
        }
        refreshPref.setSummary(refresh + "ms");
        refreshPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                refreshPref.setSummary(newValue.toString() + "ms");
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this.getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

}
