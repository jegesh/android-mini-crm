package com.example.learningsqlite;

import java.util.concurrent.atomic.AtomicInteger;

//import com.example.learningsqlite.CustomersContract.Users;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
		setContentView(R.layout.activity_main);

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	/*
	public void makeDb(View view){
		UsersDBHelper usersDB = new UsersDBHelper(this);
		SQLiteDatabase db = usersDB.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		EditText user = (EditText)findViewById(R.id.user_name);
		String userContent = user.getText().toString();
		values.put(Users.COLUMN_NAME_USERNAME, userContent);
		EditText pass = (EditText)findViewById(R.id.email);
		String passContent = pass.getText().toString();
		values.put(Users.COLUMN_NAME_PASSWORD, passContent);
		EditText email = (EditText)findViewById(R.id.password);
		String emailContent = email.getText().toString();
		values.put(Users.COLUMN_NAME_EMAIL, emailContent);
		long newRowId = db.insert(Users.TABLE_NAME, null, values);
		Intent intent = new Intent(this, ViewDbActivity.class);
		startActivity(intent);

	}*/
	
	public void viewDb(View view){
		Intent intent = new Intent(this, ViewDbActivity.class);
	//	String[] dbInfoArray;
		String domain = (String) view.getTag();
	//	Log.d(TAG, domain);
				
		intent.putExtra(DOMAIN, domain);
		startActivity(intent);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
