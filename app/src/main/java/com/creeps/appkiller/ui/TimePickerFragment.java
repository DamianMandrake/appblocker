package com.creeps.appkiller.ui;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Created by rohan on 3/13/18.
 */

public class TimePickerFragment extends DialogFragment {

    private static TimePickerDialog.OnTimeSetListener listener;
    private static Context context;
    public static TimePickerFragment getInstance(Context ctx, TimePickerDialog.OnTimeSetListener l, ProfileDetailsFragment.HoursMinutesHolder holder){
        TimePickerFragment tp=new TimePickerFragment();

            Bundle bundle=new Bundle();
            bundle.putInt("hours",holder.hours);
            bundle.putInt("minutes",holder.minutes);
            tp.setArguments(bundle);

        context=ctx;
        listener=l;
        return tp;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hh = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        Bundle bundle=getArguments();
        if(bundle!=null) {
        /* TODO retreive hh and mm from bundle and set the time*/
            hh=bundle.getInt("hours");
            mm=bundle.getInt("minutes");
        }

        return new TimePickerDialog(context, listener, hh, mm, DateFormat.is24HourFormat(getActivity()));

    }

}
