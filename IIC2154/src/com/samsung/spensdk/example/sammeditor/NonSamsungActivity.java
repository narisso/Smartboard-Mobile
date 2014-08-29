package com.samsung.spensdk.example.sammeditor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.iic2154.Globals;
import com.iic2154.LoginActivity;
import com.iic2154.R;
import com.samsung.spensdk.example.sammeditor.MyResultReceiver.Receiver;

public class NonSamsungActivity extends Activity implements WifiP2PInterface, Receiver {

	public ImageView img;
	
	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	MyResultReceiver syncObjectReceiver;
	DevicesDialog lista;
	WifiP2pDevice last_device_update;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.non_samsung_layout);
		
		Intent stopIntent = new Intent(this, SyncService.class);
		stopService(stopIntent);
		
		createView();
	
	}
		
	public void createView()
	{	
		img = (ImageView) findViewById(R.id.stream_image);
		
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(this, getMainLooper(), null);
	    mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
		
	    mIntentFilter = new IntentFilter();
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    
	    mManager.discoverPeers(mChannel,null);
	    
	    syncObjectReceiver = new MyResultReceiver(new Handler());
	   
	    syncObjectReceiver.setReceiver(this);
	}
	
	/* register the broadcast receiver with the intent values to be matched */
	@Override
	protected void onResume() {
	    super.onResume();
	    registerReceiver(mReceiver, mIntentFilter);
	}
	/* unregister the broadcast receiver */
	@Override
	protected void onPause() {
	    super.onPause();
	    unregisterReceiver(mReceiver);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		MenuItem logout = menu.findItem(R.id.logout);
		logout.setTitle(logout.getTitle()+"  "+Globals.getEmail(this));		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		switch(item.getItemId()){
		case R.id.logout:
		{
			Globals.setEmail(null, this);
			Globals.setPass(null, this);
			Globals.setToken(null, this);
			Intent intent = new Intent(getBaseContext(), LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		break;
		
		case R.id.wifi_p2p:
		{
			
			mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			    @Override
			    public void onSuccess() {
			    	Toast.makeText(NonSamsungActivity.this, "Scanning...", Toast.LENGTH_LONG).show();
			    }
		
			    @Override
			    public void onFailure(int reasonCode) {
			        Toast.makeText(NonSamsungActivity.this, "Scanning failed", Toast.LENGTH_LONG).show();
			    }
			});
			showPeersDialog(new WifiP2pDeviceList());
		
		}
		break;
		}
		return true;

	}


	@Override
	public void connectionStarted() {
		//Toast.makeText(this, "Connection Started", Toast.LENGTH_SHORT).show();
				mManager.requestConnectionInfo(mChannel,new ConnectionInfoListener() {
				
					@Override
					public void onConnectionInfoAvailable(WifiP2pInfo info) {
						Log.d("test",info.toString());
						if(info.groupFormed && !info.isGroupOwner)
						{
							//Toast.makeText(NonSamsungActivity.this, "Wifi Group Detected", Toast.LENGTH_SHORT).show();
							String owner_ip = info.groupOwnerAddress.getHostAddress();
							int owner_port = 8988;
							
							Intent serverIntent = new Intent(NonSamsungActivity.this, SyncService.class);
							serverIntent.setAction(SyncService.ACTION_CONNECT_TO_OWNER_AS_NON_SAMSUNG);
							serverIntent.putExtra(SyncService.EXTRAS_GROUP_OWNER_ADDRESS, owner_ip);
							serverIntent.putExtra(SyncService.EXTRAS_GROUP_OWNER_PORT, owner_port);
							serverIntent.putExtra(SyncService.EXTRAS_RECEIVER, syncObjectReceiver);
							
							startService(serverIntent);
							
							if(lista != null)
							{
								lista.hideProgressDialog();
							}
		
						}
						else if (info.groupFormed && info.isGroupOwner)
						{
							Toast.makeText(NonSamsungActivity.this, "This device cannot be owner", Toast.LENGTH_LONG).show();
						}
					}
				});		
	}


	@Override
	public void connectionEnded() {
		try
		{
			Intent closeIntent = new Intent(NonSamsungActivity.this, SyncService.class);
			closeIntent.setAction(SyncService.ACTION_CLOSE_ALL);
			startService(closeIntent);
		}
		catch(Exception e)
		{
			
		}		
	}
	
	public void showPeersDialog(WifiP2pDeviceList peers){
		
		ArrayList<Map<String,Object>> pares = new ArrayList<Map<String,Object>>();
		
		for( WifiP2pDevice x : peers.getDeviceList()){
			
			Map<String,Object> dispositivo = new HashMap<String, Object>();
			
			dispositivo.put("device_name", x.deviceName);
			dispositivo.put("device_address", x.deviceAddress);
			dispositivo.put("device_status", x.status);
			dispositivo.put("device_is_owner", (Boolean) x.isGroupOwner());
			WifiP2pConfig wif = new WifiP2pConfig();
			wif.deviceAddress = (String)dispositivo.get("device_address");
			wif.wps.setup = WpsInfo.PBC;
			wif.groupOwnerIntent = 0;
			
			dispositivo.put("device_config",wif);
			pares.add(dispositivo);
		}
		
		if(lista==null)
		{	
			lista = new DevicesDialog(this,last_device_update);
			lista.setUp(pares);
			lista.show();
		}
	}


	public void UpdatePeersDialog(WifiP2pDeviceList peers){
		
		if(lista!=null)
		{
			ArrayList<Map<String,Object>> pares = new ArrayList<Map<String,Object>>();
		
			for( WifiP2pDevice x : peers.getDeviceList()){
				
				Map<String,Object> dispositivo = new HashMap<String, Object>();
				
				dispositivo.put("device_name", x.deviceName);
				dispositivo.put("device_address", x.deviceAddress);
				dispositivo.put("device_status", x.status);
				dispositivo.put("device_is_owner", (Boolean) x.isGroupOwner());
				WifiP2pConfig wif = new WifiP2pConfig();
				wif.deviceAddress = (String)dispositivo.get("device_address");
				dispositivo.put("device_config",wif);
				pares.add(dispositivo);
			}
			lista.update(pares);
		
		}
	}


	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		if(resultCode == 100)
		{
			
			try{
				byte[] image = resultData.getByteArray(SyncService.EXTRAS_OBJECT_RECEIVED);
				Log.d("test", "Rendering "+image.length+" bytes" );
				InputStream is = new ByteArrayInputStream(image);
				Bitmap received_image = BitmapFactory.decodeStream(is); 
				img.setImageBitmap(received_image);
				img.invalidate();
			}
			catch(Exception e){
				Log.d("test","error",e);
			}
		}		
	}


	@Override
	public DevicesDialog getDevicesDialog() {
		return lista;
	}


	@Override
	public void setDevicesDialog(DevicesDialog dialog) {
		lista = dialog;
	}


	@Override
	public WifiP2pManager getWifiManager() {
		return mManager;
	}


	@Override
	public Channel getWifiChannel() {
		return mChannel;
	}
	
	@Override
	public void updateThisDevice(WifiP2pDevice me) {
		last_device_update = me;

		if(lista!=null)
		{
		
			lista.updateMe(me);
		
		}
	}

	@Override
	public void onProgressDialogCancelled() {
		// TODO Auto-generated method stub
		
	}

}
