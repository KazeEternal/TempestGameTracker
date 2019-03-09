package com.kylepeplow.gamestracker.codereader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ListView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.kylepeplow.gamestracker.MainActivity;
import com.kylepeplow.gamestracker.R;
import com.kylepeplow.gamestracker.data.Game;
import com.kylepeplow.gamestracker.itemadapters.GameListArrayAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class CodeReaderView implements
        Detector.Processor,
        SurfaceHolder.Callback
{

    private static final int CAMERA_REQUEST_CODE = 1888;

    //Activity Related
    private MainActivity mMainActivity;

    //UI Widgets
    private SurfaceView mCameraSurfaceView;
    private ListView mItemListView;

    //Android Hardware Handling
    private CameraSource.Builder mCameraBuilder;
    private CameraSource mCameraSource;
    private SurfaceHolder mSurfaceHolder;
    private BarcodeDetector mBarcodeDetector;
    private Integer mSensorOrientation;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private CameraDevice mCameraDevice;


    public CodeReaderView(MainActivity ma)
    {
        mMainActivity = ma;
        //Camera Permission Requests
        ActivityCompat.requestPermissions(mMainActivity, new String[]{Manifest.permission.CAMERA},
                CAMERA_REQUEST_CODE);
        ActivityCompat.requestPermissions(mMainActivity, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);

        mCameraSurfaceView = mMainActivity.findViewById(R.id.surface_view);
        mItemListView = mMainActivity.findViewById(R.id.barcode_output);
    }

    //PlaceHolder
    CodeReaderProcessor mCodeReaderProcessor = null;

    public void initilaize()
    {
        //PlaceHolder
        mCodeReaderProcessor = new GameCodeProcessor();

        //---------------------------------------------------------------Move to a mutator?
        mItemListView.setAdapter(mCodeReaderProcessor.getArrayAdapter());

        //---------------------------------------------------------------DeInitialize last instance
        if(mBarcodeDetector != null)
        {
            mBarcodeDetector.release();
        }

        mBarcodeDetector = new BarcodeDetector.Builder(mMainActivity).setBarcodeFormats(mCodeReaderProcessor.getCodeType().Value()).build();

        mCameraBuilder = new CameraSource.Builder(mMainActivity, mBarcodeDetector);
        mCameraBuilder.setAutoFocusEnabled(true);

        mCameraSource = mCameraBuilder.setRequestedPreviewSize(640,480).build();

        mCameraSurfaceView.setFocusableInTouchMode(true);
        mCameraSurfaceView.setFocusable(true);
        mCameraSurfaceView.requestFocus();
        mSurfaceHolder = mCameraSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        //mSurfaceHolder.setFixedSize(1200,544);

        mBarcodeDetector.setProcessor(this);


        //mBarcodeDetector.release();

        //mCameraSource.release();
    }

    private void openCamera(int width, int height) {

        if (null == mMainActivity || mMainActivity.isFinishing()) {
            return;
        }
        CameraManager manager = (CameraManager) mMainActivity.getSystemService(Context.CAMERA_SERVICE);
        try {
            Log.d(TAG, "tryAcquire");
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            /**
             * default front camera will activate
             */
            String cameraId = manager.getCameraIdList()[0];

            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (map == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }
            //mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            //mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    //width, height, mVideoSize);

            int orientation = mMainActivity.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //mCameraSurfaceView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                //mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }
            //configureTransform(width, height);
            //mMediaRecorder = new MediaRecorder();
            if (ActivityCompat.checkSelfPermission(mMainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //requestPermission();
                return;
            }
            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened( @NonNull CameraDevice cameraDevice) {
                    mCameraDevice = cameraDevice;
                    //startPreview();
                    mCameraOpenCloseLock.release();
                    //if (null != mTextureView) {
                    //    configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
                    //}
                }

                @Override
                public void onDisconnected( @NonNull CameraDevice cameraDevice) {
                    mCameraOpenCloseLock.release();
                    cameraDevice.close();
                    mCameraDevice = null;
                }

                @Override
                public void onError( @NonNull CameraDevice cameraDevice, int error) {
                    mCameraOpenCloseLock.release();
                    cameraDevice.close();
                    mCameraDevice = null;

                    if (null != mMainActivity) {
                        mMainActivity.finish();
                    }
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "openCamera: Cannot access the camera.");
        } catch (NullPointerException e) {
            Log.e(TAG, "Camera2API is not supported on the device.");
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.");
        }
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
            option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            //return Collections.min(bigEnough, new CompareSizesByArea());
            return choices[0];
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    public void deinitialize()
    {
        //mBarcodeDetector.release();
    }
    //-------------------------------------------------------------Detector.Processor Interface
    @Override
    public void release() {

    }

    private Semaphore mCodeReadSemaphore = new Semaphore(1);
    private Barcode mCurrentCode = null;
    private int mReadCount = 0;
    @Override
    public void receiveDetections(Detector.Detections detections) {
        try {
            mCodeReadSemaphore.acquire();
            final SparseArray<Barcode> barcodes = detections.getDetectedItems();

            if (barcodes.size() != 0) {
                Barcode found = barcodes.valueAt(0);
                if (mCurrentCode == null) {
                    mReadCount = 0;
                    mCurrentCode = found;
                } else if (mCodeReaderProcessor.validate(mCurrentCode, found)) {
                    mReadCount++;
                } else {
                    mReadCount = 0;
                    mCurrentCode = found;
                }

                if (mReadCount == 5) {
                    mReadCount = 6;
                    mMainActivity.runOnUiThread(() ->
                    {
                        mCodeReaderProcessor.processFoundData(found);
                    });
                }
            } else {
                mReadCount = 0;
                mCurrentCode = null;
            }
            mCodeReadSemaphore.release();
        }
        catch(InterruptedException ex)
        {
            Log.e("[DETECTION]", ex.getLocalizedMessage());
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
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height)
    {
        //openCamera(width,height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        mCameraSource.stop();
    }


}
