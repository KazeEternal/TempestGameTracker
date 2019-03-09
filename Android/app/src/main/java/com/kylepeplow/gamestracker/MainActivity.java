package com.kylepeplow.gamestracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ViewFlipper;

import com.kylepeplow.gamestracker.codereader.CodeReaderView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private View mScanButton;

    public static Context MAContext;

    //Application Setup
    private CodeReaderView mCodeReaderView = null;
    private ViewFlipper mViewFlipper = null;

    private int mCodeReaderViewIndex = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        MAContext = MainActivity.this;

        mCodeReaderView = new CodeReaderView(this);
        mViewFlipper = findViewById(R.id.mainViewFlipper);


        mCodeReaderViewIndex = mViewFlipper.indexOfChild(findViewById(R.id.codeReaderView));
        //mScanButton = findViewById(R.id.scan_new_game_button);
        //mScanButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onClick(View view) {
        ViewFlipper vf = findViewById(R.id.mainViewFlipper);
        //vf.setDisplayedChild();
    }

    public void onScanClick(View view)
    {
        mViewFlipper.setDisplayedChild(mCodeReaderViewIndex);
        mCodeReaderView.initilaize();
    }
}
