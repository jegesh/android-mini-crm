package net.gesher.minicrm;

import database_files.CustomersDBHelper;
import database_files.ProductsDBHelper;
import database_files.WorkersDBHelper;
import database_files.CustomersContract.Customers;
import database_files.ProductsContract.Products;
import database_files.WorkersContract.Workers;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;


public class AddMemberDialog extends DialogFragment implements OnItemSelectedListener {
	 	    
		public interface AddMemberDialogListener{
			public void onAddMemberAcceptClickListener();
			public void onAddMemberNewClickListener();
		}
		SQLiteDatabase db;
		int dialogTitle;
		Cursor dbCursor;
		String[] fromFields;
		int[] toElements;
		SimpleCursorAdapter cAdapter;
		public String addedMemberId;
		Spinner spinner;
	    // Use this instance of the interface to deliver action events
	    AddMemberDialogListener mListener;
		private boolean initialization;
		protected int unavailableMemberMsgId;
	    
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        // set title and form context in accordance with button that opened the dialog
	        
	        initialization = true;
	        if(getActivity() instanceof NewRecordFormActivity){
	        	switch (((NewRecordFormActivity)getActivity()).addedComponentDomain) {
		        	case "customers":
		        		Log.d("in switch", "customers");
		        		dialogTitle = R.string.button_add_new_customer;
		        		db = new CustomersDBHelper(getActivity()).getReadableDatabase();
		        		dbCursor = db.query(Customers.TABLE_NAME, new String[]{Customers._ID,Customers.COLUMN_NAME_FIRST_NAME,
		        				Customers.COLUMN_NAME_LAST_NAME,Customers.COLUMN_NAME_PHONE1}, null , null, null, null, Customers.COLUMN_NAME_FIRST_NAME);
		        		unavailableMemberMsgId = R.string.message_no_customers;
		        		fromFields =  new String[]{Customers.COLUMN_NAME_FIRST_NAME,Customers.COLUMN_NAME_LAST_NAME,Customers.COLUMN_NAME_PHONE1};
		        		//	toElements = new int[]{R.id.titleText1,R.id.titleText2,R.id.subtitle};
		        	//	db.close();
		        		break;
		        	case "workers":
		        		dialogTitle = R.string.button_add_new_worker;
		        		db = new WorkersDBHelper(getActivity()).getReadableDatabase();
		        		dbCursor = db.query(Workers.TABLE_NAME, new String[]{Workers._ID , Workers.COLUMN_NAME_FIRST_NAME,Workers.COLUMN_NAME_LAST_NAME,
		        				Workers.COLUMN_NAME_OCCUPATION}, null, null, null, null, Workers.COLUMN_NAME_FIRST_NAME);
		        		unavailableMemberMsgId = R.string.message_no_workers;
		        		fromFields = new String[]{Workers.COLUMN_NAME_FIRST_NAME,Workers.COLUMN_NAME_LAST_NAME,Workers.COLUMN_NAME_OCCUPATION};
		        //		toElements = new int[]{R.id.titleText1,R.id.titleText2,R.id.subtitle};
//		        		db1.close();
		        		break;
	
		        	case "products":
		        		dialogTitle = R.string.button_add_new_product;
		        		db = new ProductsDBHelper(getActivity()).getReadableDatabase();
		        		dbCursor = db.query(Products.TABLE_NAME, new String[]{Products._ID , Products.COLUMN_NAME_TITLE,Products.COLUMN_NAME_SUBTITLE}, 
		        				null, null, null, null, Products.COLUMN_NAME_TITLE);
		        		unavailableMemberMsgId = R.string.message_no_products;
		        		fromFields = new String[]{Products.COLUMN_NAME_TITLE,Products.COLUMN_NAME_SUBTITLE};
		        //		toElements = new int[]{R.id.titleText1,R.id.subtitle};
	//	        		db2.close();
		        		break;
	        	}
	        }
	        
	        // Get the layout inflater
	        LayoutInflater inflater = getActivity().getLayoutInflater();

	        // Inflate and set the layout for the dialog
	        // Pass null as the parent view because its going in the dialog layout
	        builder.setView(inflater.inflate(R.layout.add_customer_dialog_layout, null))
	        
	               .setPositiveButton(R.string.button_accept_added_component, new DialogInterface.OnClickListener() {
	                   @Override
	                   public void onClick(DialogInterface dialog, int id) {
	                	   if(dbCursor.getCount()<1)
	                		   Toast.makeText(getActivity(), getString(unavailableMemberMsgId), Toast.LENGTH_SHORT).show();
	                	   mListener.onAddMemberAcceptClickListener();
	                   }
	               })
	               .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       AddMemberDialog.this.getDialog().cancel();
	                   }
	               }).setNeutralButton(R.string.button_add_new_component_from_dialog, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO close this dialog and open a new customer/worker/product dialog
						mListener.onAddMemberNewClickListener();
					//	AddCustomerDialog.this.getDialog().cancel();
					}
	               }).setTitle(R.string.add_record_button)
	               ;
	        
	        
	        
	        return builder.create();
	    }
	    
	      
	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	    	super.onActivityCreated(savedInstanceState);
	    	

	    }
	    

	    
	    // Override the Fragment.onAttach() method to instantiate the listener
	    @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        // Verify that the host activity implements the callback interface
	        try {
	            // Instantiate the listener so we can send events to the host
	            mListener = (AddMemberDialogListener) activity;
	           
	        } catch (ClassCastException e) {
	            // The activity doesn't implement the interface, throw exception
	            throw new ClassCastException(activity.toString() + " must implement Listener interface");
	        }
	    }
	    
	    @Override
	    public void onResume() {
	    	spinner = (Spinner)getDialog().findViewById(R.id.add_component_spinner);
	        if(dbCursor.moveToFirst()){
	        	/*
	        	cAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_item, dbCursor, fromFields, toElements, 0);
		        
		        */
	        	String[] names = new String[dbCursor.getCount()];
	        	dbCursor.moveToFirst();
	        	for(int i=0;i<dbCursor.getCount();i++){
	        		if(dbCursor.getColumnCount()>3)
	        			names[i] = dbCursor.getString(1)+" "+dbCursor.getString(2)+" ("+dbCursor.getString(3)+")";
	        		else
	        			names[i] = dbCursor.getString(1)+" ("+dbCursor.getString(2)+")";
	        		dbCursor.moveToNext();
	        	}
	        	ArrayAdapter<String> spinAdapt = new ArrayAdapter<>(getActivity(), R.layout.add_member_spinner_item, names);
	        	
		        // set click listener for spinner
		        spinner.setOnItemSelectedListener(this);
		        spinAdapt.setDropDownViewResource(R.layout.add_member_dropdown_item);
		        spinner.setAdapter(spinAdapt);
		        
	        }else{
	        	spinner.setVisibility(View.GONE); // TODO set prompt to message: no items available
	        	
	        }
	        super.onResume();
	    }
	    
	    @Override
	    public void onStop() {
	    if(db!=null)
	    	db.close();
	    dbCursor.close();
	    super.onStop();
	    }


		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position,	long id) {
			if(initialization){
		//		spinner.setSelection(-1);
				initialization = false;
				spinner.setPrompt(getString(R.string.add_member_spinner_prompt));
				dbCursor.moveToPosition(position);
				addedMemberId = dbCursor.getString(0);

			}else{
				dbCursor.moveToPosition(position);
				addedMemberId = dbCursor.getString(0);
			
				Toast.makeText(parent.getContext(),"record id: "+dbCursor.getString(0) , Toast.LENGTH_SHORT).show();
			}
		}


		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// do nothing
			
		}
		

	
}
