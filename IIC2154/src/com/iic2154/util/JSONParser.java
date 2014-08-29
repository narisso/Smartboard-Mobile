package com.iic2154.util;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.content.Context;
import android.media.audiofx.BassBoost.Settings;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
 
public class JSONParser {
 
    static InputStream is = null;
    static JSONObject jObj = null;
    static JSONArray jArr = null;
    static String json = "";
 
    // constructor
    public JSONParser() {
 
    }
 
    public JSONArray getJSONFromUrl(String url) {
    	
    	StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        
        try {
          HttpResponse response = client.execute(httpGet);
          StatusLine statusLine = response.getStatusLine();
          int statusCode = statusLine.getStatusCode();
          if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
              builder.append(line);
            }
            
            json = builder.toString();
            
            System.out.println(json);
          } else {
        	  return null;
          }
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        } 
        
        // try parse the string to a JSON object
        try {
        	jArr = new JSONArray(json);
            //jObj = new JSONObject(json);
        } catch (JSONException e) {
            // error parsing data
        	System.out.println("no json");
        	return null;
        }
 
        // return JSON String
        return jArr;
    }
    
    public JSONObject postJSONFromUrl(String url, List<NameValuePair> nameValuePairs) {
    	
    	StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        
        try {
            //nameValuePairs.add(new BasicNameValuePair("id", "12345"));
            //nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
          HttpResponse response = client.execute(httppost);
          StatusLine statusLine = response.getStatusLine();
          int statusCode = statusLine.getStatusCode();
          if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
              builder.append(line);
            }
            
            json = builder.toString();
            System.out.println(json);
          } else {
        	  return null;
          }
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        } 
        
     // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            // error parsing data
        	return null;
        }
 
        // return JSON String
        return jObj;
    }


    public JSONObject postFileJSONFromURL(String url, String file_path, List<NameValuePair> nameValuePairs) {
    	
    	File file = new File(file_path);
    	
    	StringBuilder builder = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        
    	try {
    		MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();        
    	    multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    	    
    	    for (NameValuePair nameValuePair : nameValuePairs) {
    	    	multipartEntity.addPart(nameValuePair.getName(), new StringBody(nameValuePair.getValue(), ContentType.TEXT_PLAIN));
			}
    	    
    	    multipartEntity.addPart("file", new FileBody(file));
 
            
            httppost.setEntity(multipartEntity.build());
            
    	    //httppost.setEntity(multipartEntity);
    	    //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	   
    	    HttpResponse response = httpclient.execute(httppost);
    	    //Do something with response...
    	    StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
              HttpEntity entity = response.getEntity();
              InputStream content = entity.getContent();
              BufferedReader reader = new BufferedReader(new InputStreamReader(content));
              String line;
              while ((line = reader.readLine()) != null) {
                builder.append(line);
              }
              
              json = builder.toString();
              System.out.println(json);
            } else {
          	  return null;
            }
          } catch (Exception e) {
            e.printStackTrace();
            return null;
          } 
          
       // try parse the string to a JSON object
          try {
              jObj = new JSONObject(json);
          } catch (JSONException e) {
              // error parsing data
          }
   
          // return JSON String
          return jObj;
    }

}

