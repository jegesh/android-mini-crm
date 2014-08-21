package com.example.learningsqlite;

import java.util.Calendar;

import com.example.learningsqlite.ConfirmDeletionDialog.NoticeDialogListener;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerInputDialog extends DialogFragment
implements DatePickerDialog.OnDateSetListener {
	 public interface NoticeDatePickerDialogListener {
	        public void onDateDialogPositiveClick(DialogFragment dialog);
	        public void onDateDialogNegativeClick(DialogFragment dialog);
	    }
	    
	    // Use this instance of the interface to deliver action events
	    NoticeDatePickerDialogListener mListener;
	    
	    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	    @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        // Verify that the host activity implements the callback interface
	        try {
	            // Instantiate the NoticeDialogListener so we can send events to the host
	            mListener = (NoticeDatePickerDialogListener) activity;
	        } catch (ClassCastException e) {
	            // The activity doesn't implement the interface, throw exception
	            throw new ClassCastException(activity.toString()
	                    + " must implement NoticeDialogListener");
	        }
	    }
		

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		int[] dateValues = {day, month, year};
		// this is imho an ugly and bad implementation, but it'll work for now. I guess...
		DbConstants.lastPickedDate = dateValues;
		mListener.onDateDialogPositiveClick(this);
	}
}