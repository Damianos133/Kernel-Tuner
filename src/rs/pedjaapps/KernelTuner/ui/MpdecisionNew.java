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
package rs.pedjaapps.KernelTuner.ui;

import com.google.android.gms.ads.*;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;

import rs.pedjaapps.KernelTuner.Constants;
import rs.pedjaapps.KernelTuner.R;
import rs.pedjaapps.KernelTuner.entry.FrequencyCollection;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;


public class MpdecisionNew extends Activity
{

	
	private List<String>                       freqs     = FrequencyCollection.getInstance().getFrequencyValues();
	private List<String>                       freqNames       = FrequencyCollection.getInstance().getFrequencyStrings();
	
	
	private String mpscroff;
	private SharedPreferences preferences;
	private String delay;
	private String pause;
	private String[] thr = new String[8];
	private String[] tim = new String[8];

	private String idle;
	private String scroff;
	private String scroff_single;
	
	private int idleNew;
	private int scroffNew;
	private String scroff_singleNew;
	
	private Switch mp_switch;
	private Spinner idleSpinner;
	private Spinner scroffSpinner;

	private String onoff;
	
	EditText delayTxt;
	EditText pauseTxt;
	EditText[] thrTxt = new EditText[12];
	int[] thrIds;
	EditText maxCpus;
	EditText minCpus;
	String max_cpus;
	String min_cpus;

	private ProgressDialog pd = null;

	private class apply extends AsyncTask<String, Void, Object>
	{


		@Override
		protected Object doInBackground(String... args)
		{

			
			for(int i = 0; i < 8; i++){
				CommandCapture command = new CommandCapture(0,
				"chmod 777 /sys/kernel/msm_mpdecision/conf/nwns_threshold_"+i,
				"chmod 777 /sys/kernel/msm_mpdecision/conf/twts_threshold_"+i);
				try{
					RootTools.getShell(true).add(command).waitForFinish();
				}
				catch(Exception e){
	
				}
			}
				CommandCapture command = new CommandCapture(0,
		            "chmod 777 /sys/kernel/msm_mpdecision/conf/scroff_single_core",
					"chmod 777 /sys/kernel/msm_mpdecision/conf/scroff_freq",
					"chmod 777 /sys/kernel/msm_mpdecision/conf/idle_freq",
					"chmod 777 /sys/kernel/msm_mpdecision/conf/dealy",
					"chmod 777 /sys/kernel/msm_mpdecision/conf/pause",
					"echo " + thrTxt[0].getText().toString() + " > /sys/kernel/msm_mpdecision/conf/nwns_threshold_"+0,
					"echo " + thrTxt[2].getText().toString() + " > /sys/kernel/msm_mpdecision/conf/nwns_threshold_"+2,
					"echo " + thrTxt[4].getText().toString() + " > /sys/kernel/msm_mpdecision/conf/nwns_threshold_"+3,
					"echo " + thrTxt[6].getText().toString() + " > /sys/kernel/msm_mpdecision/conf/nwns_threshold_"+4,
					"echo " + thrTxt[8].getText().toString() + " > /sys/kernel/msm_mpdecision/conf/nwns_threshold_"+5,
					"echo " + thrTxt[10].getText().toString() + " > /sys/kernel/msm_mpdecision/conf/nwns_threshold_"+7,
					"echo " + thrTxt[1].getText().toString() + " > /sys/kernel/msm_mpdecision/conf/twts_threshold_"+0,
					"echo " + thrTxt[3].getText().toString() + " > /sys/kernel/msm_mpdecision/conf/twts_threshold_"+2,
					"echo " + thrTxt[5].getText().toString() + " > /sys/kernel/msm_mpdecision/conf/twts_threshold_"+3,
					"echo " + thrTxt[7].getText().toString() + " > /sys/kernel/msm_mpdecision/conf/twts_threshold_"+4,
					"echo " + thrTxt[9].getText().toString() + " > /sys/kernel/msm_mpdecision/conf/twts_threshold_"+5,
					"echo " + thrTxt[11].getText().toString() + " > /sys/kernel/msm_mpdecision/conf/twts_threshold_"+7,
					"echo " + maxCpus.getText().toString() + " > /sys/kernel/msm_mpdecision/conf/max_cpus",
					"echo " + minCpus.getText().toString() + " > /sys/kernel/msm_mpdecision/conf/min_cpus",
					"echo " + mpscroff + " > /sys/kernel/msm_mpdecision/conf/scroff_single_core",
					"echo " + onoff + " > /sys/kernel/msm_mpdecision/conf/scroff_profile",
					"echo " + scroffNew + " > /sys/kernel/msm_mpdecision/conf/scroff_freq",
					"echo " + scroff_singleNew + " > /sys/kernel/msm_mpdecision/conf/scroff_single_core");
				try{
					RootTools.getShell(true).add(command).waitForFinish();
				}
				catch(Exception e){

				}	 
					
					
		            
		           

			return "";
		}

