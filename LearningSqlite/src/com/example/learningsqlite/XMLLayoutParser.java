package com.example.learningsqlite;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;

public class XMLLayoutParser {
	Context activity;
	public static final String EDIT_TEXT = "EditText";
	public static final String TEXT_VIEW = "TextView";
	
	
	public XMLLayoutParser(Context act){
		activity = act;
	}
	

	
	public int[] getElementIds(int layoutId, String viewType) throws XmlPullParserException, IOException{
		XmlResourceParser parser = activity.getResources().getLayout(layoutId);
		LinkedList<Integer> idList = new LinkedList<Integer>();
	//	int i;
		int counter = 0;
		while(parser.getEventType()!=XmlResourceParser.END_DOCUMENT){
			counter++;
			parser.next();
			if(parser.getEventType()==XmlResourceParser.START_TAG){
				//Log.d("parser","gettext: "+parser.getText());
				if(parser.getName().equals(viewType)){
			/*		long viewId = parser.getIdAttributeResourceValue(0); // this is what's supposed to work
					if(viewId!=0)
						idList.add((int) viewId);
					Log.d("Test", "Id: "+ viewId );
					*/
					// this is the workaround!
					if(parser.getAttributeName(0).equals("id")){
						long viewId = parser.getAttributeResourceValue(0, 0);
						idList.add((int)viewId);
						Log.d("Test", "Id: "+ viewId );
					}else{
						boolean reachedEnd = false;
						int total = parser.getAttributeCount();
						int attCounter = 0;
						if(activity instanceof InputFormActivity){
							while(!reachedEnd && attCounter<total){

								attCounter++;
								if(parser.getAttributeName(attCounter).equals("id")){
									long viewId = parser.getAttributeResourceValue(attCounter, 0);
									idList.add((int)viewId);
									reachedEnd = true;
								}
							}
						}
					}
				}
			}
		}
		parser.close();
		Log.d("Counter", "no of passes: "+counter);
		int[] inputIds = new int[idList.size()];
		for(int j = 0;j<inputIds.length;j++)
			
			inputIds[j] = idList.pollFirst();
			
		return inputIds;
	}

}
