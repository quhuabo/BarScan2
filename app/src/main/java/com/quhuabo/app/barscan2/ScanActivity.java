package com.quhuabo.app.barscan2;

import android.os.Bundle;

import com.journeyapps.barcodescanner.CaptureActivity;

public class ScanActivity extends CaptureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.continuous_scan);
    }
}
