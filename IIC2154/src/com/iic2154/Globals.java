package com.iic2154;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.SharedPreferences;
import android.content.Context;
import android.text.TextUtils;



public class Globals {
	
	static String id = "com.iic2154";
	
	public static String getBaseUrl(Context cont){
		SharedPreferences settings = cont.getSharedPreferences(id, 0);
		String url = settings.getString("base_url",cont.getResources().getString(R.string.base_url));
		return url; 
	}
	
	public static void setBaseUrl(String url, Context cont){		
		SharedPreferences settings = cont.getSharedPreferences(id, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("base_url", url);
		editor.commit();
	}
	
	public static String getEmail(Context cont){
		SharedPreferences settings = cont.getSharedPreferences(id, 0);
		String email = settings.getString("email",null);
		return email; 
	}
	
	public static void setEmail(String email, Context cont){		
		SharedPreferences settings = cont.getSharedPreferences(id, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("email", email);
		editor.commit();
	}
	
	public static String getPass(Context cont){
		SharedPreferences settings = cont.getSharedPreferences(id, 0);
		String password = settings.getString("password", null);
		return password;
	}
	
	public static void setPass(String pass, Context cont){
		SharedPreferences settings = cont.getSharedPreferences(id, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("password", pass);
		editor.commit();
	}
	
	public static String getToken(Context cont){
		SharedPreferences settings = cont.getSharedPreferences(id, 0);
		String token = settings.getString("token", null);
		return token;
	}
	
	public static void setToken(String token, Context cont){
		SharedPreferences settings = cont.getSharedPreferences(id, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("token", token);
		editor.commit();
	}
	
	public static String getProjectList(Context cont){
		SharedPreferences settings = cont.getSharedPreferences(id, 0);
		String project_list = settings.getString("project_list", null);
		return project_list;
	}
	
	public static void setProjectList(String list, Context cont){
		SharedPreferences settings = cont.getSharedPreferences(id, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("project_list", list);
		editor.commit();
	}
	
	
}
