package com.samsung.spensdk.example.sammeditor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

public class SyncService extends Service {

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_OBJECT = "com.iic2154.wifidirect.SEND_OBJECT";
    public static final String ACTION_CONNECT_TO_OWNER = "com.iic2154.wifidirect.CONNECT_TO_OWNER";
    public static final String ACTION_CONNECT_TO_OWNER_AS_NON_SAMSUNG = "com.iic2154.wifidirect.ACTION_CONNECT_TO_OWNER_AS_NON_SAMSUNG";
    public static final String ACTION_START_SERVER = "com.iic2154.wifidirect.START_SERVER";
    public static final String ACTION_CLOSE_ALL = "com.iic2154.wifidirect.CLOSE_ALL";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    public static final String EXTRAS_OBJECT_TO_SEND = "sobject";
    public static final String EXTRAS_IMAGE_TO_SEND = "scanvas_image";
    public static final String EXTRAS_OBJECT_RECEIVED = "sobject_received";
    public static final String EXTRAS_RECEIVER = "messenger_callback";

    public Boolean isConnected = false;
    public Boolean isServer = false;

    //Client
    int port = 9090;
    int buffer_size = 65536;
    long channel_write_sleep = 10L;
    long channel_read_sleep = 5;
    long channel_read_sleep_ns = 500;
    private ByteBuffer writeBuffer;
    private ByteBuffer readBuffer;
    private SocketChannel cChannel;
    private Selector readSelector;
    
    
    ServerSocketChannel ssChannel;
    SocketChannel sChannel;    
    Selector sSelector;
    Selector cSelector;
    
    ByteBuffer buffer;
    int numClient = 0;
    LinkedList<SocketChannel> samsungClients;
    LinkedList<SocketChannel> nonSamsungClients;

    
    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private boolean mRedelivery;
    private String mName;
    private Handler toastHandler;
    private HandlerThread mThread;

