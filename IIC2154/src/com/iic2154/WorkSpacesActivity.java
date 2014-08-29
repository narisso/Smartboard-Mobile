package com.iic2154;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iic2154.util.JSONParser;
import com.samsung.samm.common.SAMMFileInfo;
import com.samsung.samm.common.SAMMLibConstants;
import com.samsung.sdraw.SDrawLibrary;
import com.samsung.spen.lib.input.SPenLibrary;
import com.samsung.spensdk.example.sammeditor.NonSamsungActivity;
import com.samsung.spensdk.example.sammeditor.SamsungEditor;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WorkSpacesActivity extends Activity implements android.widget.AdapterView.OnItemClickListener{

	public static String CURRENT_PROJECT_ID = "";
	
	ArrayList<List<String>> listItems=new ArrayList<List<String>>();
	ListAdapter adapter;
	ListView listview;
	JSONArray last_request;
	String[] ids;
	ProgressBar workspacesSpinningWheel;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_spaces);
		workspacesSpinningWheel = (ProgressBar) findViewById(R.id.WorkspacesSpinningWheel);
		// Just for testing, allow network access in the main thread
	    // NEVER use this is productive code
	    StrictMode.ThreadPolicy policy = new StrictMode.
	    ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy); 
	    
	    
		listview = (ListView) findViewById(R.id.listView1);
		
        
	    adapter = new ListAdapter(WorkSpacesActivity.this);
	    
	    listview.setAdapter(adapter);
	    
	    listview.setOnItemClickListener(this);
	    
	    new getProjectsTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.work_spaces, menu);
		MenuItem logout = menu.findItem(R.id.logout_menu_item);
		logout.setTitle(logout.getTitle()+"  "+Globals.getEmail(WorkSpacesActivity.this));
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> list1, View arg1, int position, long id) {
		
		// position: item seleccionado (0 a length-1)
		CURRENT_PROJECT_ID = ids[position];
		
		if(SDrawLibrary.isSupportedModel())
		{
			Intent intent = new Intent(this, SamsungEditor.class);
			startActivity(intent);
		}
		else
		{
			Intent intent = new Intent(this, NonSamsungActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()) {		
		//================================================
		// File Menu
		//================================================
		case R.id.logout_menu_item:
		{
			Globals.setEmail(null, WorkSpacesActivity.this);
			Globals.setPass(null, WorkSpacesActivity.this);
			Globals.setToken(null, WorkSpacesActivity.this);
			Intent intent = new Intent(this, LoginActivity.class);
			intent.setFlags(intent.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
		break;
		}
		return true;
	}
	
	public  class ListAdapter extends ArrayAdapter<List<String>> {

		private final LayoutInflater mInflater;

		public ListAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_2);
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setData(ArrayList<List<String>> data) {
			clear();
			if(data != null)
			{
				for(List<String> item : data){
					add(item);
				}
			}
		}


		@Override public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView == null) {
				view = mInflater.inflate(R.layout.project, parent, false);
			} else {
				view = convertView;
			}

			List<String> item = getItem(position);
			TextView Name = (TextView)view.findViewById(R.id.ProjectName);
			TextView Rol = (TextView)view.findViewById(R.id.RoleName);
			TextView Status = (TextView)view.findViewById(R.id.ProjectStatus);
			TextView Desc = (TextView)view.findViewById(R.id.ProjectDescription);
			
			Name.setText(item.get(0));
			Rol.setText(item.get(1));
			Status.setText(item.get(2));
			Desc.setText(item.get(3));	

			return view;
		}
		
	}
	
	public class getProjectsTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {

			String url = Globals.getBaseUrl(WorkSpacesActivity.this)+"/api/v1/getProjects?token=" + LoginActivity.CURRENT_TOKEN;
			// Creating JSON Parser instance
	        JSONParser jParser = new JSONParser();

	        
	        // getting JSON string from URL
	        JSONArray json = jParser.getJSONFromUrl(url);
	        
	        try {        	
	        	ids = new String[json.length()];
	        	
	        	for(int i=0;i<json.length();i++){
	        		JSONObject json_data = json.getJSONObject(i);
	        		ArrayList <String> data = new ArrayList<String>();
	        		data.add(json_data.getString("project_name"));
	        		data.add(json_data.getString("role_name"));
	        		data.add(json_data.getString("project_status"));
	        		String aux = json_data.getString("project_description");
	        		ids[i] = json_data.getString("project_id");
	  
	        		if(aux.length() > 85){
	        			aux = aux.substring(0,  85) + "...";
	        		}
	        		data.add(aux);
	        		listItems.add(data);
	        	}
				
			} catch (Exception e) {
				return false;
			}
	        last_request= json;
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if(success)
			{
				Globals.setProjectList(last_request.toString(), WorkSpacesActivity.this);
				adapter.setData(listItems);
				listview.invalidate();
			}
			else
			{
				try {
					last_request = new JSONArray(Globals.getProjectList(WorkSpacesActivity.this));
		        	ids = new String[last_request.length()];

		        	for(int i=0;i<last_request.length();i++){

		        		JSONObject json_data = last_request.getJSONObject(i);
		        		ArrayList <String> data = new ArrayList<String>();
		        		data.add(json_data.getString("project_name"));
		        		data.add(json_data.getString("role_name"));
		        		data.add(json_data.getString("project_status"));
		        		String aux = json_data.getString("project_description");
		        		ids[i] = json_data.getString("project_id");

		  
		        		if(aux.length() > 85){
		        			aux = aux.substring(0,  85) + "...";
		        		}
		        		data.add(aux);
		        		listItems.add(data);
		        	}
					adapter.setData(listItems);
					listview.invalidate();
				} catch (JSONException e) {
					Log.d("test","error",e);
				}
				
			}
			
			workspacesSpinningWheel.setVisibility(View.GONE);
			
		}

	}

}

