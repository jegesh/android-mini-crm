package com.example.learningsqlite;

import java.util.HashMap;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.learningsqlite.CustomersContract.Customers;

public class CustomersRecord extends DatabaseRecord {

	public CustomersRecord(Context activity){
		super(CustomersContract.Customers.TABLE_NAME,R.layout.layout_customer_input,R.layout.customer_display,R.string.title_customers_main);
		try{
			db = new CustomersDBHelper(activity).getWritableDatabase();
		}catch(Exception e){
			Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		inputIdsToColumns = new HashMap<Integer, String>();
		displayIdsToColumns = new HashMap<Integer, String>();
		setInputIdsToColumns();
		setDisplayIdsToColumns();
	}
	
	public CustomersRecord(String id, Context activity){
		this(activity);
		recordId = id;
		setValues();
	}

	@Override
	protected void setInputIdsToColumns() {
		inputIdsToColumns.put(R.id.customers_input_first_name, Customers.COLUMN_NAME_FIRST_NAME);
		inputIdsToColumns.put(R.id.customers_input_last_name, Customers.COLUMN_NAME_LAST_NAME);
		inputIdsToColumns.put(R.id.customers_input_phone1, Customers.COLUMN_NAME_PHONE1);
		inputIdsToColumns.put(R.id.customers_input_phone2, Customers.COLUMN_NAME_PHONE2);
		inputIdsToColumns.put(R.id.customers_input_email, Customers.COLUMN_NAME_EMAIL);
		inputIdsToColumns.put(R.id.customers_input_address, Customers.COLUMN_NAME_ADDRESS);
		inputIdsToColumns.put(R.id.customers_input_contact_name, Customers.COLUMN_NAME_CUSTOMER_CONTACT);
		inputIdsToColumns.put(R.id.customers_input_contact_phone, Customers.COLUMN_NAME_CONTACT_PHONE);
	}

	@Override
	protected void setDisplayIdsToColumns() {
		displayIdsToColumns.put(R.id.customers_first_name, Customers.COLUMN_NAME_FIRST_NAME);
		displayIdsToColumns.put(R.id.customers_last_name, Customers.COLUMN_NAME_LAST_NAME);
		displayIdsToColumns.put(R.id.customers_phone1, Customers.COLUMN_NAME_PHONE1);
		displayIdsToColumns.put(R.id.customers_phone2, Customers.COLUMN_NAME_PHONE2);
		displayIdsToColumns.put(R.id.customers_email, Customers.COLUMN_NAME_EMAIL);
		displayIdsToColumns.put(R.id.customers_address, Customers.COLUMN_NAME_ADDRESS);
		displayIdsToColumns.put(R.id.customers_contact_name, Customers.COLUMN_NAME_CUSTOMER_CONTACT);
		displayIdsToColumns.put(R.id.customers_contact_phone, Customers.COLUMN_NAME_CONTACT_PHONE);
	}
	
	
	
	
}
