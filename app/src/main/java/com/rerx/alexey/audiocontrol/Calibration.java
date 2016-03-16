package com.rerx.alexey.audiocontrol;

import android.content.Context;
import android.media.AudioRecord;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Калибровка перед использованием программы
 */
public class Calibration {
    // TODO: 20.02.16 сделать калибровку

    MainActivity mainActivity;
    Context context;
    AudioRecord audioRecord;
    Window window;
    Complex complex;
    FFTAnother fftAnother;
    UI ui;

    short myBufferSize;
    boolean isReading = false;
    double ShiftsPerFrame, sampleRate;
    short[] myBuffer, myBufferOld;
    Complex[] frame0, frame1, spec0, spec1;
    HashMap<Integer, Integer> map = new HashMap<>();


    Calibration(Context context) {
        this.context = context;
        mainActivity = (MainActivity) context;
        myBufferSize = 1024;
        audioRecord = mainActivity.audioRecord;
        ShiftsPerFrame = mainActivity.ShiftsPerFrame;
        sampleRate = mainActivity.sampleRate;
        complex = new Complex();
        fftAnother = new FFTAnother();
        ui = mainActivity.ui;
    }

    public void start() {
        ui.createCalibrationDialog(this).show();
    }


    public void startReading() {
        isReading = true;
        audioRecord.startRecording();
        int recordingState = audioRecord.getRecordingState();
        Log.d("Calibration", "recordingState = " + recordingState);
        read();
    }

    public void stopReading() {
        isReading = false;
        audioRecord.stop();
    }

    private void read() {
        new Thread(() -> {

            if (mainActivity.audioRecord == null) {
                return;
            }

            int freq = 0;

            myBuffer = new short[myBufferSize];
            myBufferOld = new short[myBufferSize];
            isReading = true;
            window = new Window();


            for (int i = 0; i < myBuffer.length; i++) {
                myBuffer[i] *= window.Gausse(myBuffer[i], myBufferSize);
            }

            frame0 = complex.realToComplex(myBuffer);
            spec0 = fftAnother.DecimationInTime(frame0, true, true);


            while (isReading) {

                window = new Window();

                audioRecord.read(myBuffer, 0, myBufferSize);

                for (int i = 0; i < myBuffer.length; i++) {
                    myBuffer[i] *= window.Gausse(i, myBufferSize);
                }

                frame1 = complex.realToComplex(myBuffer);
                spec1 = fftAnother.DecimationInTime(frame1, true, false);
                final LinkedHashMap<Integer, Integer> spectrumNew =
                        Filters.GetJoinedSpectrum(spec0, spec1, ShiftsPerFrame, sampleRate);

                new Thread(() -> {
                    spec0 = fftAnother.DecimationInTime(frame1, true, false);
                }).start();
                freq = printFreq(freq, spectrumNew);
            }
        }).start();
    }


    private int printFreq(int freq, LinkedHashMap<Integer, Integer> spectrumNew) {

        int freq_tmp = mainActivity.getFrequence(spectrumNew);
        if (freq_tmp != freq) {
            freq = freq_tmp;
            if ((freq > 60) && (freq < 1047)) {
                addFreq(freq);
                ui.printCalibrationFrequnce(getMaxFreq());
            }
        }
        return freq;
    }

    private void addFreq(int freq) {
        if (map.get(freq) != null) {
            map.put(freq, map.get(freq) + 1);
        } else {
            map.put(freq, 1);
        }
        Log.i("MAP", String.valueOf(map.get(freq)));
    }

    public int getMaxFreq() {
        int max = 0;
        int maxFreq = 0;
        for (int freq : map.keySet()) {
            max = map.get(freq) > max ? map.get(maxFreq = freq) : max;
        }
        return maxFreq;
    }

}
