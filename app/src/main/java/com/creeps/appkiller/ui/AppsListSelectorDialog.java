package com.creeps.appkiller.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.app.usage.UsageStats;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.creeps.appkiller.R;
import com.creeps.appkiller.core.services.DatabaseHandler;
import com.creeps.appkiller.core.services.model.Profile;
import com.creeps.appkiller.core.services.model.ProfilePackages;
import com.creeps.appkiller.core.services.thread.ProcessLister;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rohan on 4/3/18.
 */

public class AppsListSelectorDialog extends android.support.v4.app.DialogFragment {
    private RecyclerView recyclerView;
    private static Activity baseActivity;
    private AppListRecyclerAdapter adapter;
    private static AppsListSelectorDialogCallback callback;
    private static Profile preexisting;
    private static ProfileAdapterInnerAppListAdapter rec;
    private final static int INIT_START_TIME=0;
    private final static int INIT_END_TIME=86340;
    private final static byte INIT_WEEK_CONST=127;
    private final static String TAG="AppSelectorDialog";
    public static AppsListSelectorDialog getInstance(String profileName, Activity baseActivity, AppsListSelectorDialogCallback callback, Profile toBeEdited,ProfileAdapterInnerAppListAdapter re){
        AppsListSelectorDialog appsListSelectorDialog=new AppsListSelectorDialog();
        Bundle bundle=new Bundle();
        bundle.putString("profileName",profileName);
        appsListSelectorDialog.setArguments(bundle);
        AppsListSelectorDialog.baseActivity=baseActivity;
        AppsListSelectorDialog.callback=callback;
        AppsListSelectorDialog.preexisting=toBeEdited;
        AppsListSelectorDialog.rec=re;
        return appsListSelectorDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        LayoutInflater inflater=LayoutInflater.from(baseActivity.getApplicationContext());
        View view=inflater.inflate(R.layout.popup_app_list,null);
        this.recyclerView=view.findViewById(R.id.appListRecycler);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(baseActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        final String profileName=this.getArguments().getString("profileName");
        this.adapter=new AppListRecyclerAdapter(baseActivity, (ArrayList<UsageStats>) ProcessLister.getUsageAccessData(baseActivity,true),preexisting==null?null:preexisting.getPackages());

        this.recyclerView.setLayoutManager(linearLayoutManager);
        this.recyclerView.setAdapter(adapter);

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(baseActivity);
        alertDialog.setView(view);

        alertDialog.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* TODO obtain all package names with whose flags are set*/
                HashMap<Integer,String> selected=AppsListSelectorDialog.this.adapter.getSelectedRef();
                //todo perform insertion in the database
                if(selected!=null && selected.size()>0){


                    if(preexisting==null) {
                        //todo change this to actual time obtained from ClockHandler and obtain the value of days selected
                        DatabaseHandler db = DatabaseHandler.getInstance(baseActivity);
                        long id = db.addProfile(profileName, INIT_START_TIME, INIT_END_TIME, INIT_WEEK_CONST);


                        db.addPackagesToProfile(id, new ArrayList<String>(selected.values()));
                        if (callback != null)
                            callback.reloadProfileFragment();
                    }else{
                        //todo obtain the new list from the adapter

                            if(selected.size()>1) {
                                DatabaseHandler db = DatabaseHandler.getInstance(baseActivity);
                                Log.d(TAG, selected.values().toString());
                                db.updateProfilePackages(preexisting.getId(), new ArrayList<String>(selected.values()));
                                preexisting.setPackages(db.readAllPackages(preexisting.getId()));
                                if (rec != null) {
                                    rec.setProfilePackages(preexisting.getPackages());

                                }
                            }else
                                Toast.makeText(baseActivity,"There must be atleast 1 app",Toast.LENGTH_SHORT).show();
                    }


                }else
                    Toast.makeText(baseActivity,"Coulndt make entry",Toast.LENGTH_SHORT).show();
            }
        });
        return alertDialog.create();

    }
    /* Callback to be fired when the Profiles table is updated.*/
    public interface AppsListSelectorDialogCallback{
        public void reloadProfileFragment();
    }
}
