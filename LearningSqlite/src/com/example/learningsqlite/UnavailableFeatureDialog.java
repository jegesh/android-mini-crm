package com.example.learningsqlite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class UnavailableFeatureDialog extends DialogFragment {

	  @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
			
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.unavailable_feature_message).setPositiveButton(R.string.unavailable_feature_confirmation_button, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   //
	                	   
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
}
