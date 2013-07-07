package dmace.gesture_recognition;

import java.io.DataOutputStream;
import java.io.IOException;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainMenu extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
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
		
        
        Button button = new Button(this);
        button.setText("Ready to Send Messages");
        final Intent intent = new Intent(this, ConsoleActivity.class);
        button.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	
	    		startCommunication(intent);
	    		
	         } 
        });
        
        setContentView(button);
    }

	public void startCommunication(Intent intent) {
		try {
			//WatchCommunication.init(this);
			//WatchCommunication.start();
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Please manually allow read/write access to the port where the watch's USB receptor is plugged in. See application description for more details.", Toast.LENGTH_LONG).show();
		}
	}
	
}
