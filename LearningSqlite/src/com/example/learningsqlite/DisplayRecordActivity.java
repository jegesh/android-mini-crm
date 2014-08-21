package com.example.learningsqlite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learningsqlite.ConfirmDeletionDialog.NoticeDialogListener;
import com.example.learningsqlite.CustomersContract.Customers;
import com.example.learningsqlite.OrdersContract.Orders;
import com.example.learningsqlite.PhonePickerFragment.PhoneDialogListener;

public class DisplayRecordActivity extends Activity implements NoticeDialogListener,PhoneDialogListener {
	public static final String DELETION_DIALOG_FRAG_TAG = "deletion dialog";
	public static final String TAG = "DisplayRecord";
	
	//private SQLiteDatabase db;
	public DatabaseRecord currentRecord;
	public int[] phoneFields;
	public String[] phoneLabels;
	int selectedPhoneIndex;
	private int[] contentIds;
	int emailAddressId;
	private String phoneString;
	//private String tableName;
	//private String[] fields;
	//private Cursor c;
	private LinearLayout custFrame;
	private LinearLayout productsSuperFrame;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	protected void onResume() {
	//	Log.d(TAG, getIntent().getStringExtra(ViewDbActivity.RECORD_ID));
		switch (getIntent().getStringExtra(ViewDbActivity.DOMAIN)) {
		case DbConstants.ORDERS:
			currentRecord = new OrdersRecord(getIntent().getStringExtra(ViewDbActivity.RECORD_ID), this);
			phoneFields = new int[3];
			phoneLabels = new String[3];
			emailAddressId = R.id.customers_email;
			break;
		case DbConstants.CUSTOMERS:
			phoneFields = new int[3];
			phoneLabels = new String[3];
			currentRecord = new CustomersRecord(getIntent().getStringExtra(ViewDbActivity.RECORD_ID), this);
			emailAddressId = R.id.customers_email;
			break;
		case DbConstants.WORKERS:
			phoneFields = new int[2];
			phoneLabels = new String[2];
			currentRecord = new WorkersRecord(getIntent().getStringExtra(ViewDbActivity.RECORD_ID), this);
			emailAddressId = R.id.workers_email;
			break;
		case DbConstants.PRODUCTS:
			currentRecord = new ProductsRecord(getIntent().getStringExtra(ViewDbActivity.RECORD_ID), this);
			break;
		default:
			break;
		}
	//	Log.d(TAG, "layout id: "+currentRecord.layoutId);
		setContentView(currentRecord.displayLayoutId);
		this.setTitle(getString(currentRecord.titleId));
		XMLLayoutParser par = new XMLLayoutParser(this);
		try {
			contentIds = par.getElementIds(currentRecord.displayLayoutId, XMLLayoutParser.TEXT_VIEW);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fillFields();
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
	
	private void fillFields(View context, DatabaseRecord memberRec){
		Integer[] displayIds = new Integer[memberRec.displayIdsToColumns.size()];
		memberRec.displayIdsToColumns.keySet().toArray(displayIds);
		for(int i = 0; i<memberRec.displayIdsToColumns.size();i++){
			TextView txtView = (TextView)context.findViewById(displayIds[i]);
			txtView.setText(memberRec.valueMap.get(memberRec.displayIdsToColumns.get(displayIds[i])));
		}
	}
	
	private void fillFields(){
		for(int i = 0; i<contentIds.length;i++){
			TextView txtView = (TextView)findViewById(contentIds[i]);
			txtView.setText(currentRecord.valueMap.get(currentRecord.displayIdsToColumns.get(contentIds[i])));
		}
		LinearLayout container = (LinearLayout) findViewById(R.id.orders_display_container);
		if(currentRecord instanceof OrdersRecord){
			
			OrdersRecord oRecord = (OrdersRecord)currentRecord;
//			Toast.makeText(this, "Cust name: "+oRecord.customer.valueMap.get(Customers.COLUMN_NAME_FIRST_NAME), Toast.LENGTH_SHORT).show();
			
			// code to handle customer textViews
			if(oRecord.customer!=null){
				custFrame = (LinearLayout)findViewById(R.id.orders_customer_fragment_container);
				getLayoutInflater().inflate(R.layout.customer_display, custFrame);
				LinearLayout custHeader = new LinearLayout(this);
				custHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				custHeader.setOrientation(LinearLayout.HORIZONTAL);
				TextView frameTitle = new TextView(this);
				LinearLayout.LayoutParams titleLp =new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
				titleLp.weight = 1;
				frameTitle.setLayoutParams(titleLp);
				frameTitle.setText(R.string.label_customer_info);
				frameTitle.setTextSize(24);
				frameTitle.setTextColor(Color.BLUE);
				int addinIndex = container.indexOfChild(custFrame);
				custHeader.addView(frameTitle);
				Button toggler = getToggleButton();
				toggler.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(custFrame.getVisibility()==View.VISIBLE){
							custFrame.setVisibility(View.GONE);
							((Button)v).setText(R.string.btn_show_orders_member);
						}else{
							custFrame.setVisibility(View.VISIBLE);
							((Button)v).setText(R.string.btn_hide_orders_member);
						}
						
					}
				});
				custHeader.addView(toggler);
				container.addView(custHeader, addinIndex);
				fillFields(custFrame, oRecord.customer);
				findViewById(R.id.orders_customer_details_divider).setVisibility(View.VISIBLE);
				custFrame.setVisibility(View.GONE);
			}
			
			// code to handle display of products
			if(oRecord.products.size()>0){
				productsSuperFrame = (LinearLayout)findViewById(R.id.orders_products_fragment_container);
				LinearLayout prodHeader = new LinearLayout(this);
				prodHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				prodHeader.setOrientation(LinearLayout.HORIZONTAL);
				TextView frameTitle = new TextView(this);
				LinearLayout.LayoutParams titleLp =new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
				titleLp.weight = 1;
				frameTitle.setLayoutParams(titleLp);					
				frameTitle.setText(R.string.label_product_info);
				frameTitle.setTextSize(24);
				frameTitle.setTextColor(Color.BLUE);
				int addinIndex = container.indexOfChild(productsSuperFrame);
				prodHeader.addView(frameTitle);
				Button toggler = getToggleButton();
				toggler.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(productsSuperFrame.getVisibility()==View.VISIBLE){
							productsSuperFrame.setVisibility(View.GONE);
							((Button)v).setText(R.string.btn_show_orders_member);
						}else{
							productsSuperFrame.setVisibility(View.VISIBLE);
							((Button)v).setText(R.string.btn_hide_orders_member);
						}
						
					}
				});
				prodHeader.addView(toggler);
				container.addView(prodHeader, addinIndex);
				for(int i = 0;i<oRecord.products.size();i++){
					LinearLayout singleProductFrame = new LinearLayout(this);
					LayoutParams singleFrameLayParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					singleProductFrame.setLayoutParams(singleFrameLayParams);
					singleProductFrame.setOrientation(LinearLayout.VERTICAL);
					productsSuperFrame.addView(singleProductFrame);
					getLayoutInflater().inflate(R.layout.products_display_form, singleProductFrame);
					singleProductFrame.findViewById(R.id.products_amount_container).setVisibility(View.VISIBLE);
					fillFields(singleProductFrame, oRecord.products.get(i));
				}
				productsSuperFrame.setVisibility(View.GONE);
			}
			
			// code to handle display of workers on job
			if(oRecord.workers.size()>0){
				//TODO load fragment and populate fields
			}
		}
	//	c.close();
	//	db.close();
			
	}
	
	private Button getToggleButton() {
		Button toggler = new Button(this);
		LinearLayout.LayoutParams togglerLayout = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	//	togglerLayout.gravity = Gravity.RIGHT;
		toggler.setLayoutParams(togglerLayout);
		toggler.setText(R.string.btn_show_orders_member);
		toggler.setTextSize(22);
		return toggler;
	}
	
			
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		if(getIntent().getStringExtra(ViewDbActivity.DOMAIN).equals(DbConstants.PRODUCTS))
			getMenuInflater().inflate(R.menu.display_product_menu, menu);
		else
			getMenuInflater().inflate(R.menu.display_record, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. 
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
			switch (getIntent().getStringExtra(ViewDbActivity.DOMAIN)) {
			case DbConstants.CUSTOMERS:
				phoneFields[0] = R.id.customers_phone1;
				phoneFields[1] = R.id.customers_phone2;
				phoneFields[2] = R.id.customers_contact_phone;
				phoneLabels[0] = getString(R.string.customer_phone1_hint)+": "+((TextView)findViewById(phoneFields[0])).getText();
				phoneLabels[1] = getString(R.string.customer_phone2_hint)+": "+((TextView)findViewById(phoneFields[1])).getText();
				phoneLabels[2] = getString(R.string.customer_contact_phone_hint)+": "+((TextView)findViewById(phoneFields[2])).getText();
				break;
			case DbConstants.ORDERS:
				if(((OrdersRecord)currentRecord).customer != null){
					phoneFields[0] = R.id.customers_phone1;
					phoneFields[1] = R.id.customers_phone2;
					phoneFields[2] = R.id.customers_contact_phone;
					
					phoneLabels[0] = getString(R.string.customer_phone1_hint)+": "+((TextView)findViewById(phoneFields[0])).getText();
					phoneLabels[1] = getString(R.string.customer_phone2_hint)+": "+((TextView)findViewById(phoneFields[1])).getText();
					phoneLabels[2] = getString(R.string.customer_contact_phone_hint)+": "+((TextView)findViewById(phoneFields[2])).getText();
				}
				break;
			case DbConstants.WORKERS:
				phoneFields[0] = R.id.workers_phone1;
				phoneFields[1] = R.id.workers_phone2;
				phoneLabels[0] = getString(R.string.workers_phone1_hint)+": "+((TextView)findViewById(phoneFields[0])).getText();
				phoneLabels[1] = getString(R.string.workers_phone2_hint)+": "+((TextView)findViewById(phoneFields[1])).getText();
				break;
			case DbConstants.PRODUCTS:
				// nothing to do
				break;

			default:
				break;
			}
			phoneString = "";
			ArrayList<String> phones = new ArrayList<>();
			for(int phoneId:phoneFields){
				TextView tView = (TextView)findViewById(phoneId);
				if(tView!=null && tView.getText().length()>=9)
					phones.add(tView.getText().toString());
			}
			if(phones.size()==1){
				phoneString = phones.get(0);
				return true;
			}
			else if(phones.size()==0){
				Toast.makeText(this, getString(R.string.message_no_phone_number), Toast.LENGTH_SHORT).show();
				return false;
			}else{
				DialogFragment newFragment = new PhonePickerFragment();
    			newFragment.show(getFragmentManager(), "phone nums");
			}
			return true;
		case R.id.menu_item_email:
			String emailAddress = ((TextView)findViewById(emailAddressId)).getText().toString();
			// TODO use email regex
			if(emailAddress.length()>8 && emailAddress.contains("@")){
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
				emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailAddress});
				startActivity(emailIntent);
				return true;
			}
			return false;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPhoneDialogPositiveClick(PhonePickerFragment dialog) {
		selectedPhoneIndex = dialog.clickedPhoneIndex;

		TextView tView = (TextView)findViewById(phoneFields[selectedPhoneIndex]);
		phoneString = tView.getText().toString();


		// strip off dashes and other unwanted characters
		StringBuilder phoneNum = new StringBuilder();
		for(int i = 0;i<phoneString.length();i++){
			if((byte)phoneString.substring(i, i+1).toCharArray()[0]<58 && (byte)phoneString.substring(i, i+1).toCharArray()[0]>46)
				phoneNum.append(phoneString.substring(i, i+1));
		}

		// check availability of phone app and open intent
		PackageManager packageManager = getPackageManager();
		Uri number = Uri.parse("tel:"+ phoneNum);
		Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
		List<ResolveInfo> activities = packageManager.queryIntentActivities(callIntent, 0);
		boolean isIntentSafe = activities.size() > 0;
		if(isIntentSafe){
			startActivity(callIntent);

		}

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
		super.onDestroy();
	}
	
	@Override
	protected void onStop() {
		currentRecord.db.close();
		super.onStop();
	}

}
