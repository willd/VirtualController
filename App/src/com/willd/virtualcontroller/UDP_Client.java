package com.willd.virtualcontroller;

import java.net.DatagramPacket;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class UDP_Client 
{
	
	public ByteBuffer keys(int[] input) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(input.length * 4);
    for(int i : input) {
        byteBuffer.putInt(i);
    }
	return byteBuffer;
	}
    private AsyncTask<Void, Void, Void> async_client;
    
    public InetAddress IP = null;
    public String IPstring = null;
    
    public int[] Message = new int[8];
    public int LocalPort;
    public String UUID = null;
    UDP_Client() {
    	if(ds == null ) {
            try {
            	ds = new DatagramSocket();
            	LocalPort = ds.getLocalPort();
            	ds.setReuseAddress(true);
            		
            }
            catch(Exception e) {
                System.out.println("Caught exception: " + e.getMessage());
        	    e.printStackTrace();
        		
            }
        	}
    }
    DatagramSocket ds = null;
    @SuppressLint("NewApi")
    public void SendMessage()
    {
    	try {
    		
        	IP = InetAddress.getByName(IPstring);
        		
            	
    	}
    	catch (Exception e) {
		    System.out.println("Caught exception: " + e.getMessage());
		    e.printStackTrace();
    		
    	}
    	
    	async_client = new AsyncTask<Void, Void, Void>() 
        {
            @Override
            protected Void doInBackground(Void... params)
            {   

                try 
                {
                    
                    DatagramPacket dp = null;
                    if(UUID != null) {
                    	dp = new DatagramPacket(UUID.getBytes(),UUID.length(), IP, 10500);
                    	ds.send(dp);	
                    	UUID=null;
                    	LocalPort = ds.getLocalPort();
                    	
                    	
                    }
                    	ByteBuffer byteBuffer = ByteBuffer.allocate(Message.length * 4);        
                    	IntBuffer intBuffer = byteBuffer.asIntBuffer();
                    	intBuffer.put(Message);

                    	byte[] array = byteBuffer.array();
                    	
                    	dp = new DatagramPacket(array,array.length, IP, LocalPort);
                    
                    	ds.send(dp);
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
              /* finally 
                {
                    if (ds != null) 
                    {   
                        ds.close();
                        
                    }
                }*/
                
                return null;
                
            }

            protected void onPostExecute(Void result) 
            {
               super.onPostExecute(result);
            }
        };
        
        if (Build.VERSION.SDK_INT >= 11) async_client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async_client.execute();
    }
}