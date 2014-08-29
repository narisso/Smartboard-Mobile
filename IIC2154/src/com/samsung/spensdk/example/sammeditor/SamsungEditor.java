package com.samsung.spensdk.example.sammeditor;


import java.io.Console;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.samsung.samm.common.SDataAttachFile;
import com.samsung.samm.common.SDataPageMemo;
import com.samsung.samm.common.SObject;
import com.samsung.samm.common.SObjectImage;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.samm.common.SObjectText;
import com.samsung.samm.common.SOptionSCanvas;
import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spen.settings.SettingFillingInfo;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spen.settings.SettingTextInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.ColorPickerColorChangeListener;
import com.samsung.spensdk.applistener.FileProcessListener;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.applistener.SObjectUpdateListener;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;
import com.samsung.spensdk.applistener.SettingStrokeChangeListener;
import com.samsung.spensdk.applistener.SettingViewShowListener;
import com.example.note.NoteList;
import com.example.note.NotesDbAdapter;
import com.iic2154.Globals;
import com.iic2154.LoginActivity;
import com.iic2154.R;
import com.iic2154.WorkSpacesActivity;
import com.iic2154.util.JSONParser;
import com.samsung.spensdk.example.sammeditor.MyResultReceiver.Receiver;
import com.samsung.spensdk.example.tools.PreferencesOfOtherOption;
import com.samsung.spensdk.example.tools.PreferencesOfSAMMOption;
import com.samsung.spensdk.example.tools.SPenSDKUtils;
import com.samsung.spensdk.example.tools.ToolFileTotalInfoShow;
import com.samsung.spensdk.example.tools.ToolListActivity;
import com.samsung.spensdk.example.tools.ToolTextDialogInput;

import android.provider.Settings.Secure;


public class SamsungEditor extends Activity implements Receiver, WifiP2PInterface {

	private final String TAG = "SPenSDK Sample";
	private final boolean SHOW_LOG = false;

	boolean isTouchPenUp = false;
	boolean isTouchPenHoverExit = false;
	
	//==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	//==============================
	// remove 'final' to edit AppID
	private String APPLICATION_ID_NAME = "SDK Sample Application";	
	private int APPLICATION_ID_VERSION_MAJOR = 1;
	private int APPLICATION_ID_VERSION_MINOR = 0;
	private String APPLICATION_ID_VERSION_PATCHNAME = "Debug";

	//==============================
	// Menu
	//==============================

	private final int MENU_FILE_GROUP = 1000;
	private final int MENU_FILE_NEW = 1001;
	private final int MENU_FILE_LOAD = 1002;
	private final int MENU_FILE_SAVE_AS = 1003;



	private final int MENU_EDIT_GROUP = 2000;
	private final int MENU_EDIT_DELETE_OBJECT = 2010;
	private final int MENU_EDIT_ROTATE_DEGREE = 2020;
	private final int MENU_EDIT_COPY = 2030;
	private final int MENU_EDIT_CUT = 2040;
	private final int MENU_EDIT_PASTE = 2050;
	private final int MENU_EDIT_OBJECT_DEPTH_CHANGE = 2090;



	//==============================
	// Activity Request code
	//==============================
	private final int REQUEST_CODE_INSERT_IMAGE_OBJECT = 100;	
	private final int REQUEST_CODE_FILE_SELECT = 101;
	private final int REQUEST_CODE_VOICE_RECORD = 102;
	private final int REQUEST_CODE_SET_BACKGROUND_AUDIO = 103;
	private final int REQUEST_CODE_ATTACH_SELECT = 104;
	private final int REQUEST_CODE_SET_PAGE_MEMO = 105;
	private final int REQUEST_CODE_SELECT_IMAGE_BACKGROUND = 106;
	private final int REQUEST_CODE_SELECT_IMAGE_FOREGROUND = 107;
	private final int REQUEST_CODE_TOTAL_INFO_SHOW = 108;
	private final int REQUEST_CODE_OTHER_OPTION = 111;

	SObject temp_sobject;

	//==============================
	// Object Depth Cange Constants
	//==============================
	private final int OBJECT_DEPTH_CHANGE_FORWARD =  0;
	private final int OBJECT_DEPTH_CHANGE_BACKWARD = 1;
	private final int OBJECT_DEPTH_CHANGE_FRONT = 2;
	private final int OBJECT_DEPTH_CHANGE_BACK = 3;


	//==============================
	// Hover Pointer Constants
	//==============================
	private final int HOVER_POINTER_DEFAULT =  0;
	private final int HOVER_POINTER_SIMPLE_ICON = 1;
	private final int HOVER_POINTER_SIMPLE_DRAWABLE = 2;
	private final int HOVER_POINTER_SPEN = 3;
	private final int HOVER_POINTER_SNOTE = 4;

	private final int HOVER_SHOW_ALWAYS_ONHOVER = 0;
	private final int HOVER_SHOW_ONCE_ONHOVER = 1;

	//==============================
	// Hover Pointer Constants
	//==============================
	private final int SIDE_BUTTON_CHANGE_SETTING = 0;
	private final int SIDE_BUTTON_SHOW_SETTING_VIEW = 1;

	//==============================
	// Insert Object Constants
	//==============================

	public final static String KEY_EDITOR_VERSION = "EditorVersion";
	public final static String KEY_EDITOR_GUI_STYLE = "EditorGUIStyle";

	//==============================
	// Variables
	//==============================
	Context mContext = null;

	private String  mTempAMSFolderPath = null;
	private String  mTempAMSAttachFolderPath = null;
	private int mSideButtonStyle;


	private final String DEFAULT_SAVE_PATH = "SPenSDK";
	private final String DEFAULT_ATTACH_PATH = "SPenSDK/attach";
	private final String DEFAULT_FILE_EXT = ".png";

	//	private RelativeLayout	mViewContainer;
	private FrameLayout		mLayoutContainer;
	private RelativeLayout	mCanvasContainer;
	private SCanvasView		mSCanvas;

	//private ImageView		mSelectionModeBtn;
	private ImageView		mPenBtn;
	private ImageView		mEraserBtn;
	private ImageView		mTextBtn;
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;

	private View mLoginStatusView;

	private boolean mMultiSelectionMode = true;
	private boolean mMultitouch = true;
	private int		mSettingviewType;
	private int		mCanvasHeight;
	private int		mCanvasWidth;

	private boolean mbContentsOrientationHorizontal = false;

	private int mEditorGUIStyle = SCanvasConstants.SCANVAS_GUI_STYLE_NORMAL;
	private boolean mbSingleSelectionFixedLayerMode = false;
	private String currentLanguage = null;

	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	MyResultReceiver syncObjectReceiver;
	WifiP2pDevice last_device_update;

	DevicesDialog lista;
	Dialog documentsListDialog;
	
	DictionarySObject dictionary;
	Timer myTimer;
	
	downloadSAMMFile currentdownloadTask;
	
	ProgressDialog mProgressDialog;
	ProgressDialog devices_progress;
	
	boolean isTouchPenDown = false;
	boolean isTouchPenDown_insert = false;
	boolean isServer = false;
	boolean isExternalClearAll = false;

