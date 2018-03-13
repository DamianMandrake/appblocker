package com.creeps.appkiller;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.creeps.appkiller.core.services.DatabaseHandler;
import com.creeps.appkiller.core.services.ProcessBlockerService;

import com.creeps.appkiller.ui.AppsListSelectorDialog;
import com.creeps.appkiller.ui.ProfileFragment;
import com.creeps.appkiller.ui.ProfileNamePopup;

public class MainActivity extends AppCompatActivity implements ProfileNamePopup.ProfileSuccessCallback,AppsListSelectorDialog.AppsListSelectorDialogCallback{

    private final static String TAG="MainActivity";
    public final static String BLACKLIST="blackList";
    private final static String perms="You must enable USAGE ACCESS permissions";
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

        if(!isAccessGranted(this)) {
            Snackbar.make(findViewById(R.id.my_cord),perms,Snackbar.LENGTH_INDEFINITE).setAction("PROVIDE", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                }
            }).show();
            ProfileFragment.mainActivity=this;
        }


        this.fragmentManager=this.getSupportFragmentManager();
        this.placeFragment(ProfileFragment.getInstance(this),false);
        this.fab=(FloatingActionButton) findViewById(R.id.fab);
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO create a popupDialog to make a new profile*/
                if(isAccessGranted(MainActivity.this)) {
                    ProfileNamePopup p = ProfileNamePopup.getInstance("Create a new Profile", MainActivity.this);
                    p.show(MainActivity.this.getSupportFragmentManager(), "CreateProfile");
                }else{
                    Toast.makeText(MainActivity.this,perms,Toast.LENGTH_SHORT).show();
                }

            }
        });
    }






    public void placeFragment(Fragment fragment,boolean addToBackStack){
        FragmentTransaction ft=fragmentManager.beginTransaction();
        ft.replace(R.id.placeholder,fragment);
        if(addToBackStack)
            ft.addToBackStack("profile");
        ft.commit();
    }

    /* Overriding ProfilePopupCallback. Using this since the Fragment already exists*/
    @Override
    public void call(String profileName){
        if(!this.isFinishing()) {
            Log.d(TAG,"call");
            /* TODO call */
            AppsListSelectorDialog frag = AppsListSelectorDialog.getInstance(profileName,this,this,null,null);
            frag.show(this.getSupportFragmentManager(), "AppsList");
        }
    }


    /* Overriding AppListSelectorDialog to be used to repaint Profiles Fragment*/
    @Override
    public void reloadProfileFragment(){
        this.placeFragment(ProfileFragment.getInstance(this),false);
    }


    public static boolean isAccessGranted(Context ctx) {
        try {
            PackageManager packageManager = ctx.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) ctx.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);

            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /* TODO override ProfileDetailsPopupCallback*/

}
