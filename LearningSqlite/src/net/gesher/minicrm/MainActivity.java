package net.gesher.minicrm;

import java.util.concurrent.atomic.AtomicInteger;

import com.parse.ParseObject;

import database_files.GeneralDbHelper;

//import com.example.learningsqlite.CustomersContract.Users;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	public static final String DOMAIN = "domain";
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**
	 * Generate a value suitable for use in {@link #setId(int)}.
	 * This value will not collide with ID values generated at build time by aapt for R.id.
	 *
	 * @return a generated ID value
	 */
	public static int generateViewId() {
		
	    for (;;) {
	        final int result = sNextGeneratedId.get();
	        // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
	        int newValue = result + 1;
	        if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
	        if (sNextGeneratedId.compareAndSet(result, newValue)) {
	            return result;
	        }
	    }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		instantiateDbTables();
	}

	private void instantiateDbTables(){
		AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {
			
			@Override
			protected String doInBackground(String... arg0) {
				SQLiteDatabase db = new GeneralDbHelper(getBaseContext()).getReadableDatabase();
				db.close();
				return null;
			}
		};
		task.execute(null,null,null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
	
	
	public void viewDb(View view){
		Intent intent = new Intent(this, ViewDbActivity.class);
		String domain = (String) view.getTag();
	//	Log.d(TAG, domain);
				
		intent.putExtra(DOMAIN, domain);
		startActivity(intent);
	}

	
}
