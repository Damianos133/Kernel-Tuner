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

import android.support.v7.app.ActionBar;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import rs.pedjaapps.kerneltuner.R;
import rs.pedjaapps.kerneltuner.helpers.TMAdapter;
import rs.pedjaapps.kerneltuner.model.Task;
import rs.pedjaapps.kerneltuner.utility.Utility;

public class TaskManager extends AbsActivity implements OnItemClickListener, Runnable
{
	public CheckBox system, user, other;
	SharedPreferences preferences;
	ProgressBar loading;
	PackageManager pm;
	
	ListView tmListView;
	public static TMAdapter tmAdapter;
	static Drawable dr;
	ProgressDialog pd;
	String set;

    Handler handler;
	
	ProgressBar pbLoading;
	

	/**
	 * Foreground Application = 10040
	 * Secondary Server  	  = 10041
	 * Content Providers      = 10043
	 * Empty Application      = 10079
	 * Visible Application    = 10025
	 * Hidden Application     =
	 * */
	/*
	 private static final int BACKGROUND = RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
	 private static final int EMPTY = RunningAppProcessInfo.IMPORTANCE_EMPTY;
	 private static final int FOREGROUND = RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
	 private static final int PERCEPTIBLE = RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE;
	 private static final int SERVICE = RunningAppProcessInfo.IMPORTANCE_SERVICE;
	 private static final int VISIBLE = RunningAppProcessInfo.IMPORTANCE_VISIBLE;
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        handler = new Handler();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_tm_list);

		pbLoading = (ProgressBar)findViewById(R.id.pbLoading);
		
		getSupportActionBar().setTitle(getResources().getString(R.string.title_task_manager));
		getSupportActionBar().setSubtitle(null);
		getSupportActionBar().setIcon(R.drawable.tm);

		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		View customNav = LayoutInflater.from(this).inflate(R.layout.ram_layout, null);

		((TextView)customNav.findViewById(R.id.free)).setText(Html.fromHtml("<b>" + getResources().getString(R.string.mem_free) + "</b>" + getFreeRAM() + "MB"));
		((TextView)customNav.findViewById(R.id.total)).setText(Html.fromHtml("<b>" + getResources().getString(R.string.mem_total) + "</b>" + getTotalRAM() + "MB"));

        //Attach to the action bar
        getSupportActionBar().setCustomView(customNav, lp);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

		tmListView =  (ListView)findViewById(R.id.list);
		pm = getPackageManager();
		
		tmAdapter = new TMAdapter(this, R.layout.tm_row);
        tmListView.setAdapter(tmAdapter);
		tmListView.setOnItemClickListener(this);

		loading = (ProgressBar)findViewById(R.id.loading);
		system = (CheckBox)findViewById(R.id.system);
		user = (CheckBox)findViewById(R.id.user);
		other = (CheckBox)findViewById(R.id.other);

		system.setChecked(preferences.getBoolean("tm_system", false));
		user.setChecked(preferences.getBoolean("tm_user", true));
		other.setChecked(preferences.getBoolean("tm_other", false));

		system.setOnCheckedChangeListener(new Listener());
		user.setOnCheckedChangeListener(new Listener());
		other.setOnCheckedChangeListener(new Listener());

        new GetRunningApps(true).execute();
	}

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(handler != null)handler.removeCallbacks(this);
    }

    @Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		Task task = tmAdapter.getItem(p3);
		Intent detailIntent = new Intent(this, TaskManagerDetailActivity.class);
		detailIntent.putExtra(TaskManagerDetailActivity.INTENT_EXTRA_TASK, task);
		startActivity(detailIntent);
	}

    @Override
    public void run()
    {
        new GetRunningApps(false).execute();
    }

    private class Listener implements CompoundButton.OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1)
		{
			new GetRunningApps(true).execute();
		}
	}

	public class GetRunningApps extends AsyncTask<String, /*TMEntry*/Void, List<Task>>
	{
		String line;
        boolean showProgress;

        public GetRunningApps(boolean showProgress)
        {
            this.showProgress = showProgress;
        }

