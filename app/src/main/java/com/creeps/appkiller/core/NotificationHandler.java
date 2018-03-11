package com.creeps.appkiller.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.creeps.appkiller.MainActivity;
import com.creeps.appkiller.R;

/**
 * Created by rohan on 4/3/18.
 */

public class NotificationHandler {
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private int notificationId;
    private Notification currentNotification;
    private Context mContext;
    public NotificationHandler(Context context,int notificationId) {
        this.mContext=context;
        this.notificationManager= (NotificationManager)this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        this.notificationId=notificationId;
        this.initNotificationBuilder();
    }

    private void initNotificationBuilder(){
        this.builder=new NotificationCompat.Builder(this.mContext);
        Intent intent=new Intent(this.mContext,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(this.mContext,123, intent,0);

        this.builder.setOngoing(true);
        this.builder.setContentIntent(pendingIntent);
        this.builder.setContentTitle(mContext.getString(R.string.app_name)).setSmallIcon(R.drawable.ic_bookmark_black_24dp);

    }
    public void buildNotification(){
        this.notificationManager.notify(this.notificationId,this.currentNotification=this.builder.build());
    }

    public Notification getCurrentNotification(){
        return this.currentNotification;
    }

}
