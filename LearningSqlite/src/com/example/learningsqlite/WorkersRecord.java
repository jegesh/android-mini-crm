package com.example.learningsqlite;

import android.content.Context;

public class WorkersRecord extends DatabaseRecord {
	public String recordId;

	
	public WorkersRecord(Context activity){
		super(WorkersContract.Workers.TABLE_NAME,0,0,0);
		db = new WorkersDBHelper(activity).getWritableDatabase();
	}
	
	public WorkersRecord(String id, Context activity){
		this(activity);
		recordId = id;
		setValues();
	}

	
}
