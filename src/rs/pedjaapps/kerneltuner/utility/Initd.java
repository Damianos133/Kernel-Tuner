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
package rs.pedjaapps.kerneltuner.utility;

import android.content.Context;
import android.os.AsyncTask;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.CommandCapture;

public class Initd extends AsyncTask<String, Void, String>
{
	Context c;
	public Initd(Context context){
		c = context;
	}
    String arch = Utility.getAbi();
	@Override
	protected String doInBackground(String... args)
	{
		if (args[0].equals("apply"))
		{
			System.out.println("Init.d: Writing init.d");
		CommandCapture command = new CommandCapture(0, 
	            "mount -o remount,rw /system",
	            c.getFilesDir().getPath()+"/cp-"+arch+" /data/data/rs.pedjaapps.KernelTuner/files/99ktcputweaks /system/etc/init.d",
	            "chmod 777 /system/etc/init.d/99ktcputweaks",
	            c.getFilesDir().getPath()+"/cp-"+arch+" /data/data/rs.pedjaapps.KernelTuner/files/99ktgputweaks /system/etc/init.d",
	            "chmod 777 /system/etc/init.d/99ktgputweaks",
	            c.getFilesDir().getPath()+"/cp-"+arch+" /data/data/rs.pedjaapps.KernelTuner/files/99ktmisctweaks /system/etc/init.d",
	            "chmod 777 /system/etc/init.d/99ktmisctweaks",
	            c.getFilesDir().getPath()+"/cp-"+arch+" /data/data/rs.pedjaapps.KernelTuner/files/99ktvoltage /system/etc/init.d",
	            "chmod 777 /system/etc/init.d/99ktvoltage",
	            c.getFilesDir().getPath()+"/cp-"+arch+" /data/data/rs.pedjaapps.KernelTuner/files/99ktsysctl /system/etc/init.d",
				"chmod 777 /system/etc/init.d/99ktsysctl");
			try{
				RootTools.getShell(true).add(command);
			}
			catch(Exception e){

			}
	           
		}
		else if (args[0].equals("rm"))
		{
			System.out.println("Init.d: Deleting init.d");
		CommandCapture command = new CommandCapture(0, 
	            "mount -o remount,rw /system",
	            "rm /system/etc/init.d/99ktcputweaks",
	            "rm /system/etc/init.d/99ktgputweaks",
	            "rm /system/etc/init.d/99ktmisctweaks",
	            "rm /system/etc/init.d/99ktvoltage",
				"rm /system/etc/init.d/99ktsysctl");
		try{
			RootTools.getShell(true).add(command);
			}
			catch(Exception e){
				
			}
		}
		return "";
	}

}


