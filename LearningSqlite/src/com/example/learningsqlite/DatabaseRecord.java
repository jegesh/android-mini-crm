package com.example.learningsqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class DatabaseRecord {
	public String recordId;
	public String tableName;
	public int editLayoutId;
	public int displayLayoutId;
	public int titleId;
	public String[] columns;
	public String[] values;
	public int[] contentIds;
//	private int colCount;
	public SQLiteDatabase db;
	
	public DatabaseRecord(String table, int editId, int displayId, int titleId){
		tableName = table;
		editLayoutId = editId;
		displayLayoutId = displayId;
		this.titleId = titleId;
	}
	
	public void setValues(){
		Cursor c=null;
		c = db.query(tableName, null, "_ID = ?", new String[]{recordId}, null, null, null);
		
		if(c.moveToFirst()){
			columns = c.getColumnNames();
			int cols = c.getColumnCount()-1;
			values= new String[cols];
			for(int i = 0;i<cols;i++)
				values[i]=c.getString(i+1);
		}
		c.close();
	}
	
	public void updateRecord(String[] values){
		ContentValues cv = new ContentValues();
		for(int i = 0;i<values.length;i++)
			cv.put(columns[i+1], values[i]);
		db.update(tableName, cv, "_ID = ?", new String[]{recordId});
	}
	
		
	public void deleteRecord(){
		db.delete(tableName, "_ID = ?", new String[]{recordId});
	}
	
	
	
	//public abstract String[] getValues();

}
