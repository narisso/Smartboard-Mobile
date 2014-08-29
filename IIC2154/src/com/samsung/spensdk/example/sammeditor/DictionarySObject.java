package com.samsung.spensdk.example.sammeditor;

import java.util.Dictionary;

import java.util.Hashtable;

import com.samsung.samm.common.SObject;
import com.samsung.spensdk.SCanvasView;

public class DictionarySObject {
	
	Dictionary<String,SObject> data = new Hashtable<String,SObject>();
	
	String key_device = "";  // clave unica para el dispositivo
	int count = 0;			 // contador para cada objeto creado
	
	public DictionarySObject(String key_device){
		this.key_device = key_device;
	}
	
	public boolean Add(SObject obj){
		
		String key = obj.getStringExtra("key_sobject", ""); 
		
		
		if(key.equals("")){ // si el objeto fue creado por el usuario de la tablet
			count++;
			key = key_device + "-" + count; 
			obj.putExtra("key_sobject", key);
			obj.putExtra("owner", true);
			data.put(key, obj);
		}
		else if(IsOwner(obj)){ // el objeto ya fue creado, y por él mismo				
			return false;
		}
		else{ // si el objeto fue creado por otro usuario ya llega con la data
			data.put(key, obj);
		}
		
		return true;
	}
	
	public SObject Remove(SObject obj){
		
		// si se indica que un objeto de otro usuario se debe remover, el valor de la extra data será igual,
		// pero el SObject no. Es por ello que se debe buscar la instancia creada en el dispositivo actual.
		String key = obj.getStringExtra("key_sobject", ""); 
		return data.remove(key);
	}
	
	public SObject Get(SObject obj){
		
		// ver explicación de remove
		String key = obj.getStringExtra("key_sobject", ""); 
		System.out.println("----------- " + key);
		return data.get(key);
	}
	
	public int GetI(SObject obj, SCanvasView mSCanvas){
		
		int ind = -1;
		
		String key = obj.getStringExtra("key_sobject", ""); 
		SObject obj2 = data.get(key);
		
		for(int i=0; i < mSCanvas.getSAMMObjectNum(); ++i){
			SObject obji = mSCanvas.getSAMMObject(i);
			String key2 = obji.getStringExtra("key_sobject", "");
			
			if(key2.equals(key)){
				ind = i;
				System.out.println("----------- " + i);
				break;
			}
		}
		
		return ind;
	}
	
	public boolean IsOwner(SObject obj){
		
		String key = obj.getStringExtra("key_sobject", ""); 
		
		if(key.equals("")){
			return true;
		}
		
		
		return obj.getBooleanExtra("owner", false);
	}
}
