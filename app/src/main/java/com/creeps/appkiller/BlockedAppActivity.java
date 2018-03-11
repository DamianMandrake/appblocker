package com.creeps.appkiller;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.creeps.appkiller.core.services.ProcessBlockerService;

/**
 * Created by rohan on 18/1/18.
 */

public class BlockedAppActivity extends AppCompatActivity {

    private String currentBlackListedPackage;
    private ActivityManager activityManager;
    private Button button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_app);
        Bundle b=getIntent().getExtras();
        this.currentBlackListedPackage=b.getString(ProcessBlockerService.BLACKLIST_APP);
        activityManager=(ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        this.button=(Button) findViewById(R.id.killApp);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlockedAppActivity.this.killTheApp();
            }
        });
    }

    private void killTheApp(){
        this.activityManager.killBackgroundProcesses(this.currentBlackListedPackage);
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.killTheApp();
    }
}
