package com.ogi.myfirebasedispatcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseJobDispatcher mDispatcher;
    private String DISPATCHER_TAG = "mydispatcher";
    private String CITY = "Jakarta, id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnSetScheduler = findViewById(R.id.btn_set_scheduler);
        Button btnCancelScheduler = findViewById(R.id.btn_cancel_scheduler);

        btnSetScheduler.setOnClickListener(this);
        btnCancelScheduler.setOnClickListener(this);

        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
    }

    public void startDispatcher(){
        Bundle myExtrasBundle = new Bundle();
        myExtrasBundle.putString(MyJobService.EXTRA_CITY, CITY);

        Job myJob = mDispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag(DISPATCHER_TAG)
                .setRecurring(true)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(0, 60))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(
                        // hanya berjalan saat ada koneksi yang unmetered (contoh Wifi)
//                        Constraint.ON_UNMETERED_NETWORK,
                        // hanya berjalan ketika device di charge
//                        Constraint.DEVICE_CHARGING
                        // berjalan saat ada koneksi internet
                        Constraint.ON_ANY_NETWORK,

                        // berjalan saat device dalam kondisi idle
                        Constraint.DEVICE_IDLE

                )
                .setExtras(myExtrasBundle)
                .build();
        mDispatcher.mustSchedule(myJob);
    }

    public void cancelDispatcher(){
        mDispatcher.cancel(DISPATCHER_TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_set_scheduler:
                startDispatcher();
                Toast.makeText(this, "Scheduler task has been started", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_cancel_scheduler:
                cancelDispatcher();
                Toast.makeText(this, "Scheduler task has been canceled", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
