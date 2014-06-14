package com.example.learningsqlite;

import java.io.IOException;
import java.security.acl.LastOwnerException;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParserException;

import com.example.learningsqlite.DatePickerInputDialog.NoticeDatePickerDialogListener;



import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class InputFormActivity extends Activity implements NoticeDatePickerDialogListener {
	private static final String NEW_RECORD_ID = "New record id";
	private static final String REQUESTING_RECORD_ID = "requesting id";
	private int[] inputIds;
	public static final String TAG = "InputFormActivity";
	public static final String CUSTOMER_ID = "customer ID";
	private DatabaseRecord record;
	private SQLiteDatabase db;
	private String[] DB_COLUMNS ;
	private String TABLE_NAME;
//	private String title;
	private String recId;
	private int layoutId;
	private int currentDateFieldId;
	private String newRecordId;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		switch (getIntent().getExtras().getString(ViewDbActivity.DOMAIN)) {
			case DbConstants.ORDERS:
				DB_COLUMNS = DbConstants.ORDERS_COLUMNS;
				TABLE_NAME = OrdersContract.Orders.TABLE_NAME;
				layoutId = R.layout.activity_orders_new_record;
				setTitle(R.string.title_activity_orders_new_record);
				if(getIntent().hasExtra(ViewDbActivity.RECORD_ID)){
					recId = getIntent().getExtras().getString(ViewDbActivity.RECORD_ID);
					record = new OrdersRecord(recId, this);
				}else{
					OrdersDBHelper dbHelp = new OrdersDBHelper(this);
					db = dbHelp.getWritableDatabase();
				}
				break;
			case DbConstants.CUSTOMERS:
				DB_COLUMNS = DbConstants.CUSTOMERS_COLUMNS;
				TABLE_NAME = CustomersContract.Customers.TABLE_NAME;
				layoutId = R.layout.layout_customer_input;
				setTitle(R.string.title_activity_customers_new_record_form);
				if(getIntent().hasExtra(ViewDbActivity.RECORD_ID)){
					recId = getIntent().getExtras().getString(ViewDbActivity.RECORD_ID);
					record = new CustomersRecord(recId, this);
				}else{
					CustomersDBHelper dbHelp1 = new CustomersDBHelper(this);
					db = dbHelp1.getWritableDatabase();
				}
				break;
	
			default:
				break;
		}
		setContentView(layoutId);
		XMLLayoutParser parser = new XMLLayoutParser(this);
		
		try {
			inputIds = parser.getElementIds(layoutId, XMLLayoutParser.EDIT_TEXT);
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		if(recId!=null){
			switch (TABLE_NAME) {
			case OrdersContract.Orders.TABLE_NAME:
			// special code for handling methods associated with special nature of orders records, i.e. one-to-many and one-to-one
				for(int i = 0;i<inputIds.length;i++){
					EditText input = (EditText)findViewById(inputIds[i]);
					try {
						input.setText(record.values[i]);
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}
					
				}
				OrdersRecord ordersRec = (OrdersRecord)record;
				if(ordersRec.customer!=null){
					// inflate a fragment that displays customer headers and edit button
				}else{
					// inflate other fragment with add customer button
				}
				break;
			case "Bids":
				// ditto
				break;

			default:
				for(int i = 0;i<inputIds.length;i++){
					EditText input = (EditText)findViewById(inputIds[i]);
					try {
						input.setText(record.values[i]);
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}
					
				}
				break;
			}
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
//		getMenuInflater().inflate(R.menu.orders_new_record_form, menu);
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	public void addCustomer(View v){
		Intent intent = new Intent(this, InputFormActivity.class);
		intent.putExtra(ViewDbActivity.DOMAIN, DbConstants.CUSTOMERS);
		intent.putExtra(REQUESTING_RECORD_ID, record.recordId);
		startActivity(intent);
	}

	public boolean insertNewRecord(String[] values){
		ContentValues cv = new ContentValues();
		for(int i = 0;i<values.length;i++){
			cv.put(this.DB_COLUMNS[i+1], values[i]);
			Log.d(TAG, "Column: " + DB_COLUMNS[i] + ". Value: " + cv.getAsString(DB_COLUMNS[i]));
		}
		try {
			// insert record and save its id in field, later to be sent back to the requesting activity
			newRecordId = Integer.toString((int) db.insert(TABLE_NAME, null, cv));
			
			return true;
		} catch (Exception e) {
			Log.d(TAG, "Error: " + e.getMessage());
		}
		return false;
		
	};
	
	// 'save' button click method -- for saving inputed data to database as a new record (or updating) 
	public void saveNew(View view) {
		String[] values = new String[inputIds.length];
		for(int i=0;i<values.length;i++){
			EditText input = (EditText)findViewById(inputIds[i]);
			values[i] = input.getText().toString();
		}
		//	Log.d(TAG, "after for stmt");
			if(record==null){
				if(insertNewRecord(values)){ // if values were typed into fields
					Toast.makeText(this, getString(R.string.record_saved_success_message), Toast.LENGTH_SHORT).show();
					if(getIntent().hasExtra(REQUESTING_RECORD_ID)){
						Intent intent = new Intent(this, InputFormActivity.class);
						// send back id of record that the request came from 
						intent.putExtra(ViewDbActivity.RECORD_ID, getIntent().getStringExtra(REQUESTING_RECORD_ID));
						// send id of new 'child' record that was just created
						intent.putExtra(NEW_RECORD_ID, newRecordId);
						startActivity(intent);
					}
					else{
						finish();
					}
				}else{
					Toast.makeText(this, getString(R.string.record_saved_failure_message), Toast.LENGTH_SHORT).show();
					finish();
				}
				
			}else{ // at the moment no toast!
				record.updateRecord(values);
				/*
				if(updateRecord(values)){
					Toast.makeText(this, getString(R.string.record_update_success_message), Toast.LENGTH_SHORT).show();	
				}else{
					Toast.makeText(this, getString(R.string.record_update_failure_message), Toast.LENGTH_SHORT).show();	
				}*/
			}
			//db.close();
			
		}
	
	
	public void cancel(View view){
		finish();
	}
	
	@Override
	protected void onDestroy() {
		if(db!=null){
			db.close();
		}
		super.onDestroy();
	}

	
	public boolean updateRecord(String[] values) {
		Log.d(TAG, "Before for");
		ContentValues cv = new ContentValues();
		for(int i = 0;i<cv.size();i++){
			cv.put(this.DB_COLUMNS[i+1], values[i]);
		}
		Log.d(TAG, "before try");
		try {
			db.update(TABLE_NAME, cv, "_ID=?", new String[]{recId});
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			return false;
		}
		return true;
				
	}
	
	public void openDatePicker(View v){
		DatePickerInputDialog dp = new DatePickerInputDialog();
		dp.show(getFragmentManager(), DbConstants.DATE_PICKER_DIALOG);
		currentDateFieldId = v.getId();
		
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		int[] dateVals = DbConstants.lastPickedDate;
		EditText dateField = (EditText)findViewById(currentDateFieldId);
		dateField.setText(Integer.toString(dateVals[0])+"."+Integer.toString(dateVals[1])+"."+Integer.toString(dateVals[2]));
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		
		
	}

}
