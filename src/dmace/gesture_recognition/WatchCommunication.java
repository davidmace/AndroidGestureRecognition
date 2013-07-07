package dmace.gesture_recognition;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android_serialport_api.SerialPort;

public class WatchCommunication {
	
	/*static ReadWriteThread mReadThread;
	static OutputStream mOutputStream;
	static InputStream mInputStream;
	static Application mApplication;
	static SerialPort mSerialPort;
	static Activity activity;
	public static ArrayList<Communicative> recievers=new ArrayList<Communicative>();
	
	public void startWatch() {
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
			//Toast.makeText(this, "You must connect your watch. Read the application description for instructions.", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public static void init(Activity activity) {
		WatchCommunication.activity=activity;
		Log.d("MyApp","start");
		mApplication = (Application) activity.getApplication();
		Log.d("MyApp","end");
		try {
			mSerialPort = mApplication.getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void addReciver(Communicative comm) {
		recievers.add(comm);
	}
	
	public static void start() {
		//send open port message
		try {
			mOutputStream.write(new byte[]{(byte)0xFF,0x07,0x03});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//start new read/write thread
		ReadWriteThread.setStuff(activity,mInputStream,mOutputStream);
		ReadWriteThread.getInstance().start();
	}
	
	public static void onDestroy() {
		if (mReadThread != null)
			mReadThread.interrupt();
		mApplication.closeSerialPort();
		mSerialPort = null;
	}*/
	
}
