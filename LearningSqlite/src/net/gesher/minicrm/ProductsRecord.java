package net.gesher.minicrm;

import java.util.HashMap;
import java.util.Map.Entry;

import database_files.ProductsContract;
import database_files.ProductsDBHelper;
import database_files.ProductsContract.Products;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class ProductsRecord extends DatabaseRecord {
	String amount;
	
	public ProductsRecord( Context activity){
		super(ProductsContract.Products.TABLE_NAME,R.layout.products_input_form,R.layout.products_display_form,R.string.title_products_main); 
		db = new ProductsDBHelper(activity).getWritableDatabase();
		inputIdsToColumns = new HashMap<Integer, String>();
		displayIdsToColumns = new HashMap<Integer, String>();
		setInputIdsToColumns();
		setDisplayIdsToColumns();
	}
	
	public ProductsRecord(String id, Context activity){
		this(activity);
		recordId = id;
		setValues();
	}
	
	// for populating individual member input forms in orders input form
	public void populateInputFields(View context){
    	for(Entry<Integer,String> e:inputIdsToColumns.entrySet()){
			EditText input = (EditText)context.findViewById(e.getKey());
			try {
				input.setText(valueMap.get(e.getValue()));
			} catch (Exception ex) {
				Log.d("ProductsRecord", ex.getMessage());
			}
			
		}
    	EditText amountInput = (EditText)context.findViewById(R.id.products_input_amount);
    	amountInput.setText(amount);
    }
	
	// for saving individual member input forms in orders input form
	public void saveDataToObject(View context){
		valueMap = new HashMap<String,String>();
    	for(Entry<Integer,String> e:inputIdsToColumns.entrySet()){

    		Log.d("Products Record", "Entry key: "+e.getKey()+", Entry value: "+e.getValue());
			EditText input = (EditText)(context.findViewById(e.getKey()));
			Log.d("Products Record", "input's value: "+input.getText().toString());
			String val = input.getText().toString();
			valueMap.put(e.getValue(), val);
    	}
    	Spinner sp = (Spinner)context.findViewById(R.id.products_input_sale_unit_spinner);
    	if(sp.getSelectedItem() != null)
    		valueMap.put(Products.COLUMN_NAME_SELL_BY_UNIT, (String) ((TextView)sp.getSelectedView()).getText());
    	EditText amountInput = (EditText)context.findViewById(R.id.products_input_amount);
    	amount = amountInput.getText().toString();
    }
	
	@Override
		public void saveDataToObject(Activity activity) {
			super.saveDataToObject(activity);
			Spinner sp = (Spinner)activity.findViewById(R.id.products_input_sale_unit_spinner);
			if(sp.isSelected())
				valueMap.put(Products.COLUMN_NAME_SELL_BY_UNIT, (String) ((TextView)sp.getSelectedView()).getText());
			else
				valueMap.put(Products.COLUMN_NAME_SELL_BY_UNIT, (String)((EditText)activity.findViewById(R.id.products_input_new_sale_unit)).getText().toString());
		}

	@Override
	protected void setInputIdsToColumns() {
		inputIdsToColumns.put(R.id.products_input_title, Products.COLUMN_NAME_TITLE);
		inputIdsToColumns.put(R.id.products_input_subtitle,	Products.COLUMN_NAME_SUBTITLE);
		inputIdsToColumns.put(R.id.products_input_price, Products.COLUMN_NAME_PRICE_PER_UNIT);
		inputIdsToColumns.put(R.id.products_input_notes, Products.COLUMN_NAME_NOTES);
		
	}

	@Override
	protected void setDisplayIdsToColumns() {
		displayIdsToColumns.put(R.id.products_content_title, Products.COLUMN_NAME_TITLE);
		displayIdsToColumns.put(R.id.products_content_subtitle, Products.COLUMN_NAME_SUBTITLE);
		displayIdsToColumns.put(R.id.products_content_unit_price, Products.COLUMN_NAME_PRICE_PER_UNIT);
		displayIdsToColumns.put(R.id.products_content_sale_unit, Products.COLUMN_NAME_SELL_BY_UNIT);
		displayIdsToColumns.put(R.id.products_content_notes, Products.COLUMN_NAME_NOTES);
		
	}
	
}
