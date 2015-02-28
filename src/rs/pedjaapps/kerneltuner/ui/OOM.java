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

import java.util.List;

import rs.pedjaapps.kerneltuner.R;
import rs.pedjaapps.kerneltuner.utility.IOHelper;
import rs.pedjaapps.kerneltuner.utility.Utility;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.CommandCapture;

public class OOM extends Activity {

	private SeekBar foregroundSeek;
	private SeekBar visibleSeek;
	private SeekBar secondarySeek;
	private SeekBar hiddenSeek;
	private SeekBar contentSeek;
	private SeekBar emptySeek;
	private Button foregroundText;
	private Button visibleText;
	private Button secondaryText;
	private Button hiddenText;
	private Button contentText;
	private Button emptyText;

	private int foreground;
	private int visible;
	private int secondary;
	private int hidden;
	private int content;
	private int empty;
	private List<String> oom;

	private ProgressDialog pd;
	boolean isLight;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oom);
		oom = IOHelper.oom();
		

		foregroundSeek = (SeekBar) findViewById(R.id.foregroundSeek);
		foregroundText = (Button) findViewById(R.id.foregroundTest);
		visibleSeek = (SeekBar) findViewById(R.id.visibleSeek);
		visibleText = (Button) findViewById(R.id.visibleText);
		secondarySeek = (SeekBar) findViewById(R.id.secondarySeek);
		secondaryText = (Button) findViewById(R.id.secondaryText);
		hiddenSeek = (SeekBar) findViewById(R.id.hiddenSeek);
		hiddenText = (Button) findViewById(R.id.hiddenText);
		contentSeek = (SeekBar) findViewById(R.id.contentSeek);
		contentText = (Button) findViewById(R.id.contentText);
		emptySeek = (SeekBar) findViewById(R.id.emptySeek);
		emptyText = (Button) findViewById(R.id.emptyText);

		updateUI();

		Button  presets = (Button)findViewById(R.id.button1);
		presets.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				registerForContextMenu(arg0); 
			    openContextMenu(arg0);
			    unregisterForContextMenu(arg0);
				
			}
			
		});
		
		foregroundText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Dialog("Foreground Application", foreground+"", 0);
			}
			
		});
		visibleText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Dialog("Visible Application", foreground+"", 1);
			}
			
		});
		secondaryText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Dialog("Secondary Server", foreground+"", 2);
			}
			
		});
		hiddenText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Dialog("Hidden Application", foreground+"", 3);
			}
			
		});
		contentText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Dialog("Content Provider", foreground+"", 4);
			}
			
		});
		emptyText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Dialog("Empty Application", foreground+"", 5);
			}
			
		});
		
		
		foregroundSeek
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					public void onProgressChanged(SeekBar p1, int pos,
							boolean p3) {
						foregroundText.setText(pos + "MB");
					}
					public void onStartTrackingTouch(SeekBar p1) {

					}

					public void onStopTrackingTouch(SeekBar p1) {
						new setOOM().execute(Utility.mbToPages(foregroundSeek.getProgress()),
                                Utility.mbToPages(visibleSeek.getProgress()),
                                Utility.mbToPages(secondarySeek.getProgress()),
                                Utility.mbToPages(hiddenSeek.getProgress()),
                                Utility.mbToPages(contentSeek.getProgress()),
                                Utility.mbToPages(emptySeek.getProgress()));

					}

				});

		visibleSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar p1, int pos, boolean p3) {
				visibleText.setText(pos + "MB");

			}

			public void onStartTrackingTouch(SeekBar p1) {

			}

			public void onStopTrackingTouch(SeekBar p1) {
				new setOOM().execute(Utility.mbToPages(foregroundSeek.getProgress()),
                        Utility.mbToPages(visibleSeek.getProgress()),
                        Utility.mbToPages(secondarySeek.getProgress()),
                        Utility.mbToPages(hiddenSeek.getProgress()),
                        Utility.mbToPages(contentSeek.getProgress()),
                        Utility.mbToPages(emptySeek.getProgress()));
			}

		});

		secondarySeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar p1, int pos, boolean p3) {
				secondaryText.setText(pos + "MB");

			}

			public void onStartTrackingTouch(SeekBar p1) {

			}

			public void onStopTrackingTouch(SeekBar p1) {
				new setOOM().execute(Utility.mbToPages(foregroundSeek.getProgress()),
                        Utility.mbToPages(visibleSeek.getProgress()),
                        Utility.mbToPages(secondarySeek.getProgress()),
                        Utility.mbToPages(hiddenSeek.getProgress()),
                        Utility.mbToPages(contentSeek.getProgress()),
                        Utility.mbToPages(emptySeek.getProgress()));
			}

		});

		hiddenSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar p1, int pos, boolean p3) {
				hiddenText.setText(pos + "MB");

			}

			public void onStartTrackingTouch(SeekBar p1) {

			}

			public void onStopTrackingTouch(SeekBar p1) {
				new setOOM().execute(Utility.mbToPages(foregroundSeek.getProgress()),
                        Utility.mbToPages(visibleSeek.getProgress()),
                        Utility.mbToPages(secondarySeek.getProgress()),
                        Utility.mbToPages(hiddenSeek.getProgress()),
                        Utility.mbToPages(contentSeek.getProgress()),
                        Utility.mbToPages(emptySeek.getProgress()));
			}

		});

		contentSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar p1, int pos, boolean p3) {
				contentText.setText(pos + "MB");

			}

			public void onStartTrackingTouch(SeekBar p1) {

			}

			public void onStopTrackingTouch(SeekBar p1) {
				new setOOM().execute(Utility.mbToPages(foregroundSeek.getProgress()),
                        Utility.mbToPages(visibleSeek.getProgress()),
                        Utility.mbToPages(secondarySeek.getProgress()),
                        Utility.mbToPages(hiddenSeek.getProgress()),
                        Utility.mbToPages(contentSeek.getProgress()),
                        Utility.mbToPages(emptySeek.getProgress()));
			}

		});

		emptySeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar p1, int pos, boolean p3) {
				emptyText.setText(pos + "MB");

			}

			public void onStartTrackingTouch(SeekBar p1) {

			}

			public void onStopTrackingTouch(SeekBar p1) {
				new setOOM().execute(Utility.mbToPages(foregroundSeek.getProgress()),
                        Utility.mbToPages(visibleSeek.getProgress()),
                        Utility.mbToPages(secondarySeek.getProgress()),
                        Utility.mbToPages(hiddenSeek.getProgress()),
                        Utility.mbToPages(contentSeek.getProgress()),
                        Utility.mbToPages(emptySeek.getProgress()));
			}

		});

	}

	public final void updateUI(){
		try{
		foreground = Utility.parseInt(oom.get(0).trim(), 0) * 4 / 1024;
		visible = Utility.parseInt(oom.get(1).trim(), 0) * 4 / 1024;
		secondary = Utility.parseInt(oom.get(2).trim(), 0) * 4 / 1024;
		hidden = Utility.parseInt(oom.get(3).trim(), 0) * 4 / 1024;
		content = Utility.parseInt(oom.get(4).trim(), 0) * 4 / 1024;
		empty = Utility.parseInt(oom.get(5).trim(), 0) * 4 / 1024;
		}
		catch(Exception e){
			foreground=0;
			visible=0;
			secondary=0;
			hidden=0;
			content=0;
			empty=0;
		}
		foregroundSeek.setProgress(foreground);
		visibleSeek.setProgress(visible);
		secondarySeek.setProgress(secondary);
		hiddenSeek.setProgress(hidden);
		contentSeek.setProgress(content);
		emptySeek.setProgress(empty);

		foregroundText.setText(foreground + "MB");
		visibleText.setText(visible + "MB");
		secondaryText.setText(secondary + "MB");
		hiddenText.setText(hidden + "MB");
		contentText.setText(content + "MB");
		emptyText.setText(empty + "MB");
	}

	private class setOOM extends AsyncTask<String, Void, Object> {

		@Override
		protected Object doInBackground(String... args) {
		
			
			CommandCapture command = new CommandCapture(0,
	            "echo "
						+ args[0]
						+ ","
						+ args[1]
						+ ","
						+ args[2]
						+ ","
						+ args[3]
						+ ","
						+ args[4]
						+ ","
						+ args[5]
						+ " > /sys/module/lowmemorykiller/parameters/minfree\n");
			try{
				RootTools.getShell(true).add(command);
			}
			catch(Exception e){

			}
	           
	            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("oom", args[0]
						+ ","
						+ args[1]
						+ ","
						+ args[2]
						+ ","
						+ args[3]
						+ ","
						+ args[4]
						+ ","
						+ args[5]);
				editor.commit();
				oom = IOHelper.oom();
	      
			
			return "";
		}

		@Override
		protected void onPostExecute(Object result) {
			
			updateUI();
			
			pd.dismiss();
		}

		@Override
		protected void onPreExecute() {
			
			pd = new ProgressDialog(OOM.this);
			pd.setMessage(OOM.this.getResources().getString(R.string.oom_please_wait));
			pd.show();
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle(getResources().getString(R.string.oom_preset));
		menu.setHeaderIcon(R.drawable.swap);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.oom_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.very_light:
			new setOOM().execute("512",
                    "1024",
                    "1280",
                    "2048",
                    "3072",
                    "4096");
			return true;
		case R.id.light:
			new setOOM().execute("1024",
                    "2048",
                    "2560",
                    "4096",
                    "6144",
                    "8192");
			return true;
		case R.id.medium:
			new setOOM().execute("1024",
                    "2048",
                    "4096",
                    "8192",
                    "12288",
                    "16384");
			return true;

		case R.id.aggressive:
			new setOOM().execute("2048",
                    "4096",
                    "8192",
                    "16384",
                    "24576",
                    "32768");
			return true;

		case R.id.very_aggressive:
			new setOOM().execute("4096",
                    "8192",
                    "16384",
                    "16384",
                    "49152",
                    "65536");
			return true;
		case R.id._256_multitasking:
			new setOOM().execute("2048",
                    "3072",
                    "5632",
                    "6144",
                    "6656",
                    "7168");
			return true;
		case R.id._256_balanced:
			new setOOM().execute("2048",
                    "3072",
                    "6656",
                    "7168",
                    "7680",
                    "8192");
			return true;
		case R.id._256_aggressive:
			new setOOM().execute("2048",
                    "3072",
                    "7168",
                    "7680",
                    "8960",
                    "12800");
			return true;
			//512
		case R.id._512_multitasking:
			new setOOM().execute("2048",
                    "3584",
                    "10240",
                    "12800",
                    "15360",
                    "19200");
			return true;
		case R.id._512_balanced:
			new setOOM().execute("2048",
                    "3584",
                    "14080",
                    "17920",
                    "21760",
                    "25600");
			return true;
		case R.id._512_aggressive:
			new setOOM().execute("2048",
                    "3584",
                    "19200",
                    "23040",
                    "24320",
                    "32000");
			return true;
			
		case R.id._768_aggressive:
			new setOOM().execute("2048",
                    "4096",
                    "38400",
                    "42240",
                    "46080",
                    "51200");
			return true;
		case R.id._1000_aggressive:
			new setOOM().execute("2048",
                    "4096",
                    "51200",
                    "56320",
                    "61440",
                    "65536");
			return true;

		}
		return false;
	}
	
	private final void Dialog(String dialogTitle, String currentValue, final int option){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(dialogTitle);

		builder.setMessage(getResources().getString(R.string.gov_new_value));

		builder.setIcon(isLight ? R.drawable.edit_light : R.drawable.edit_dark);


		final EditText input = new EditText(this);
		input.setHint(currentValue);
		input.selectAll();
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setGravity(Gravity.CENTER_HORIZONTAL);

		builder.setPositiveButton(getResources().getString(R.string.apply), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					switch(option){
					case 0:
						new setOOM().execute(Utility.mbToPages(Utility.parseInt(input.getText().toString(), 0)),
                                Utility.mbToPages(visibleSeek.getProgress()),
                                Utility.mbToPages(secondarySeek.getProgress()),
                                Utility.mbToPages(hiddenSeek.getProgress()),
                                Utility.mbToPages(contentSeek.getProgress()),
                                Utility.mbToPages(emptySeek.getProgress()));
						break;
					case 1:
						new setOOM().execute(Utility.mbToPages(foregroundSeek.getProgress()),
                                Utility.mbToPages(Utility.parseInt(input.getText().toString(), 0)),
                                Utility.mbToPages(secondarySeek.getProgress()),
                                Utility.mbToPages(hiddenSeek.getProgress()),
                                Utility.mbToPages(contentSeek.getProgress()),
                                Utility.mbToPages(emptySeek.getProgress()));
						break;
					case 2:
						new setOOM().execute(Utility.mbToPages(foregroundSeek.getProgress()),
                                Utility.mbToPages(visibleSeek.getProgress()),
                                Utility.mbToPages(Utility.parseInt(input.getText().toString(), 0)),
                                Utility.mbToPages(hiddenSeek.getProgress()),
                                Utility.mbToPages(contentSeek.getProgress()),
                                Utility.mbToPages(emptySeek.getProgress()));
						break;
					case 3:
						new setOOM().execute(Utility.mbToPages(foregroundSeek.getProgress()),
                                Utility.mbToPages(visibleSeek.getProgress()),
                                Utility.mbToPages(secondarySeek.getProgress()),
                                Utility.mbToPages(Utility.parseInt(input.getText().toString(), 0)),
                                Utility.mbToPages(contentSeek.getProgress()),
                                Utility.mbToPages(emptySeek.getProgress()));
						break;
					case 4:
						new setOOM().execute(Utility.mbToPages(foregroundSeek.getProgress()),
                                Utility.mbToPages(visibleSeek.getProgress()),
                                Utility.mbToPages(secondarySeek.getProgress()),
                                Utility.mbToPages(hiddenSeek.getProgress()),
                                Utility.mbToPages(Utility.parseInt(input.getText().toString(), 0)),
                                Utility.mbToPages(emptySeek.getProgress()));
						break;
					case 5:
						new setOOM().execute(Utility.mbToPages(foregroundSeek.getProgress()),
                                Utility.mbToPages(visibleSeek.getProgress()),
                                Utility.mbToPages(secondarySeek.getProgress()),
                                Utility.mbToPages(hiddenSeek.getProgress()),
                                Utility.mbToPages(contentSeek.getProgress()),
                                Utility.mbToPages(Utility.parseInt(input.getText().toString(), 0)));
						break;
						
					}
					

					
					
				}
			});
		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1)
				{
					

				}

			});
		builder.setView(input);

		AlertDialog alert = builder.create();

		alert.show();
	}

}
