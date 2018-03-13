package com.creeps.appkiller.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creeps.appkiller.MainActivity;
import com.creeps.appkiller.R;
import com.creeps.appkiller.core.services.DatabaseHandler;
import com.creeps.appkiller.core.services.model.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohan on 4/3/18.
 * A fragment that show a list of profiles. If none exist lets the user create a new one.
 */

public class ProfileFragment extends Fragment implements ProfileAdapter.ProfileAdapterCallback,ProfileDetailsFragment.ProfileDetailsFragmentCallback{

    private RecyclerView mRecycler;
    private ProfileAdapter profileAdapter;
    private DatabaseHandler ref;
    private ArrayList<Profile> profiles;
    private final static String TAG="ProfileFragment";
    public static MainActivity mainActivity;
    public static ProfileFragment getInstance(MainActivity mainActivity){
        ProfileFragment fragment=new ProfileFragment();
        ProfileFragment.mainActivity=mainActivity;
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile,container,false);
        this.mRecycler=view.findViewById(R.id.profilesList);
        LinearLayoutManager mgr=new LinearLayoutManager(this.getContext());
        mgr.setOrientation(LinearLayoutManager.VERTICAL);
        this.mRecycler.setLayoutManager(mgr);
        this.ref=DatabaseHandler.getInstance(this.getContext());
        this.profiles=ref.getAllProfiles();
        for(Profile p:this.profiles)
            p.setPackages(this.ref.readAllPackages(p.getId()));
        this.profileAdapter=new ProfileAdapter(this.getContext(),this.profiles,this);
        this.mRecycler.setAdapter(this.profileAdapter);

        return view;
    }

    /* Overriding ProfileAdapterCallback*/
    @Override
    public void onClick(int position){
        Log.d(TAG,position+"");
        /* Todo open a fragment dialog with the apps present in the given Profile*/
        Log.d(TAG,"here");
        Profile current=this.profiles.get(position);
        ProfileDetailsFragment f=ProfileDetailsFragment.getInstance(current, mainActivity,this);
        Bundle bundle=new Bundle();
        bundle.putInt("id",current.getId());
        f.setArguments(bundle);
        mainActivity.placeFragment(f,true);
    }
    @Override
    public void forceRepaint(){
        this.mRecycler.setAdapter(this.profileAdapter);
    }




    /* Overriding popup callbacks*/

    @Override
    public void onSuccess(Profile profile) {
        //todo insert in the database
    }

    @Override
    public void onDelete(Profile profile) {
        //todo delete from the database
    }
}
