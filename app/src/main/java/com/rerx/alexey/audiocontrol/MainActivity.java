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
    FFTAnother fftAnother;
    Complex complex;
    FFTKuli_Turky fftKuliTurky;
    final String TAG = "myLogs";
    Window window;
    ProgressBar pb;
    LinearLayout amplitudeLayoutTOP, amplitudeLayoutBOTTOM;
    HorizontalScrollView amplitudeScrollTOP, amplitudeScrollBOTTOM;

    Context context;
    short myBufferSize = 256;
    int amplitudeColor;
    int maxAmplitudeColor;
    int maxAmplitudeIndex = 0;
    int barSize = 4;
    float sensivityRatio = (float) 0.01;
    AudioRecord audioRecord;
    boolean isReading = false;

    public final int frequenceA = 440;
public double basisDb = 0.0000000000001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        amplitudeColor = getResources().getColor(R.color.amplitude);
        maxAmplitudeColor = getResources().getColor(R.color.maxAmplitude);


        pb = (ProgressBar) findViewById(R.id.progressBar);
        amplitudeLayoutTOP = (LinearLayout) findViewById(R.id.amplitudeLayoutTOP);
        amplitudeLayoutBOTTOM = (LinearLayout) findViewById(R.id.amplitudeLayoutBOTTOM);

        amplitudeScrollTOP = (HorizontalScrollView) findViewById(R.id.amplitudeSCrollTOP);
        amplitudeScrollBOTTOM = (HorizontalScrollView) findViewById(R.id.amplitudeSCrollBOTTOM);
        createAudioRecorder();

        Log.e(TAG, "init state = " + audioRecord.getState());
        initializeAFC();
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

    short[] myBuffer;

    public void readStart() {
        //fftKuliTurky = new FFTKuli_Turky();
        complex = new Complex();
        fftAnother = new FFTAnother();
        Log.e(TAG, "read start ");
        isReading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (audioRecord == null)
                    return;

                myBuffer = new short[myBufferSize];
                window = new Window();
                int readCount = 0;
                int totalCount = 0;
                while (isReading) {
                    readCount = audioRecord.read(myBuffer, 0, myBufferSize);
                    totalCount += readCount;
//                    Log.e(TAG, "readCount = " + readCount + ", totalCount = "
//                            + totalCount);
                    for (int i = 0; i < myBuffer.length; i++) {
                        myBuffer[i] *= (sensivityRatio * window.Gausse(i, myBufferSize));
                    }
                    Complex[] spectrumComplex = fftAnother.DecimationInTime(complex.realToComplex(myBuffer), true);
                   short[] spectrum = complex.complexToShort(spectrumComplex);
                    magnitudeTransform(spectrum);

                    setAFC(spectrum);
//                    for (int k = 0;k < myBufferSize;k++){
//                            fftKuliTurky.Calculate(myBuffer);
//                            Log.i(TAG,Integer.toString() );
//                        }
                }
            }
        }).start();
//        for (int k = 0;k < myBufferSize;k++){
//            fftKuliTurky.Calculate(myBuffer);
//            Log.i(TAG,Integer.toString() );
//        }


    }

    private void setVisualization(final short data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb.setProgress((data));
            }
        });

    }

    public void setAmplitude(int amplitude, final LinearLayout layout) {
        final ImageView img = new ImageView(context);
        img.setLayoutParams(new LinearLayout.LayoutParams(barSize, amplitude));
        img.setBackgroundColor(amplitudeColor);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.addView(img);
            }
        });
//        amplitudeScrollTOP.scrollBy(10, 0);
    }

    public void setMaxAmplitudeColor() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                amplitudeLayoutTOP.getChildAt(maxAmplitudeIndex).setBackgroundColor(maxAmplitudeColor);
            }
        });

    }

    void initializeAFC() {
        for (int i = 0; i < myBufferSize; i++) {
            setAmplitude(2, amplitudeLayoutTOP);
            setAmplitude(2, amplitudeLayoutBOTTOM);
        }
    }

    public void setAFC(short[] spectrum) {

        for (int i = 0; i < myBufferSize / 2; i++) {
//                        Log.e(TAG, Integer.toString(i) + ":" + myBuffer[i] + ":" + (myBuffer[i]));
            //spectrum[i] *= window.Hamming(i, myBufferSize/2);
//                        setVisualization(myBuffer[i]);
            updateAFC(i, (short) ((spectrum[i])));
        }
//        setMaxAmplitudeColor();
    }

    void updateAFC(final int index, final short amplitude) {
        maxAmplitudeColor = 0;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (amplitude > 0) {
                    amplitudeLayoutTOP.getChildAt(index).setLayoutParams(new LinearLayout.LayoutParams(barSize, amplitude));
                    amplitudeLayoutBOTTOM.getChildAt(index).setLayoutParams(new LinearLayout.LayoutParams(barSize, 0));
                } else {
                    amplitudeLayoutTOP.getChildAt(index).setLayoutParams(new LinearLayout.LayoutParams(barSize, 0));
                    amplitudeLayoutBOTTOM.getChildAt(index).setLayoutParams(new LinearLayout.LayoutParams(barSize, -amplitude));
                }
            }
        });

    }
    public int frequeceFromA(int noteNumber){
        int frequence = frequenceA*(int)Math.pow(2.0,noteNumber/12);
        return frequence;
    }

    void updateAFC_2(final int index, final short amplitude) {
        if (amplitude > 0) {
            setAmplitude(amplitude, amplitudeLayoutTOP);
            setAmplitude(0, amplitudeLayoutBOTTOM);
        } else {
            setAmplitude(0, amplitudeLayoutTOP);
            setAmplitude(-amplitude, amplitudeLayoutBOTTOM);
        }
    }

    public void readStop() {
        Log.e(TAG, "read stop");
        isReading = false;
    }

    public short[] magnitudeTransform(short spectrum[]){
        maxAmplitudeColor = 0;
        for (int i = 0; i < spectrum.length / 2; i++) {
            spectrum[i] *= window.Hamming(i, myBufferSize);
            spectrum[i] *= window.Hamming(i, myBufferSize);
            if (spectrum[i] > spectrum[maxAmplitudeIndex]) {
                maxAmplitudeIndex = i;
            }
            //spectrum[i] /= spectrum.length;
            //spectrum[i] = (short)(10*Math.log10(spectrum[i]/basisDb));

        }
        return spectrum;
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