        @Override
		protected List<Task> doInBackground(String... args)
		{
			List<Task> entries = new ArrayList<>();
			Process proc = null;
			InputStream inputStream = null;
			InputStreamReader inputStreamReader = null;
			BufferedReader bufferedReader = null;
			try
			{
				proc = Runtime.getRuntime().exec("ps");
				inputStream = proc.getInputStream();
				inputStreamReader = new InputStreamReader(inputStream);
				bufferedReader = new BufferedReader(inputStreamReader);
				int i = 0;
				while ((line = bufferedReader.readLine()) != null)
				{
					line = line.trim().replaceAll(" {1,}", " ");
					String[] temp = line.split("\\s");
					List<String> tmp = Arrays.asList(temp);
					if (i > 0)
					{
						if (!tmp.get(4).equals("0"))
						{
							Task tmpEntry = new Task(getApplicationName(tmp.get(8)), Utility.parseInt(tmp.get(1), 0), getApplicationIcon(tmp.get(8)), Utility.parseInt(tmp.get(4), 0), appType(tmp.get(8)));
							entries.add(tmpEntry);
						}
					}
					else
					{
						i++;
					}
				}
				proc.waitFor();
				proc.destroy();
			}
			catch (Exception e)
			{
				Log.e("ps", "error " + e.getMessage());
			}
			finally
			{
				try
				{
					if(inputStream != null)inputStream.close();
					if(inputStreamReader != null)inputStreamReader.close();
					if(bufferedReader != null)bufferedReader.close();
				}
				catch (IOException ignore)
				{
				}
			}
			return entries;
		}

		@Override
		protected void onPostExecute(List<Task> entries)
		{
			Collections.sort(entries, new SortByMb());

            tmAdapter.clear();
			for (Task e : entries)
			{
				if (e.getType() == 2)
				{
					if (other.isChecked())
					{
						tmAdapter.add(e);
					}
				}
				else if (e.getType() == 1)
				{
					if (user.isChecked())
					{
						tmAdapter.add(e);
					}
				}
				else if (e.getType() == 0)
				{
					if (system.isChecked())
					{
						tmAdapter.add(e);
					}
				}
			}
			tmAdapter.notifyDataSetChanged();

            if (preferences != null)
            {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("tm_system", system.isChecked())
                    .putBoolean("tm_user", user.isChecked())
                    .putBoolean("tm_other", other.isChecked())
                    .apply();
            }
            handler.postDelayed(TaskManager.this, 3000);
			pbLoading.setVisibility(View.GONE);
		}
		@Override
		protected void onPreExecute()
		{
			if(showProgress)pbLoading.setVisibility(View.VISIBLE);
			//tmAdapter.clear();
		}
	}

	public Drawable getApplicationIcon(String packageName)
	{
		try
		{
			return pm.getApplicationIcon(packageName);
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return getResources().getDrawable(R.drawable.apk);
		}
	}
	public String getApplicationName(String packageName)
	{
		try
		{
			return (String)pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0));
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return packageName;
		}
	}

	/**
	 * @return 0 if system, 1 if user, 2 if unknown
	 */
	public int appType(String packageName)
	{
		try
		{
			ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
			int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
			if ((ai.flags & mask) == 0)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		catch (PackageManager.NameNotFoundException e)
		{
			return 2;
		}
	}

	class SortByMb implements Comparator<Task>
	{
		@Override
		public int compare(Task ob1, Task ob2)
		{
			return ob2.getRss() - ob1.getRss() ;
		}
	}

	static class SortByName implements Comparator<Task>
	{
		@Override
		public int compare(Task s1, Task s2)
		{
		    String sub1 = s1.getName();
		    String sub2 = s2.getName();
		    return sub2.compareTo(sub1);
		} 
	}

	class SortByType implements Comparator<Task>
	{
		@Override
		public int compare(Task ob1, Task ob2)
		{
			return ob1.getType() - ob2.getType() ;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				Intent intent = new Intent(this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static Integer getTotalRAM()
	{
		RandomAccessFile reader = null;
		String load;
		Integer mem = null;
		try
		{
			reader = new RandomAccessFile("/proc/meminfo", "r");
			load = reader.readLine();
			mem = Utility.parseInt(load.substring(load.indexOf(":") + 1,
                    load.lastIndexOf(" ")).trim(), 0) / 1024;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{

				e.printStackTrace();
			}
		}
		return mem;
	}

	public Integer getFreeRAM()
	{
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
        return (int) (mi.availMem / 1048576L);
	}
}
