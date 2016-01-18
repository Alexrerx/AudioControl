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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends Activity {
    FFT fft;
    TextView textView;
    FFTAnother fftAnother;
    Complex complex;
    FFTKuli_Turky fftKuliTurky;
    final String TAG = "myLogs";
    Window window;
    ProgressBar pb;
    LinearLayout amplitudeLayoutTOP, amplitudeLayoutBOTTOM;
    HorizontalScrollView amplitudeScrollTOP, amplitudeScrollBOTTOM;

    Context context;
    short myBufferSize = 512;
    int amplitudeColor;
    int maxAmplitudeColor;
    int maxAmplitudeIndex = 0;
    int barSize = 1;
    float sensivityRatio = (float) 0.065;
    AudioRecord audioRecord;
    boolean isReading = false;
    int frequance;
    public final double frequenceA = 421.875;
    public final short a110 = 110;
public double basisDb = 0.0000000000001;
     String  NOTES[] = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
 public int sampleRate = 8000;
    Complex[] frame0;
    Complex[] frame1;
    short ShiftsPerFrame = 16;
    String note;
    TextView noteText;
    ArrayList<Complex> spectrum1;
    ArrayList<Complex> spectrum0;
    Complex[] spec0;
    Complex[] spec1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        amplitudeColor = getResources().getColor(R.color.amplitude);
        maxAmplitudeColor = getResources().getColor(R.color.maxAmplitude);

        textView = (TextView) findViewById(R.id.a110);
        noteText = (TextView) findViewById(R.id.note);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        amplitudeLayoutTOP = (LinearLayout) findViewById(R.id.amplitudeLayoutTOP);
        amplitudeLayoutBOTTOM = (LinearLayout) findViewById(R.id.amplitudeLayoutBOTTOM);

        amplitudeScrollTOP = (HorizontalScrollView) findViewById(R.id.amplitudeSCrollTOP);
        amplitudeScrollBOTTOM = (HorizontalScrollView) findViewById(R.id.amplitudeSCrollBOTTOM);
        createAudioRecorder();

        Log.e(TAG, "init state = " + audioRecord.getState());
        initializeAFC();
        setMaxAmplitudeColor();
    }

    void createAudioRecorder() {

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
        fft = new FFT();
//        spec0 = new Complex[myBuffer.length];
 //       spec 1 = new Complex[myBuffer.length];
        Log.e(TAG, "read start ");
        isReading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (audioRecord == null)
                    return;

                double l = 0, k = 0, freq = 0;


                myBuffer = new short[myBufferSize];
                window = new Window();
                int readCount = 0;
                int totalCount = 0;
                boolean first = true;

                first = false;
                readCount = audioRecord.read(myBuffer, 0, myBufferSize);
                totalCount += readCount;
                for (int i = 0; i < myBuffer.length; i++) {
                    myBuffer[i] *= (sensivityRatio * window.Gausse(i, myBufferSize));
//                        if (myBuffer[i+1] == myBuffer[i]){
//                            myBuffer[i+1] = 0;
//                        }
                }
                frame0 = complex.realToComplex(myBuffer);

                while (isReading) {

                    readCount = audioRecord.read(myBuffer, 0, myBufferSize);
                    totalCount += readCount;
//                    Log.e(TAG, "readCount = " + readCount + ", totalCount = "
//                            + totalCount);

                    Log.i("Max buffer",String.valueOf(getMaxByffer(myBuffer)));
                    for (int i = 0; i < myBuffer.length; i++) {
                        myBuffer[i] *= (sensivityRatio   * window.Gausse(i, myBufferSize));
//                        if (myBuffer[i+1] == myBuffer[i]){
//                            myBuffer[i+1] = 0;
//                        }
                    }

//                   setAFC(myBuffer);
                    //setAFC(getMaxByfferArray(myBuffer));
                    //Complex[] spectrumComplex = fftAnother.DecimationInTime(complex.realToComplex(myBuffer), true);

                   //short[] spectrum = complex.complexToShort(spectrumComplex);
                    Complex[] spectrum = fft.fft(complex.realToComplex(myBuffer));
                    final short[] afc = complex.complexToShort(spectrum);
//                    spectrum0.toArray(spec0);

                    frame1 = complex.realToComplex(myBuffer);

                    spec0 = fftAnother.DecimationInTime(frame0, true);

//                    spectrum1.toArray(spec1);
                    spec1 = fftAnother.DecimationInTime(frame1, true);

                    frame0 = frame1;

                    for (int r = 0; r < myBuffer.length; r++)
                    {
                        spec0[r].abs /= myBuffer.length;
                        spec1[r].abs /= myBuffer.length;
                    }

                    LinkedHashMap<Integer, Integer> spectrumNew = Filters.GetJoinedSpectrum(spec0, spec1, ShiftsPerFrame, sampleRate);

                    setAFC(spectrumNew);

//                    LinkedHashMap<Double, Boolean> map = new LinkedHashMap<>();

                    if ((!(getFrequence(afc) == freq)) || (getFrequence(afc)>1047) || (getFrequence(afc)<76))  {
                        freq = getFrequence(afc);
                        //Log.i("wswsws = ", String.valueOf(freq));


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(String.valueOf(getFrequence(afc)));
                            }
                        });

                        if ((getFrequence(afc)) == 328.125) {
                            Log.e(TAG, "1st");
                            note = "1st";
                            k++;
                        } else {
                            l++;
                        }
                        if ((getFrequence(afc)) == 343.75) {
                            Log.e(TAG, "1-1");
                            note = "1-1";
                        }
                        if ((getFrequence(afc)) == 359.375) {
                            Log.e(TAG, "1-2");
                            note = "1-2";
                        }
                        if ((getFrequence(afc)) == 390.625) {
                            Log.e(TAG, "1-3");
                            note = "1-3";
                        }
                        if ((getFrequence(afc)) == 406.25) {
                            Log.e(TAG, "1-4");
                            note = "1-4";
                        }
                        if ((getFrequence(afc)) == 437.5) {
                            Log.e(TAG, "1-5");
                            note = "1-5";
                        }
                        if ((getFrequence(afc)) == 453.125) {
                            Log.e(TAG, "1-6");
                            note = "1-6";
                        }
                        if ((getFrequence(afc)) == 484.375) {
                            Log.e(TAG, "1-7");
                            note = "1-7";
                        }
                        if ((getFrequence(afc)) == 500) {
                            Log.e(TAG, "1-8");
                            note = "1-8";
                        }
                        if ((getFrequence(afc)) == 531.25) {
                            Log.e(TAG, "1-9");
                            note = "1-9";
                        }
                        if ((getFrequence(afc)) == 562.5) {
                            Log.e(TAG, "1-10");
                            note = "1-10";
                        }
                        if ((getFrequence(afc)) == 593.75) {
                            Log.e(TAG, "1-11");
                            note = "1-11";
                        }
                        if ((getFrequence(afc)) == 625) {
                            Log.e(TAG, "1-12");
                            note = "1-12";
                        }
                        if ((getFrequence(afc)) == 671.875) {
                            Log.e(TAG, "1-13");
                            note = "1-13";
                        }
                        if ((getFrequence(afc)) == 703.125) {
                            Log.e(TAG, "1-14");
                            note = "1-14";
                        }
                        if ((getFrequence(afc)) == 750.0) {
                            Log.e(TAG, "1-15");
                            note = "1-15";
                        }
                        if ((getFrequence(afc)) == 796.875) {
                            Log.e(TAG, "1-16");
                            note = "1-16";
                        }
                        if ((getFrequence(afc)) == 828.125) {
                            Log.e(TAG, "1-17");
                            note = "1-17";
                        }
                        if ((getFrequence(afc)) == 890.625) {
                            Log.e(TAG, "1-18");
                            note = "1-18";
                        }
                        if ((getFrequence(afc)) == 953.125) {
                            Log.e(TAG, "1-19");
                            note = "1-19";
                        }
                        if ((getFrequence(afc)) == 1000) {
                            Log.e(TAG, "1-20");
                            note = "1-20";
                        }
                        if ((getFrequence(afc)) == 1046.875) {
                            Log.e(TAG, "1-21");
                            note = "1-21";
                        }

                        /**********2nd********************/
                        if ((getFrequence(afc)) == 250) {
                            Log.e(TAG, "2nd");
                            note = "2-0";
                        }

                        if ((getFrequence(afc)) == 265.625) {
                            Log.e(TAG, "2-1");
                            note = "2-1";
                        }
                        if ((getFrequence(afc)) == 281.25) {
                            Log.e(TAG, "2-2");
                            note = "2-2";
                        }
                        if ((getFrequence(afc)) == 296.875) {
                            Log.e(TAG, "2-3");
                            note = "2-3";
                        }
                        if ((getFrequence(afc)) == 312.5) {
                            Log.e(TAG, "2-4");
                            note = "2-4";
                        }
                        if ((getFrequence(afc)) == 328.125) {
                            Log.e(TAG, "2-5");
                            note = "2-5";
                        }
                        if ((getFrequence(afc)) == 343.75) {
                            Log.e(TAG, "2-6");
                            note = "2-6";
                        }
                        if ((getFrequence(afc)) == 375.0) {
                            Log.e(TAG, "2-7");
                            note = "2-7";
                        }
                        if ((getFrequence(afc)) == 390.625) {
                            Log.e(TAG, "2-8");
                            note = "2-8";
                        }
                        if ((getFrequence(afc)) == 406.25) {
                            Log.e(TAG, "2-9");
                            note = "2-9";
                        }
                        if ((getFrequence(afc)) == 437.5) {
                            Log.e(TAG, "2-10");
                            note = "2-10";
                        }
                        if ((getFrequence(afc)) == 468.75) {
                            Log.e(TAG, "2-11");
                            note = "2-11";
                        }
                        if ((getFrequence(afc)) == 484.375) {
                            Log.e(TAG, "2-12");
                            note = "2-12";
                        }
                        if ((getFrequence(afc)) == 515.625) {
                            Log.e(TAG, "2-13");
                            note = "2-13";
                        }
                        if ((getFrequence(afc)) == 546.875) {
                            Log.e(TAG, "2-14");
                            note = "2-14";
                        }
                        if ((getFrequence(afc)) == 578.125) {
                            Log.e(TAG, "2-15");
                            note = "2-15";
                        }
                        if ((getFrequence(afc)) == 625.0) {
                            Log.e(TAG, "2-16");
                            note = "2-16";
                        }
                        if ((getFrequence(afc)) == 656.25) {
                            Log.e(TAG, "2-17");
                            note = "2-17";
                        }
                        if ((getFrequence(afc)) == 687.5) {
                            Log.e(TAG, "2-18");
                            note = "2-18";
                        }
                        if ((getFrequence(afc)) == 718.75) {
                            Log.e(TAG, "2-19");
                            note = "2-19";
                        }
                        if ((getFrequence(afc)) == 765.625) {
                            Log.e(TAG, "2-20");
                            note = "2-20";
                        }
                        if ((getFrequence(afc)) == 812.5) {
                            Log.e(TAG, "2-21");
                            note = "2-21";
                        }


                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3rd");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-1");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-2");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-3");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-4");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-5");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-6");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-7");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-8");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-9");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-10");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-11");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-12");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-13");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-14");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-15");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-16");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-17");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-18");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-19");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-20");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "3-21");
                        }

                        /*****************BASS**********************/
                        /******************4th*********************/
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4th");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-1");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-2");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-3");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-4");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-5");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-6");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-7");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-8");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-9");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-10");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-11");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-12");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-13");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-14");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-15");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-16");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-17");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-18");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-19");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-20");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "4-21");
                        }

                        /*********************5th*******************/
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5th");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-1");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-2");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-3");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-4");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-5");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-6");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-7");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-8");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-9");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-10");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-11");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-12");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-13");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-14");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-15");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-16");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-17");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-18");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-19");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-20");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "5-21");
                        }
                        /******************6th********************/
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6th");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-2");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-3");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-4");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-5");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-6");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-7");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-8");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-9");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-10");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-11");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-12");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-13");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-14");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-15");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-16");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-17");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-18");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-19");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-20");
                        }
                        if ((getFrequence(afc)) == 1) {
                            Log.e(TAG, "6-21");
                        }
                        /***************МАРАЗМ ОКОНЧЕН*********************/
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noteText.setText(note);
                            }
                        });
                    }
                }

                //magnitudeTransform(spectrum);
