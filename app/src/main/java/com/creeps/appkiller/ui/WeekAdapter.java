package com.creeps.appkiller.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.creeps.appkiller.R;
import com.creeps.appkiller.core.services.DatabaseHandler;
import com.creeps.appkiller.core.services.model.Week;

/**
 * Created by rohan on 3/13/18.
 */

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WeekAdapterViewHolder>{
    private final static int NUM_DAY=7;
    private boolean isChecked[];
    private Week currentWeek;
    private LayoutInflater layoutInflater;
    private DatabaseHandler ref;
    private int profileId;
    private final static String TAG="WeekAdapter";
    public WeekAdapter(Context context, Week week,int profileId){
        this.currentWeek=week;
        layoutInflater=LayoutInflater.from(context);
        this.profileId=profileId;
        this.isChecked=new boolean[NUM_DAY];
        for(int i=0;i<7;i++)
            isChecked[i]=week.isDayActive(Week.allBitmasks[i]);
        ref=DatabaseHandler.getInstance(context);
    }

    @Override
    public WeekAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=this.layoutInflater.inflate(R.layout.days_of_week_item,parent,false);
        return new WeekAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeekAdapterViewHolder holder, final int position) {

        holder.textView.setText(Week.daysConst[position]);
        holder.c.setChecked(isChecked[position]);
        holder.c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChecked[position]=b;
                byte be=Week.allBitmasks[position];
                Log.d(TAG,be+" current "+position);
                if(b){
                    currentWeek.add(Week.allBitmasks[position]);
                }else{
                    currentWeek.remove(Week.allBitmasks[position]);
                }
                Log.d(TAG,currentWeek.getCurrent()+" current");
                ref.updateDays(profileId,currentWeek.getCurrent());
            }
        });
    }

    @Override
    public int getItemCount() {
        return NUM_DAY;
    }


    public static class WeekAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        CheckBox c;
        public WeekAdapterViewHolder(View view){
            super(view);
            textView=view.findViewById(R.id.day);
            c=view.findViewById(R.id.cb);
        }
    }
}
