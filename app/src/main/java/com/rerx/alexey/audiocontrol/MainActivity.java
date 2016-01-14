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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

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
    int barSize = 4;
    float sensivityRatio = (float) 0.01;
    AudioRecord audioRecord;
    boolean isReading = false;
    int frequance;
    public final double frequenceA = 421.875;
    public final short a110 = 110;
public double basisDb = 0.0000000000001;
     String  NOTES[] = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
 public int sampleRate = 8000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        amplitudeColor = getResources().getColor(R.color.amplitude);
        maxAmplitudeColor = getResources().getColor(R.color.maxAmplitude);

        textView = (TextView) findViewById(R.id.a110);
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

                    //Complex[] spectrumComplex = fftAnother.DecimationInTime(complex.realToComplex(myBuffer), true);

                   //short[] spectrum = complex.complexToShort(spectrumComplex);
                    Complex[] spectrum = fft.fft(complex.realToComplex(myBuffer));
                    short[] afc = complex.complexToShort(spectrum);
//                    for(int j = 0;j<afc.length;j++){
//                        if((afc[j+1] == afc[j]) && (afc[j+1] != 0) && (afc[j]) != 0){
//                            afc[j+1] = 100;
//                        }
//                    }

                    LinkedHashMap<Short, Boolean> map = new LinkedHashMap<>();

                    for (short i : afc) {
                        map.put(i, true);
                    }

                    List<Short> list = (List<Short>) map.keySet();
                    list.get(4);

                    Log.i("wswsws = ", String.valueOf(getFrequence(complex.complexToShort(spectrum))));
                    if ((getFrequence(afc)) == 328.125){
                        Log.e(TAG,"1st");
                    }
                    if((getFrequence(afc)/2) == 367.1875){
                        Log.e(TAG,"1-2");
                    }
                    if((getFrequence(afc)/2) == 390.625){
                        Log.e(TAG,"1-3");
                    }
                    if((getFrequence(afc)/2) == 218.75){
                        Log.e(TAG,"1-5");
                    }
                    if(((getFrequence(afc)/2) == 484.375) || ((getFrequence(afc)/2) == 234.375)){
                        Log.e(TAG,"1-7");
                    }
                    if((getFrequence(afc)/2) == 296.875){
                        Log.e(TAG,"2-3");
                    }
                    if(getFrequence(afc) == 171.875){
                        Log.e(TAG,String.valueOf((getFrequence(afc))/2));
                    }
                    //magnitudeTransform(spectrum);
//
//                    Log.i("ssss = ", String.valueOf(getMax(spectrum)));

                    setAFC(afc);

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
    public double getFrequence(short[] arr){
        double f = 0;
            f = getMax(arr)*sampleRate/arr.length;
        return f;
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
    public double frequeceFromA(int noteNumber){
        double frequence = frequenceA*(int)Math.pow(2.0,noteNumber/12);
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




    public double getMax(short[] arr){
        double fr = 0;
        double spectrumMax = arr[1];
        for(short i = 0;i < arr.length;i++){
            if (arr[i]>spectrumMax){
                spectrumMax = arr[i];
                fr = i;
            }
        }
        return fr;
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
