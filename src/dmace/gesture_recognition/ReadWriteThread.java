package dmace.gesture_recognition;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import android.app.Activity;
import android_serialport_api.SerialPort;

class ReadWriteThread extends Thread {

	protected static Application mApplication;
	protected static SerialPort mSerialPort;
	protected static OutputStream mOutputStream;
	private static InputStream mInputStream;
	private static Activity reciever;
	public static ReadWriteThread instance;
	public static int i;
	
	public static ReadWriteThread getInstance() {
		if(instance==null) instance=new ReadWriteThread();
		return instance;
	}
	
	public static void setStuff(Activity a, InputStream is, OutputStream os) {
		reciever=a;
		mInputStream=is;
		mOutputStream=os;
	}
	
	
	@Override
	public void run() {
		super.run();
		while(!isInterrupted()) {
			int size;
			try {
				
				//write acc request
				mOutputStream.write(new byte[]{(byte)0xFF,0x08,0x07,0x00,0x00,0x00,0x00});
				//read in acc data
				byte[] buffer = new byte[7];
				size=mInputStream.read(buffer);
				if(size>0) {
					DataStore.add(buffer);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}