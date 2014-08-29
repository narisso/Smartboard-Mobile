package com.samsung.spensdk.example.sammeditor;


import java.util.ArrayList;
import java.util.Map;
import com.iic2154.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DevicesDialog extends Dialog {

	ListView lv;
	Button button;
	ArrayList<WifiP2pConfig> p2p;
	WifiP2PInterface context;
	Context activity;
	ListAdapter adapter;
	ProgressDialog progressDialog;
	boolean full_access_mode = false;
	
	public DevicesDialog(Context context, WifiP2pDevice me) {
		super(context);
		setContentView(R.layout.device_dialog);
		setTitle("Connect devices");
		
		if( context.getClass() == SamsungEditor.class)
		{
			full_access_mode = true;
		}

		lv = (ListView)findViewById(R.id.DeviceList);
		this.context = (WifiP2PInterface) context;
		this.activity = context;
		
		if(me != null)
		{
			updateMe(me);
		}
		
		if(full_access_mode)
		{
		
			findViewById(R.id.GroupButton).setOnClickListener(new View.OnClickListener() {
	
	
				@Override
				public void onClick(View v) {
					createWifiGroup();
				}
			});
		}
		else if (!full_access_mode)
		{
			findViewById(R.id.GroupButton).setEnabled(false);
		}
		
		
		findViewById(R.id.DisconnectButton).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				disconnect();
			}
		});

		p2p = new ArrayList<WifiP2pConfig>();
		this.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				DevicesDialog.this.context.setDevicesDialog(null);
				
			}
		});
	}

	public void createWifiGroup(){
		context.getWifiManager().createGroup(context.getWifiChannel(), new ActionListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(activity, "Group created", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onFailure(int reason) {
				Toast.makeText(activity, "Group already created", Toast.LENGTH_LONG).show();
			}
		});
	}

	public void connect(final WifiP2pConfig p2p){
		
		showProgressDialog();
	
		context.getWifiManager().connect(context.getWifiChannel(), p2p, new ActionListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(activity, "Connecting with "+p2p.deviceAddress, Toast.LENGTH_LONG).show();
				hideProgressDialog();
			}

			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				String reason_str="";
				switch (reason) {
				case WifiP2pManager.BUSY:	
					reason_str = "Busy";
					break;
				case WifiP2pManager.ERROR:
					reason_str = "Error";
					break;
				case WifiP2pManager.P2P_UNSUPPORTED:
					reason_str = "Unsupported";
					break;
				}
				hideProgressDialog();
				Toast.makeText(activity, "Failed "+p2p.deviceAddress+ " : "+reason_str, Toast.LENGTH_LONG).show();
			}
		});
	
	}
	
	public void disconnect(){
		context.getWifiManager().cancelConnect(context.getWifiChannel(), null);
		context.getWifiManager().removeGroup(context.getWifiChannel(), new ActionListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(activity, "Disconnected", Toast.LENGTH_LONG).show();
				context.connectionEnded();
				}

			@Override
			public void onFailure(int reason) {
				Toast.makeText(activity, "Disconnected", Toast.LENGTH_LONG).show();
			}
		});
	}

	public void showProgressDialog()
	{
		
		progressDialog = ProgressDialog.show(getContext(), "Connecting...", "Please wait");
		progressDialog.setCancelable(true);
		progressDialog.setButton(BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				cancel();
				
			}
		});
		progressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//disconnect();
				context.onProgressDialogCancelled();
			}
		});
		progressDialog.show();
		
		if(activity.getClass() == SamsungEditor.class)
		{
			((SamsungEditor)activity).devices_progress = progressDialog;
		}
	}
	
	public void hideProgressDialog()
	{
		if(progressDialog != null)
		{
			progressDialog.cancel();
			progressDialog.dismiss();
			Log.d("test","hidedlg");
		}
		else
			Log.d("test","nothidedlg");
	}
	
	public void setUp(ArrayList<Map<String,Object>> list){

		adapter = new ListAdapter(this.getContext());
		adapter.setData(list);

		lv.setAdapter(adapter);
		lv.invalidate();
	}
	

	public  class ListAdapter extends ArrayAdapter<Map<String,Object>> {

		private final LayoutInflater mInflater;

		public ListAdapter(Context context) {
			super(context, R.layout.device);
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setData(ArrayList<Map<String,Object>> data) {
			clear();
			for(Map<String,Object> item : data){
				add(item);
			}
		}

		public String statusToText(int status)
		{
			switch (status) {
			case 0:
				return "Connected";
			case 1:
				return "Invited";
			case 2:
				return "Failed";
			case 3:
				return "Available";
			case 4:
				return "Unavailable";
			}
			
			return null;
		}

		@Override public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView == null) {
				view = mInflater.inflate(R.layout.device, parent, false);
			} else {
				view = convertView;
			}

			final Map<String,Object> item = getItem(position);
			
			TextView Name = (TextView)view.findViewById(R.id.DeviceName);
			Name.setText((String)item.get("device_name"));
			
			TextView Status = (TextView)view.findViewById(R.id.DeviceStatus);
			Status.setText("Status : " + statusToText((Integer)item.get("device_status")) );
			
			TextView isOwner = (TextView)view.findViewById(R.id.DeviceOwner);
			isOwner.setText("Owner : "+(Boolean)item.get("device_is_owner"));
			
			Button btn = (Button)view.findViewById(R.id.DeviceButton);

			btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					connect( (WifiP2pConfig)item.get("device_config") );
				}
			});
			
			if( (Integer)item.get("device_status") == 0)
			{
				btn.setEnabled(false);
			}
			else
			{
				btn.setEnabled(true);
			}

			return view;
		}

	}

	public void update(ArrayList<Map<String, Object>> pares) {
		findViewById(R.id.DialogSpinningWheel).setVisibility(View.GONE);
		//Log.d("test","listview update");
		adapter.setData(pares);
		lv.invalidate();
	}

	public void updateMe(WifiP2pDevice me) {
		
		TextView Name = (TextView)this.findViewById(R.id.MyDeviceName);
		Name.setText((String)me.deviceName);
		
		TextView Status = (TextView)this.findViewById(R.id.MyDeviceStatus);
		Status.setText("Status : " + statusToText(me.status) );
		
		TextView isOwner = (TextView)this.findViewById(R.id.MyDeviceOwner);
		isOwner.setText("Owner : "+me.isGroupOwner());
		isOwner.setVisibility(View.GONE);
	}	
	
	public String statusToText(int status)
	{
		switch (status) {
		case 0:
			return "Connected";
		case 1:
			return "Invited";
		case 2:
			return "Failed";
		case 3:
			return "Available";
		case 4:
			return "Unavailable";
		}
		
		return null;
	}



}
