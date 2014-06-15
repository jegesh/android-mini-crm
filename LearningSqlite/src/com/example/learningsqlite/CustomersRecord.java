package com.example.learningsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.learningsqlite.CustomersContract.Customers;
import com.example.learningsqlite.OrdersContract.Orders;

public class CustomersRecord extends DatabaseRecord {
	public String recordId;

	public CustomersRecord(Context activity){
		super(CustomersContract.Customers.TABLE_NAME,R.layout.layout_customer_input,R.layout.customer_display,R.string.title_activity_customer_edit);
		db = new CustomersDBHelper(activity).getWritableDatabase(); 
	
	}
	
	public CustomersRecord(String id, Context activity){
		this(activity);
		recordId = id;
		setValues();
	}
	
	
	
	
}
