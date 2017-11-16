package com.gc.cvrapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gc.cvrapp.R;
import com.gc.cvrapp.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogActivity extends AppCompatActivity {
    private int mPId;
    private static final String TAG = "LogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        LogUtil.i(TAG, "onCreate");
        mPId = android.os.Process.myPid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            String pid = String.valueOf(mPId);
            Process process = Runtime.getRuntime().exec("logcat *:e *:i -d | grep \"(" + pid + ")\"");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(pid)) {
                    log.append(line + "\n");
                }
            }
            TextView tv = (TextView) findViewById(R.id.tvLogcat);
            tv.setText(log.toString());

            final ScrollView scrollView = (ScrollView) findViewById(R.id.scrlLogcat);
            scrollView.post(new Runnable() {
                 @Override
                 public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
