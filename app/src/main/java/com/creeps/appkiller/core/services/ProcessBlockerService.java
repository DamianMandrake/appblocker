package com.creeps.appkiller.core.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.creeps.appkiller.BlockedAppActivity;
import com.creeps.appkiller.core.MyBroadcastReceiver;
import com.creeps.appkiller.core.NotificationHandler;
import com.creeps.appkiller.core.services.model.Profile;
import com.creeps.appkiller.core.services.thread.ProcessLister;
import com.creeps.appkiller.core.services.thread.ProcessListerCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Created by rohan on 15/1/18.
 */

public class ProcessBlockerService extends Service implements ProcessListerCallback,ActiveProfileCallback{
    private final static String THREAD_NAME="ProcessHandler";
    private final IBinder mBinder= new LocalBinder();
    public final static String BLACKLIST_APP="blacklistedApp";
    private Profile currentlyActiveProfile;
    private static final String TAG="ProcessBlockerService";
    private Notification currentNotifcationReference;
    private final static int NOTIFICATION_ID=2222;

    /* Will hold a reference to a Profile and check its contents for the currently running app.*/
    private static List<String> mBlackList;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        ProcessLister processLister=ProcessLister.prepareInstance(this.getApplicationContext(),THREAD_NAME);
        processLister.setProcessListerCallback(this);
        if (processLister.getState() == Thread.State.NEW){
            processLister.start();
        }
        DatabaseHandler ref=DatabaseHandler.getInstance(this.getApplicationContext());
        ref.setActiveCallback(this);
        this.initCurrentlyActiveProfile();
        if(currentlyActiveProfile!=null) {
            Log.d(TAG, currentlyActiveProfile.toString());

        }
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(MyBroadcastReceiver.BOOT_EVENT);
        intentFilter.addAction(MyBroadcastReceiver.RESTART_SERVICE);
        this.getApplicationContext().registerReceiver(new MyBroadcastReceiver(),intentFilter);
        NotificationHandler notificationHandler=new NotificationHandler(this.getApplicationContext(),NOTIFICATION_ID);
        notificationHandler.buildNotification();
        this.currentNotifcationReference=notificationHandler.getCurrentNotification();
        this.startForeground(NOTIFICATION_ID,this.currentNotifcationReference);
        return START_STICKY;
    }

    public class LocalBinder extends Binder{

        public ProcessBlockerService getService(){
            return ProcessBlockerService.this;
        }
    }

    public static void setList(ArrayList<String> list){
        mBlackList=list;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //super.onTaskRemoved(rootIntent);
        this.tryToReinitService();
    }

    @Override
    public void onDestroy(){

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }


    /* Overriding ProcessListerCallback*/

    @Override
    public void call(String currentPackageName) {
        /* todo check blacklist and display activity*/

        Log.d(TAG,"received "+currentPackageName);
        if(currentlyActiveProfile!=null)            Log.d(TAG,"PROFILE "+currentlyActiveProfile.toString());

        if(currentlyActiveProfile!=null && currentlyActiveProfile.contains(currentPackageName)){
            Intent blockeActivityIntent=new Intent(this, BlockedAppActivity.class);
                    blockeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            blockeActivityIntent.putExtra(BLACKLIST_APP,currentPackageName);
            startActivity(blockeActivityIntent);
        }
    }



    /* Overriding ActiveProfileCallback
    * This means that the databaseHandler has set new active profile*/
    @Override
    public void profileNotify(){
        this.initCurrentlyActiveProfile();
    }

    @Override
    public void notifyNoneSelected(){
        this.currentlyActiveProfile=null;
    }

    private void initCurrentlyActiveProfile(){
        DatabaseHandler ref=DatabaseHandler.getInstance(this.getApplicationContext());
        this.currentlyActiveProfile=ref.getCurrentlyActiveProfile();
        if(currentlyActiveProfile!=null)
            this.currentlyActiveProfile.setPackages(ref.readAllPackages(this.currentlyActiveProfile.getId()));

    }
    private void tryToReinitService(){
         /* ToDO restart the service ie send a broadcast*/
        this.sendBroadcast(new Intent(MyBroadcastReceiver.RESTART_SERVICE));
        Log.d(TAG,"service being destroyed and prolly restarted");
    }

}
