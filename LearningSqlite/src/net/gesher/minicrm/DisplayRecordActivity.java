package net.gesher.minicrm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gesher.minicrm.ConfirmDeletionDialog.NoticeDialogListener;
import net.gesher.minicrm.PhonePickerFragment.PhoneDialogListener;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import database_files.CustomersRecord;
import database_files.DatabaseRecord;
import database_files.DbConstants;
import database_files.JobsProductsContract.JobsProducts;
import database_files.OrdersContract;
import database_files.OrdersRecord;
import database_files.ProductsContract.Products;
import database_files.ProductsRecord;
import database_files.WorkersContract.Workers;
import database_files.WorkersRecord;


public class DisplayRecordActivity extends Activity implements NoticeDialogListener,PhoneDialogListener {
	private static final String KEY_CHILD_SUBTITLE2 = "subtitle2";
	private static final String KEY_CHILD_SUBTITLE1 = "subtitle1";
	private static final String KEY_CHILD_TITLE2 = "title2";
	private static final String KEY_CHILD_TITLE1 = "title1";
	private static final String KEY_MEMBER_TITLE = "Title";
	private static final String KEY_MEMBER_COUNT = "Count";
	public static final String TAG_SELL_UNIT = "sell_unit";
	public static final String DELETION_DIALOG_FRAG_TAG = "deletion dialog";
	public static final String TAG = "DisplayRecord";
	private static final String KEY_PRODUCT_AMOUNT = "product amount";
	public static final String KEY_PRODUCT_PARENT_ID = "parent id";
	
