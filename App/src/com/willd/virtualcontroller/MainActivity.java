package com.willd.virtualcontroller;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity  {
	
	public static final String LP = "LP";
	public EditText edittext = null;
	public TextView textview = null;
	String[] returnData = null;
	String UUID = null;
	int LocalPort;
	
	public static final String IPstring = "IPstring";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	try {
    	  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
    	  if (scanResult != null) {
    	    returnData = scanResult.getContents().split(" ");
    	    UUID = returnData[0];
    		String uuid = getUUID();
    		if(uuid == null) {
    			System.out.println("empty");
    			saveUUID(UUID);
    			textview.setText("UUID: "+UUID);
    		}
    		else {
    			System.out.println("else");
    			textview.setText("UUID: "+uuid);
    			UUID=uuid;
	    			
    		}
    	    edittext.setText(returnData[1]);
    	  }}
    	catch(Exception e) {
    		
    	}
    	  // else continue with any other code you need in the method
    	  
    	}
    protected void saveUUID(String data) {
    	String filename = "UUID";
    	FileOutputStream fos;
		try {
			fos = openFileOutput(filename, Context.MODE_PRIVATE);
	    	fos.write(data.getBytes());
	    	fos.close();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    public String getUUID() {
    	String line = null;
    	String output = "";
    	String[] files = null;
    	files = getApplicationContext().fileList();

    	if(files.length == 0) {
    		return null;
    	}
    	try {
			FileReader fileReader = new FileReader("/data/data/com.willd.virtualcontroller/files/"+files[0]);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
            while((line = bufferedReader.readLine()) != null) {

                output += line;
            }	

            // Always close files.
            bufferedReader.close();	
		} catch (Exception e) {
			
			e.printStackTrace();
		}
    	return output;
    }
    IntentIntegrator setupIntegrator() {
    	IntentIntegrator intent = new IntentIntegrator(this);
		return intent;
    	
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		edittext = (EditText) findViewById(R.id.number);
		textview = (TextView) findViewById(R.id.textView2);
		final UDP_Client client = new UDP_Client();
		
	    final Intent i = new Intent(this,com.willd.virtualcontroller.Controller.class);
		
		String uuid = getUUID();
		if(uuid != null) {
			textview.setText("UUID: "+uuid);
			UUID=uuid;
    			
		}
	    final Button button1 = (Button) findViewById(R.id.button1);
	    button1.setOnClickListener(new View.OnClickListener() {
	    	@Override
	        public void onClick(View v) {
	    		
	    		IntentIntegrator integrator = setupIntegrator();
	    		integrator.initiateScan();

	    	}
	    });	 	
	    
	    final Button button2 = (Button) findViewById(R.id.sendmessage2);
	    button2.setOnClickListener(new View.OnClickListener() {
	    	
	    	@Override
	        public void onClick(View v) {

	    		client.UUID = UUID;
	    		client.IPstring = edittext.getText().toString();
	    		client.SendMessage();
	    		LocalPort = client.LocalPort;
	    	    		
	    		Log.i("VirtualController", "Current local port is: " + LocalPort + " in MainActivity");
	    		i.putExtra(com.willd.virtualcontroller.MainActivity.IPstring, edittext.getText().toString());
	    		i.putExtra(com.willd.virtualcontroller.MainActivity.LP, LocalPort);
	    		startActivity(i);
	    	}
	    });

	}



}