	ArrayList<SyncObjectStroke> queue_sobject = new ArrayList<SyncObjectStroke>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String android_id = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID); 
		dictionary = new DictionarySObject(android_id);

		Intent stopIntent = new Intent(this, SyncService.class);
		stopService(stopIntent);

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

		setContentView(R.layout.editor_samm_editor);

		mLoginStatusView = findViewById(R.id.login_status);

		mContext = this;

		//------------------------------------
		// UI Setting
		//------------------------------------
		//mSelectionModeBtn = (ImageView) findViewById(R.id.selectionModeBtn);
		//mSelectionModeBtn.setOnClickListener(mBtnClickListener);

		mPenBtn = (ImageView) findViewById(R.id.penBtn);
		mPenBtn.setOnClickListener(mBtnClickListener);
		mPenBtn.setOnLongClickListener(mBtnLongClickListener);
		mEraserBtn = (ImageView) findViewById(R.id.eraseBtn);
		mEraserBtn.setOnClickListener(mBtnClickListener);
		mEraserBtn.setOnLongClickListener(mBtnLongClickListener);
		mTextBtn = (ImageView) findViewById(R.id.textBtn);
		mTextBtn.setOnClickListener(mBtnClickListener);
		mTextBtn.setOnLongClickListener(mBtnLongClickListener);

		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);

		//------------------------------------
		// Create SCanvasView
		//------------------------------------
		mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);
		mCanvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);

		// Add SCanvasView under minSDK 14(AndroidManifext.xml)
		mSCanvas = new SCanvasView(mContext);
		//RelativeLayout.LayoutParams sizeParams = new RelativeLayout.LayoutParams(1280, 720);
		//mSCanvas.setLayoutParams(sizeParams);
		//mSCanvas.setCanvasBaseZoomScale(.0f);
		
		mCanvasContainer.addView(mSCanvas);

		// Add SCanvasView under minSDK 10(AndroidManifext.xml) for preventing text input error
		//mSCanvas = new SCanvasView(mContext);
		//mSCanvas.addedByResizingContainer(mCanvasContainer);


		Intent intent = getIntent();		
		mbSingleSelectionFixedLayerMode = intent.getBooleanExtra(KEY_EDITOR_VERSION, mbSingleSelectionFixedLayerMode);
		mEditorGUIStyle = intent.getIntExtra(KEY_EDITOR_GUI_STYLE, mEditorGUIStyle);

		//------------------------------------
		// SettingView Setting
		//------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, true, true);
		// Talk & Description Setting by Locale
		SPenSDKUtils.addTalkbackAndDescriptionStringResourceMap(settingResourceMapInt);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, true, true);	

		// Create Setting View
		mSCanvas.createSettingView(mLayoutContainer, settingResourceMapInt, settingResourceMapString);

		// Save current locale
		Configuration config = getBaseContext().getResources().getConfiguration();
		currentLanguage = config.locale.getLanguage();



		//====================================================================================
		// DB Note
		LoadDBNote();
		//====================================================================================
		
		
		//====================================================================================
		//
		// Set Callback Listener(Interface)
		//
		//====================================================================================
		//------------------------------------------------
		// SCanvas Listener
		//------------------------------------------------
		mSCanvas.setSCanvasInitializeListener(new SCanvasInitializeListener() {
			@Override
			public void onInitialized() {
				//--------------------------------------------
				// Start SCanvasView/CanvasView Task Here
				//--------------------------------------------			
				// Place SCanvasView In the Center
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mSCanvas.getLayoutParams(); 
				layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0); 
				mSCanvas.setLayoutParams(layoutParams); 

				// Application Identifier Setting
				if(!mSCanvas.setAppID(APPLICATION_ID_NAME, APPLICATION_ID_VERSION_MAJOR, APPLICATION_ID_VERSION_MINOR,APPLICATION_ID_VERSION_PATCHNAME))
					Toast.makeText(mContext, "Fail to set App ID.", Toast.LENGTH_LONG).show();

				// Set Title
				if(!mSCanvas.setTitle("SPen-SDK Test"))
					Toast.makeText(mContext, "Fail to set Title.", Toast.LENGTH_LONG).show();

				// Set Initial Setting View Size
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);

				// Set Editor Version (mEditorGUIStyle)	
				// - SCanvasConstants.SCANVAS_GUI_STYLE_NORMAL;
				// - SCanvasConstants.SCANVAS_GUI_STYLE_KIDS;
				mSCanvas.setSCanvasGUIStyle(mEditorGUIStyle);

				// Set Editor GUI Style (mbSingleSelectionFixedLayerMode)
				// - true :  S Pen SDK 2.2 (Single selection, Fixed layer Editor : Image-Text-Stroke ordering)
				// - false : S Pen SDK 2.3 (Multi-selection, Flexible layer Editor : Input ordering)				
				//mSCanvas.setSingleSelectionFixedLayerMode(mbSingleSelectionFixedLayerMode);
				//if(mbSingleSelectionFixedLayerMode)
					//mSelectionModeBtn.setVisibility(View.GONE);
				
				mCanvasWidth = mSCanvas.getWidth();
				mCanvasHeight = mSCanvas.getHeight();
				
				Log.d("test",mCanvasWidth+"x"+mCanvasHeight);
				
				// Get the direction of contents(Canvas)
				if(mCanvasWidth > mCanvasHeight)
					mbContentsOrientationHorizontal = true;
				else
					mbContentsOrientationHorizontal = false;

				applyOtherOption();
				mSCanvas.setBackgroundColor(Color.WHITE);
				
				// Update button state
				updateModeState();
			}
		});

		//------------------------------------------------
		// History Change Listener
		//------------------------------------------------
		mSCanvas.setHistoryUpdateListener(new HistoryUpdateListener() {
			@Override
			public void onHistoryChanged(boolean undoable, boolean redoable) {
				mUndoBtn.setEnabled(undoable);
				mRedoBtn.setEnabled(redoable);
				//trySendLastUpdate();
			}
		});

		mSCanvas.setSObjectUpdateListener(new SObjectUpdateListener() {

			@Override
			public boolean onSObjectStrokeInserting(SObjectStroke arg0) {		
				
				/*if(dictionary.IsOwner(arg0)){
					if(queue_sobject.size() > 0){
						return true;
					}
				}^*/
				
				return false;
			}

			@Override
			public void onSObjectSelected(SObject arg0, boolean arg1) {

			}

			@Override
			public void onSObjectInserted(SObject object, boolean arg1, boolean arg2,
					boolean arg3) {
				
				// S�lo si se dibuja un SObject propio
				if(dictionary.Add(object)){
					
					// se env�a al servidor
					if(dictionary.IsOwner(object)){
						trySendLastUpdate(object, SyncObjectStroke.ACTION_INSERTED);
					}
				}
				
			}

			@Override
			@Deprecated 
			public void onSObjectInserted(SObject arg0, boolean arg1, boolean arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSObjectDeleted(SObject object, boolean tyarg1, boolean arg2,
					boolean arg3, boolean arg4) {

				System.out.println("------- REMOVE");

				SObject obj = dictionary.Remove(object);

				if(dictionary.IsOwner(object)){
					trySendLastUpdate(obj, SyncObjectStroke.ACTION_DELETED);
				}
			}

			@Override
			@Deprecated
			public void onSObjectDeleted(SObject arg0, boolean arg1, boolean arg2,
					boolean arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSObjectClearAll(boolean arg0) {
				// TODO Auto-generated method stub
				SObjectStroke obj = new SObjectStroke();
				
				if( !isExternalClearAll && !isServer)
					trySendLastUpdate(obj, SyncObjectStroke.ACTION_CLEAR_ALL);
				if (isServer)
				{
					//Toast.makeText(SPen_Example_SAMMEditor.this, "Enviando Clear All", Toast.LENGTH_SHORT).show();
					trySendLastUpdate(obj, SyncObjectStroke.ACTION_CLEAR_ALL);
				}
				
				isExternalClearAll=false;
			}

			@Override
			public void onSObjectChanged(SObject object, boolean arg1, boolean arg2) {

				if(dictionary.IsOwner(object)){
					trySendLastUpdate(object, SyncObjectStroke.ACTION_CHANGED);
				}
			}
		});


		//------------------------------------------------
		// OnSettingStrokeChangeListener Listener 
		//------------------------------------------------		
		SettingStrokeChangeListener	mSettingStrokeChangeListener = new SettingStrokeChangeListener() {
			@Override
			public void onClearAll(boolean bClearAllCompleted) {
				// If don't set eraser mode, then change to pen mode automatically.
				if(bClearAllCompleted)
					updateModeState();
			}
			@Override
			public void onEraserWidthChanged(int eraserWidth) {				
			}

			@Override
			public void onStrokeColorChanged(int strokeColor) {
			}

			@Override
			public void onStrokeStyleChanged(int strokeStyle) {			
			}

			@Override
			public void onStrokeWidthChanged(int strokeWidth) {				
			}

			@Override
			public void onStrokeAlphaChanged(int strokeAlpha) {								
			}

			@Override
			public void onBeautifyPenStyleParameterCursiveChanged(int cursiveParameter) {				
			}

			@Override
			public void onBeautifyPenStyleParameterDummyChanged(int dummyParamter) {				
			}

			@Override
			public void onBeautifyPenStyleParameterModulationChanged(int modulationParamter) {				
			}

			@Override
			public void onBeautifyPenStyleParameterSustenanceChanged(int sustenanceParamter) {				
			}

			@Override
			public void onBeautifyPenStyleParameterBeautifyStyleIDChanged(int styleID) {				
			}

			@Override
			public void onBeautifyPenStyleParameterFillStyleChanged(int fillStyle) {				
			}
		};

		mSCanvas.setSettingStrokeChangeListener(mSettingStrokeChangeListener);
		mSCanvas.setStrokeLongClickSelectOption(false);
		mSCanvas.setTextLongClickSelectOption(false);
		mSCanvas.setRemoveLongPressStroke(false);
		mSCanvas.setLongClickable(false);
		mSCanvas.setSCanvasLongPressListener(null);
		//------------------------------------------------
		// SCanvas Mode Changed Listener 
		//------------------------------------------------
		mSCanvas.setSCanvasModeChangedListener(new SCanvasModeChangedListener() {

			@Override
			public void onModeChanged(int mode) {
				if(!(mSCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_SELECT)){
					mMultiSelectionMode = false;
				}
				else{
					if(mSCanvas.isMultiSelectionMode())
						mMultiSelectionMode = true;
					else
						mMultiSelectionMode = false;
				}
				updateSelectButton();
				updateModeState();
			}

			@Override
			public void onMovingModeEnabled(boolean bEnableMovingMode) {
				updateModeState();
			}

			@Override
			public void onColorPickerModeEnabled(boolean bEnableColorPickerMode) {
				updateModeState();
			}
		});

		//------------------------------------------------
		// Color Picker Listener 
		//------------------------------------------------
		mSCanvas.setColorPickerColorChangeListener(new ColorPickerColorChangeListener(){
			@Override
			public void onColorPickerColorChanged(int nColor) {

				int nCurMode = mSCanvas.getCanvasMode();
				if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN) {
					SettingStrokeInfo strokeInfo = mSCanvas.getSettingViewStrokeInfo();
					if(strokeInfo != null) {
						strokeInfo.setStrokeColor(nColor);	
						mSCanvas.setSettingViewStrokeInfo(strokeInfo);
					}	
				}
				else if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER) {
					// do nothing
				}
				else if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
					SettingTextInfo textInfo = mSCanvas.getSettingViewTextInfo();
					if(textInfo != null) {
						textInfo.setTextColor(nColor);
						mSCanvas.setSettingViewTextInfo(textInfo);
					}
				}
				else if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING) {
					SettingFillingInfo fillingInfo = mSCanvas.getSettingViewFillingInfo();
					if(fillingInfo != null) {
						fillingInfo.setFillingColor(nColor);
						mSCanvas.setSettingViewFillingInfo(fillingInfo);
					}
				}	
			}			
		});	

		//------------------------------------------------
		// File Processing 
		//------------------------------------------------
		mSCanvas.setFileProcessListener(new FileProcessListener() {
			@Override
			public void onChangeProgress(int nProgress) {
				//Log.i(TAG, "Progress = " + nProgress);
			}

			@Override
			public void onLoadComplete(boolean bLoadResult) {			 
				if(bLoadResult){
					// Show Application Identifier
					String appID = mSCanvas.getAppID();
					Toast.makeText(SamsungEditor.this, "Load AMS File Success!", Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(SamsungEditor.this, "Load AMS File Fail!", Toast.LENGTH_LONG).show();				
				}
			}
		});

		//------------------------------------------------
		// SettingView Listener : Optional
		//------------------------------------------------				
		mSCanvas.setSettingViewShowListener(new SettingViewShowListener() {
			@Override
			public void onEraserSettingViewShow(boolean bVisible) {
				if(SHOW_LOG){		
					if(bVisible) Log.i(TAG, "Eraser setting view is shown");
					else		 Log.i(TAG, "Eraser setting view is closed");
				}
			}
			@Override
			public void onPenSettingViewShow(boolean bVisible) {
				if(SHOW_LOG){		
					if(bVisible) Log.i(TAG, "Pen setting view is shown");
					else		 Log.i(TAG, "Pen setting view is closed");
				}
			}
			@Override
			public void onTextSettingViewShow(boolean bVisible) {
				if(SHOW_LOG){		
					if(bVisible) Log.i(TAG, "Text setting view is shown");
					else		 Log.i(TAG, "Text setting view is closed");
				}
			}
			@Override
			public void onFillingSettingViewShow(boolean bVisible) {
				if(SHOW_LOG){		
					if(bVisible) Log.i(TAG, "Text setting view is shown");
					else		 Log.i(TAG, "Text setting view is closed");
				}
			}
		});


		//--------------------------------------------
		// Set S pen Touch Listener
		//--------------------------------------------
		mSCanvas.setSPenTouchListener(new SPenTouchListener(){

			@Override
			public boolean onTouchFinger(View view, MotionEvent event) {
				return false;
			}

			@Override
			public boolean onTouchPen(View view, MotionEvent event) {

				if(event.getAction() == MotionEvent.ACTION_DOWN){
					// DOWN TOUCH -> bloquear pintado de externos
					isTouchPenDown = true;
					isTouchPenDown_insert = true;
					
					isTouchPenUp = false;
				}
				if(event.getAction() == MotionEvent.ACTION_UP){
					
				}
				
				System.out.println("isTouchPenUp: " + isTouchPenUp);
				

				return false;
			}

			@Override
			public boolean onTouchPenEraser(View view, MotionEvent event) {
				return false;
			}

			@Override
			public void onTouchButtonDown(View view, MotionEvent event) {				
			}

			@Override
			public void onTouchButtonUp(View view, MotionEvent event) {
				showObjectPopUpMenu((int)event.getX(), (int)event.getY());
			}		

		});


		//--------------------------------------------
		// Set S pen HoverListener
		//--------------------------------------------
		mSCanvas.setSPenHoverListener(new SPenHoverListener(){

			@Override
			public boolean onHover(View view, MotionEvent event) {				
				
				
				if(event.getAction() == MotionEvent.ACTION_HOVER_EXIT){
					
					if(queue_sobject.size() > 0){ 							
							if(isTouchPenDown){
								isTouchPenDown_insert = false;
								
								// Dibujar todos los SObject externos que est�n en la cola
								while(queue_sobject.size() > 0){
									
									SyncObjectStroke sos = queue_sobject.get(0);
									queue_sobject.remove(0);
									updateWithNewObject( sos );
									
									if(isTouchPenDown_insert){
										break;
									}
								}
								
								// desbloquear "isTouchPenDown", SObject externos se dibujar�n directamente
								isTouchPenDown = false;
							}
						}
					else{
						isTouchPenDown = false;
					
					}
				}
				else{
					isTouchPenHoverExit = false;
					isTouchPenDown_insert = true;
					isTouchPenDown = true;
				}
				
				return false;
			}

			@Override
			public void onHoverButtonDown(View view, MotionEvent event) {
				//isPenButtonDown= true;
			}

			@Override
			public void onHoverButtonUp(View view, MotionEvent event) {
				/*if(isPenButtonDown==false)	// ignore button up event if button was not pressed on hover
					return;
				isPenButtonDown = false;

				//--------------------------------------------------------------
				// Show popup menu if the object is selected or if sobject exist in clipboard
				//--------------------------------------------------------------
				if(mSCanvas.isSObjectSelected() || mSCanvas.isClipboardSObjectListExist()){
					showObjectPopUpMenu((int)event.getX(), (int)event.getY());
				}
				else {	
					if(!mSCanvas.isVideoViewExist()){
						if(mSideButtonStyle == SIDE_BUTTON_CHANGE_SETTING){

							boolean bIncludeDefinedSetting = true;
							boolean bIncludeCustomSetting = true;
							boolean bIncludeEraserSetting = true;
							SettingStrokeInfo settingInfo = mSCanvas.getSettingViewNextStrokeInfo(bIncludeDefinedSetting, bIncludeCustomSetting, bIncludeEraserSetting);

							if(settingInfo!=null) {
								mSCanvas.setSettingViewStrokeInfo(settingInfo);
								int nPreviousMode = mSCanvas.getCanvasMode(); 
								// Mode Change : Pen => Eraser					
								if(nPreviousMode == SCanvasConstants.SCANVAS_MODE_INPUT_PEN
										&& settingInfo.getStrokeStyle()==SObjectStroke.SAMM_STROKE_STYLE_ERASER){
									// Change Mode
									mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
									// Show Setting View
									if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN)){
										mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
										mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, true);
									}
									updateModeState();
								}
								// Mode Change : Eraser => Pen 
								if(nPreviousMode == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER
										&& settingInfo.getStrokeStyle()!=SObjectStroke.SAMM_STROKE_STYLE_ERASER){
									// Change Mode
									mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
									// Show Setting View
									if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER)){
										mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
										mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);
									}
									updateModeState();
								}							
							}
						} // end of if(mSideButtonStyle == SIDE_BUTTON_CHANGE_SETTING){
						// Show SettingView(Toggle SettingView)
						else if(mSideButtonStyle == SIDE_BUTTON_SHOW_SETTING_VIEW){
							doHoverButtonUp((int)event.getX(), (int)event.getY());
						}
					}
				}*/ // end of else // if(mSCanvas.isSObjectSelected() || mSCanvas.isClipboardSObjectListExist()){
			} // end of onHoverButtonUp
		});


		// Update UI
		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);
		mPenBtn.setSelected(true);

		// create basic save/road file path
		File sdcard_path = Environment.getExternalStorageDirectory();
		File default_path =  new File(sdcard_path, DEFAULT_SAVE_PATH);
		if(!default_path.exists()){
			if(!default_path.mkdirs()){
				Log.e(TAG, "Default Save Path Creation Error");
				return ;
			}
		}

		// attach file path
		File spen_attach_path =  new File(sdcard_path, DEFAULT_ATTACH_PATH);
		if(!spen_attach_path.exists()){
			if(!spen_attach_path.mkdirs()){
				Log.e(TAG, "Default Attach Path Creation Error");
				return ;
			}
		}

		mTempAMSFolderPath = default_path.getAbsolutePath();
		mTempAMSAttachFolderPath = spen_attach_path.getAbsolutePath();
		
		
		
		// Caution:
		// Do NOT load file or start animation here because we don't know canvas size here.
		// Start such SCanvasView Task at onInitialized() of SCanvasInitializeListener
		
		mSCanvas.createSCanvasView(1280, 720);
		
	}

	private void LoadDBNote() {
		// TODO Auto-generated method stub
		NotesDbAdapter mDbHelper;
		mDbHelper = new NotesDbAdapter (this);
		mDbHelper.open();
		mDbHelper.deleteAllNote();
		
		NoteList.Notes_Count = mSCanvas.getIntExtra("size-note", 0);
		for(int i=0; i < NoteList.Notes_Count; i++){
        	
        	String s1 = NotesDbAdapter.KEY_TITLE + "-" + (i+1);        	
        	String s2 = NotesDbAdapter.KEY_DATE + "-" + (i+1);
        	String s3 = NotesDbAdapter.KEY_BODY + "-" + (i+1);
        	
        	
        	String title = mSCanvas.getStringExtra(s1, "");
        	String body = mSCanvas.getStringExtra(s3, "");
        	String date = mSCanvas.getStringExtra(s2, "");
        	
        	mDbHelper.createNote(title, body, date);
        }
		
		mDbHelper.close();	
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
	protected void onDestroy() {
		super.onDestroy();
		if(mManager != null)
			mManager.removeGroup(mChannel, null);
		// Release SCanvasView resources
		if(!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");
	}

	@Override
	public void onBackPressed() {
		SPenSDKUtils.alertActivityFinish(this, "Exit");
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {	
		if(mSCanvas.isVideoViewExist()){
			mSCanvas.closeSAMMVideoView();
			updateModeState();
		}		

		if(!newConfig.locale.getLanguage().equals(currentLanguage)){			
			// Recreate SettingView to text string as locale 
			mSCanvas.recreateSettingView();
			currentLanguage = newConfig.locale.getLanguage();
		}				

		super.onConfigurationChanged(newConfig);
	}

	private OnClickListener undoNredoBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mUndoBtn)) {
				mSCanvas.undo();
			}
			else if (v.equals(mRedoBtn)) {
				mSCanvas.redo();
			}
			mUndoBtn.setEnabled(mSCanvas.isUndoable());
			mRedoBtn.setEnabled(mSCanvas.isRedoable());
		}
	};

	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int nBtnID = v.getId();
			boolean bMovingMode = mSCanvas.isMovingMode();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			/*if(nBtnID == mSelectionModeBtn.getId()){				
				if(mSCanvas.getCanvasMode() != SCanvasConstants.SCANVAS_MODE_SELECT){
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_SELECT);
					mMultiSelectionMode = false;
				}
				else{
					mMultiSelectionMode = !mMultiSelectionMode;
				}
				selectModeChange(true);
			}
			else*/ 
			if(nBtnID == mPenBtn.getId()){				
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
					mSettingviewType = SCanvasConstants.SCANVAS_SETTINGVIEW_PEN;
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);	
					selectModeChange(false);				
					updateModeState();
				}
			}
			else if(nBtnID == mEraserBtn.getId()){
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
					mSettingviewType = SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER;
				}
				else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
					selectModeChange(false);
					updateModeState();
				}
			}
			else if(nBtnID == mTextBtn.getId()){
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
					mSettingviewType = SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT;
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, false);	
					selectModeChange(false);									
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to insert Text", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	private OnLongClickListener mBtnLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {

			int nBtnID = v.getId();
			boolean bMovingMode = mSCanvas.isMovingMode();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){					
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);					
					updateModeState();
				}
				return true;
			}
			else if(nBtnID == mEraserBtn.getId()){
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
				}
				else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, true);
					updateModeState();
				}
				return true;
			}
			else if(nBtnID == mTextBtn.getId()){
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
				if(!bMovingMode && mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, true);										
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to insert Text", Toast.LENGTH_SHORT).show();
				}
				return true;
			}

			return false;
		}
	};

	// Update tool button
	private void updateModeState(){
		SPenSDKUtils.updateModeState(mSCanvas, null, null, mPenBtn, mEraserBtn, mTextBtn, null, null, null, null, null);
	}	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Check result error
		if(requestCode != REQUEST_CODE_OTHER_OPTION){
			if(resultCode!=RESULT_OK){
				return;		
			}
			if(data == null)
				return;
		}

		if(requestCode==REQUEST_CODE_FILE_SELECT){
			Bundle bundle = data.getExtras();
			if(bundle == null)
				return;
			String strFileName = bundle.getString(ToolListActivity.EXTRA_SELECTED_FILE);
			loadSAMMFile(strFileName);						
		}
		else if(requestCode==REQUEST_CODE_VOICE_RECORD){
			if(data.getBooleanExtra("VOICERECORD", false)){
				if(mSCanvas.setBGAudioAsRecordedVoice()){
					Toast.makeText(this, "Set Background audio voice recording Success!", Toast.LENGTH_SHORT).show();	
				}
				else{
					Toast.makeText(this, "Set Background audio voice recording Fail!", Toast.LENGTH_LONG).show();    				
				}
			}   
		}
		else if(requestCode==REQUEST_CODE_SET_BACKGROUND_AUDIO){
			String strBackgroundAudioFileName = data.getStringExtra("BackgroundAudioFileName");				
			if(mSCanvas.setBGAudioFile(strBackgroundAudioFileName)){
				Toast.makeText(this, "Set Background audio file("+ strBackgroundAudioFileName +") Success!", Toast.LENGTH_SHORT).show();	
			}
			else{
				Toast.makeText(this, "Set Background audio file("+ strBackgroundAudioFileName +") Fail!", Toast.LENGTH_LONG).show();    				
			}	    						 
		}
		else if(requestCode==REQUEST_CODE_ATTACH_SELECT){
			Bundle bundle = data.getExtras();
			if(bundle == null)
				return;
			String strFileName = bundle.getString(ToolListActivity.EXTRA_SELECTED_FILE);

			SDataAttachFile attachData = new SDataAttachFile();
			attachData.setFileData(strFileName, "SPen Example Selected File");
			if(mSCanvas.attachFile(attachData)){
				Toast.makeText(this, "Attach file("+ strFileName +") Success!", Toast.LENGTH_SHORT).show();	
			}
			else{
				Toast.makeText(this, "Attach file("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();    				
			}
		}
		else if(requestCode == REQUEST_CODE_INSERT_IMAGE_OBJECT) {    			
			Uri imageFileUri = data.getData();
			if(imageFileUri == null)
				return;
			String imagePath = SPenSDKUtils.getRealPathFromURI(this, imageFileUri);

			// Check Valid Image File
			if(!SPenSDKUtils.isValidImagePath(imagePath))
			{
				Toast.makeText(this, "Invalid image path or web image", Toast.LENGTH_LONG).show();	
				return;
			}

			// canvas option setting
			SOptionSCanvas canvasOption = mSCanvas.getOption();					
			if(canvasOption == null)
				return;

			if(canvasOption.mSAMMOption == null)
				return;

			canvasOption.mSAMMOption.setContentsQuality(PreferencesOfSAMMOption.getPreferenceSaveImageQuality(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);

			RectF rectF = getDefaultRect(imagePath);
			int nContentsQualityOption = canvasOption.mSAMMOption.getContentsQuality();
			SObjectImage sImageObject = new SObjectImage(nContentsQualityOption);
			sImageObject.setRect(rectF);
			sImageObject.setImagePath(imagePath);

			if(mSCanvas.insertSAMMImage(sImageObject, true)){
				Toast.makeText(this, "Insert image file("+ imagePath +") Success!", Toast.LENGTH_SHORT).show();	
			}
			else{
				Toast.makeText(this, "Insert image file("+ imagePath +") Fail!", Toast.LENGTH_LONG).show();    				
			}
		}
		else if(requestCode == REQUEST_CODE_SELECT_IMAGE_BACKGROUND) {    			
			Uri imageFileUri = data.getData();
			if(imageFileUri == null)
				return;
			String strBackgroundImagePath = SPenSDKUtils.getRealPathFromURI(this, imageFileUri);

			// Check Valid Image File
			if(!SPenSDKUtils.isValidImagePath(strBackgroundImagePath))
			{
				Toast.makeText(this, "Invalid image path or web image", Toast.LENGTH_LONG).show();	
				return;
			}

			// Set SCanvas
			if(!mSCanvas.setBGImagePath(strBackgroundImagePath)){
				Toast.makeText(mContext, "Fail to set Background Image Path.", Toast.LENGTH_LONG).show();
			}
		}
		else if(requestCode == REQUEST_CODE_SELECT_IMAGE_FOREGROUND) {    			
			Uri imageFileUri = data.getData();
			if(imageFileUri == null)
				return;
			String strBackgroundImagePath = SPenSDKUtils.getRealPathFromURI(this, imageFileUri);

			// Check Valid Image File
			if(!SPenSDKUtils.isValidImagePath(strBackgroundImagePath))
			{
				Toast.makeText(this, "Invalid image path or web image", Toast.LENGTH_LONG).show();	
				return;
			}

			Bitmap loadBitmap = SPenSDKUtils.getSafeResizingBitmap(strBackgroundImagePath, mSCanvas.getWidth(), mSCanvas.getHeight(), true);

			// canvas option setting
			SOptionSCanvas canvasOption = mSCanvas.getOption();					
			if(canvasOption == null)
				return;
			canvasOption.mSAMMOption.setConvertCanvasSizeOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasSize(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);

			// Set SCanvas
			if(!mSCanvas.setClearImageBitmap(loadBitmap)){
				Toast.makeText(mContext, "Fail to set Background Image Path.", Toast.LENGTH_LONG).show();
			}
		}
		else if(requestCode == REQUEST_CODE_SET_PAGE_MEMO) {
			String tmpStr = data.getStringExtra(ToolTextDialogInput.TEXT_DIALOG_INPUT);						
			SDataPageMemo pageMemo = new SDataPageMemo();
			pageMemo.setText(tmpStr);
			if(mSCanvas.setPageMemo(pageMemo, 0)){
				Toast.makeText(this, "Set Page Memo Success!", Toast.LENGTH_SHORT).show();	
			}
			else{
				Toast.makeText(this, "Set Page Memo Fail!", Toast.LENGTH_LONG).show();    				
			}
		}
		else if(requestCode==REQUEST_CODE_TOTAL_INFO_SHOW){
			String tmpStr = data.getStringExtra(ToolFileTotalInfoShow.EXTRA_SAMM_FILE_INFO);
			if(tmpStr != null) {
				File saveFile = new File(tmpStr);
				if(saveFile.exists())
				{
					if(!saveFile.delete())
					{
						Log.e(TAG, "Fail to delete SaveFile!");
						return;
					}
				}
			}
		}
		else if(requestCode==REQUEST_CODE_OTHER_OPTION){
			applyOtherOption();
		}
	}


	RectF getDefaultRect(String strImagePath){
		// Rect Region : Consider image real size
		BitmapFactory.Options opts = SPenSDKUtils.getBitmapSize(strImagePath);
		int nImageWidth = opts.outWidth;
		int nImageHeight = opts.outHeight;
		int nScreenWidth = mSCanvas.getWidth();
		int nScreenHeight = mSCanvas.getHeight();    			
		int nBoxRadius = (nScreenWidth>nScreenHeight) ? nScreenHeight/4 : nScreenWidth/4;
		int nCenterX = nScreenWidth/2;
		int nCenterY = nScreenHeight/2;
		if(nImageWidth > nImageHeight)
			return new RectF(nCenterX-nBoxRadius,nCenterY-(nBoxRadius*nImageHeight/nImageWidth),nCenterX+nBoxRadius,nCenterY+(nBoxRadius*nImageHeight/nImageWidth));
		else
			return new RectF(nCenterX-(nBoxRadius*nImageWidth/nImageHeight),nCenterY-nBoxRadius,nCenterX+(nBoxRadius*nImageWidth/nImageHeight),nCenterY+nBoxRadius);
	}

	RectF getVideoObjectDefaultRect(Bitmap videoThumbnail, boolean bVideoLink){
		if(videoThumbnail==null)
			return null;
		// Rect Region : Consider image real size		
		int nImageWidth = videoThumbnail.getWidth();
		int nImageHeight = videoThumbnail.getHeight();
		int nScreenWidth = mSCanvas.getWidth();
		int nScreenHeight = mSCanvas.getHeight();    			
		int nBoxRadius;
		if(bVideoLink)
			nBoxRadius = (nScreenWidth>nScreenHeight) ? nScreenHeight/8 : nScreenWidth/8;
		else
			nBoxRadius = (nScreenWidth>nScreenHeight) ? nScreenHeight/3 : nScreenWidth/3;
		int nCenterX = nScreenWidth/2;
		int nCenterY = nScreenHeight/2;
		if(nImageWidth > nImageHeight)
			return new RectF(nCenterX-nBoxRadius,nCenterY-(nBoxRadius*nImageHeight/nImageWidth),nCenterX+nBoxRadius,nCenterY+(nBoxRadius*nImageHeight/nImageWidth));
		else
			return new RectF(nCenterX-(nBoxRadius*nImageWidth/nImageHeight),nCenterY-nBoxRadius,nCenterX+(nBoxRadius*nImageWidth/nImageHeight),nCenterY+nBoxRadius);
	}

	void deleteSelectedSObject(){
		if(!mSCanvas.deleteSelectedSObject()){
			Toast.makeText(mContext, "Fail to delete object list.", Toast.LENGTH_LONG).show();
		}
	}

	void rotateSelectedObject(){
		//-------------------------------
		// layout setting
		//-------------------------------
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.alert_dialog_get_rotation_angle, null);
		TextView majorVersion = (TextView)textEntryView.findViewById(R.id.rotationangle);
		majorVersion.setText("Enter Rotation Angle here");

		// Setting 
		int curRotateAngle = 0;
		EditText rotationangle_edit = (EditText)textEntryView.findViewById(R.id.rotationangle_edit);
		if(rotationangle_edit!=null) rotationangle_edit.setText(Integer.toString(curRotateAngle));

		AlertDialog dlg = new AlertDialog.Builder(this)
		.setTitle("Set Rotation Angle")
		.setView(textEntryView)
		.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText rotationangle_edit = (EditText)textEntryView.findViewById(R.id.rotationangle_edit);

				// Update Rotation Angle
				int rotationAngle;
				try {
					rotationAngle = Integer.parseInt(rotationangle_edit.getText().toString());
				}catch(NumberFormatException e) {
					Toast.makeText(mContext, "Can not parse rotation angle.", Toast.LENGTH_LONG).show();
					return;
				}
				if(rotationAngle >= 0 && rotationAngle < 360){
					if(!mSCanvas.rotateSelectedSObject((float)rotationAngle)){
						Toast.makeText(mContext, "Fail to rotate object.", Toast.LENGTH_LONG).show();
					}
					else{
						Toast.makeText(mContext, "Object rotated.", Toast.LENGTH_SHORT).show();
					}
				}
				else{
					inputQuestion();
				}
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				/* User clicked cancel so do some stuff */
			}
		})
		.create();
		dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		dlg.show();

	}

	void copySelectedObject(){
		boolean bResetClipboard = true;
		if(!mSCanvas.copySelectedSObjectList(bResetClipboard)){
			Toast.makeText(mContext, "Fail to copy selected object list.", Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(mContext, "Object copied.", Toast.LENGTH_SHORT).show();
		}
	}

	void cutSelectedObject(){
		boolean bResetClipboard = true;
		if(!mSCanvas.cutSelectedSObjectList(bResetClipboard)){
			Toast.makeText(mContext, "Fail to cut selected object list.", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(mContext, "Object cut.", Toast.LENGTH_SHORT).show();
		}
	}

	void clearClipboardObject(){
		mSCanvas.clearClipboardSObjectList();
		Toast.makeText(mContext, "Clipboard cleared.", Toast.LENGTH_SHORT).show();
	}

	void pasteClipboardObject(int nEventPositionX, int nEventPositionY){
		boolean bSelectObject = true;
		// mapping to the matrix
		PointF mapPoint = mSCanvas.mapSCanvasPoint(new PointF(nEventPositionX, nEventPositionY)) ;
		int nMappedEventX = (int)mapPoint.x;
		int nMappedEventY =(int)mapPoint.y;
		if(!mSCanvas.pasteClipboardSObjectList(bSelectObject, nMappedEventX, nMappedEventY)){
			Toast.makeText(mContext, "Fail to paste clipboard object list.", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(mContext, "Object pasted.", Toast.LENGTH_SHORT).show();
		}
	}

	boolean loadSAMMFile(String strFileName){
		if(mSCanvas.isAnimationMode()){
			// It must be not animation mode.
		}
		else {
			// set progress dialog
			mSCanvas.setProgressDialogSetting(R.string.load_title, R.string.load_msg, ProgressDialog.STYLE_HORIZONTAL, false);

			// canvas option setting
			SOptionSCanvas canvasOption = mSCanvas.getOption();					
			if(canvasOption == null)
				return false;
			canvasOption.mSAMMOption.setConvertCanvasSizeOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasSize(mContext));
			canvasOption.mSAMMOption.setConvertCanvasHorizontalAlignOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasHAlign(mContext));
			canvasOption.mSAMMOption.setConvertCanvasVerticalAlignOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasVAlign(mContext));
			canvasOption.mSAMMOption.setDecodePriorityFGData(PreferencesOfSAMMOption.getPreferenceDecodePriorityFGData(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);					

			// show progress for loading data
			if(mSCanvas.loadSAMMFile(strFileName, true, true, true)){
				// Loading Result can be get by callback function
				LoadDBNote();
			}
			else{
				Toast.makeText(this, "Load AMS File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
	}



	boolean saveSAMMFile(String strFileName, boolean bShowSuccessLog){		
		if(mSCanvas.saveSAMMFile(strFileName)){
			if(bShowSuccessLog){
				//System.out.println("Save AMS File("+ strFileName +") Success!");
				//Toast.makeText(this, "Save AMS File("+ strFileName +") Success!", Toast.LENGTH_LONG).show();
			}
			return true;
		}
		else{
			//Toast.makeText(this, "Save AMS File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
			return false;
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu){	
		SubMenu fileMenu = menu.addSubMenu("File");
		fileMenu.add(MENU_FILE_GROUP, MENU_FILE_NEW, 1, "New");
		fileMenu.add(MENU_FILE_GROUP, MENU_FILE_LOAD, 2, "Load (SAMM file)");
		fileMenu.add(MENU_FILE_GROUP, MENU_FILE_SAVE_AS, 3, "Save (SAMM file)");

		SubMenu editMenu = menu.addSubMenu("Edit");
		editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_DELETE_OBJECT, 10, "Delete Object");
		editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_ROTATE_DEGREE, 20, "Rotate Object");
		editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_COPY, 30, "Copy Object");
		editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_CUT, 40, "Cut Object");
		editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_PASTE, 50, "Paste Object");
		if(!mbSingleSelectionFixedLayerMode){
			editMenu.add(MENU_EDIT_GROUP, MENU_EDIT_OBJECT_DEPTH_CHANGE, 90, "Object Depth Change");
		}

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		MenuItem logout = menu.findItem(R.id.logout);
		logout.setTitle(logout.getTitle()+"  "+Globals.getEmail(SamsungEditor.this));		
		return super.onCreateOptionsMenu(menu);
	} 


	@Override
	public boolean onMenuOpened(int featureId, Menu menu){
		super.onMenuOpened(featureId, menu);

		if (menu == null) 
			return true;

		MenuItem menuItemDeleteObject = menu.findItem(MENU_EDIT_DELETE_OBJECT);
		int nSelectedObjectListType = mSCanvas.getSelectedSObjectType();
		if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_NONE){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(false);			
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_STROKE){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);			
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_IMAGE){			
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);			
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_TEXT){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_FILLING){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);			
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_VIDEO){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_GROUP){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);
		}
		else if(nSelectedObjectListType==SObject.SOBJECT_LIST_TYPE_MIXED){
			if(menuItemDeleteObject!=null) menuItemDeleteObject.setEnabled(true);			
		}
		else{
			// ??
		}

		MenuItem menuItemImageRotateDegree = menu.findItem(MENU_EDIT_ROTATE_DEGREE);
		if(menuItemImageRotateDegree!=null) menuItemImageRotateDegree.setEnabled(mSCanvas.isSelectedObjectRotatable());

		boolean bSObjectSelected = mSCanvas.isSObjectSelected();

		MenuItem menuItemCopy = menu.findItem(MENU_EDIT_COPY);
		MenuItem menuItemCut = menu.findItem(MENU_EDIT_CUT);
		MenuItem menuItemPaste = menu.findItem(MENU_EDIT_PASTE);

		if(menuItemCopy!=null) menuItemCopy.setEnabled(bSObjectSelected);			
		if(menuItemCut!=null) menuItemCut.setEnabled(bSObjectSelected);
		if(menuItemPaste!=null) menuItemPaste.setEnabled(mSCanvas.isClipboardSObjectListExist());

		if(!mbSingleSelectionFixedLayerMode){
			MenuItem menuItemObjectDepthChange = menu.findItem(MENU_EDIT_OBJECT_DEPTH_CHANGE);
			if(menuItemObjectDepthChange!=null) menuItemObjectDepthChange.setEnabled(bSObjectSelected);			
		}

		// Stop video
		if(mSCanvas.isVideoViewExist()){
			mSCanvas.closeSAMMVideoView();
			updateModeState();
		}

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()) {		
		//================================================
		// File Menu
		//================================================
		case MENU_FILE_NEW:
		{
			//Confirm New
			AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
			// ad.setIcon(R.drawable.alert_dialog_icon);
			ad.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));	// Android Resource
			ad.setTitle(getResources().getString(R.string.app_name))
			.setMessage("All Data will be initialized")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();		
					mSCanvas.clearSCanvasView();
					mSCanvas.setBackgroundColor(Color.WHITE);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();		
				}
			})
			.show();
		}
		break;
		case MENU_FILE_LOAD:
		{
			Intent intent = new Intent(this, ToolListActivity.class);
			String [] exts = new String [] { "jpg", "png", "ams" }; // file extension 			
			intent.putExtra(ToolListActivity.EXTRA_LIST_PATH, mTempAMSFolderPath);
			intent.putExtra(ToolListActivity.EXTRA_FILE_EXT_ARRAY, exts);
			intent.putExtra(ToolListActivity.EXTRA_SEARCH_ONLY_SAMM_FILE, true);
			startActivityForResult(intent, REQUEST_CODE_FILE_SELECT);
		}
		break;
		case R.id.comments:
		{
			com.example.note.NoteList.mSCanvas = this.mSCanvas;
			Intent intent = new Intent(this, com.example.note.NoteList.class);
			startActivity(intent);
		}
		break;
		case R.id.dropbox_sync:
		{
			//-------------------------------
			// layout setting
			//-------------------------------
			//check canvas drawing empty
			if(mSCanvas.isCanvasDrawingEmpty(true)){
				Toast.makeText(mContext, "There is no Canvas Drawing", Toast.LENGTH_LONG).show();
				return true;
			}

			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_get_text, null);
			TextView textTitle = (TextView)textEntryView.findViewById(R.id.textTitle);
			textTitle.setText("Enter filename to save (default: *.png)");
			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Save As")
			.setView(textEntryView)
			.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText et = (EditText)textEntryView.findViewById(R.id.text);
					String strFileName = et.getText().toString();

					// check file name length, invalid characters, overwrite, extension, etc.
					if(strFileName==null)
						return; 

					if(strFileName.length()<=0){
						Toast.makeText(mContext, "Enter file name to save", Toast.LENGTH_LONG).show();
						return;
					}
					if(!SPenSDKUtils.isValidSaveName(strFileName)) {						
						Toast.makeText(mContext, "Invalid character to save! Save file name : "+ strFileName, Toast.LENGTH_LONG).show();
						return;
					}

					int nExtIndex = strFileName.lastIndexOf(".");	
					if(nExtIndex==-1)	
						strFileName += DEFAULT_FILE_EXT;
					else{
						String strExt = strFileName.substring(nExtIndex + 1);
						if(strExt==null)
							strFileName += DEFAULT_FILE_EXT;
						else{
							if(strExt.compareToIgnoreCase("png")!=0 && strExt.compareToIgnoreCase("jpg")!=0){
								strFileName += DEFAULT_FILE_EXT;
							}							
						}							
					}				

					String saveFileName = mTempAMSFolderPath + "/" + strFileName;
					checkSameSaveFileName(saveFileName);	


					showProgress(true);
					DropboxSyncTask mTask = new DropboxSyncTask();
					mTask.file_path = saveFileName;
					mTask.file_name = strFileName;
					mTask.execute((Void) null);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			dlg.show();



		}
		break;

		case R.id.action_load:
		{
			getDocumentsSyncTask mTask = new getDocumentsSyncTask();
			mTask.execute((Void) null);
		}
		break;

		case MENU_FILE_SAVE_AS:
		{
			//-------------------------------
			// layout setting
			//-------------------------------
			//check canvas drawing empty
			if(mSCanvas.isCanvasDrawingEmpty(true)){
				Toast.makeText(mContext, "There is no Canvas Drawing", Toast.LENGTH_LONG).show();
				return true;
			}

			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_get_text, null);
			TextView textTitle = (TextView)textEntryView.findViewById(R.id.textTitle);
			textTitle.setText("Enter filename to save (default: *.png)");
			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Save As")
			.setView(textEntryView)
			.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText et = (EditText)textEntryView.findViewById(R.id.text);
					String strFileName = et.getText().toString();

					// check file name length, invalid characters, overwrite, extension, etc.
					if(strFileName==null)
						return; 

					if(strFileName.length()<=0){
						Toast.makeText(mContext, "Enter file name to save", Toast.LENGTH_LONG).show();
						return;
					}
					if(!SPenSDKUtils.isValidSaveName(strFileName)) {						
						Toast.makeText(mContext, "Invalid character to save! Save file name : "+ strFileName, Toast.LENGTH_LONG).show();
						return;
					}

					int nExtIndex = strFileName.lastIndexOf(".");	
					if(nExtIndex==-1)	
						strFileName += DEFAULT_FILE_EXT;
					else{
						String strExt = strFileName.substring(nExtIndex + 1);
						if(strExt==null)
							strFileName += DEFAULT_FILE_EXT;
						else{
							if(strExt.compareToIgnoreCase("png")!=0 && strExt.compareToIgnoreCase("jpg")!=0){
								strFileName += DEFAULT_FILE_EXT;
							}							
						}							
					}				

					String saveFileName = mTempAMSFolderPath + "/" + strFileName;
					checkSameSaveFileName(saveFileName);	
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			dlg.show();
		}
		break;		

		//================================================
		// Edit Menu
		//================================================
		// Delete Object
		case MENU_EDIT_DELETE_OBJECT:
		{
			deleteSelectedSObject();
		}
		break;		
		// stroke, text, image, filling object can be rotated
		case MENU_EDIT_ROTATE_DEGREE:
		{
			rotateSelectedObject();
		}
		break;	
		// Copy 
		case MENU_EDIT_COPY:
		{	
			copySelectedObject();
		}
		break;
		// Cut 
		case MENU_EDIT_CUT:
		{		
			cutSelectedObject();
		}
		break;
		// Paste
		case MENU_EDIT_PASTE:
		{		
			pasteClipboardObject(-1, -1);			
		}
		break;	

		case MENU_EDIT_OBJECT_DEPTH_CHANGE:
		{
			applyDepthChange();
		}
		break;	

		case R.id.logout:
		{
			Globals.setEmail(null, SamsungEditor.this);
			Globals.setPass(null, SamsungEditor.this);
			Globals.setToken(null, SamsungEditor.this);
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
					Toast.makeText(SamsungEditor.this, "Scanning...", Toast.LENGTH_LONG).show();
				}

				@Override
				public void onFailure(int reasonCode) {
					Toast.makeText(SamsungEditor.this, "Scanning failed", Toast.LENGTH_LONG).show();
				}
			});
			showPeersDialog(new WifiP2pDeviceList());

		}
		break;
		}
		return true;

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
				wif.wps.setup = WpsInfo.PBC;
				wif.groupOwnerIntent = 15;
				dispositivo.put("device_config",wif);
				pares.add(dispositivo);

			}
			lista.update(pares);

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
			dispositivo.put("device_config",wif);
			pares.add(dispositivo);
		}

		if(lista==null)
		{	
			lista = new DevicesDialog(SamsungEditor.this,last_device_update);

			lista.setUp(pares);
			lista.show();
		}
	}

	private void checkSameSaveFileName(final String saveFileName) {	

		File fSaveFile = new File(saveFileName);
		if(fSaveFile.exists())
		{
			AlertDialog dlg = new AlertDialog.Builder(this)
			.setTitle("Same file name exists! Overwrite?")		
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {				
					// canvas option setting
					SOptionSCanvas canvasOption = new SOptionSCanvas();
					// medium size : to reduce saving time 
					canvasOption.mSAMMOption.setSaveImageSize(PreferencesOfSAMMOption.getPreferenceSaveImageSize(mContext));
					// canvasOption.mSaveOption.setSaveImageSize(SOptionSAMM.SAMM_SAVE_OPTION_MEDIUM_SIZE);
					// valid only to save jpg
					// canvasOption.mSAMMOption.setJPGImageQuality(100);
					// Cropping option 
					canvasOption.mSAMMOption.setSaveImageLeftCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageHorizontalCrop(mContext));
					canvasOption.mSAMMOption.setSaveImageRightCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageHorizontalCrop(mContext));
					canvasOption.mSAMMOption.setSaveImageTopCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageVerticalCrop(mContext));
					canvasOption.mSAMMOption.setSaveImageBottomCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageVerticalCrop(mContext));
					canvasOption.mSAMMOption.setSaveContentsCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveContentsCrop(mContext));
					// content quality minimum 
					canvasOption.mSAMMOption.setContentsQuality(PreferencesOfSAMMOption.getPreferenceSaveImageQuality(mContext));
					// canvasOption.mSAMMOption.setContentsQuality(SOptionSAMM.SAMM_CONTENTS_QUALITY_MINIMUM);
					// with background(image, color) set
					canvasOption.mSAMMOption.setSaveOnlyForegroundImage(PreferencesOfSAMMOption.getPreferenceSaveOnlyForegroundImage(mContext));
					// canvasOption.mSAMMOption.setSaveOnlyForegroundImage(false);	// with background(image, color) set 
					// canvasOption.mSAMMOption.setSaveOnlyForegroundImage(true);	// no background
					// Create new image file to save
					canvasOption.mSAMMOption.setCreateNewImageFile(PreferencesOfSAMMOption.getPreferenceSaveCreateNewImageFile(mContext));
					canvasOption.mSAMMOption.setEncodeForegroundImage(PreferencesOfSAMMOption.getPreferenceEncodeForegroundImageFile(mContext));
					canvasOption.mSAMMOption.setEncodeThumbnailImage(PreferencesOfSAMMOption.getPreferenceEncodeThumbnailImageFile(mContext));
					canvasOption.mSAMMOption.setEncodeObjectData(PreferencesOfSAMMOption.getPreferenceEncodeObjectDataFile(mContext));
					canvasOption.mSAMMOption.setEncodeVideoFileDataOption(PreferencesOfSAMMOption.getPreferenceEncodeVideoFileData(mContext));
					// option setting
					mSCanvas.setOption(canvasOption);					
					saveSAMMFile(saveFileName, true);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked cancel so do some stuff */
				}
			})
			.create();
			dlg.show();
		}
		else {
			// canvas option setting
			SOptionSCanvas canvasOption = new SOptionSCanvas();
			// Cropping option 
			canvasOption.mSAMMOption.setSaveImageLeftCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageHorizontalCrop(mContext));
			canvasOption.mSAMMOption.setSaveImageRightCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageHorizontalCrop(mContext));
			canvasOption.mSAMMOption.setSaveImageTopCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageVerticalCrop(mContext));
			canvasOption.mSAMMOption.setSaveImageBottomCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveImageVerticalCrop(mContext));
			canvasOption.mSAMMOption.setSaveContentsCroppingOption(PreferencesOfSAMMOption.getPreferenceSaveContentsCrop(mContext));
			// medium size : to reduce saving time 
			canvasOption.mSAMMOption.setSaveImageSize(PreferencesOfSAMMOption.getPreferenceSaveImageSize(mContext));
			// canvasOption.mSAMMOption.setSaveImageSize(SOptionSAMM.SAMM_SAVE_OPTION_MEDIUM_SIZE);
			// valid only to save jpg
			// canvasOption.mSAMMOption.setJPGImageQuality(100);
			// content quality minimum 
			canvasOption.mSAMMOption.setContentsQuality(PreferencesOfSAMMOption.getPreferenceSaveImageQuality(mContext));
			// canvasOption.mSAMMOption.setContentsQuality(SOptionSAMM.SAMM_CONTENTS_QUALITY_MINIMUM);
			// save with background setting
			canvasOption.mSAMMOption.setSaveOnlyForegroundImage(PreferencesOfSAMMOption.getPreferenceSaveOnlyForegroundImage(mContext));	// with background(image, color) set
			// canvasOption.mSAMMOption.setSaveOnlyForegroundImage(false);	// with background(image, color) set 
			// canvasOption.mSAMMOption.setSaveOnlyForegroundImage(true);	// no background
			canvasOption.mSAMMOption.setCreateNewImageFile(PreferencesOfSAMMOption.getPreferenceSaveCreateNewImageFile(mContext));	// with background(image, color) set
			canvasOption.mSAMMOption.setEncodeForegroundImage(PreferencesOfSAMMOption.getPreferenceEncodeForegroundImageFile(mContext));
			canvasOption.mSAMMOption.setEncodeThumbnailImage(PreferencesOfSAMMOption.getPreferenceEncodeThumbnailImageFile(mContext));
			canvasOption.mSAMMOption.setEncodeObjectData(PreferencesOfSAMMOption.getPreferenceEncodeObjectDataFile(mContext));
			canvasOption.mSAMMOption.setDecodePriorityFGData(PreferencesOfSAMMOption.getPreferenceDecodePriorityFGData(mContext));
			canvasOption.mSAMMOption.setEncodeVideoFileDataOption(PreferencesOfSAMMOption.getPreferenceEncodeVideoFileData(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);					
			saveSAMMFile(saveFileName, true);			
		}
	}

	// Side Button during hover
	private void doHoverButtonUp(int nEventPositionX, int nEventPositionY){

		//--------------------------------------------------------------
		// Close setting view if the setting view is visible
		//--------------------------------------------------------------
		if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN)){
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
			return;
		}
		else if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER)){
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
			return;
		}
		else if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT)){
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, false);
			return;
		}
		else if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING)){
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, false);
			return;
		}


		//--------------------------------------------------------------
		// Show popup menu if the object is selected or if sobject exist in clipboard
		//--------------------------------------------------------------
		if(mSCanvas.isSObjectSelected() || mSCanvas.isClipboardSObjectListExist()){
			showObjectPopUpMenu(nEventPositionX, nEventPositionY);
			return;
		}

		//--------------------------------------------------------------
		// Show Setting view
		//--------------------------------------------------------------
		if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
			mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);			
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);
		}
		else if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
			mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, true);
		}
		else if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
			mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);			
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, true);
		}
		else if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING){
			mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI);			
			mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, true);
		}
	}


	private void showObjectPopUpMenu(int nEventPositionX, int nEventPositionY){

		int nSelectObjectType = mSCanvas.getSelectedSObjectType();
		final boolean bClipboardObjectExist = mSCanvas.isClipboardSObjectListExist();
		int nMenuArray;
		final int xPos = nEventPositionX;
		final int yPos = nEventPositionY;

		if(nSelectObjectType==SObject.SOBJECT_LIST_TYPE_IMAGE){

			if(bClipboardObjectExist) nMenuArray = R.array.popup_menu_image_with_paste;
			else nMenuArray = R.array.popup_menu_image;

			new AlertDialog.Builder(this)
			.setTitle("Select Image Menu")
			.setItems(nMenuArray, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Delete Image
					if(which==0){
						deleteSelectedSObject();
					}
					// Rotate Image
					else if(which==1){
						rotateSelectedObject();
					}
					// Copy Image
					else if(which==2){
						copySelectedObject();
					}
					// Cut Image
					else if(which==3){
						cutSelectedObject();
					}
					// Paste object in clipboard
					else if(which==4){
						pasteClipboardObject(xPos, yPos);
					}
					// Clear object in clipboard
					else if(which==5){
						clearClipboardObject();
					}
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		}
		else if(nSelectObjectType==SObject.SOBJECT_LIST_TYPE_TEXT){

			if(bClipboardObjectExist) nMenuArray = R.array.popup_menu_text_with_paste;
			else nMenuArray = R.array.popup_menu_text;

			new AlertDialog.Builder(this)
			.setTitle("Select Text Menu")
			.setItems(nMenuArray, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Delete Text
					if(which==0){
						deleteSelectedSObject();
					}
					// Copy Text
					else if(which==1){
						copySelectedObject();
					}
					// Cut Text
					else if(which==2){
						cutSelectedObject();
					}
					// Paste object in clipboard
					else if(which==3){
						pasteClipboardObject(xPos, yPos);
					}
					// Clear object in clipboard
					else if(which==4){
						clearClipboardObject();
					}
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		}
		else {
			if(bClipboardObjectExist){
				nMenuArray = R.array.popup_menu_else_with_paste;

				new AlertDialog.Builder(this)
				.setTitle("Select Pop-Up Menu")
				.setItems(nMenuArray, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Paste object in clipboard
						if(which==0){
							pasteClipboardObject(xPos, yPos);
						}
						// Clear object in clipboard
						else if(which==1){
							clearClipboardObject();
						}
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show();
			}
		}
	}

	private void inputQuestion(){
		new AlertDialog.Builder(this)
		.setTitle("Warning!!")
		.setMessage("Input Correct Rotation Angle(0~359)")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		})

		.show();	
	}

	private void applyDepthChange(){
		new AlertDialog.Builder(this)
		.setTitle("Depth Change Operaction")
		.setItems(R.array.object_depth_change_operation, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				switch (which) {
				case OBJECT_DEPTH_CHANGE_FORWARD:			
					mSCanvas.bringObjectsForward();
					break;
				case OBJECT_DEPTH_CHANGE_BACKWARD:			
					mSCanvas.sendObjectsBackward();
					break;
				case OBJECT_DEPTH_CHANGE_FRONT:				
					mSCanvas.bringObjectsFront();
					break;
				case OBJECT_DEPTH_CHANGE_BACK:			
					mSCanvas.sendObjectsBack();
					break;				
				}				
				dialog.dismiss();
			}
		})
		.show();
	}


	private void applyOtherOption(){
		int hoverPointerShowOption = PreferencesOfOtherOption.getPreferenceHoverPointerShowOption(mContext);
		switch(hoverPointerShowOption){
		case HOVER_SHOW_ALWAYS_ONHOVER:
			mSCanvas.setSCanvasHoverPointerShowOption(SCanvasConstants.SCANVAS_HOVERPOINTER_SHOW_OPTION_ALWAYS_ON_HOVER);
			break;
		case HOVER_SHOW_ONCE_ONHOVER:
			mSCanvas.setSCanvasHoverPointerShowOption(SCanvasConstants.SCANVAS_HOVERPOINTER_SHOW_OPTION_ONCE_ON_HOVER);
			break;	
		}		

		int hoverPointerStyle = PreferencesOfOtherOption.getPreferenceHoverPointerStyle(mContext);
		switch (hoverPointerStyle) {
		case HOVER_POINTER_DEFAULT:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_NONE);
			break;
		case HOVER_POINTER_SIMPLE_ICON:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SIMPLE_CUSTOM);
			mSCanvas.setSCanvasHoverPointerSimpleIcon(SPenEventLibrary.HOVERING_SPENICON_MOVE);			
			break;
		case HOVER_POINTER_SIMPLE_DRAWABLE:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SIMPLE_CUSTOM);
			mSCanvas.setSCanvasHoverPointerSimpleDrawable(getResources().getDrawable(R.drawable.tool_ic_pen));
			break;
		case HOVER_POINTER_SPEN:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SPENSDK);
			break;
		case HOVER_POINTER_SNOTE:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SNOTE);
			break;				
		}

		mSideButtonStyle = PreferencesOfOtherOption.getPreferencePenSideButtonStyle(mContext);			
		mSCanvas.setKeyboardPredictiveTextDisable(!PreferencesOfOtherOption.getPreferencePredictiveText(mContext));		
		mSCanvas.setSettingViewPinUpState(PreferencesOfOtherOption.getPreferenceSettingviewPinup(mContext));
		mSCanvas.setFingerControlPenDrawing(true);
		mSCanvas.setStrokeLongClickSelectOption(false);
		mSCanvas.setTextLongClickSelectOption(false);
		mSCanvas.setEnableHoverScroll(PreferencesOfOtherOption.getPreferenceHoverScroll(mContext));
		mSCanvas.maintainScaleOnResize(PreferencesOfOtherOption.getPreferenceMaintainScaleOnResize(mContext));
		mSCanvas.maintainSettingPenColor(PreferencesOfOtherOption.getPreferenceMaintainPenColor(mContext));
		mSCanvas.supportBeautifyStrokeSetting(PreferencesOfOtherOption.getPreferenceSupportBeautifyStrokeSetting(mContext));
		mSCanvas.setEnableBoundaryTouchScroll(PreferencesOfOtherOption.getPreferenceBoundaryTouchScroll(mContext));
		mSCanvas.setEnableMultiTouch(mMultitouch);
		mSCanvas.setMultiTouchCancel(false);
	}


	private void selectModeChange(boolean updateMode){
		mSCanvas.showSettingView(mSettingviewType, false);
		if(updateMode){
			mSCanvas.setMultiSelectionMode(mMultiSelectionMode);
		}
		else{
			mMultiSelectionMode = false;
		}
		updateSelectButton();
	}

	private void updateSelectButton(){
		/*if(mMultiSelectionMode){
			mSelectionModeBtn.setImageResource(R.drawable.selector_multiselect);
		}
		else{
			mSelectionModeBtn.setImageResource(R.drawable.selector_singleselect);
		}*/
	}


	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			/*
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			
			});
			
			mSCanvas.setVisibility(View.VISIBLE);
			mSCanvas.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mSCanvas.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
			*/
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mSCanvas.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public class DropboxSyncTask extends AsyncTask<Void, Void, Boolean> {
		
		ProgressDialog progressDialog;
		public String file_path = "";
		public String file_name = "";

		@Override
		protected Boolean doInBackground(Void... params) {

			//String file_path = Environment.getExternalStorageDirectory().toString() + "/SPenSDK/iic.png";
			String url = Globals.getBaseUrl(SamsungEditor.this) +"/api/v1/upload";

			JSONParser jParser = new JSONParser();

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("token", LoginActivity.CURRENT_TOKEN));
			nameValuePairs.add(new BasicNameValuePair("id", WorkSpacesActivity.CURRENT_PROJECT_ID));
			nameValuePairs.add(new BasicNameValuePair("name", file_name));

			// getting JSON string from URL
			JSONObject json = jParser.postFileJSONFromURL(url, file_path, nameValuePairs);
			if(json != null)
			{
				if(json.has("link"))
					return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			hideProgressDialog();
			if(success)
			{
				Toast.makeText(SamsungEditor.this, "Success Upload Image", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(SamsungEditor.this, "Fail Upload Image", Toast.LENGTH_SHORT).show();
			}
			//showProgress(false);
		}
		
		@Override
		protected void onPreExecute()
		{
			showProgressDialog();
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(SamsungEditor.this, "Cancel Upload Image", Toast.LENGTH_SHORT).show();
			hideProgressDialog();
			//showProgress(false);
		}
		
		public void showProgressDialog()
		{
			progressDialog = ProgressDialog.show(SamsungEditor.this, "Uploading...", "Please wait");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		
		public void hideProgressDialog()
		{
			if(progressDialog != null)
			{
				progressDialog.cancel();
				progressDialog.dismiss();
			}
		}
	}

	public class getDocumentsSyncTask extends AsyncTask<Void, Void, Boolean> {

		ArrayList <Map <String,String>> listItems = new ArrayList<Map<String,String>>();
		Dialog lista;
		ListView docs;
		ProgressBar wheel;

		@Override
		protected Boolean doInBackground(Void... params) {
			String url = Globals.getBaseUrl(SamsungEditor.this) +"/api/v1/getDocuments?project_id="+WorkSpacesActivity.CURRENT_PROJECT_ID+"&token="+LoginActivity.CURRENT_TOKEN;

			//Controlar tokend e usuario

			//if(token existe)
			//	if (usuario que tiene el token tiene el proyecto de project_id entre sus proyectos)
			//		trabajar
			//	else
			//		no tiene acceso a las imagenes de este proyecto
			//else
			//	token no existe


			// Creating JSON Parser instance
			JSONParser jParser = new JSONParser();

			// getting JSON string from URL
			JSONArray json = jParser.getJSONFromUrl(url);
			//try{
			//	Log.d("Webservice",json.toString(1));
			//}catch(Exception e){}

			if(json == null){
				return false;
			}

			try {
				for(int i=0;i<json.length();i++){
					JSONObject json_data = json.getJSONObject(i);
					Map<String,String> data = new HashMap<String, String>();
					String url_to_download = json_data.getString("url_path");
					String description = json_data.getString("description");
					String name = json_data.getString("name");

					data.put("url", url_to_download);
					data.put("description", description);
					data.put("name",name);

					listItems.add(data);

				}



			} catch (JSONException e) {
				
				return false;
			}

			if(listItems.size() == 0){
				//listItems.add("Ud no tiene un proyecto asociado");
			}


			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			Toast.makeText(SamsungEditor.this, "Success", Toast.LENGTH_SHORT).show();

			String[] from = {"name","url"};
			int[] to = {android.R.id.text1, android.R.id.text2};
			
			
			if(listItems.isEmpty()){
				Map<String,String> mapa = new HashMap<String, String>();
				mapa.put("name",getResources().getString(R.string.no_documents));
				listItems.add(mapa);
			}
			
			SimpleAdapter adapter = new SimpleAdapter(SamsungEditor.this, listItems, android.R.layout.simple_list_item_2, from, to);
			
			docs.setAdapter(adapter);
			docs.invalidate();
			
			docs.setOnItemClickListener(new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    	TextView tname = (TextView)view.findViewById(android.R.id.text1);
			    	TextView turl = (TextView)view.findViewById(android.R.id.text2);
			    	
			    	new downloadSAMMFile().execute(turl.getText().toString(), tname.getText().toString());
			    	lista.dismiss();
			    }
			  });
			

			wheel.setVisibility(View.GONE);

		}

		@Override
		protected void onPreExecute(){
			lista = new Dialog(SamsungEditor.this);
			documentsListDialog = lista;
			AlertDialog.Builder builder = new AlertDialog.Builder(SamsungEditor.this);
			LinearLayout dialog_layout = new LinearLayout(SamsungEditor.this);

			docs = new ListView(SamsungEditor.this);

			docs.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			dialog_layout.setOrientation(LinearLayout.VERTICAL);
			dialog_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			wheel = new ProgressBar(SamsungEditor.this);
			wheel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			wheel.setVisibility(View.VISIBLE);

			dialog_layout.addView(wheel);
			dialog_layout.addView(docs);
			
			builder.setTitle("Files saved on project");
			
			builder.setView(dialog_layout);

			lista = builder.create();
			SamsungEditor.this.runOnUiThread(new Runnable() {
			    public void run() {
			    	lista.setCanceledOnTouchOutside(true);
			    	lista.show();
			    }
			});
			
			docs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				  @Override
				  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				    Object o = docs.getItemAtPosition(position);
				    System.out.println("CLICK");
				    /* write you handling code like...
				    String st = "sdcard/";
				    File f = new File(st+o.toString());
				    // do whatever u want to do with 'f' File object
				    */  
				  }
				});
		}
	}

	public class downloadSAMMFile extends AsyncTask<String,Integer,Boolean>
	{

		String downloadedFileString=null;
		
		@Override
		protected Boolean doInBackground(String... params) {
			String path = "";
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                String strFileName = params[1];
               
                //GET URL FIRST
                HttpGet request_get = new HttpGet(params[0]);
                HttpParams httpParameters = new BasicHttpParams(); 
                HttpClientParams.setRedirecting(httpParameters, false); 
                HttpClient client = new DefaultHttpClient(httpParameters);
                HttpResponse response = client.execute(request_get);
                
                String real_url = response.getHeaders("Location")[0].getValue().replace("www.dropbox.com", "dl.dropboxusercontent.com");
                
                URL url = new URL(real_url);
                
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report 
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                     return false;

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                
                path = mTempAMSFolderPath + "/" + strFileName;
                downloadedFileString = path;
                
                output = new FileOutputStream(path);

                byte data[] = new byte[1];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled())
                    {
                        return false;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
            	Log.d("ws","error",e);
            	return false;
            } 
            
            finally
            {
		        try {
		            if (output != null)
		                output.close();
		            if (input != null)
		                input.close();
		        } 
		        catch (IOException ignored) { Log.d("ws","error",ignored); }
            }

            if (connection != null)
                connection.disconnect();
            
            Log.d("ws","Dowload success to "+path );
            return true;
		
		}
		
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        setupProgressDialog(this);
	        mProgressDialog.show();
	    }
		
		@Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        // if we get here, length is known, now set indeterminate to false
	        mProgressDialog.setIndeterminate(false);
	        mProgressDialog.setMax(100);
	        mProgressDialog.setProgress(progress[0]);
	    }
		
		@Override
	    protected void onPostExecute(Boolean result) {
	        mProgressDialog.dismiss();
	        
	        if(result) 
	        {
	        	SamsungEditor.this.loadSAMMFile(downloadedFileString);
	        	if(documentsListDialog != null )
	        		documentsListDialog.dismiss();
	        }
	    }
		
		public void setupProgressDialog(downloadSAMMFile task){
			
			currentdownloadTask = task;

			// instantiate it within the onCreate method
			mProgressDialog = new ProgressDialog(SamsungEditor.this);
			mProgressDialog.setMessage("Download in progress...");
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(true);

			mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			    @Override
			    public void onCancel(DialogInterface dialog) {
			    	if(currentdownloadTask != null)
			    		currentdownloadTask.cancel(true);
			    }
			});
		}
		
		
	}
	

	public void connectionStarted() {
		//Toast.makeText(this, "Connection Started", Toast.LENGTH_SHORT).show();
		mManager.requestConnectionInfo(mChannel,new ConnectionInfoListener() {

			@Override
			public void onConnectionInfoAvailable(WifiP2pInfo info) {
				Log.d("test",info.toString());
				if(info.groupFormed && info.isGroupOwner)
				{
					Intent serverIntent = new Intent(SamsungEditor.this, SyncService.class);
					serverIntent.setAction(SyncService.ACTION_START_SERVER);
					serverIntent.putExtra(SyncService.EXTRAS_RECEIVER, syncObjectReceiver);

					startService(serverIntent);
					
					if(devices_progress != null)
					{
						devices_progress.dismiss();
						devices_progress = null;
					}
					
					isServer = true;
					
					myTimer = new Timer();
					myTimer.schedule(new TimerTask() {			
						@Override
						public void run() {
							//TimerMethod();
						}

					}, 0, 1500);

				}
				else if (info.groupFormed)
				{
					String owner_ip = info.groupOwnerAddress.getHostAddress();
					int owner_port = 8988;
					
					Intent serverIntent = new Intent(SamsungEditor.this, SyncService.class);
					serverIntent.setAction(SyncService.ACTION_CONNECT_TO_OWNER);
					serverIntent.putExtra(SyncService.EXTRAS_GROUP_OWNER_ADDRESS, owner_ip);
					serverIntent.putExtra(SyncService.EXTRAS_GROUP_OWNER_PORT, owner_port);
					serverIntent.putExtra(SyncService.EXTRAS_RECEIVER, syncObjectReceiver);

					startService(serverIntent);
					
					if(devices_progress != null)
					{
						devices_progress.dismiss();
						devices_progress = null;
					}
					
					isServer = false;
				}
			}
		});
	}

	public void connectionEnded() {
		try
		{
			Intent closeIntent = new Intent(SamsungEditor.this, SyncService.class);
			closeIntent.setAction(SyncService.ACTION_CLOSE_ALL);
			startService(closeIntent);
			myTimer.cancel();
			myTimer=null;
		}
		catch(Exception e)
		{

		}
	}

	private void TimerMethod()
	{
		Intent clientIntent = new Intent(SamsungEditor.this, SyncService.class);
		clientIntent.setAction(SyncService.ACTION_SEND_OBJECT);

		try
		{
			Bitmap bmp = mSCanvas.getBitmap(false);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 0, stream);
			byte[] byteBitmap = stream.toByteArray();

			clientIntent.putExtra(SyncService.EXTRAS_IMAGE_TO_SEND, byteBitmap);
			startService(clientIntent);
		}
		catch(Exception e)
		{
			Log.d("test","error",e);
		}
	}

	protected void trySendLastUpdate(SObject object, int action) {

		SyncObjectStroke obj_stroke = null;

		if(object instanceof SObjectText){
			obj_stroke = new SyncObjectStroke((SObjectText)object, action);
		}
		else if(object instanceof SObjectStroke){
			obj_stroke = new SyncObjectStroke((SObjectStroke)object, action);
		}
		else{
			return;
		}


		//Try Send String

		Intent clientIntent = new Intent(SamsungEditor.this, SyncService.class);
		clientIntent.setAction(SyncService.ACTION_SEND_OBJECT);
		clientIntent.putExtra(SyncService.EXTRAS_OBJECT_TO_SEND, obj_stroke);

		startService(clientIntent);
	}

	protected void updateWithNewObject(SyncObjectStroke object)
	{
		System.out.println("action: " + SyncObjectStroke.ACTION_CLEAR_ALL);
		if(object.action == SyncObjectStroke.ACTION_CLEAR_ALL){
			isExternalClearAll = true;
			mSCanvas.clearAll(false);
		}
		else if(object.type == SyncObjectStroke.TYPE_TEXT){
			SObjectText obj = object.getSObjectText();

			if(object.action == SyncObjectStroke.ACTION_INSERTED){
				mSCanvas.insertSAMMObject(obj, false);
			}
			else if(object.action == SyncObjectStroke.ACTION_DELETED){

				SObjectText obj2 = (SObjectText)dictionary.Get(obj);
				mSCanvas.deleteSAMMObject(obj2);
			}
			else if(object.action == SyncObjectStroke.ACTION_CHANGED){

				int ind = dictionary.GetI(obj, mSCanvas);

				if(ind != -1){
					mSCanvas.changeSAMMObject(ind, obj);
				}
			}			
		}
		else if(object.type == SyncObjectStroke.TYPE_STROKE){
			SObjectStroke obj = object.getSObjectStroke();

			if(object.action == SyncObjectStroke.ACTION_INSERTED){
				dictionary.Add(obj);
				mSCanvas.insertSAMMObject(obj, true);
			}
			else if(object.action == SyncObjectStroke.ACTION_DELETED){

				SObjectStroke obj2 = (SObjectStroke)dictionary.Get(obj);
				mSCanvas.deleteSAMMObject(obj2);
			}
			else if(object.action == SyncObjectStroke.ACTION_CHANGED){

				int ind = dictionary.GetI(obj, mSCanvas);

				if(ind != -1){
					mSCanvas.changeSAMMObject(ind, obj);
				}
			}
		}
	}
	
	public void hideDialog()
	{
		
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		if(resultCode == 100)
		{
			try{
				SyncObjectStroke s = (SyncObjectStroke) resultData.getSerializable(SyncService.EXTRAS_OBJECT_RECEIVED);
				
				if(isTouchPenDown){
					queue_sobject.add(s);
				}
				else{
					updateWithNewObject(s);
				}
				
			}
			catch(Exception e){
			}
		}
	}

	@Override
	public DevicesDialog getDevicesDialog() {
		return lista;
	}

	@Override
	public void setDevicesDialog(DevicesDialog dialog) {
		lista=dialog;
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
		
		
	}
}
