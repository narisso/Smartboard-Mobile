package com.samsung.spensdk.example.sammeditor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private WifiP2PInterface mActivity;
    private PeerListListener myPeerListListener;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
    		WifiP2PInterface activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        this.myPeerListListener = new PeerListListener() {
			
			@Override
			public void onPeersAvailable(WifiP2pDeviceList peers) {
				mActivity.UpdatePeersDialog(peers);
				
			}
		};
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        	int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
            } else {
                // Wi-Fi P2P is not enabled
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        	if (mManager != null) {
                mManager.requestPeers(mChannel, myPeerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            WifiP2pInfo info = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO) ;
            if (info.groupFormed && ! info.isGroupOwner)
            {
            	mActivity.connectionStarted();
            }
            else if (info.groupFormed && info.isGroupOwner)
            {
				mActivity.connectionStarted();
            }
            else
            {
            	mActivity.connectionEnded();
            }

            
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        	
            mActivity.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));        }
    }
}
