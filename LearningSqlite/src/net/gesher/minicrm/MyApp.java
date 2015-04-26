package net.gesher.minicrm;

import com.parse.Parse;
import com.parse.ParseCrashReporting;

import database_files.DatabaseRecord;
import database_files.OrdersRecord;
import android.app.Application;

public class MyApp extends Application {
	public int memberId;
	public DatabaseRecord newMember;
	public OrdersRecord tempOrdersRecord;
	
	@Override
	public void onCreate() {
		// Enable Local Datastore.
		Parse.enableLocalDatastore(this);
		// Enable Crash Reporting
		ParseCrashReporting.enable(this);
		Parse.initialize(this, "ybOVDxbMuqoGSrGQRa1jnzmgwPTLzZ2ZnwrMKTFG", "t62Au8cWm9l79mON50DvYnMcNOVJnFXZu8ykQYGu");

		super.onCreate();
	}
		
	
}