    private final class ServiceHandler extends Handler {
    	 public ServiceHandler(Looper looper) {
             super(looper);
         }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);

        }
    }


    public SyncService()
    {
    	super();
    	mName = "com.iic2154.wifip2p.SYNC_SERVICE";
    }

    @Override
    public void onCreate() {
        // during processing, and to have a static startService(Context, Intent)
        // method that would launch the service & hand off a wakelock.

    	
        super.onCreate();
        samsungClients = new LinkedList<SocketChannel>();
        nonSamsungClients = new LinkedList<SocketChannel>();
        mThread = new HandlerThread("IntentService[" + mName + "]");
        mThread.start();

        toastHandler = new Handler();
        mServiceLooper = mThread.getLooper();

        mServiceHandler = new ServiceHandler(mServiceLooper);
    
        buffer = ByteBuffer.allocate(buffer_size);
        writeBuffer = ByteBuffer.allocateDirect(buffer_size);
        readBuffer = ByteBuffer.allocateDirect(buffer_size);
    }

    public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }

    @Override
    public void onStart(Intent intent, int startId) {    	
    	
    	if(intent.getAction().equals(ACTION_CLOSE_ALL))
        {
    		try 
    		{
	    		if(sChannel != null)	
					sChannel.close();

	    		if(ssChannel != null)
	    			ssChannel.close();

	    		mThread.interrupt();
	    		mThread = new HandlerThread("IntentService[" + mName + "]");
	    		mThread.start();

    	        toastHandler = new Handler();
    	        mServiceLooper = mThread.getLooper();
    	        mServiceHandler = new ServiceHandler(mServiceLooper);
    	        samsungClients.clear();
    	        nonSamsungClients.clear();
    	        isConnected = false;
			} 
    		catch (IOException e) 
    		{
				e.printStackTrace();
			}
        }    	

    	else if (intent.getAction().equals(ACTION_SEND_OBJECT) && isConnected && !isServer) {

    		/********************/
            Serializable object_to_send = intent.getExtras().getSerializable(EXTRAS_OBJECT_TO_SEND);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            byte[] object_data = null;
            
			try {
				oos = new ObjectOutputStream(stream);
				oos.writeObject(object_to_send);
				
				object_data = stream.toByteArray();
				
				
				oos.close();
				stream.close();
			} catch (Exception e1) {
			}
    		
    		if(object_data == null)
    		{
    			Log.d("TEST", "NO HAY DATA");
    			return;
    		}
    		
    		System.out.println("------ " + object_data.length);
    		
    		Log.d("test", "------ " + object_data.length);
    		
    		writeBuffer.clear();
    		writeBuffer.put(object_data);
    		writeBuffer.flip();

    		long nbytes = 0;
    		long toWrite = writeBuffer.remaining();

    		while(nbytes != toWrite) {
    			try {
    				nbytes += cChannel.write(writeBuffer);						
    				try {
    					Thread.sleep(channel_write_sleep);
    				} catch (InterruptedException e) {
    					Log.d("test","error",e);
    				}
    			} catch (IOException e) {
					Log.d("test","error",e);
    			}
    		}
    		
    		//toastHandler.post(new DisplayToast("SE ENVIO DATA"));

    		writeBuffer.rewind();
    	}
    	
    	else if (intent.getAction().equals(ACTION_SEND_OBJECT) && isConnected && isServer){
    		
    		Serializable object_to_send = intent.getExtras().getSerializable(EXTRAS_OBJECT_TO_SEND);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            byte[] object_data = null;
            
			try {
				oos = new ObjectOutputStream(stream);
				oos.writeObject(object_to_send);
				
				object_data = stream.toByteArray();
				
				
				oos.close();
				stream.close();
			} catch (Exception e1) {
			}
    		
    		
    		if(object_data == null)
    		{
    			Log.d("TEST", "NO HAY DATA");
    			return;
    		}
    		if(intent.getExtras().containsKey(EXTRAS_OBJECT_TO_SEND) && samsungClients.size() > 0 )
    		{    		
    			sendBroadcastMessageServer(object_data);
    		}
    		if(intent.getExtras().containsKey(EXTRAS_IMAGE_TO_SEND) && nonSamsungClients.size() > 0 )
    		{
    			byte[] image_data = intent.getExtras().getByteArray(EXTRAS_IMAGE_TO_SEND);
    			sendBroadcastMessageServerToNonSamsung(image_data);
    		}
    		
    	}
    	
    	else
    	{
    		//Send message
	    	Message msg = mServiceHandler.obtainMessage();
	        msg.arg1 = startId;
	        msg.obj = intent;
	        mServiceHandler.sendMessage(msg);
    	}
    }
 
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return START_STICKY;
    }

	protected void onHandleIntent(Intent intent) {
		Context context = getApplicationContext();			

		// Create ClientSocket
        if (intent.getAction().equals(ACTION_CONNECT_TO_OWNER)) {
        	isServer = false;
        	        	
        	String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
        	
        	try {
    			readSelector = Selector.open();
    			InetAddress addr = InetAddress.getByName(host);
    			
				cChannel = SocketChannel.open(new InetSocketAddress(addr, port));
				
				cChannel.configureBlocking(false);
				cChannel.register(readSelector, SelectionKey.OP_READ, new StringBuffer());
				
				while(! cChannel.finishConnect()){
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Log.d("test","error",e);
					}
				}
				
				toastHandler.post(new DisplayToast("Connected"));
				isConnected = true;
				
				/***** INFORM SERVER I'M SAMSUNG ******/
				writeBuffer.clear();
	    		writeBuffer.put("SamsungDevice".getBytes("UTF-8"));
	    		writeBuffer.flip();
	    		
	    		long nbytes = 0;
	    		long toWrite = writeBuffer.remaining();

	    		while(nbytes != toWrite) {
	    			try {
	    				nbytes += cChannel.write(writeBuffer);						
	    				try {
	    					Thread.sleep(channel_write_sleep);
	    				} catch (InterruptedException e) {
	    					Log.d("test","error",e);
	    				}
	    			} catch (IOException e) {
						Log.d("test","error",e);
	    			}
	    		}
	    		writeBuffer.rewind();
				
				while(true){
					
					
					readIncomingChanges(intent);
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Log.d("test","error",e);
					}
				}
    			
    		} catch (Exception e) {
    			Log.d("test","error",e);
    		}
        }
        else if (intent.getAction().equals(ACTION_CONNECT_TO_OWNER_AS_NON_SAMSUNG))
        {
        	isServer = false;
        	
        	String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
        	
        	try {
    			readSelector = Selector.open();
    			InetAddress addr = InetAddress.getByName(host);
    			
				cChannel = SocketChannel.open(new InetSocketAddress(addr, port));
				
				cChannel.configureBlocking(false);
				cChannel.register(readSelector, SelectionKey.OP_READ, new StringBuffer());
				
				while(! cChannel.finishConnect()){
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Log.d("test","error",e);
					}
				}
				
				toastHandler.post(new DisplayToast("Conected"));
				isConnected = true;
				
				/***** INFORM SERVER I'M NOT SAMSUNG ******/
				writeBuffer.clear();
	    		writeBuffer.put("NonSamsungDevice".getBytes("UTF-8"));
	    		writeBuffer.flip();
	    		
	    		long nbytes = 0;
	    		long toWrite = writeBuffer.remaining();

	    		while(nbytes != toWrite) {
	    			try {
	    				nbytes += cChannel.write(writeBuffer);						
	    				try {
	    					Thread.sleep(channel_write_sleep);
	    				} catch (InterruptedException e) {
	    					Log.d("test","error",e);
	    				}
	    			} catch (IOException e) {
						Log.d("test","error",e);
	    			}
	    		}
	    		writeBuffer.rewind();
				
				while(true){
					
					
					readIncomingChangesAsNonSamsung(intent);
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Log.d("test","error",e);
					}
				}
    			
    		} catch (Exception e) {
    			Log.d("test","error",e);
    		}
        }
        else if(intent.getAction().equals(ACTION_START_SERVER))
        {
        	isServer = true;
        	try {
        		/*
        		ResultReceiver rec = intent.getParcelableExtra(SyncService.EXTRAS_RECEIVER);

                // Leer string
                InputStream inputstream = client.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(inputstream);
            	// BufferedReader socketReader = new BufferedReader(new InputStreamReader(inputstream,"UTF-8"));

                while(true)
                {
                	// String reading
	                String inputStreamString = socketReader.readLine();
	                toastHandler.post(new DisplayToast(inputStreamString)); 
	                Log.d("test", "Data recibida: " + inputStreamString);   

                	// Serializable reading
                	SyncObjectStroke object = (SyncObjectStroke) ois.readObject();
                	toastHandler.post(new DisplayToast("Data recieved: " + object.type)); 
                	System.out.println( "++ Data recieved: " + " - " + object.type);
                	System.out.println("++ init bundle");
                	Bundle b = new Bundle();
                	b.putSerializable(SyncService.EXTRAS_OBJECT_RECEIVED, object);
                	rec.send(100, b);
                	System.out.println("++ send bundle");
                }*/

        		ResultReceiver rec = intent.getParcelableExtra(SyncService.EXTRAS_RECEIVER);
        		
                sSelector = Selector.open();
    			
				int sInterestSet = SelectionKey.OP_ACCEPT;
				int cInterestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
				
				ssChannel = ServerSocketChannel.open();
				ssChannel.socket().bind(new InetSocketAddress(port));
				ssChannel.configureBlocking(false);

				SelectionKey serverKey = ssChannel.register(sSelector, sInterestSet);
				isConnected = true;
				
				while(true){
					sSelector.select(); 
					Set<SelectionKey> keys = sSelector.selectedKeys();
					Iterator<SelectionKey> keyIterator = keys.iterator();
					
					while(keyIterator.hasNext()){
						SelectionKey key = keyIterator.next();
						keyIterator.remove();
						
						//Connect new clients
						if (key==serverKey){
							if(key.isAcceptable()){
								SocketChannel csChannel = ssChannel.accept();
								csChannel.configureBlocking(false);
								SelectionKey clientKey = csChannel.register(sSelector, cInterestSet);
								clientKey.attach(numClient);
								numClient ++;
							}
						}
						else if (key != serverKey){
							SocketChannel channel = (SocketChannel)key.channel();
							if(key.isReadable()){
								//Thread.sleep(channel_read_sleep);
								
								int bytesRead = 0;
								buffer.clear();
								while(key.isReadable())
								{
									Thread.sleep(channel_read_sleep);
									int last_reading = channel.read(buffer);
									bytesRead += last_reading;
									
									if(last_reading == 0)
										break;
								}
								
								if(bytesRead <0){
									//key.cancel();
									channel.close();
									//channel = null;
								}
								else{
									buffer.flip();

									byte[] data_array =	Arrays.copyOfRange(buffer.array(), 0, buffer.limit());
									String data_array_string = new String(data_array,"UTF-8");
									if (data_array_string.equals("SamsungDevice"))
									{
										if(channel!=null){
											samsungClients.add(channel);
											
					                        buffer.clear();		
					                        toastHandler.post(new DisplayToast("Connected with (S) device "));
					                        Log.d("test","Connected with (S) device ");
										}
									}
									else if (data_array_string.equals("NonSamsungDevice"))
									{
										if(channel!=null){
											nonSamsungClients.add(channel);
											
					                        buffer.clear();		
					                        toastHandler.post(new DisplayToast("Connected with (NS) device "));
					                        Log.d("test","Connected with (NS) device ");
										}
									}
									else
									{
							            ByteArrayInputStream stream = new ByteArrayInputStream(data_array);
							            ObjectInputStream ois;
							            
										try {
											ois = new ObjectInputStream(stream);
											SyncObjectStroke object = (SyncObjectStroke) ois.readObject();
											
											System.out.println("Data recieved: " + object.type);
											//toastHandler.post(new DisplayToast("Data recieved: " + object.type)); 
											
											
					                        Bundle b = new Bundle();
					                        b.putSerializable(SyncService.EXTRAS_OBJECT_RECEIVED, object);
					                        rec.send(100, b);
					                        System.out.println("Pintar");
					                        
					                        buffer.clear();		
											
											int num = ((Integer) key.attachment()).intValue();
											sendBroadcastMessage(data_array,channel);
											key.attach(num);
											
											
											//ois.close();
											//stream.close();
											
											System.out.println("close buffer");
										} catch (Exception e1) {
											Log.d("test", "SE CAE: ", e1);
					                        buffer.clear();		

										}
									}
									
									try {
										Thread.sleep(50);
									} catch (InterruptedException e) {
										Log.d("test","error",e);
									}
								
									
									
									
								}
							}
						}
					}
				}
                
            } catch (Exception e) {
                Log.e("test", "error" ,e);
            }
        }
	}
	
	private void readIncomingChanges(Intent intent ) throws InterruptedException{
		try {
			readSelector.selectNow();
			Set readyKeys = readSelector.selectedKeys();
			Iterator i = readyKeys.iterator();

			ResultReceiver rec = intent.getParcelableExtra(SyncService.EXTRAS_RECEIVER);
			
			while(i.hasNext()){
				SelectionKey key = (SelectionKey) i.next();
				i.remove();
				if (key.isReadable())
				{
					SocketChannel channel = (SocketChannel) key.channel();
					readBuffer.clear();
					
					int bytesRead = 0;
					readBuffer.clear();
					while(key.isReadable())
					{
						Thread.sleep(channel_read_sleep);
						int last_reading = channel.read(readBuffer);
						bytesRead += last_reading;
						
						if(last_reading == 0)
							break;
					}
					
					if(bytesRead <0){
						key.cancel();
						channel.close();
						channel = null;
					}
					else{
						readBuffer.flip();
						
						byte[] data_array =	Arrays.copyOfRange(readBuffer.array(), 0, readBuffer.limit());
			            ByteArrayInputStream stream = new ByteArrayInputStream(data_array);
			            ObjectInputStream ois;
			            
						try {
							ois = new ObjectInputStream(stream);
							SyncObjectStroke object = (SyncObjectStroke) ois.readObject();
							
							//toastHandler.post(new DisplayToast("Data recieved: " + object.type)); 
							
							Log.d("test","Read : "+data_array.length + " bytes");
							
	                        Bundle b = new Bundle();
	                        b.putSerializable(SyncService.EXTRAS_OBJECT_RECEIVED, object);
	                        rec.send(100, b);
	                        
	                        readBuffer.clear();	
							
							
							ois.close();
							stream.close();
						} catch (Exception e1) {  
							Log.d("test","error",e1);
						}   
					}
				}						
			}

		} catch (IOException e) {
			Log.d("test","error",e);
		}
	}
	
	private void readIncomingChangesAsNonSamsung(Intent intent ) throws InterruptedException{
		try {
			readSelector.selectNow();
			Set readyKeys = readSelector.selectedKeys();
			Iterator i = readyKeys.iterator();

			ResultReceiver rec = intent.getParcelableExtra(SyncService.EXTRAS_RECEIVER);
			
			while(i.hasNext()){
				SelectionKey key = (SelectionKey) i.next();
				i.remove();
				if (key.isReadable())
				{
					SocketChannel channel = (SocketChannel) key.channel();
					readBuffer.clear();
					
					int bytesRead = 0;
					readBuffer.clear();
					while(key.isReadable())
					{
						Thread.sleep(channel_read_sleep_ns);
						int last_reading = channel.read(readBuffer);
						bytesRead += last_reading;
						
						if(last_reading == 0)
							break;
					}
					
					if(bytesRead <0){
						//key.cancel();
						channel.close();
						//channel = null;
					}
					else{
						readBuffer.flip();
						
						byte[] data_array =	Arrays.copyOfRange(readBuffer.array(), 0, readBuffer.limit());
						
						if(data_array.length != 0)
						{
							//toastHandler.post(new DisplayToast("Data recieved "+data_array.length )); 
							
		                    Bundle b = new Bundle();
		                    b.putByteArray(SyncService.EXTRAS_OBJECT_RECEIVED, data_array);
		                    rec.send(100, b);
						}
	                    readBuffer.clear();	
						 
					}		
				}
			}

		} catch (IOException e) {
			Log.d("test","error",e);
		}
	}

	
	private void channelWrite(SocketChannel channel, ByteBuffer buff){
		long nbytes = 0;
		long toWrite = buff.remaining();
		while(nbytes != toWrite){
			try {
				nbytes+=channel.write(buff);
				try {
					Thread.sleep(channel_write_sleep);
				} catch (InterruptedException e) {
					Log.d("test","error",e);
				}
			} catch (IOException e) {
				Log.d("test","error",e);
			}
		}
		Log.d("test","Write : "+nbytes + " bytes");
		
		buff.rewind();
	}
	
	
	@Override
    public void onDestroy() {
        mServiceLooper.quit();
        Log.d("test","SyncService destroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DisplayToast implements Runnable{
    	  String mText;

    	  public DisplayToast(String text){
    	    mText = text;
    	  }

    	  public void run(){
    	     Toast.makeText(SyncService.this, mText, Toast.LENGTH_SHORT).show();
    	  }

    }
    
    private void sendBroadcastMessage(byte[] message, SocketChannel from){
        buffer.clear();
        buffer.put(message);
        buffer.flip();
        
        Iterator i = samsungClients.iterator();
        while(i.hasNext()){
            SocketChannel sc = (SocketChannel)i.next();
            if(sc != from)
                channelWrite(sc, buffer);
        }
    }
    

    private void sendBroadcastMessageServer(byte[] message){
    	if(samsungClients.size() > 0)
    	{
    		writeBuffer.clear();
			System.out.println("Sending sync stroke, bytes: " + message.length);
			writeBuffer.put(message);
			writeBuffer.flip();
			Iterator i = samsungClients.iterator();
			while(i.hasNext()){
				SocketChannel sc = (SocketChannel)i.next();
				channelWrite(sc, writeBuffer);
				if (!sc.isConnected())
					toastHandler.post(new DisplayToast("Disconnected")); 
			}
    	}
		
	}
    
    private void sendBroadcastMessageServerToNonSamsung(byte[] message){
    	if(nonSamsungClients.size() > 0)
    	{
    		writeBuffer.clear();
	  		System.out.println("Sending full image, bytes: " + message.length);
	  		writeBuffer.put(message);
	  		writeBuffer.flip();
	  		Iterator i = nonSamsungClients.iterator();
	  		while(i.hasNext()){
	  			SocketChannel sc = (SocketChannel)i.next();
	  			channelWrite(sc, writeBuffer);
	  			if (!sc.isConnected())
	  				toastHandler.post(new DisplayToast("Disconnected")); 
	  		}
    	}
  	}
	
	
}