		@Override
		protected void onPostExecute(Object result)
		{
			preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("onoff", onoff);
			editor.putString("idle_freq", idleNew+"");
			editor.putString("scroff", scroffNew+"");
			editor.putString("scroff_single", scroff_singleNew);
			editor.putString("max_cpus", maxCpus.getText().toString());
			editor.putString("min_cpus", minCpus.getText().toString());

			editor.putString("thr0", thrTxt[0].getText().toString());
			editor.putString("thr2", thrTxt[1].getText().toString());
			editor.putString("thr3", thrTxt[2].getText().toString());
			editor.putString("thr4", thrTxt[3].getText().toString());
			editor.putString("thr5", thrTxt[4].getText().toString());
			editor.putString("thr7", thrTxt[5].getText().toString());
			editor.putString("tim0", thrTxt[0].getText().toString());
			editor.putString("tim2", thrTxt[1].getText().toString());
			editor.putString("tim3", thrTxt[2].getText().toString());
			editor.putString("tim4", thrTxt[3].getText().toString());
			editor.putString("tim5", thrTxt[4].getText().toString());
			editor.putString("tim7", thrTxt[5].getText().toString());
			editor.commit();
			MpdecisionNew.this.pd.dismiss();
			finish();

		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mpdecision_new);
		
		
		mp_switch = (Switch)findViewById(R.id.mp_switch);
		idleSpinner =(Spinner)findViewById(R.id.bg);
		scroffSpinner =(Spinner)findViewById(R.id.spinner2);
		
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		boolean ads = preferences.getBoolean("ads", true);
        final AdView adView = (AdView)findViewById(R.id.adView);
        com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder()
                .addTestDevice(com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5750ECFACEA6FCE685DE7A97D8C59A5F")
                .addTestDevice("05FBCDCAC44495595ACE7DC1AEC5C208")
                .addTestDevice("40AA974617D79A7A6C155B1A2F57D595")
                .build();
        if(ads)adView.loadAd(adRequest);
        adView.setAdListener(new AdListener()
        {
            @Override
            public void onAdLoaded()
            {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }
        });
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		maxCpus = (EditText)findViewById(R.id.max_cpus);
		minCpus = (EditText)findViewById(R.id.min_cpus);
		thrIds = new int[]{R.id.one_cpu_hotplug, 
				R.id.one_cpu_hotplug_time,
				R.id.two_cpus_hotplug,
				R.id.two_cpu_hotplug_time,
				R.id.two_cpus_unplug,
				R.id.two_cpu_unplug_time,
				R.id.three_cpus_hotplug,
				R.id.three_cpu_hotplug_time,
				R.id.three_cpus_unplug,
				R.id.three_cpu_unplug_time,
				R.id.four_cpus_unplug,
				R.id.four_cpu_unplug_time};
		for(int i = 0; i < thrIds.length; i++){
			thrTxt[i] = (EditText)findViewById(thrIds[i]);
		}
		readMpdec();
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}


