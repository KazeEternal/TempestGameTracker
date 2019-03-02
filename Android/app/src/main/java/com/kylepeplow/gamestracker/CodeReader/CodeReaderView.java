package com.kylepeplow.gamestracker.CodeReader;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.kylepeplow.gamestracker.MainActivity;
import com.kylepeplow.gamestracker.R;

import java.io.IOException;

public class CodeReaderView implements
        Detector.Processor,
        SurfaceHolder.Callback
{
    private static final int CAMERA_REQUEST_CODE = 1888;

    //Activity Related
    private MainActivity mMainActivity;

    //UI Widgets
    private SurfaceView mCameraSurfaceView;
    private TextView mTextTestView;

    //Android Hardware Handling
    private CameraSource mCameraSource;
    private BarcodeDetector mBarcodeDetector;


    public CodeReaderView(MainActivity ma)
    {
        mMainActivity = ma;
    }

    public void initilaize()
    {
        //Camera Permission Requests
        ActivityCompat.requestPermissions(mMainActivity, new String[]{Manifest.permission.CAMERA},
                CAMERA_REQUEST_CODE);
        ActivityCompat.requestPermissions(mMainActivity, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);

        mCameraSurfaceView = mMainActivity.findViewById(R.id.surface_view);
        mTextTestView = mMainActivity.findViewById(R.id.Barcode_Output);

        mBarcodeDetector = new BarcodeDetector.Builder(mMainActivity).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        CameraSource.Builder cBuilder = new CameraSource.Builder(mMainActivity, mBarcodeDetector);
        cBuilder.setAutoFocusEnabled(true);
        mCameraSource = cBuilder.setRequestedPreviewSize(640, 480).build();

        mCameraSurfaceView.setFocusableInTouchMode(true);
        mCameraSurfaceView.setFocusable(true);
        mCameraSurfaceView.requestFocus();
        mCameraSurfaceView.getHolder().addCallback(this);
        
        mBarcodeDetector.setProcessor(this);
    }

    //-------------------------------------------------------------Detector.Processor Interface
    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections detections) {
        final SparseArray<Barcode> barcodes = detections.getDetectedItems();
        if(barcodes.size() != 0)
        {

            final boolean post = mTextTestView.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            String code = barcodes.valueAt(0).displayValue;
                            mTextTestView.setText(code);
                            Log.i("[DETECTION]", code);
                        }
                    }
            );

        }
    }

    //-------------------------------------------------------------SurfaceHolder.Callback Interface
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCameraSource.start(mCameraSurfaceView.getHolder());
        }catch(SecurityException ex){
            Log.e("CAMERA SOURCE", ex.getMessage());
        } catch (IOException ex) {
            Log.e("CAMERA SOURCE", ex.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        mCameraSource.stop();
    }
}
