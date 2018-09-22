package com.quhuabo.app.barscan2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This sample performs continuous scanning, displaying the barcode and source image whenever
 * a barcode is scanned.
 */
public class ContinuousCaptureActivity extends CaptureActivity {
    private static final String TAG = ContinuousCaptureActivity.class.getSimpleName();
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private ImageView btnDrag;

    private Button btnCopy;

    private Handler mHandler = new Handler();
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null /*|| result.getText().equals(lastText) */) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();
            barcodeView.setStatusText(result.getText());

            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

            barcodeView.pause();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    barcodeView.resume();
                }
            }, 2000);

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.continuous_scan);

        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.EAN_13);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.decodeContinuous(callback);
        btnCopy = (Button)findViewById(R.id.BTN_COPY);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    ClipData myClip;
                    myClip = ClipData.newPlainText("text", lastText);//text是内容

                    ClipboardManager myClipboard;
                    myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(ContinuousCaptureActivity.this, "扫码内容已复制到粘贴板.", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        beepManager = new BeepManager(this);

        btnDrag = findViewById(R.id.imageButton);
        btnDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnDrag.setOnTouchListener(new View.OnTouchListener()
        {
            int prevX,prevY;

            @Override
            public boolean onTouch(final View v,final MotionEvent event)
            {
                final FrameLayout.LayoutParams par=(FrameLayout.LayoutParams)v.getLayoutParams();
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        par.topMargin+=(int)event.getRawY()-prevY;
                        prevY=(int)event.getRawY();
                        par.leftMargin+=(int)event.getRawX()-prevX;
                        prevX=(int)event.getRawX();
                        v.setLayoutParams(par);
                        changeCameraHeight(par.topMargin);
                        return true;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        par.topMargin+=(int)event.getRawY()-prevY;
                        par.leftMargin+=(int)event.getRawX()-prevX;
                        v.setLayoutParams(par);
                        return true;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        prevX=(int)event.getRawX();
                        prevY=(int)event.getRawY();
                        par.bottomMargin=-2*v.getHeight();
                        par.rightMargin=-2*v.getWidth();
                        v.setLayoutParams(par);
                        return true;
                    }
                }
                return false;
            }
        });


    }

    private void changeCameraHeight(int topMargin) {
        ViewGroup.LayoutParams x = barcodeView.getLayoutParams();
        x.height = topMargin;
        barcodeView.setLayoutParams(x);
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
