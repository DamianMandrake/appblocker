package com.creeps.appkiller.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.creeps.appkiller.MainActivity;
import com.creeps.appkiller.R;
import com.creeps.appkiller.core.services.DatabaseHandler;
import com.creeps.appkiller.core.services.model.Profile;
import com.creeps.appkiller.core.services.model.Week;

import org.w3c.dom.Text;

/**
 * Created by rohan on 3/13/18.
 * To be inflated in onClick of ProfileFragment's Recycler item
 */

public class ProfileDetailsFragment extends Fragment {
    private RecyclerView recyclerView;
    private static MainActivity baseActivity;
    private WeekAdapter adapter;
    private ProfileAdapterInnerAppListAdapter innerAppListAdapter;
    private static ProfileDetailsFragmentCallback callback;
    private TextView starTimeHolder,endTimeHolder,startTime,endTime;
    private long profileStartTime,profileEndTime;
    private static Profile profile;
    private Button deleteButton;
    private RecyclerView daysRecycler;
    private final static String TAG="ProfileDetailsFragment";
    private TimePickerDialog.OnTimeSetListener startTimeListener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
            profileStartTime = hour * 3600 + minutes * 60;
            if(profileStartTime < profileEndTime) {
                Log.d(TAG, "start " + profileStartTime);
                startTime.setText(hour + ":" + minutes);
                profile.setStartTime(profileStartTime);
                DatabaseHandler.getInstance(baseActivity).updateTime(profile.getId(),Profile.ST_KEY,profileStartTime);

            }else
                Toast.makeText(baseActivity,"START TIME MUST BE LESS THAN END TIME",Toast.LENGTH_SHORT).show();
        }
    };
    private TimePickerDialog.OnTimeSetListener endTimeListener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hours, int minutes) {

            profileEndTime= hours*3600 + minutes*60;
            if(profileEndTime < profileStartTime)
                Toast.makeText(baseActivity,"END TIME MUST BE GREATER THAN START TIME ",Toast.LENGTH_SHORT).show();
            else {
                endTime.setText(hours + ":" + minutes);
                profile.setEndTime(profileEndTime);
                DatabaseHandler.getInstance(baseActivity).updateTime(profile.getId(),Profile.ET_KEY,profileEndTime);
            }
        }
    };

    public static ProfileDetailsFragment getInstance(Profile profile, MainActivity activity, ProfileDetailsFragmentCallback call){
        ProfileDetailsFragment popup=new ProfileDetailsFragment();
        ProfileDetailsFragment.profile=profile;
        Bundle bundle=new Bundle();
        bundle.putString("profileName",profile.getProfileName());
        popup.setArguments(bundle);
        ProfileDetailsFragment.baseActivity=activity;
        ProfileDetailsFragment.callback=call;
        return popup;
    }
    public interface ProfileDetailsFragmentCallback{

        public void onSuccess(Profile profile);//fired when the success button is clicked
        public void onDelete(Profile profile);//fired when the delete button is clicked
    }


    /*@Override
    public Dialog onCreateDialog(Bundle savedInstance){
        LayoutInflater inflater=LayoutInflater.from(baseActivity.getApplicationContext());
        View view=inflater.inflate(R.layout.popup_time_day_profile,null);


        


        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this.getContext());
        alertDialog.setTitle(getArguments().getString("title"));
        alertDialog.setView(view);
        *//* TODO ADD SUCCESS AND NEGATIVE BUTTONS*//*
        alertDialog.setPositiveButton("GO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                callback.onSuccess(profile);

                ProfileDetailsFragment.this.dismiss();
            }
        });

        return alertDialog.create();
    }*/


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.popup_time_day_profile,container,false);
        starTimeHolder=view.findViewById(R.id.start_placeholder);
        profileStartTime=profile.getStartTime();profileEndTime=profile.getEndTime();
        final HoursMinutesHolder hoursMinutesHolderStart=secondsToMinutes(profileStartTime),endHolder=secondsToMinutes(profileEndTime);
        startTime=view.findViewById(R.id.startTime);
        endTime=view.findViewById(R.id.endTime);
        startTime.setText(hoursMinutesHolderStart.hours+":"+hoursMinutesHolderStart.minutes);
        endTime.setText(endHolder.hours+":"+endHolder.minutes);
        starTimeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo open timepicker dialog and set time. obtain hours and minutes if isEdit is set

                TimePickerFragment tp=TimePickerFragment.getInstance(baseActivity,startTimeListener,hoursMinutesHolderStart);
                tp.show(baseActivity.getSupportFragmentManager(),"startTime");
            }
        });
        endTimeHolder=view.findViewById(R.id.end_time_placholder);
        endTimeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo open timepicker dialog and set time
                TimePickerFragment tp=TimePickerFragment.getInstance(baseActivity,endTimeListener,endHolder);
                tp.show(baseActivity.getSupportFragmentManager(),"startTime");

            }
        });

        startTime=view.findViewById(R.id.startTime);
        endTime=view.findViewById(R.id.endTime);

        this.deleteButton=view.findViewById(R.id.delete_profile);
        this.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler.getInstance(baseActivity).deleteProfile(profile.getId());
                baseActivity.onBackPressed();
            }
        });



        /* TODO inflate recycler here*/
        this.recyclerView=view.findViewById(R.id.appList);
        this.innerAppListAdapter=new ProfileAdapterInnerAppListAdapter(baseActivity.getApplicationContext(),profile.getPackages());
        this.recyclerView.setAdapter(innerAppListAdapter);
        LinearLayoutManager mgr=new LinearLayoutManager(this.getContext());
        mgr.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.recyclerView.setLayoutManager(mgr);
        this.recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //todo open applist fragment
                Log.d(TAG,"in on touch");
                if(motionEvent.getAction()==MotionEvent.ACTION_UP) {
                    AppsListSelectorDialog dialog = AppsListSelectorDialog.getInstance(profile.getProfileName(), baseActivity, null, profile,innerAppListAdapter);
                    dialog.show(baseActivity.getSupportFragmentManager(), "UpdateAppsList");
                    return true;
                }
                return false;
            }
        });
        this.daysRecycler=view.findViewById(R.id.days_of_week_recycler);





        return view;
    }

    
    

    /* to be used while fetching data from db*/

    private HoursMinutesHolder secondsToMinutes(long seconds){
        double hours=seconds/3600;
        double minutes=(seconds/60 )% 60;
            HoursMinutesHolder h=new HoursMinutesHolder((int)hours,(int)minutes);
        return h;
    }
    static class HoursMinutesHolder{
        int hours,minutes;
        public HoursMinutesHolder(int hours,int minutes){
            this.hours=hours;
            this.minutes=minutes;
        }
    }


}
