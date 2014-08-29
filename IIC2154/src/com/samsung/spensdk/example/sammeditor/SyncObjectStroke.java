package com.samsung.spensdk.example.sammeditor;


import java.io.Serializable;


import android.graphics.PointF;
import android.graphics.RectF;

import com.samsung.samm.common.SObjectStroke;
import com.samsung.samm.common.SObjectText;

public class SyncObjectStroke implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static int TYPE_TEXT = 1;
	public static int TYPE_STROKE = 2;
	
	public static int ACTION_INSERTED = 1;
	public static int ACTION_DELETED = 2;
	public static int ACTION_CHANGED = 3;
	public static int ACTION_CLEAR_ALL = 4;
	
	
	public int type = 0;
	public int action = 0;
	
	
	// SObject
	public int Color;
	public String Description;
	public String HyperText;
	public int Latitude;
	public int Longitude;
	public Rect Rect;
	public float RotateAngle;
	public float Size;
	
	// SObjectStroke
	public int mBeautifyID;
	public int BeautifyLineFillStyleIndex;
	public int BeautifySlantIndex;
	public int MetaData;
	public Point[] Points;
	public float[] Pressures;
	public int Style;
	
	// SObjectText
	public int BGColor;
	public String FontName;
	public int HorizTextAlign;
	public String Text;
	public int VertTextAlign;
	
	// Extra Data
	public String key_extra_data;
	
 	public SyncObjectStroke(){	

	}
	
	public SyncObjectStroke(SObjectText o, int action2){
		
		type = TYPE_TEXT;
		action = action2;
		
		Color = o.getColor();
		Description = o.getDescription();
		HyperText = o.getHyperText();
		Latitude = o.getLatitude();
		Longitude = o.getLongitude();
		RotateAngle = o.getRotateAngle();
		Size = o.getSize();
		Style = o.getStyle();
		
		BGColor = o.getBGColor();
		FontName = o.getFontName();
		HorizTextAlign = o.getHorizTextAlign();
		Text = o.getText();
		VertTextAlign = o.getVertTextAlign();
		
		key_extra_data = o.getStringExtra("key_sobject", ""); 
		
		RectF rect = o.getRect();
		if(rect != null){
			Rect = new Rect();
			Rect.bottom = rect.bottom;
			Rect.left = rect.left;
			Rect.top = rect.top;
			Rect.right = rect.right;
		}
	}
	
	public SyncObjectStroke(SObjectStroke o, int action2)
	{	
		type = TYPE_STROKE;
		action = action2;
		
		mBeautifyID = o.getBeautifyID();
		BeautifyLineFillStyleIndex = o.getBeautifyLineFillStyleIndex();
		BeautifySlantIndex = o.getBeautifySlantIndex();
		Color = o.getColor();
		Description = o.getDescription();
		HyperText = o.getHyperText();
		Latitude = o.getLatitude();
		Longitude = o.getLongitude();
		MetaData = o.getMetaData();
		RotateAngle = o.getRotateAngle();
		Size = o.getSize();
		Style = o.getStyle();
		
		key_extra_data = o.getStringExtra("key_sobject", ""); 
		
		float[] Pressures2 = o.getPressures();
		if(Pressures2 != null){
			Pressures = new float[Pressures2.length];
			for(int i=0; i<Pressures2.length; ++i){
				Pressures[i] = Pressures2[i];
			}
		}
		
		PointF[] Points2 = o.getPoints();
		if(Points2 != null){
			Points = new Point[Points2.length]; 
			for(int i=0; i<Points2.length; ++i){
				Points[i] = new Point();
				Points[i].x = Points2[i].x;
				Points[i].y = Points2[i].y;
			}
		}
		
		RectF rect = o.getRect();
		if(rect != null){
			Rect = new Rect();
			Rect.bottom = rect.bottom;
			Rect.left = rect.left;
			Rect.top = rect.top;
			Rect.right = rect.right;
		}
		
	
	}
	
	public SObjectStroke getSObjectStroke(){

		SObjectStroke o = new SObjectStroke();
		
		o.setBeautifyID(mBeautifyID);
		o.setBeautifyLineFillStyleIndex(BeautifyLineFillStyleIndex);
		o.setBeautifySlantIndex(BeautifySlantIndex);
		o.setColor(Color);
		o.setDescription(Description);
		o.setHyperText(HyperText);
		o.setLatitude(Latitude);
		o.setLongitude(Longitude);
		o.setMetaData(MetaData);
		o.setRotateAngle(RotateAngle);
		o.setSize(Size);
		o.setStyle(Style);
		
		o.putExtra("key_sobject", key_extra_data);
		
		if(Pressures != null){
			float[] Pressures2 = new float[Pressures.length];
			for(int i=0; i<Pressures.length; ++i){
				Pressures2[i] = Pressures[i];
			}
			o.setPressures(Pressures2);
		}
		
		
		if(Points != null){
			PointF[] Points2 =  new PointF[Points.length];
			for(int i=0; i<Points.length; ++i){
				Points2[i] = new PointF();
				Points2[i].x = Points[i].x;
				Points2[i].y = Points[i].y;
			}
			o.setPoints(Points2);
		}
		
		if(Rect != null){
			RectF rect = new RectF();
			rect.bottom = Rect.bottom;
			rect.left = Rect.left;
			rect.top = Rect.top;
			rect.right = Rect.right;
			o.setRect(rect);
		}
		
		
		return o;
	}

	public SObjectText getSObjectText(){
		SObjectText o = new SObjectText();
		
		o.setColor(Color);
		o.setDescription(Description);
		o.setHyperText(HyperText);
		o.setLatitude(Latitude);
		o.setLongitude(Longitude);
		o.setRotateAngle(RotateAngle);
		o.setSize(Size);
		o.setStyle(Style);
		
		o.putExtra("key_sobject", key_extra_data);
		
		if(Rect != null){
			RectF rect = new RectF();
			rect.bottom = Rect.bottom;
			rect.left = Rect.left;
			rect.top = Rect.top;
			rect.right = Rect.right;
			o.setRect(rect);
		}
		
		o.setBGColor(BGColor);
		o.setFontName(FontName);
		o.setTextAlign(HorizTextAlign, VertTextAlign);
		o.setText(Text);
		
		return o;
	}
	
	
	

	public class Point implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public float x;
		public float y;
		
		public Point(){
		}
	}
	
	public class Rect implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public float left;
		public float top;
		public float right;
		public float bottom;
		
		public Rect(){
		}
	}
}
