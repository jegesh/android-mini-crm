package database_files;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;

public abstract class DatabaseRecord {
	private static final String TAG	= "DatabaseRecord class";
    public String recordId;
    public String tableName;
    public int editLayoutId;
    public int displayLayoutId;
    public int titleId;

    public Map<String,String> valueMap;
    public int[] contentIds;
    public Map<Integer,String> inputIdsToColumns; // to be initialized in child class
    public Map<Integer,String> displayIdsToColumns; // ditto

    public SQLiteOpenHelper dbHelper;
    public SQLiteDatabase db;

    public DatabaseRecord(String table, int editId, int displayId, int titleId) {
        tableName = table;
        editLayoutId = editId;
        displayLayoutId = displayId;
        this.titleId = titleId;
        
    }

    public void setValues() {
        Cursor c ;
        c = db.query(tableName, null, "_ID = ?", new String[]{recordId}, null, null, null);

        if (c.moveToFirst()) {
            Map<String,String> map = new HashMap<>();
            for(int i = 0; i<c.getColumnNames().length;i++)
                map.put(c.getColumnNames()[i],c.getString(i));
            valueMap = map;
        }
        c.close();
    }
    
    public void populateInputFields(Activity activity){
    	for(Entry<Integer,String> e:inputIdsToColumns.entrySet()){
			EditText input = (EditText)activity.findViewById(e.getKey());
			try {
				input.setText(valueMap.get(e.getValue()));
			} catch (Exception ex) {
				Log.d(TAG, "Error: "  + ex.getMessage());
			}
			
		}
    }

    public void saveDataToObject(Activity activity){
    	if(valueMap == null)
    		valueMap = new HashMap<String,String>();
    	for(Entry<Integer,String> e:inputIdsToColumns.entrySet()){

    		Log.d(TAG, "Entry key: "+e.getKey()+", Entry value: "+e.getValue());
			EditText input = (EditText)(activity.findViewById(e.getKey()));
	//		Log.d(TAG, "input's value: "+input.getText().toString());
			String val = "";
			if(input.getText() != null)
				val = input.getText().toString();
			valueMap.put(e.getValue(), val);
    	}
    }
    
    private ContentValues mapValuesToCV(){
        ContentValues cv = new ContentValues();
        for(Map.Entry<String,String> e:valueMap.entrySet())
            cv.put(e.getKey(),e.getValue());
        return cv;
    }

    public static ContentValues mapValuesToCV(Map<String,String> values){
        ContentValues cv = new ContentValues();
        for(Map.Entry<String,String> e:values.entrySet())
            cv.put(e.getKey(),e.getValue());
        return cv;
    }
    
    public boolean updateRecord(Map<String,String> values) {
  //      updateValueMap(values);
        ContentValues cv = mapValuesToCV(values);
    	if(!values.values().isEmpty()){
    		db.update(tableName, cv, "_ID = ?", new String[]{recordId});
    		return true;
    	}
    		return false;
    }

    public void deleteRecord() {
        db.delete(tableName, "_ID = ?", new String[]{recordId});
    }
    
    public boolean insertNewRecord(){
    	try {
    		// check that map contains some data
        	if(!valueMap.values().isEmpty()){
        		ContentValues cv = mapValuesToCV();
        		
        		try {
            		recordId = Integer.toString((int)db.insert(tableName, null, cv));
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
        	}
        	
		} catch (Exception e) {
			Log.d(TAG, "Error inserting new record: "+e.getMessage());
			return false;
		}
		return true;
    	
    }

    protected abstract void setInputIdsToColumns();
    protected abstract void setDisplayIdsToColumns();

}