//
//                    Log.i("ssss = ", String.valueOf(getMax(spectrum)));

                    //setAFC(afc);

//                    for (int k = 0;k < myBufferSize;k++){
//                            fftKuliTurky.Calculate(myBuffer);
//                            Log.i(TAG,Integer.toString() );
//                        }
                }

//                Log.d("K/L",String.valueOf(k/l));
//                final double kl = k / l;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        textView.setText("Хороших/Плохим=" + String.valueOf(kl));
//                    }
//                });



        }).start();
//
//        for (int k = 0;k < myBufferSize;k++){
//            fftKuliTurky.Calculate(myBuffer);
//            Log.i(TAG,Integer.toString() );
//        }


    }
    public double getFrequence(short[] arr){
        double f = 0;
            f = getMaxIndex(arr)*sampleRate/arr.length;
        return f;
    }
    public short getMaxByffer(short[] buffer){
        short maxAmplitude = 0;
        for (int i = 0; i<buffer.length; i++){
            if (buffer[i] > maxAmplitude){
                maxAmplitude = myBuffer[i];
            }
        }
        return maxAmplitude;
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
        for (int i = 0; i < 1100; i++) {
            setAmplitude(2, amplitudeLayoutTOP);
            setAmplitude(2, amplitudeLayoutBOTTOM);
        }
    }

    public void setAFC(Map<Integer, Integer> spectrum) {
//        Log.d("MAP", "00000000");

        for (int i : spectrum.keySet()) {

            updateAFC(i, spectrum.get(i));

//            Log.d("MAP", String.valueOf(spectrum.get(i)));

        }

//        for (int i = 0; i < myBufferSize / 2; i++) {
//                        Log.e(TAG, Integer.toString(i) + ":" + myBuffer[i] + ":" + (myBuffer[i]));
        //spectrum[i] *= window.Hamming(i, myBufferSize/2);
//                        setVisualization(myBuffer[i]);
//            updateAFC(i, (short) ((spectrum[i])));
//        }
//        setMaxAmplitudeColor();
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

    void updateAFC(final int index, final int amplitude) {
        maxAmplitudeColor = 0;
        if (index < 1050) {

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
    }
    public double frequeceFromA(int noteNumber){
        double frequence = frequenceA*(int)Math.pow(2.0,noteNumber/12);
        return frequence;
    }

    void updateAFC_2(final int index, final int amplitude) {

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
        int j = 0;
        short x = spectrum[0];
        maxAmplitudeColor = 0;
        for (int i = 0; i < spectrum.length / 2; i++) {
           // spectrum[i] *= window.Hamming(i, myBufferSize);
            //spectrum[i] *= window.Hamming(i, myBufferSize);
            if (spectrum[i] > spectrum[maxAmplitudeIndex]) {
                maxAmplitudeIndex = i;
            }
            if (spectrum[i]>x){
                x = spectrum[i];
                j = i;
            }


//                final int finalI = i;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        textView.setText(spectrum[finalI]);
//                    }
//                });
//

        }
            //spectrum[i] /= spectrum.length;
            //spectrum[i] = (short)(10*Math.log10(spectrum[i]/basisDb));

        return spectrum;
    }
//    public short[] deleteOver(final short [] arr) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0;i<arr.length;i++){
//                    if(arr[i] % arr[1] == 0){
//                        arr[i] = -1;
//                    }
//                }
//            }
//        });
//        return arr;
//    }




    public double getMaxIndex(short[] arr){
        double fr = 0;
        double spectrumMax = arr[0];
        for(short i = 0;i < arr.length;i++){
            if (arr[i]>spectrumMax){
                spectrumMax = arr[i];
                fr = i;
            }
        }
        return fr;
    }
    public short[] getMaxByfferArray(short[] buffer){
        short max = buffer[0];
        short[] maxBufferArray = new short[buffer.length];
        for (int i = 1; i< buffer.length; i++){
            if (buffer[i] > max){
                max = buffer[i];

            }
        }
        return maxBufferArray;
    }
//        public static int calculate(int sampleRate, short [] audioData){
//
//        int numSamples = audioData.length;
//        int numCrossing = 0;
//        for (int p = 0; p < numSamples-1; p++)
//        {
//            if ((audioData[p] > 0 && audioData[p + 1] <= 0) ||
//                    (audioData[p] < 0 && audioData[p + 1] >= 0))
//            {
//                numCrossing++;
//            }
//        }
//        float numSecondsRecorded = (float)numSamples/(float)sampleRate;
//        float numCycles = numCrossing/2;
//        float frequency = numCycles/numSecondsRecorded;
//
//        return (int)frequency;
//    }

//    public double getPitchInSampleRange(AudioSamples as, int start, int end) throws Exception {
//        //If your sound is musical note/voice you need to limit the results because it wouldn't be above 4500Hz or bellow 20Hz
//        int nLowPeriodInSamples = (int) as.getSamplingRate() / 4500;
//        int nHiPeriodInSamples = (int) as.getSamplingRate() / 20;
//
//        //I get my sample values from my AudioSamples class. You can get them from wherever you want
//        double[] samples = Arrays.copyOfRange((as.getSamplesChannelSegregated()[0]), start, end);
//        if(samples.length < nHiPeriodInSamples) throw new Exception("Not enough samples");
//
//        //Since we're looking the periodicity in samples, in our case it won't be more than the difference in sample numbers
//        double[] results = new double[nHiPeriodInSamples - nLowPeriodInSamples];
//
//        //Now you iterate the time lag
//        for(int period = nLowPeriodInSamples; period < nHiPeriodInSamples; period++) {
//            double sum = 0;
//            //Autocorrelation is multiplication of the original and time lagged signal values
//            for(int i = 0; i < samples.length - period; i++) {
//                sum += samples[i]*samples[i + period];
//            }
//            //find the average value of the sum
//            double mean = sum / (double)samples.length;
//            //and put it into results as a value for some time lag.
//            //You subtract the nLowPeriodInSamples for the index to start from 0.
//            results[period - nLowPeriodInSamples] = mean;
//        }
//        //Now, it is obvious that the mean will be highest for time lag equal to the periodicity of the signal because in that case
//        //most of the positive values will be multiplied with other positive and most of the negative values will be multiplied with other
//        //negative resulting again as positive numbers and the sum will be high positive number. For example, in the other case, for let's say half period
//        //autocorrelation will multiply negative with positive values resulting as negatives and you will get low value for the sum.
//        double fBestValue = Double.MIN_VALUE;
//        int nBestIndex = -1; //the index is the time lag
//        //So
//        //The autocorrelation is highest at the periodicity of the signal
//        //The periodicity of the signal can be transformed to frequency
//        for(int i = 0; i < results.length; i++) {
//            if(results[i] > fBestValue) {
//                nBestIndex = i;
//                fBestValue = results[i];
//            }
//        }
//        //Convert the period in samples to frequency and you got yourself a fundamental frequency of a sound
//        double res = as.getSamplingRate() / (nBestIndex + nLowPeriodInSamples);
//
//        return res;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isReading = false;
        if (audioRecord != null) {
            audioRecord.release();
        }
    }
}
