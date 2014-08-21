package com.example.learningsqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import android.view.View;

public interface EditableForm {
	
	
	
	public boolean newRecord(LinkedList<Object> values);


	void saveNew(View view);
	boolean updateRecord(String[] values);

}
