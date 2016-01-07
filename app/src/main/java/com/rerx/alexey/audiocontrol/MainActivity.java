package com.rerx.alexey.audiocontrol;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    final String TAG = "myLogs";

    ProgressBar pb;

    int myBufferSize = 8192;
    AudioRecord audioRecord;
    boolean isReading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar);


        createAudioRecorder();

        Log.e(TAG, "init state = " + audioRecord.getState());
    }

    void createAudioRecorder() {
        int sampleRate = 8000;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        int minInternalBufferSize = AudioRecord.getMinBufferSize(sampleRate,
                channelConfig, audioFormat);
        int internalBufferSize = minInternalBufferSize * 4;
        Log.e(TAG, "minInternalBufferSize = " + minInternalBufferSize
                + ", internalBufferSize = " + internalBufferSize
                + ", myBufferSize = " + myBufferSize);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfig, audioFormat, internalBufferSize);
    }

    public void recordStart(View v) {
        Log.e(TAG, "record start");
        audioRecord.startRecording();
        int recordingState = audioRecord.getRecordingState();
        Log.e(TAG, "recordingState = " + recordingState);
    }

    public void recordStop(View v) {
        Log.e(TAG, "record stop ");
        audioRecord.stop();
    }


    byte[] myBuffer;
    int i;
    public void readStart(View v) {

        Log.e(TAG, "read start ");
        isReading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (audioRecord == null)
                    return;

                myBuffer = new byte[myBufferSize];
                int readCount = 0;
                int totalCount = 0;
                while (isReading) {
                    readCount = audioRecord.read(myBuffer, 0, myBufferSize);
                    totalCount += readCount;
//                    Log.e(TAG, "readCount = " + readCount + ", totalCount = "
//                            + totalCount);
                    for (i= 0;i<50;i+=2){
                        Log.e(TAG,Integer.toString(i)+":"+myBuffer[i]);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.setProgress(toProgress(myBuffer[i]));
                            }
                        });
                    }
                }
            }
        }).start();
    }

    int toProgress(byte a){
        if(a<0){
            a+=256;
        }
        return  a/10;
    }

    public void readStop(View v) {
        Log.e(TAG, "read stop");
        isReading = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isReading = false;
        if (audioRecord != null) {
            audioRecord.release();
        }
    }
}
