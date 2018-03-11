package com.creeps.appkiller.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.creeps.appkiller.R;
import com.creeps.appkiller.core.services.DatabaseHandler;
import com.creeps.appkiller.core.services.model.Profile;
import com.creeps.appkiller.core.services.thread.ProcessLister;

import java.util.ArrayList;

/**
 * Created by rohan on 4/3/18.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileRecyclerViewHolder>{

    /* TODO populate the recycler inside the Item...*/

    private ArrayList<Profile> profiles;
    private LayoutInflater mLayoutInflater;
    private final ProfileAdapterCallback profileAdapterCallback;
    private final static String SELECTED_COLOR="#FF4081",WHITE="#FFFFFF";
    private Profile previous;
    private Context context;
    public ProfileAdapter(Context context, ArrayList<Profile> profiles,ProfileAdapterCallback callback){
        this.profiles=profiles;
        this.mLayoutInflater=LayoutInflater.from(context);
        this.profileAdapterCallback=callback;
        this.context=context;
    }



    @Override
    public ProfileRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=this.mLayoutInflater.inflate(R.layout.profile_item,parent,false);
        return new ProfileRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProfileRecyclerViewHolder holder, final int position) {
        final Profile current=this.profiles.get(position);
        holder.text.setText(current.getProfileName());
        if(current.isActive()) {
            holder.view.setBackgroundColor(Color.parseColor(SELECTED_COLOR));
            previous=current;
            holder.aSwitch.setChecked(true);
        }else {
            holder.aSwitch.setChecked(false);
            holder.view.setBackgroundColor(Color.parseColor(WHITE));
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ProfileAdapter.this.profileAdapterCallback!=null)
                    ProfileAdapter.this.profileAdapterCallback.onClick(position);
            }
        });
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //todo init via databseHandler... I know its going to take a long time, this is being done so that the service detects its immediately and blocks said apps
                if(isChecked) {
                    DatabaseHandler ref=DatabaseHandler.getInstance(ProfileAdapter.this.context);
                    ref.setProfileActive(current.getId());
                    current.setIsActive(1);
                    if(previous!=null && previous!=current) {
                        previous.setIsActive(0);
                    }
                }else {
                    current.setIsActive(0);
                    DatabaseHandler ref=DatabaseHandler.getInstance(ProfileAdapter.this.context);
                    ref.setStatus(current.getId(),0);
                    ref.notifyNoneActive();
                }

                previous=current;
                /* todo force repaint in recycler*/
                ProfileAdapter.this.profileAdapterCallback.forceRepaint();
            }
        });
        LinearLayoutManager layoutManager=new LinearLayoutManager(this.context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setAdapter(new ProfileAdapterInnerAppListAdapter(this.context,current.getPackages()));



    }

    @Override
    public int getItemCount() {
        return this.profiles.size();
    }

    static class ProfileRecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        View view;
        Switch aSwitch;
        RecyclerView recyclerView;
        public ProfileRecyclerViewHolder(View view){
            super(view);
            this.view=view;
            this.aSwitch=(Switch)view.findViewById(R.id.activeProfile);
            text=(TextView)view.findViewById(R.id.profileName);

            this.recyclerView=view.findViewById(R.id.applist);
        }
    }


    public interface ProfileAdapterCallback{
        public void onClick(int position);
        public void forceRepaint();
    }

}