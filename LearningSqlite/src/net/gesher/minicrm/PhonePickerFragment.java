package net.gesher.minicrm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class PhonePickerFragment extends DialogFragment {
    PhoneDialogListener phoneListener;
    int clickedPhoneIndex;
    boolean initializing;
    
    public interface PhoneDialogListener {
    	    public void onPhoneDialogPositiveClick(PhonePickerFragment dialog);
  	     	
  	    }
  	    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initializing = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_phone_number_dialog);
        builder.setSingleChoiceItems(((DisplayRecordActivity)getActivity()).phoneLabels, -1, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int whichIndexSelected) {
					clickedPhoneIndex = whichIndexSelected;
			}
		});
        builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                      phoneListener.onPhoneDialogPositiveClick(PhonePickerFragment.this);
                   }
               })
               .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            phoneListener = (PhoneDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
