package com.creeps.appkiller.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.creeps.appkiller.R;
import com.creeps.appkiller.core.services.model.Profile;
import com.creeps.appkiller.core.services.model.ProfilePackages;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohan on 4/3/18.
 * An adapter for the inner recycler view of every ProfileAdapter recycler Item
 */

public class ProfileAdapterInnerAppListAdapter extends RecyclerView.Adapter<ProfileAdapterInnerAppListAdapter.ProfileAdapterInnerAppListAdapterViewHolder> {

    private ArrayList<ProfilePackages> profilePackages;
    private LayoutInflater inflater;
    private PackageManager packageManager;
    private final static String TAG="InnerRecycler";

    private Drawable drawable;
    public ProfileAdapterInnerAppListAdapter(Context context,ArrayList<ProfilePackages> list){
        this.inflater=LayoutInflater.from(context);
        this.profilePackages=list;
        this.packageManager=context.getPackageManager();
        this.drawable=context.getDrawable(R.mipmap.ic_launcher);

    }
    public void setProfilePackages(ArrayList<ProfilePackages> list){this.profilePackages=list;this.notifyDataSetChanged();}
    @Override
    public ProfileAdapterInnerAppListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.app_list_item_inner_recycler_item,parent,false);

        return new ProfileAdapterInnerAppListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileAdapterInnerAppListAdapterViewHolder holder, int position) {
        Log.d(TAG,"binding");
        try {
            drawable=this.packageManager.getApplicationIcon(this.profilePackages.get(position).getPackageName());
        }catch (PackageManager.NameNotFoundException nmfe){
            nmfe.printStackTrace();
            drawable=this.drawable;
        }finally {
            holder.imageView.setImageDrawable(drawable);
        }


    }

    @Override
    public int getItemCount() {
        return this.profilePackages.size();
    }

    static class ProfileAdapterInnerAppListAdapterViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ProfileAdapterInnerAppListAdapterViewHolder(View view){
            super(view);
            this.imageView=view.findViewById(R.id.app_icon);
        }
    }
}