	private final void setCheckBoxes()
	{


		EditText del=(EditText)findViewById(R.id.ed1);
		del.setText(delay.trim());

		EditText paus=(EditText)findViewById(R.id.ed2);
		paus.setText(pause.trim());

		thrTxt[0].setText(thr[0]);
		thrTxt[1].setText(tim[0]);
		thrTxt[2].setText(thr[2]);
		thrTxt[3].setText(tim[2]);
		thrTxt[4].setText(thr[3]);
		thrTxt[5].setText(tim[3]);
		thrTxt[6].setText(thr[4]);
		thrTxt[7].setText(tim[4]);
		thrTxt[8].setText(thr[5]);
		thrTxt[9].setText(tim[5]);
		thrTxt[10].setText(thr[7]);
		thrTxt[11].setText(tim[7]);
		maxCpus.setText(max_cpus);
		minCpus.setText(min_cpus);
		if(scroff_single.equals("1")){
			mp_switch.setChecked(true);
		}
		else if(scroff_single.equals("0")){
			mp_switch.setChecked(false);
		}
		else{
			mp_switch.setEnabled(false);
		}
		mp_switch.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					scroff_singleNew = "1";
				}
				else{
					scroff_singleNew = "0";
				}
				
			}
			
		});
		
		ArrayAdapter<String> freqsArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, freqNames);
		freqsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		scroffSpinner.setAdapter(freqsArrayAdapter);

		scroffSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
				{
					scroffNew = Integer.parseInt(freqs.get(pos))+1;

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent)
				{
					//do nothing
				}
			});

		try{
		int scroffPosition = freqsArrayAdapter.getPosition(freqNames.get(freqs.indexOf(scroff)));
		scroffSpinner.setSelection(scroffPosition);
		}
		catch(Exception e){
		}
		idleSpinner.setAdapter(freqsArrayAdapter);

		idleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
				{
					idleNew = Integer.parseInt(freqs.get(pos))+1;

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent)
				{
					//do nothing
				}
			});

		try{
		int idlePosition = freqsArrayAdapter.getPosition(freqNames.get(freqs.indexOf(idle)));
		idleSpinner.setSelection(idlePosition);
		}
		catch(Exception e){
		}
		
	
	}

	private final void readMpdec()
	{
		try
		{
			delay = FileUtils.readFileToString(new File(Constants.MPDEC_DELAY));
		}
		catch (Exception e)
		{
			delay = "err";
			EditText ed=(EditText)findViewById(R.id.ed1);
			ed.setEnabled(false);
		}


		try
		{
			pause = FileUtils.readFileToString(new File(Constants.MPDEC_PAUSE));
		}
		catch (Exception e)
		{
			pause = "err";
			EditText ed=(EditText)findViewById(R.id.ed2);
			ed.setEnabled(false);

		}

		for(int i = 0; i < 8; i++){
			try
			{
				thr[i] = FileUtils.readFileToString(
						new File("/sys/kernel/msm_mpdecision/conf/nwns_threshold_"+i));
			}
			catch (Exception e)
			{
				thr[i] = "err";
			}
		}
		for(int i = 0; i < 8; i++){
			try
			{
				tim[i] = FileUtils.readFileToString(
						new File("/sys/kernel/msm_mpdecision/conf/twts_threshold_"+i));
			}
			catch (Exception e)
			{
				tim[i] = "err";
			}
		}

	    
		try
		{
			idle = FileUtils.readFileToString(new File(Constants.MPDEC_IDLE_FREQ)).trim();
		}
		catch (Exception e)
		{
			idle = "err";
			idleSpinner.setEnabled(false);
		}
		
		try
		{
			scroff = FileUtils.readFileToString(new File(Constants.MPDEC_SCROFF_FREQ)).trim();
		}
		catch (Exception e)
		{
			scroff= "err";
			scroffSpinner.setEnabled(false);
		}
		
		try
		{
			scroff_single = FileUtils.readFileToString(new File(Constants.MPDEC_SCROFF_SINGLE)).trim();	
		
		}
		catch (Exception e)
		{
			scroff_single = "err";
			mp_switch.setEnabled(false);
		
		}
		try
		{
			max_cpus = FileUtils.readFileToString(new File(Constants.MPDEC_MAX_CPUS));	
		}
		catch (Exception e)
		{
			max_cpus = "err";
			maxCpus.setEnabled(false);
		}
		try
		{
			min_cpus = FileUtils.readFileToString(new File(Constants.MPDEC_MIN_CPUS));	
		}
		catch (Exception e)
		{
			min_cpus = "err";
			minCpus.setEnabled(false);
		}
		setCheckBoxes();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.misc_tweaks_options_menu, menu);
		return super.onCreateOptionsMenu(menu);
}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	           
	            Intent intent = new Intent(this, KernelTuner.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        case R.id.apply:
	        	apply();
	        	return true;
	        case R.id.cancel:
	        	finish();
	        	return true;
	        
	            
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	private final void apply(){
		MpdecisionNew.this.pd = ProgressDialog.show(MpdecisionNew.this, null, getResources().getString(R.string.applying_settings), true, true);
		new apply().execute();
	}

}
