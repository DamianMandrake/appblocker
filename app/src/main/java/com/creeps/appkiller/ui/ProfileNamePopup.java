package com.creeps.appkiller.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.creeps.appkiller.MainActivity;
import com.creeps.appkiller.R;

/**
 * Created by rohan on 4/3/18.
 * This popup is used to obtain the name of the profile
 */

public class ProfileNamePopup extends DialogFragment {
    private EditText profileName;
    private static ProfileSuccessCallback successCallback;
    public static ProfileNamePopup getInstance(String title,ProfileSuccessCallback callback){
        ProfileNamePopup popupDialogFrag=new ProfileNamePopup();
        Bundle bundle =new Bundle();
        bundle.putString("title", title);
        popupDialogFrag.setArguments(bundle);
        successCallback=callback;
        return popupDialogFrag;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        LayoutInflater layoutInflater=LayoutInflater.from(this.getContext());
        View view=layoutInflater.inflate(R.layout.popup_profile_name,null);
        this.profileName=view.findViewById(R.id.prof_actual_name);


        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this.getContext());
        alertDialog.setTitle(getArguments().getString("title"));
        alertDialog.setView(view);
        alertDialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
           @Override
            public void onClick(DialogInterface dialogInterface,int which){
                /* TODO open a dialogFragment containing a list of apps*/
                if(!profileName.getText().toString().trim().equals(""))
                    if(successCallback!=null) {
                        ProfileNamePopup.this.dismiss();
                        successCallback.call(profileName.getText().toString());


                }
                else
                        Toast.makeText(ProfileNamePopup.this.getActivity(),"please enter valid text",Toast.LENGTH_SHORT).show();

           }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog,int which) {
                ProfileNamePopup.this.dismiss();
            }
        });
        return alertDialog.create();
    }
    public interface ProfileSuccessCallback{
        public void call(String profileName);
    }


}
