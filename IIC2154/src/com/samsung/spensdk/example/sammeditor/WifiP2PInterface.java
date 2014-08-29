package com.samsung.spensdk.example.sammeditor;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

interface WifiP2PInterface {

	   
	   public void connectionStarted();
	   public void connectionEnded();
	   public void UpdatePeersDialog(WifiP2pDeviceList peers);
	   public DevicesDialog getDevicesDialog();
	   public void setDevicesDialog(DevicesDialog dialog);
	   public WifiP2pManager getWifiManager();
	   public Channel getWifiChannel();
	   public void updateThisDevice(WifiP2pDevice me);
	   public void onProgressDialogCancelled();
	   
}

