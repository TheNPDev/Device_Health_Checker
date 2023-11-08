package com.example.devicehealthchecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private ProgressBar progressPrimaryMicrophone;
    private ProgressBar progressSecondaryMicrophones;
    private ProgressBar progressBackCamera;
    private ProgressBar progressFrontCamera;
    private ProgressBar progressVibration;
    private CardView primaryMicrophones;
    private CardView secondaryMicrophone;
    private CardView backCamera;
    private CardView frontCamera;
    private CardView vibration;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Handler secondHandler = new Handler(Looper.getMainLooper());
    private PreviewView previewView;
    int backCameraFacing = CameraSelector.LENS_FACING_BACK;
    int frontCameraFacing = CameraSelector.LENS_FACING_FRONT;
    private boolean isBackCameraOpened = false;
    private boolean isFrontCameraOpened = false;
    private Button checkBtn;
    private ArrayList<String> stringArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressPrimaryMicrophone = findViewById(R.id.progressPrimaryMicrophone);
        progressSecondaryMicrophones = findViewById(R.id.progressSecondaryMicrophone);
        primaryMicrophones = findViewById(R.id.primaryMicrophone);
        secondaryMicrophone = findViewById(R.id.secondaryMicrophone);
        backCamera = findViewById(R.id.backCamera);
        progressBackCamera = findViewById(R.id.progressBackCamera);
        frontCamera = findViewById(R.id.frontCamera);
        progressFrontCamera = findViewById(R.id.progressFrontCamera);
        progressVibration = findViewById(R.id.progressVibration);
        vibration = findViewById(R.id.vibration);
        checkBtn = findViewById(R.id.checkBtn);
        previewView = findViewById(R.id.cameraPreview);

         previewView.setVisibility(View.INVISIBLE);


        progressSecondaryMicrophones.setVisibility(View.GONE);
        progressBackCamera.setVisibility(View.GONE);
        progressFrontCamera.setVisibility(View.GONE);
        progressVibration.setVisibility(View.GONE);
        testPrimaryMicrophone();

        stringArray = new ArrayList<>();


        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this, PdfActivity.class);
                intent.putStringArrayListExtra("STRING_ARRAY", stringArray);
                startActivity(intent);
            }
        });

    }

    private void testPrimaryMicrophone() {
        progressPrimaryMicrophone.setVisibility(View.VISIBLE);
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Simulate a successful test
                int audioSource = MediaRecorder.AudioSource.MIC;
                int sampleRate = 44100;
                int channelConfig = AudioFormat.CHANNEL_IN_MONO;
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

                // Initialize AudioRecord
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_CODE);

                } else {
                    AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize);

                    if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                        // Microphone test passed
                        primaryMicrophones.setCardBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                        String Pmicrophone = "Primary Microphone is Working";
                        stringArray.add(Pmicrophone);
                    } else {
                        // Microphone test failed
                        primaryMicrophones.setCardBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                        String Pmicrophone = "Primary Microphone is Not Working";
                        stringArray.add(Pmicrophone);
                    }
                }

                progressPrimaryMicrophone.setVisibility(View.GONE);
                testSecondaryMicrophone();

            }
        }, 3000); // 3-second delay


    }

    private void testSecondaryMicrophone() {

        progressSecondaryMicrophones.setVisibility(View.VISIBLE);
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int audioSource = MediaRecorder.AudioSource.CAMCORDER; // Secondary microphone
                int sampleRate = 44100;
                int channelConfig = AudioFormat.CHANNEL_IN_MONO;
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

                // Initialize AudioRecord
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_CODE);

                }
                else{
                    AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize);

                    if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                        // Microphone test passed
                        secondaryMicrophone.setCardBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

                        String Smicrophone = "Secondary microphone is Working";
                        stringArray.add(Smicrophone);

                    } else {
                        // Microphone test failed
                        secondaryMicrophone.setCardBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                        String Smicrophone = "Secondary Microphone is not Working";
                        stringArray.add(Smicrophone);
                    }
                }
                progressSecondaryMicrophones.setVisibility(View.GONE);
                testBackCamera();
            }
        }, 3000); // 3-second delay


    }

    private void testBackCamera(){

        progressBackCamera.setVisibility(View.VISIBLE);
        secondHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                    previewView.setVisibility(View.VISIBLE);
                    int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
                    ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(getApplicationContext());

                    listenableFuture.addListener(() -> {
                        try {
                            ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();

                            Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                            ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                    .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                            CameraSelector cameraSelector = new CameraSelector.Builder()
                                    .requireLensFacing(backCameraFacing).build();

                            cameraProvider.unbindAll();

                            Camera camera = cameraProvider.bindToLifecycle(MainActivity.this, cameraSelector, preview, imageCapture);

                            isBackCameraOpened = true;

                            if (isBackCameraOpened) {
                                backCamera.setCardBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

                                String backCameraS = "Back camera is Working";
                                stringArray.add(backCameraS);
                            } else {
                                backCamera.setCardBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                                String backCameraS = "Back camera is Not Working";
                                stringArray.add(backCameraS);
                            }


                            preview.setSurfaceProvider(previewView.getSurfaceProvider());
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, ContextCompat.getMainExecutor(getApplicationContext()));

                    progressBackCamera.setVisibility(View.GONE);
                    testFrontCamera();
                }

        },3000);



    }

    private void testFrontCamera(){

        progressFrontCamera.setVisibility(View.VISIBLE);

        secondHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                previewView.setVisibility(View.VISIBLE);
                int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
                ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(getApplicationContext());

                listenableFuture.addListener(() -> {
                    try {
                        ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();

                        Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                        ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                        CameraSelector cameraSelector = new CameraSelector.Builder()
                                .requireLensFacing(frontCameraFacing).build();

                        cameraProvider.unbindAll();



                        Camera camera = cameraProvider.bindToLifecycle(MainActivity.this, cameraSelector, preview, imageCapture);


                        isFrontCameraOpened = true;


                        if (isFrontCameraOpened) {
                            frontCamera.setCardBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

                            String frontCameraS = "Front camera is Working";
                            stringArray.add(frontCameraS);
                        } else {
                            frontCamera.setCardBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

                            String frontCameraS = "Front camera is not Working";
                            stringArray.add(frontCameraS);
                        }


                        preview.setSurfaceProvider(previewView.getSurfaceProvider());
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }, ContextCompat.getMainExecutor(getApplicationContext()));



                progressFrontCamera.setVisibility(View.GONE);
                testVibration();

            }
        },3000);



    }

    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    private void testVibration(){

        progressVibration.setVisibility(View.VISIBLE);
        secondHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v1.vibrate(400);

                if (v1 != null) {
                    vibration.setCardBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

                    String vibrationS = "Vibration is Working";
                    stringArray.add(vibrationS);
                }
                else{
                        vibration.setCardBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                        String vibrationS = "Vibration is not Working";
                        stringArray.add(vibrationS);
                    }
                    progressVibration.setVisibility(View.GONE);
                    previewView.setVisibility(View.GONE);
                }

        },3000);

        }


}