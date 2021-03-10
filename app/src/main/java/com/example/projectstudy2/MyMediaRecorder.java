package com.example.projectstudy2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MyMediaRecorder {

    private String pathSave = "";
    private MediaRecorder mediaRecorder;
    private Activity activity;

    public static final int REQUEST_PERMISSION_MEDIA_RECORDER_CODE = 100;

    public MyMediaRecorder(Activity activity){
        this.activity = activity;
    }

    public void setPathSave(){
        pathSave = Environment.getExternalStorageDirectory()
                .getAbsolutePath()+"/"
                + UUID.randomUUID().toString()+"_audio_record.3gp";
    }

    public void setupMediaRecorder(File file){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(file.getAbsoluteFile().toString());
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setMediaRecorderToStop(){
        mediaRecorder.stop();
    }

    public float getMaxAmplitude() {
        if (mediaRecorder != null) {
            try {
                return mediaRecorder.getMaxAmplitude();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return 0;
            }
        } else {
            return 5;
        }
    }

    public boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result  == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions(){
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_MEDIA_RECORDER_CODE);
    }
}
