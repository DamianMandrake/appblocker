package com.creeps.appkiller.ui;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.creeps.appkiller.R;
import com.creeps.appkiller.core.services.model.ProfilePackages;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rohan on 4/3/18.
 * A recyclerAdapter to show list of apps. pass in a boolean to show/maintain list of Switch states
 */

public class AppListRecyclerAdapter extends RecyclerView.Adapter<AppListRecyclerAdapter.AppListRecyclerViewHolder>{
    private ArrayList<UsageStats> appsList;
    private LayoutInflater layoutInflater;
    private AppListRecyclerViewHolder previous;
    private PackageManager packageManager;
    private Drawable drawable;
    private boolean arr[];
    private HashMap<Integer,String> selectedRef;
    private ArrayList<ProfilePackages> preexisting;
    private ArrayList<Integer> indices;
    private final static String TAG="ApplistAdapter";
    public AppListRecyclerAdapter(Context context, ArrayList<UsageStats> appsList, ArrayList<ProfilePackages> preexisting){
        this.appsList=appsList;
        this.layoutInflater=LayoutInflater.from(context);
        this.packageManager=context.getPackageManager();
        this.drawable=context.getDrawable(R.mipmap.ic_launcher);
        this.selectedRef=new HashMap<>();
        this.preexisting=preexisting;
        this.arr=new boolean[appsList.size()];
        if(preexisting!=null)
            this.handleBooleans();
    }
    private void handleBooleans(){
        this.indices=new ArrayList<>();
        for(ProfilePackages p:preexisting){
            int i;
            for( i=0;i<appsList.size() && !appsList.get(i).getPackageName().equals(p.getPackageName());i++);
            if(i<appsList.size() ) {//found a recurring entry
                this.arr[i] = true;
                Log.d(TAG,i+" recurred");
                this.indices.add(i);
                //adding to selected
                this.selectedRef.put(i,p.getPackageName());
            }
        }
    }


    @Override
    public AppListRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=this.layoutInflater.inflate(R.layout.app_llist_item,parent,false);

        return new AppListRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AppListRecyclerViewHolder holder, final int position) {

        final UsageStats usageStats=this.appsList.get(position);
        Drawable drawable=null;
        try {
            drawable=this.packageManager.getApplicationIcon(usageStats.getPackageName());
        }catch (PackageManager.NameNotFoundException nmfe){
            nmfe.printStackTrace();
            drawable=this.drawable;
        }finally {
            holder.imageView.setImageDrawable(drawable);
        }

        holder.textView.setText(usageStats.getPackageName());
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                arr[position]=isChecked;
                Log.d(TAG,position+  " ischecked "+arr[position]);
                if(arr[position])
                    selectedRef.put(position,usageStats.getPackageName());
                else
                    selectedRef.remove(position);
            }
        });
        holder.aSwitch.setChecked(arr[position]);
    }

    @Override
    public int getItemCount() {
        return this.appsList.size();
    }

    public static class AppListRecyclerViewHolder extends RecyclerView.ViewHolder{
        Switch aSwitch;
        ImageView imageView;
        TextView textView;
        public AppListRecyclerViewHolder(View view){
            super(view);
            aSwitch=view.findViewById(R.id.isChecked);
            textView=view.findViewById(R.id.packageName);
            imageView=view.findViewById(R.id.appIcon);
        }

    }
    public HashMap<Integer, String> getSelectedRef(){
        if(indices!=null  && areAllStillTrue() && selectedRef.size()==indices.size())
            return null;
        return this.selectedRef;
    }
    private boolean areAllStillTrue(){
        int i=0;
        Log.d(TAG,"looping");
        while(i< indices.size() && arr[indices.get(i)]) {
            Log.d(TAG, i + " isChecked " + arr[i]);
            i++;
        }
        Log.d(TAG,"final i "+i);
        return i==indices.size();
    }



}
