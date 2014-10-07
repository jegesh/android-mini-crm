package database_files;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import database_files.WorkersContract.Workers;

import net.gesher.minicrm.R;
import net.gesher.minicrm.R.id;
import net.gesher.minicrm.R.layout;
import net.gesher.minicrm.R.string;



import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class WorkersRecord extends DatabaseRecord {
	//public String recordId;
	public boolean newlyAdded;

	
	public WorkersRecord(Context activity){
		super(WorkersContract.Workers.TABLE_NAME,R.layout.workers_input_form,R.layout.workers_display_form,R.string.title_workers_main);
		db = new WorkersDBHelper(activity).getWritableDatabase();
		inputIdsToColumns = new HashMap<Integer, String>();
		displayIdsToColumns = new HashMap<Integer, String>();
		setInputIdsToColumns();
		setDisplayIdsToColumns();
		newlyAdded = false;
	}
	
	public WorkersRecord(String id, Context activity){
		this(activity);
		recordId = id;
		setValues();
	}
	
	// method for populating multiple member input forms in orders input form
	public void populateInputFields(View context){
    	for(Entry<Integer,String> e:inputIdsToColumns.entrySet()){
			EditText input = (EditText)context.findViewById(e.getKey());
			try {
				input.setText(valueMap.get(e.getValue()));
			} catch (Exception ex) {
				Log.d("WorkersRecord", ex.getMessage());
			}
			
		}
    }
	
	// method for saving from multiple member input forms in orders input form
	public void saveDataToObject(View context){
		valueMap = new HashMap<String,String>();
    	for(Entry<Integer,String> e:inputIdsToColumns.entrySet()){

    		Log.d("Workers Record", "Entry key: "+e.getKey()+", Entry value: "+e.getValue());
			EditText input = (EditText)(context.findViewById(e.getKey()));
			Log.d("Workers Record", "input's value: "+input.getText().toString());
			String val = input.getText().toString();
			valueMap.put(e.getValue(), val);
    	}
    	Spinner sp = (Spinner)context.findViewById(R.id.workers_relationship_spinner);
    	valueMap.put(Workers.COLUMN_NAME_RELATIONSHIP, (String)sp.getSelectedItem());
    }

	@Override
	public void saveDataToObject(Activity activity) {
		super.saveDataToObject(activity);
		Spinner sp = (Spinner)activity.findViewById(R.id.workers_relationship_spinner);
    	valueMap.put(Workers.COLUMN_NAME_RELATIONSHIP, (String)sp.getSelectedItem());
	}


	@Override
	protected void setInputIdsToColumns() {
		inputIdsToColumns.put(R.id.worker_new_record_first_name, Workers.COLUMN_NAME_FIRST_NAME);
		inputIdsToColumns.put(R.id.worker_new_record_last_name, Workers.COLUMN_NAME_LAST_NAME);
		inputIdsToColumns.put(R.id.worker_new_record_phone1, Workers.COLUMN_NAME_PHONE1);
		inputIdsToColumns.put(R.id.worker_new_record_phone2, Workers.COLUMN_NAME_PHONE2);
		inputIdsToColumns.put(R.id.worker_new_record_address, Workers.COLUMN_NAME_ADDRESS);
		inputIdsToColumns.put(R.id.worker_new_record_email, Workers.COLUMN_NAME_EMAIL);
		inputIdsToColumns.put(R.id.worker_new_record_occupation, Workers.COLUMN_NAME_OCCUPATION);
	}

	@Override
	protected void setDisplayIdsToColumns() {
		displayIdsToColumns.put(R.id.workers_first_name, Workers.COLUMN_NAME_FIRST_NAME);
		displayIdsToColumns.put(R.id.workers_last_name, Workers.COLUMN_NAME_LAST_NAME);
		displayIdsToColumns.put(R.id.workers_address, Workers.COLUMN_NAME_ADDRESS);
		displayIdsToColumns.put(R.id.workers_email, Workers.COLUMN_NAME_EMAIL);
		displayIdsToColumns.put(R.id.workers_occupation, Workers.COLUMN_NAME_OCCUPATION);
		displayIdsToColumns.put(R.id.workers_phone1, Workers.COLUMN_NAME_PHONE1);
		displayIdsToColumns.put(R.id.workers_phone2, Workers.COLUMN_NAME_PHONE2);
		displayIdsToColumns.put(R.id.workers_relationship, Workers.COLUMN_NAME_RELATIONSHIP);
		
	}

	
}
