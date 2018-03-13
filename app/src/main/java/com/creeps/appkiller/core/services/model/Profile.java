package com.creeps.appkiller.core.services.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by rohan on 3/3/18.
 * A SQLite model class for the table Profiles
 */

public class Profile {

    /* These constants are the column names present in the database*/
    public final static String ID_KEY="profile_id";
    public final static String PNAME_KEY="profile_name";
    public final static String ST_KEY="start_time";
    public final static String ET_KEY="end_time";
    public final static String ACTIVE_KEY="currently_active";
    public final static String DAYS_BITMASK_KEY="days_active";
    private final static String TAG="Profile";
    private int id;
    private long startTime,endTime;
    private String profileName;
    private int isActive=0;
    //could use a HashMap of packageName to ProfilePackages for o(1) retrievals.
    private ArrayList<ProfilePackages> packages;
    private Week daysActive;
    public Profile(int id,long startTime,long endTime,String profileName,int isActive,Week week){
        this.id=id;
        this.startTime=startTime;this.endTime=endTime;
        this.profileName=profileName;
        this.isActive=isActive;
        this.daysActive=week;
    }
    public String getProfileName(){return this.profileName;}
    public int getId(){return this.id;}

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
    public void setPackages(ArrayList<ProfilePackages> packages){
        this.packages=packages;
    }

    public ArrayList<ProfilePackages> getPackages() {
        return packages;
    }
    public boolean isActive(){return this.isActive==1;}

    public boolean contains(String packageName){
        int i=-1;
        if(this.packages!=null) {
            while (++i < this.packages.size() && !(this.packages.get(i)).getPackageName().equals(packageName))
                ;
            return i != this.packages.size();
        }
        return false;
    }


    public void setStartTime(long s){this.startTime=s;}
    public void setEndTime(long s){this.endTime=s;}
    @Override
    public String toString(){
        return this.id+" "+this.profileName+" active: "+this.isActive;
    }
    public void setIsActive(int a){this.isActive=a;}
    public int getIsActive(){return this.isActive;}
    public Week getDaysActive(){return this.daysActive;}
    //checks time and determines whether the apps must be blocked
    public boolean shouldBlock(long time){
        Log.d(TAG,this.startTime+" s "+this.endTime+ " e "+time+" c");
        return time>=this.startTime && time<this.endTime; }


}