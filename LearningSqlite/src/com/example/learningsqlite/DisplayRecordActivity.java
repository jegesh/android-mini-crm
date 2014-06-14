package com.example.learningsqlite;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import com.example.learningsqlite.ConfirmDeletionDialog.NoticeDialogListener;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DisplayRecordActivity extends Activity implements NoticeDialogListener {
	public static final String DELETION_DIALOG_FRAG_TAG = "deletion dialog";
	public static final String TAG = "DisplayRecord";
	
	//private SQLiteDatabase db;
	public DatabaseRecord currentRecord;
	//private int[] contentIds;
	//private String tableName;
	//private String[] fields;
	//private Cursor c;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		
	}
	
	@Override
	protected void onResume() {
		switch (getIntent().getStringExtra(ViewDbActivity.DOMAIN)) {
		case DbConstants.ORDERS:
			currentRecord = new OrdersRecord(getIntent().getStringExtra(ViewDbActivity.RECORD_ID), this);
			break;
		case DbConstants.CUSTOMERS:
			currentRecord = new CustomersRecord(getIntent().getStringExtra(ViewDbActivity.RECORD_ID), this);
			break;
		default:
			break;
		}
	//	Log.d(TAG, "layout id: "+currentRecord.layoutId);
		setContentView(currentRecord.displayLayoutId);
		this.setTitle(getString(currentRecord.titleId));
		fillFields();
		XMLLayoutParser par = new XMLLayoutParser(this);
		try {
			int[] inputs = par.getElementIds(currentRecord.displayLayoutId, XMLLayoutParser.TEXT_VIEW);
			Log.d(TAG,"in try for parser");
			Log.d(TAG,"Length of array: "+inputs.length);
	//		Log.d(TAG, inputs[0]+" "+inputs[1]+" "+inputs[6]+" "+inputs[7]);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onResume();
	}
	
	public void editRecord(String requestedDomain, String recordId){
		Intent intent = new Intent(this, InputFormActivity.class);
		intent.putExtra(ViewDbActivity.DOMAIN, requestedDomain);
		intent.putExtra(ViewDbActivity.RECORD_ID, recordId);
		startActivity(intent);
	}
	
	public void editCustomer(View v){
		editRecord(DbConstants.CUSTOMERS, ((OrdersRecord)currentRecord).customer.recordId);
	}
	
	private void fillFields(){
	/*	String recId = getIntent().getExtras().getString(ViewDbActivity.RECORD_ID);
		c = db.query(tableName, fields, "_ID = ?",new String[]{ recId }, null, null, null );
		c.moveToFirst();
		Log.d(TAG, Integer.toString(c.getColumnCount()));*/
		for(int i = 0; i<currentRecord.values.length;i++){
			TextView txtView = (TextView)findViewById(currentRecord.contentIds[i]);
			txtView.setText(currentRecord.values[i]);
		}
		if(currentRecord.tableName==DbConstants.ORDERS){
			OrdersRecord oRecord = (OrdersRecord)currentRecord;
			for(int i = currentRecord.values.length;i<currentRecord.contentIds.length;i++){
				TextView txtView = (TextView)findViewById(currentRecord.contentIds[i]);
				txtView.setText(oRecord.customer.values[i-currentRecord.values.length]);
			}
		}
	//	c.close();
	//	db.close();
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_record, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		
		case R.id.menu_item_edit_record:
			String recID = currentRecord.recordId;
			String d = getIntent().getExtras().getString(ViewDbActivity.DOMAIN);
			editRecord(d, recID);
			return true;
		case R.id.menu_item_delete_record:
			ConfirmDeletionDialog dialog = new ConfirmDeletionDialog();
			dialog.show(getFragmentManager(), DELETION_DIALOG_FRAG_TAG);
		//	if(deletionAffirmed)
		
			return true;
		case R.id.menu_item_call:
			// Intent call = new Intent("",Uri.) call contact's phone number
			return true;
		case R.id.menu_item_email:
			// send email to customer
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		Log.d(TAG, "Yes clicked");
		currentRecord.deleteRecord();
			

		Log.d(TAG, "Before refresh");
		Intent intent2 = new Intent(this, ViewDbActivity.class);
		startActivity(intent2);
		
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
	
		
	}
	
	@Override
	protected void onDestroy() {
		currentRecord.db.close();
		super.onDestroy();
	}

}
