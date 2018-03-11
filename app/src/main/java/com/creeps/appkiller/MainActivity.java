package com.creeps.appkiller;

import android.app.DialogFragment;
import android.content.Intent;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.creeps.appkiller.core.services.DatabaseHandler;
import com.creeps.appkiller.core.services.ProcessBlockerService;
import com.creeps.appkiller.R;
import com.creeps.appkiller.core.services.thread.ProcessLister;
import com.creeps.appkiller.ui.AppListRecyclerAdapter;
import com.creeps.appkiller.ui.AppsListSelectorDialog;
import com.creeps.appkiller.ui.ProfileFragment;
import com.creeps.appkiller.ui.ProfileNamePopup;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity implements ProfileNamePopup.ProfileSuccessCallback,AppsListSelectorDialog.AppsListSelectorDialogCallback{

    private final static String TAG="MainActivity";
    public final static String BLACKLIST="blackList";

    private FragmentManager fragmentManager;
    private FloatingActionButton fab;
    @Override
    protected void onStart(){
        super.onStart();
        Intent intent=new Intent(this, ProcessBlockerService.class);
        this.startService(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHandler ref=DatabaseHandler.getInstance(this);
        this.fragmentManager=this.getSupportFragmentManager();
        this.placeFragment(new ProfileFragment());
        this.fab=(FloatingActionButton) findViewById(R.id.fab);
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO create a popupDialog to make a new profile*/
                ProfileNamePopup p=ProfileNamePopup.getInstance("Create a new Profile",MainActivity.this);
                p.show(MainActivity.this.getSupportFragmentManager(),"CreateProfile");

            }
        });
    }






    public void placeFragment(Fragment fragment){
        FragmentTransaction ft=fragmentManager.beginTransaction();
        ft.replace(R.id.placeholder,fragment);
        ft.commit();
    }

    /* Overriding ProfilePopupCallback. Using this since the Fragment already exists*/
    @Override
    public void call(String profileName){
        if(!this.isFinishing()) {
            Log.d(TAG,"call");
            AppsListSelectorDialog frag = AppsListSelectorDialog.getInstance(profileName,this,this);
            frag.show(this.getSupportFragmentManager(), "AppsList");
        }
    }


    /* Overriding AppListSelectorDialog to be used to repaint Profiles Fragment*/
    @Override
    public void reloadProfileFragment(){
        this.placeFragment(new ProfileFragment());
    }





}
