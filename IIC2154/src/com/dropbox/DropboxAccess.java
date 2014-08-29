package com.dropbox;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

public class DropboxAccess extends Activity {

    private static final String appKey = "p78hpiyefxd4p9z";
    private static final String appSecret = "hwjzoa1qoxjoqft";

    private static final int REQUEST_LINK_TO_DBX = 0;

    private DbxAccountManager mDbxAcctMgr;

    // in applicationContext use getApplicationContext()
    public void Connect(Context applicationContext){
    	// Linking accounts
        mDbxAcctMgr = DbxAccountManager.getInstance(applicationContext, appKey, appSecret);
    }
    
    public boolean hasLinkedAccount(){
    	return mDbxAcctMgr.hasLinkedAccount();
    }

    public void LinkToDropbox(Activity currentActivity) {
        mDbxAcctMgr.startLink(currentActivity, REQUEST_LINK_TO_DBX);
    }

    // String directory_path = Environment.getExternalStorageDirectory().toString() + "/SPenSDK";
    // String file_name_to_copy = "prueba.png";
    public boolean createNewFile(String directory_path, String file_name_to_copy, String new_file_name) {
        try {
        	// Access to External Storage
            File myFile = new File(directory_path + "/" + file_name_to_copy);
        	
            // Create DbxFileSystem for synchronized file access.
            DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());

            // Crea una ruta de dropbox para el nuevo archivo
            DbxPath newFilePath = new DbxPath(DbxPath.ROOT, new_file_name);
            
            // Create a test file only if it doesn't already exist.
            if (!dbxFs.exists(newFilePath)) {
                DbxFile newFile = dbxFs.create(newFilePath);
                
                try {
                	newFile.writeFromExistingFile(myFile, false);
                } finally {
                	newFile.close();
                }
            }
            else{
            	return false;
            }
            
        } catch (IOException e) {
            return false;
        }
        
        return true;
    }
    
    public boolean existsFile(String file_name){
    	
    	try {
            // Create DbxFileSystem for synchronized file access.
            DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());

            // Crea una ruta de dropbox para el archivo
            DbxPath newFilePath = new DbxPath(DbxPath.ROOT, file_name);
            
            if (dbxFs.exists(newFilePath)) {
                return true;
            }
            
        } catch (Exception e) {
        }
    	
    	return false;
    }
}
