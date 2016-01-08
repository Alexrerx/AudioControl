package com.rerx.alexey.audiocontrol;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    final String TAG = "myLogs";
    Window window;
    ProgressBar pb;
    LinearLayout amplitudeLayout;
    HorizontalScrollView amplitudeScroll;

    Context context;

    int myBufferSize = 64;
    int amplitudeColor;
    AudioRecord audioRecord;
    boolean isReading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        amplitudeColor = getResources().getColor(R.color.amplitude);


        pb = (ProgressBar) findViewById(R.id.progressBar);
        amplitudeLayout = (LinearLayout) findViewById(R.id.amplitudeLayout);
        amplitudeScroll = (HorizontalScrollView) findViewById(R.id.amplitudeSCroll);

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
        readStart();
    }

    public void recordStop(View v) {
        readStop();
        Log.e(TAG, "record stop ");
        audioRecord.stop();
    }

    byte[] myBuffer;

    public void readStart() {

        Log.e(TAG, "read start ");
        isReading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (audioRecord == null)
                    return;

                myBuffer = new byte[myBufferSize];
                window = new Window();
                int readCount = 0;
                int totalCount = 0;
                while (isReading) {
                    readCount = audioRecord.read(myBuffer, 0, myBufferSize);
                    totalCount += readCount;
//                    Log.e(TAG, "readCount = " + readCount + ", totalCount = "
//                            + totalCount);
                    for (int i = 0; i < myBufferSize; i += 2) {
                        Log.e(TAG, Integer.toString(i) + ":" + myBuffer[i] + ":" + dataToProgress(myBuffer[i]));
                        myBuffer[i] *= window.Hamming(i,myBufferSize);
                        setVisualization(myBuffer[i]);
                    }
                }
            }
        }).start();
    }

    private void setVisualization(final byte data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb.setProgress(dataToProgress(data));
            }
        });
        setAmplitude(dataToAmplitude(data));

    }

    int dataToProgress(int a) {
        if(a<0){
            a+=256;
        }
        return (256 - a) / 5;
    }

    int dataToAmplitude(int a) {
        if (a < 0) {
            a += 256;
        }
        return (256 - a) * 2;
    }

    public void setAmplitude(int amplitude) {
        final ImageView img = new ImageView(context);
        img.setLayoutParams(new LinearLayout.LayoutParams(5, amplitude));
        img.setBackgroundColor(amplitudeColor);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                amplitudeLayout.addView(img);
            }
        });
        amplitudeScroll.scrollBy(10, 0);
    }

    public void readStop() {
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
