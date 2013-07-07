/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package dmace.gesture_recognition;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.view.Display;
import android.widget.TextView;
import android.widget.Toast;
import androidIOAlarmClock2.sample.Exception;
import androidIOAlarmClock2.sample.File;
import androidIOAlarmClock2.sample.Process;
import androidIOAlarmClock2.sample.ReadWriteThread;
import androidIOAlarmClock2.sample.SecurityException;
import androidIOAlarmClock2.sample.String;
import android_serialport_api.SerialPort;

public class ConsoleActivity extends Activity {

	TextView text;
	static ReadWriteThread mReadThread;
	static OutputStream mOutputStream;
	static InputStream mInputStream;
	static protected Application mApplication;
	static protected SerialPort mSerialPort;
	DataStore dataStore;
	boolean watchStarted=false;
	public static int width, height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		////ROOT
	    Process p;  
	    try {  
	       // Preform su to get root privledges  
	       p = Runtime.getRuntime().exec("su");   
	      
	       // Attempt to write a file to a root-only  
	       DataOutputStream os = new DataOutputStream(p.getOutputStream());  
	       os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");  
	      
	       // Close the terminal  
	       os.writeBytes("exit\n");
	       os.flush();  
	       try {  
	          p.waitFor();  
	               if (p.exitValue() != 255) {  
	                  // TODO Code to run on success  
	            	   Toast.makeText(getApplicationContext(), "root", Toast.LENGTH_SHORT).show(); 
	               }  
	               else {  
	                   // TODO Code to run on unsuccessful  
	            	   Toast.makeText(getApplicationContext(), "not root", Toast.LENGTH_SHORT).show();  
	               }  
	       } catch (InterruptedException e) {  
	          // TODO Code to run in interrupted exception  
	    	   Toast.makeText(getApplicationContext(), "not root", Toast.LENGTH_SHORT).show();
	       }  
	    } catch (IOException e) {  
	       // TODO Code to run in input/output exception  
	    	Toast.makeText(getApplicationContext(), "not root", Toast.LENGTH_SHORT).show();
	    }  

		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
        height = display.getHeight();
        startWatch();
		setContentView(new Panel(this)); 
	}
	
	public void connect(String portPath, int baudRate) throws SecurityException, IOException{
    	File device = new File(portPath);
    	// Asking for permission of R/W to the device via system command as superuser
    	if(!device.canRead() || !device.canWrite())
    	{
    	//if (D) Log.d(TAG, " asking for w/r permission ");
    		try{
    			Process su;
    			su = Runtime.getRuntime().exec("su");
    			String cmd = "chmod 777 " + device.getAbsolutePath() + "\n" + "exit\n";
    			su.getOutputStream().write(cmd.getBytes());
    			if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
    				throw new SecurityException();
    			}
    		}catch (Exception e) {
    			e.printStackTrace();
    			throw new SecurityException();
    		}
    	}

    	SerialPort mSerialPort = new SerialPort(device, baudRate, 0);
    	mOutputStream = mSerialPort.getOutputStream();
    	mInputStream = mSerialPort.getInputStream();
    	
    	mOutputStream.write(new byte[]{(byte)0xFF,0x07,0x03});// which is FF 07 03
    	byte[] recvData = new byte[7];
		mInputStream.read(recvData);
		
		mReadThread=ReadWriteThread.getInstance();
		ReadWriteThread.setStuff(this,mInputStream,mOutputStream);
		mReadThread.start();
		
    }
	
	/*public void startWatch() {
		if(watchStarted) return;
		watchStarted=true;
		//get references
		mApplication = (Application) getApplication();
		try {
			mSerialPort = mApplication.getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			
			mOutputStream.write(new byte[]{(byte)0xFF,0x07,0x03});
			
			mReadThread=ReadWriteThread.getInstance();
			ReadWriteThread.setStuff(this,mInputStream,mOutputStream);
			mReadThread.start();
		} catch (Exception e) {
			//Looper.prepare();
			Toast.makeText(this, "You must connect your watch. Read the application description for instructions.", Toast.LENGTH_SHORT).show();
		}
		
	}*/

	public void onDataRecieved() {
		runOnUiThread(new Runnable() {
			public void run() {
				//DataStore.getAverageDivergence(text);
				//DataStore.findShape(text);
				//DataStore.displayPosition(text);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