	public DatabaseRecord currentRecord;
	int groupsCode = 0;
	public int[] phoneFields;
	public String[] phoneLabels;
	int selectedPhoneIndex;
	private int[] contentIds;
	int emailAddressId;
	private String phoneString;
	private LinearLayout custFrame;
	private LinearLayout ordersFormContainer;
	private int workersHeaderId;
	private int productsHeaderId;
	private int workersListItemLayout;
	private int productsListItemLayout;
	int groupHeaderHeight;
	private ExpandableListView lView;
	private boolean firstTime;
	protected int initialListHeight;
	protected OrdersDisplayListAdapter mListAdapter;
	private InputMethodManager imm;
	private String fieldContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		workersListItemLayout = R.layout.main_screen_listview_item_2;
		productsListItemLayout = R.layout.main_screen_listview_item_1;
		
	}
	
	@Override
	protected void onResume() {
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
		setContentView(currentRecord.displayLayoutId);
		if(getIntent().hasExtra(KEY_PRODUCT_AMOUNT)){ // this record is a product in an order
			((ProductsRecord)currentRecord).orderId = getIntent().getStringExtra(KEY_PRODUCT_PARENT_ID);
			((ProductsRecord)currentRecord).amount = getIntent().getStringExtra(KEY_PRODUCT_AMOUNT);
			findViewById(R.id.products_amount_container).setVisibility(View.VISIBLE);
			
		}
		if(currentRecord instanceof OrdersRecord){
			lView = (ExpandableListView)findViewById(R.id.orders_display_members_list);
			

		}
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
	
	class SaveEditClickListener implements OnClickListener{
		View edit;
		String column;
		
		public SaveEditClickListener(View editableField, int fieldId){
			column = currentRecord.displayIdsToColumns.get(fieldId);
			edit = editableField;
		}
		
		@Override
		public void onClick(View v) {
			String content = "";
			if(v.getTag() != null){
				switch ((String)v.getTag()) {
				case TAG_SELL_UNIT: // products sell-by unit spinner
					Spinner spinner = (Spinner)edit;
					content = ((TextView)spinner.getSelectedView()).getText().toString();
					break;

				default:
					break;
				}
			}else{
				content = ((EditText)edit).getText().toString();
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			if(column != null)
				currentRecord.valueMap.put(column, content);
			else{
				if(getIntent().hasExtra(KEY_PRODUCT_AMOUNT)) // clicked to save product amount, which is not part of the record's value map
					((ProductsRecord)currentRecord).amount = content;
			}
			if(currentRecord.updateRecord(currentRecord.valueMap))
				Toast.makeText(getBaseContext(), getString(R.string.message_changes_saved), Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(getBaseContext(), getString(R.string.message_error_saving), Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public void editField(View v){
		if(v.getTag() != null){
		switch ((String)v.getTag()) {
		case TAG_SELL_UNIT:
			// replace textView with Spinner populated by existant values in db
			Spinner spinner = new Spinner(this);
			LayoutParams spinnerLp = new TableRow.LayoutParams(0,LayoutParams.WRAP_CONTENT,1);
			spinner.setLayoutParams(spinnerLp);
			replaceFieldWithEditable(spinner, (ImageView)v);
			NewRecordFormActivity.populatePriceUnitSpinner(spinner, currentRecord, this);
			break;

		default:
			
			break;
		}
		}else{
			EditText fieldEdit = new EditText(this);
			LayoutParams editLp = new TableRow.LayoutParams(0,LayoutParams.WRAP_CONTENT,1);
			fieldEdit.setLayoutParams(editLp);
			fieldEdit.setSelectAllOnFocus(true);
			fieldEdit.requestFocus();
			fieldEdit.setTextSize(22);
			replaceFieldWithEditable(fieldEdit, (ImageView)v);
			fieldEdit.setText(fieldContent);
			imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(fieldEdit, InputMethodManager.SHOW_IMPLICIT);
			
		}
		
	}

	private void replaceFieldWithEditable(View editableField, ImageView button) {
		ViewGroup parent = (ViewGroup) button.getParent();
		TextView fieldTxt = (TextView) parent.getChildAt(0);
		fieldContent = fieldTxt.getText().toString();
		fieldTxt.setVisibility(View.GONE);
		
		parent.addView(editableField, 0);
		button.setImageResource(R.drawable.ic_action_save_grey);
		button.setOnClickListener(new SaveEditClickListener(editableField, fieldTxt.getId()));
		
	}
	
	public void editRecord(String requestedDomain, String recordId){
		Intent intent = new Intent(this, NewRecordFormActivity.class);
		intent.putExtra(ViewDbActivity.DOMAIN, requestedDomain);
		intent.putExtra(ViewDbActivity.RECORD_ID, recordId);
		startActivity(intent);
	}
	
	public void editCustomer(View v){
		editRecord(DbConstants.CUSTOMERS, ((OrdersRecord)currentRecord).customer.recordId);
	}
	
	private void fillFields(){
		for(int i = 0; i<contentIds.length;i++){
			TextView txtView = (TextView)findViewById(contentIds[i]);
			if(txtView != null)
				txtView.setText(currentRecord.valueMap.get(currentRecord.displayIdsToColumns.get(contentIds[i])));
		}
		LinearLayout container = (LinearLayout) findViewById(R.id.orders_display_container);
		if(currentRecord instanceof OrdersRecord){
			
			OrdersRecord oRecord = (OrdersRecord)currentRecord;
			ArrayList<HashMap<String,String>> headerMapList = new ArrayList<>();
			ArrayList<ArrayList<HashMap<String, String>>> childDataList = new ArrayList<>();
			
			HashMap<String, String> orderDetailsMap = new HashMap<>();
			orderDetailsMap.put(KEY_MEMBER_TITLE, getString(R.string.label_order_details));
			orderDetailsMap.put(KEY_MEMBER_COUNT, null);
			headerMapList.add(orderDetailsMap);
			childDataList.add(getMemberList(DbConstants.ORDERS));
			
			// code to handle customer info
			if(oRecord.customer!=null){
				groupsCode += 1;
				HashMap<String, String> custHeaderMap = new HashMap<>();
				custHeaderMap.put(KEY_MEMBER_TITLE, getString(R.string.label_customer_info));
				custHeaderMap.put(KEY_MEMBER_COUNT, null);
				headerMapList.add(custHeaderMap);
				
				childDataList.add(getMemberList(DbConstants.CUSTOMERS));
				
			}
			
			// code to handle display of products
			if(oRecord.products.size()>0){
				groupsCode += 2;
				HashMap<String, String> prodHeaderMap = new HashMap<>();
				prodHeaderMap.put(KEY_MEMBER_TITLE, getString(R.string.label_product_info));
				prodHeaderMap.put(KEY_MEMBER_COUNT, Integer.toString(oRecord.products.size()));
				headerMapList.add(prodHeaderMap);
				
				childDataList.add(getMemberList(DbConstants.PRODUCTS));
			}
			
			// code to handle display of workers on job
			if(oRecord.workers.size()>0){
				groupsCode += 4;
				HashMap<String, String> workersHeaderMap = new HashMap<>();
				workersHeaderMap.put(KEY_MEMBER_TITLE, getString(R.string.label_workers_info));
				workersHeaderMap.put(KEY_MEMBER_COUNT, Integer.toString(oRecord.workers.size()));
				headerMapList.add(workersHeaderMap);
				
				childDataList.add(getMemberList(DbConstants.WORKERS));
			}
			int[] toFields = {R.id.field_title1,R.id.field_title2};
			String[] fromKeys = {KEY_MEMBER_TITLE,KEY_MEMBER_COUNT};
			String[] fromChildrenKeys = {KEY_CHILD_TITLE1,KEY_CHILD_TITLE2,KEY_CHILD_SUBTITLE1,KEY_CHILD_SUBTITLE2,KEY_PRODUCT_AMOUNT};
			int[] toChildFields = {R.id.field_title1,R.id.field_title2,R.id.field_subtitle1,R.id.field_subtitle2,R.id.products_display_amount};
			Map<Integer,String> custContentMap = null;
			if(oRecord.customer != null)
				custContentMap = oRecord.customer.displayIdsToColumns;
			OrdersDisplayListAdapter adapter = new OrdersDisplayListAdapter(this, headerMapList, fromKeys, toFields, 
					childDataList, R.layout.listview_item_3, fromChildrenKeys, toChildFields, oRecord.displayIdsToColumns,
					custContentMap, groupsCode);
			mListAdapter = adapter;
			lView.setAdapter(adapter);
			lView.setOnChildClickListener(new MemberOpenListener());
			lView.setDividerHeight(3);
			lView.expandGroup(0);
	//		lView.setFocusable(false);
		} else if(currentRecord instanceof ProductsRecord){
			((TextView)findViewById(R.id.products_display_amount)).setText(((ProductsRecord)currentRecord).amount);
			((TextView)findViewById(R.id.products_content_notes)).setText(((ProductsRecord)currentRecord).valueMap.get(Products.COLUMN_NAME_NOTES));
		}
	}
	
	
		
	
	//NOTE: constructing the adapter may take some time -- consider doing asynchronously -- NOT
	private ArrayList<HashMap<String, String>> getMemberList(String memberType){
		ArrayList<HashMap<String, String>> memberList = new ArrayList<>();
		OrdersRecord order = (OrdersRecord)currentRecord;
		
		// construct list of value hashes for display in expandable list
		// key corresponds to to[] and from[] parameters, value is value that will be displayed therein
		switch (memberType) {
		case DbConstants.ORDERS:
			memberList.add((HashMap<String, String>) order.valueMap);
			break;
		case DbConstants.PRODUCTS:
			for(ProductsRecord rec:order.products){
				HashMap<String, String> childData = new HashMap<>();
				childData.put(KEY_CHILD_TITLE1, rec.valueMap.get(Products.COLUMN_NAME_TITLE));
				childData.put(KEY_CHILD_TITLE2, rec.valueMap.get(Products.COLUMN_NAME_SUBTITLE));
				childData.put(KEY_CHILD_SUBTITLE1, rec.valueMap.get(Products.COLUMN_NAME_PRICE_PER_UNIT));
				childData.put(KEY_CHILD_SUBTITLE2, rec.valueMap.get(JobsProducts.COLUMN_NAME_AMOUNT));
				childData.put(KEY_PRODUCT_AMOUNT, ((ProductsRecord)rec).amount);
				memberList.add(childData);
			}
			break;
		case DbConstants.WORKERS:
			for(WorkersRecord rec:order.workers){
				HashMap<String, String> childData = new HashMap<>();
				childData.put(KEY_CHILD_TITLE1, rec.valueMap.get(Workers.COLUMN_NAME_FIRST_NAME));
				childData.put(KEY_CHILD_TITLE2, rec.valueMap.get(Workers.COLUMN_NAME_LAST_NAME));
				childData.put(KEY_CHILD_SUBTITLE1, rec.valueMap.get(Workers.COLUMN_NAME_OCCUPATION));
				childData.put(KEY_CHILD_SUBTITLE2, null);
				childData.put(KEY_PRODUCT_AMOUNT, null);
				memberList.add(childData);
			}
			break;
		case DbConstants.CUSTOMERS:
			memberList.add((HashMap<String, String>) order.customer.valueMap);
			break;

		default:
			break;
		}
		return memberList;
	}
	
	class MemberOpenListener implements OnChildClickListener{

		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			// TODO figure out a better way to do this
			// current implementation makes too many assumptions, and depends on current configuration not changing
			
			// Here we are giving each possible section weight if it's present in order to determine which
			// group was clicked
			int custPosition, workerPosition, prodPosition;
			OrdersRecord oRec = (OrdersRecord)currentRecord;
			if(oRec.customer != null)
				custPosition = 1;
			else
				custPosition = 0;
			// the following lines are just in case we add something else after workers in the future
			if(oRec.workers.size() > 0)
				workerPosition = 1;
			else
				workerPosition = 0;
			if(oRec.products.size() > 0)
				prodPosition = 1;
			else
				prodPosition = 0;
			
			String domain = "";
			DatabaseRecord member = null;
			Intent intent = new Intent(getBaseContext(), DisplayRecordActivity.class);
			if(groupPosition == custPosition + prodPosition + 1 && oRec.workers.size() > 0){
				domain = DbConstants.WORKERS;
				member = ((OrdersRecord)currentRecord).workers.get(childPosition);
			}else if(groupPosition == custPosition + 1 && oRec.products.size() > 0){
				domain = DbConstants.PRODUCTS;
				member = ((OrdersRecord)currentRecord).products.get(childPosition);
				intent.putExtra(KEY_PRODUCT_AMOUNT, ((ProductsRecord)member).amount);
				intent.putExtra(KEY_PRODUCT_PARENT_ID, currentRecord.recordId);
			}else
				return false;
			
			intent.putExtra(ViewDbActivity.DOMAIN, domain);
			intent.putExtra(ViewDbActivity.RECORD_ID, member.recordId);
			startActivity(intent);
			return true;
		}

		
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
			// workaround for problem of TextViews being null when ExpandableListView group containing them is not open
			if(currentRecord instanceof OrdersRecord){
				OrdersRecord record = (OrdersRecord)currentRecord;
				if(record.customer == null)
					return true;
				else{
					AsyncTask<String, Integer, Integer> task = new AsyncTask<String, Integer, Integer>() {
						
						@Override
						protected Integer doInBackground(String... params) {
							
							return null;
						}
						
						@Override
						protected void onPreExecute() {
							lView.expandGroup(1);
							super.onPreExecute();
						}
						
						@Override
						protected void onPostExecute(Integer result) {
							phoneFields[0] = R.id.customers_phone1;
							phoneFields[1] = R.id.customers_phone2;
							phoneFields[2] = R.id.customers_contact_phone;
							
							phoneLabels[0] = getString(R.string.customer_phone1_hint)+": "+((TextView)findViewById(phoneFields[0])).getText();
							phoneLabels[1] = getString(R.string.customer_phone2_hint)+": "+((TextView)findViewById(phoneFields[1])).getText();
							phoneLabels[2] = getString(R.string.customer_contact_phone_hint)+": "+((TextView)findViewById(phoneFields[2])).getText();
							
							phoneString = "";
							ArrayList<String> phones = new ArrayList<>();
							for(int phoneId:phoneFields){
								TextView tView = (TextView)findViewById(phoneId);
								if(tView!=null && tView.getText().length()>=9)
									phones.add(tView.getText().toString());
							}
							if(phones.size()==1){
								phoneString = phones.get(0);
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
							else if(phones.size()==0){
								Toast.makeText(getBaseContext(), getString(R.string.message_no_phone_number), Toast.LENGTH_SHORT).show();
								return ;
							}else{
								DialogFragment newFragment = new PhonePickerFragment();
				    			newFragment.show(getFragmentManager(), "phone nums");
							}
							super.onPostExecute(result);
							
						}
					};
					task.execute(new String[]{});
					
					return true;
					// end stupid workaround
				}
			}
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
				
				break;
			case DbConstants.WORKERS:
				phoneFields[0] = R.id.workers_phone1;
				phoneFields[1] = R.id.workers_phone2;
				phoneLabels[0] = getString(R.string.workers_phone1_hint)+": "+((TextView)findViewById(phoneFields[0])).getText();
				phoneLabels[1] = getString(R.string.workers_phone2_hint)+": "+((TextView)findViewById(phoneFields[1])).getText();
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
			// ridiculous workaround for orders records
			if(currentRecord instanceof OrdersRecord && ((OrdersRecord)currentRecord).customer != null){
				AsyncTask<String, Integer, Integer> task = new AsyncTask<String, Integer, Integer>() {
					
					@Override
					protected void onPreExecute() {
						lView.expandGroup(1);
						super.onPreExecute();
					}
					
					@Override
					protected Integer doInBackground(String... params) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					protected void onPostExecute(Integer result) {
						TextView emailTxt = (TextView)findViewById(emailAddressId);
						if(emailTxt != null){
							String emailAddress = emailTxt.getText().toString();
							// TODO use email regex
							if(emailAddress.length()>9 && emailAddress.contains("@")){
								Intent emailIntent = new Intent(Intent.ACTION_SEND);
								emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
								emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailAddress});
								startActivity(emailIntent);
								return ;
							}
						}else{
							Toast.makeText(getBaseContext(), R.string.message_no_email, Toast.LENGTH_SHORT).show();
						}

						super.onPostExecute(result);
					}
				};
				task.execute(new String[]{});
				return true;
				// end of workaround
			}
			TextView emailTxt = (TextView)findViewById(emailAddressId);
			if(emailTxt != null){
				String emailAddress = emailTxt.getText().toString();
				// TODO use email regex
				if(emailAddress.length()>9 && emailAddress.contains("@")){
					Intent emailIntent = new Intent(Intent.ACTION_SEND);
					emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
					emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailAddress});
					startActivity(emailIntent);
					return true;
				}
			}else{
				Toast.makeText(this, R.string.message_no_email, Toast.LENGTH_SHORT).show();
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
	public void onDialogNegativeClick(DialogFragment dialog) {}
	
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
