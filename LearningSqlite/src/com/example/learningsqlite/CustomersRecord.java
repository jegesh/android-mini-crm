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
/*	private String tableName;
	public static SQLiteDatabase db;
	public String[] values;
	/*public String fName;
	public String lName;
	public String phone1;
	public String phone2;
	public String email;
	public String address;
	public String contactName;
	public String contactPhone;*/
	

	public CustomersRecord(Context activity){
		super(CustomersContract.Customers.TABLE_NAME,R.layout.layout_customer_input,R.layout.customer_display,R.string.title_activity_customer_edit);
		db = new CustomersDBHelper(activity).getWritableDatabase(); 
	
	}
	
	public CustomersRecord(String id, Context activity){
		this(activity);
		setValues();
	}
	
	
	
	
}
