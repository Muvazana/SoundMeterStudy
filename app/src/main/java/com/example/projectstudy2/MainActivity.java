package com.example.projectstudy2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import static com.example.projectstudy2.MyMediaRecorder.REQUEST_PERMISSION_MEDIA_RECORDER_CODE;

public class MainActivity extends AppCompatActivity {

    private Button btnStart, btnStop;
    private TextView txtDBCount;
    private ImageView imgSoundSilence, imgSoundNoisy;
    private MyMediaRecorder myMediaRecorder;

    float volume = 10000;
    private static final int msgWhat = 0x1001;
    private static final int refreshTime = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setEnabled(false);
        txtDBCount = (TextView) findViewById(R.id.txtDBCount);
        imgSoundSilence = (ImageView) findViewById(R.id.imgSoundSilence);
        imgSoundNoisy = (ImageView) findViewById(R.id.imgSoundNoisy);
        imgSoundNoisy.setVisibility(View.GONE);
        myMediaRecorder = new MyMediaRecorder(this);

        if(!myMediaRecorder.checkPermissionFromDevice()){
            myMediaRecorder.requestPermissions();
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myMediaRecorder.checkPermissionFromDevice()){
                    File file = FileUtil.createFile("temp.amr");
//                    myMediaRecorder.setPathSave();
                    myMediaRecorder.setupMediaRecorder(file);
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);

                    handler.sendEmptyMessageDelayed(msgWhat, refreshTime);

                    Toast.makeText(MainActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
                }else
                    myMediaRecorder.requestPermissions();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaRecorder.setMediaRecorderToStop();
                handler.removeMessages(msgWhat);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (this.hasMessages(msgWhat)) {
                return;
            }
            volume = myMediaRecorder.getMaxAmplitude();
            if(volume > 0 && volume < 1000000){
                World.setDbCount(20 * (float)(Math.log10(volume)));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtDBCount.setText(""+World.dbCount);
                        if(World.dbCount > 60){
                            imgSoundSilence.setVisibility(View.INVISIBLE);
                            imgSoundNoisy.setVisibility(View.VISIBLE);
                        }else{
                            imgSoundSilence.setVisibility(View.VISIBLE);
                            imgSoundNoisy.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
            handler.sendEmptyMessageDelayed(msgWhat, refreshTime);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_MEDIA_RECORDER_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}