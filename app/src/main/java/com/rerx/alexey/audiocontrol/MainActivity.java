package com.rerx.alexey.audiocontrol;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity {
    FFT fft;

    TextView textView, noteText;
    Button startRecordBtn, stopRecordBtn;

    FFTAnother fftAnother;
    Complex complex;
    FFTKuli_Turky fftKuliTurky;
    Tablature tab;
    FilesControl filesControl;
    UI ui;

    final String TAG = "myLogs";
    Window window;
    LinearLayout amplitudeLayoutTOP, amplitudeLayoutBOTTOM;
    HorizontalScrollView amplitudeScrollTOP, amplitudeScrollBOTTOM;

    double realDPI, myDPI = 240.0;
    ImageView iview;
    Texts texts;
    Context context;
    short myBufferSize = 1024;
    int amplitudeColor;
    int maxAmplitudeColor;
    int maxAmplitudeIndex = 0;
    int barSize = 1;
    int baseFreq = 441;
    short spinnerSelection = 1;
    AudioRecord audioRecord;
    public boolean isReading = false;
    boolean isVisualized, isPaused = false;
    String notesInTabs[] = {};
    public int sampleRate = 8000;

    Complex[] frame0, frame1, spec0, spec1;

    short ShiftsPerFrame = 16;
    String note;


    HashMap<Integer, String> notesMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realDPI = getResources().getDisplayMetrics().densityDpi;

        context = this;

        tab = new Tablature(context);
        filesControl = new FilesControl(context);
        texts = new Texts(context);
        ui = new UI(context);

        amplitudeColor = getResources().getColor(R.color.amplitude);
        maxAmplitudeColor = getResources().getColor(R.color.maxAmplitude);

        startRecordBtn = (Button) findViewById(R.id.button_start_record);
        stopRecordBtn = (Button) findViewById(R.id.button_stop_record);

        textView = (TextView) findViewById(R.id.a110);
        noteText = (TextView) findViewById(R.id.note);
        amplitudeLayoutTOP = (LinearLayout) findViewById(R.id.amplitudeLayoutTOP);
        amplitudeLayoutBOTTOM = (LinearLayout) findViewById(R.id.amplitudeLayoutBOTTOM);

        amplitudeScrollTOP = (HorizontalScrollView) findViewById(R.id.amplitudeSCrollTOP);
        amplitudeScrollBOTTOM = (HorizontalScrollView) findViewById(R.id.amplitudeSCrollBOTTOM);

//        iview = ((ImageView) findViewById(R.id.text_note));
//        texts = (Texts) iview.getDrawable();


        createAudioRecorder();

        Log.e(TAG, "init state = " + audioRecord.getState());
        initializeAFC();
        setMaxAmplitudeColor();

        notesInTabs = setNotes();
//        initializeMap();
//        initializeMap_old();
//        testTab();
        initializeMap_new();

    }

    private String[] setNotes() {
        String[] s = new String[50];
        int count = 0;
        for (int i =6 ; i > 0; i--) {
            if (i == 2) {
                for (int j = 0; j < 4; j++) {
                    s[count++] = String.valueOf(i) + "-" + String.valueOf(j);
                }
            }else if (i == 1) {
                for (int j = 0; j < 21; j++) {
                    s[count++] = String.valueOf(i) + "-" + String.valueOf(j);
                }
            } else {
                for (int j = 0; j < 5; j++) {
                    s[count++] = String.valueOf(i) + "-" + String.valueOf(j);
                }
            }
        }
        return s;
    }

    private void initializeMap_new() {

        int old = countFreq(-30);
        int old_v = 70;

        for (int i = -29; i < 16; i++) {
            int delta = (countFreq(i) - old) / 2;
            for (int j = -delta; j < delta + 1; j++) {
                if (countFreq(i) + j != old_v) {
                    notesMap.put(countFreq(i) + j, notesInTabs[i + 29]);
                }
            }
            old_v = countFreq(i) + delta;
            old = countFreq(i);
        }

        //костыль! Работает, не трогай XD
        notesMap.put(142, notesInTabs[9]);
        notesMap.put(285, notesInTabs[21]);
        notesMap.put(320, notesInTabs[23]);
        notesMap.put(539, notesInTabs[32]);
        notesMap.put(571, notesInTabs[33]);
        notesMap.put(605, notesInTabs[34]);
        notesMap.put(641, notesInTabs[35]);
        notesMap.put(679, notesInTabs[36]);
        notesMap.put(762, notesInTabs[38]);
        notesMap.put(808, notesInTabs[39]);
        notesMap.put(856, notesInTabs[40]);
        notesMap.put(961, notesInTabs[42]);
    }

    private int countFreq(int i) {
        return (int) (baseFreq * Math.pow(2, (((double) i) / 12)));
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

    Spinner getBufferSizeChooser() {
        Spinner spinner = new Spinner(context);
        ArrayList<Integer> list = new ArrayList<>();
        int size = 256;
        for (int i = 0; i < 6; i++) {
            list.add(size *= 2);
        }
        spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                myBufferSize = Short.valueOf(((TextView) view).getText().toString());
                spinnerSelection = (short) position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                myBufferSize=1024;
            }
        });
        spinner.setSelection(spinnerSelection);
        return spinner;
    }


    public void onRecorsStartClick(View v) {


        if (!(isReading || isPaused)) {
            ui.setStartAlertDialog(); //открытие меню
        } else if (isReading) {
            pauseRecord();
        } else if (isPaused) {
            startRecord();
        }


    }


    public void finishRecord() {
        stopRecordBtn.setEnabled(false);
        startRecordBtn.setText(getString(R.string.menu));
        isPaused = false;
        isReading = false;
        tab.clearTab();
    }

    /**
     * Расчитывает относительно значение в пикселях исходя из реальной плотности пикселей.
     * Плотность на моем huawei = 240dpi, поэтому расчет ведется из отношения dpi устройства к dpi моего устройства.
     * Иначе местами разметка кривая.
     *
     * @return pixels
     */
    int countSize(int pixels) {
        return (int) (pixels * (realDPI / myDPI));
    }

    public void startRecord() {
        isPaused = false;
        stopRecordBtn.setEnabled(true);
        startRecordBtn.setText(getString(R.string.pause_record));
        Log.e(TAG, "record start");
        audioRecord.startRecording();
        int recordingState = audioRecord.getRecordingState();
        Log.d(TAG, "recordingState = " + recordingState);
        readStart();
        tab.startRecord();

    }

    public void onRecordStopClick(View v) {
        stopRecord();
        ui.setStopAlertDialog();
    }

    private void stopRecord() {
        readStop();
        Log.e(TAG, "record stop ");
        audioRecord.stop();
        tab.stopRecord();


    }

    private void pauseRecord() {
        readStop();
        isPaused = true;
        startRecordBtn.setText(getString(R.string.continue_record));
        Log.i(TAG, "Record paused");
        audioRecord.stop();
        tab.pauseRecord();
    }


    short[] myBuffer, myBufferOld;


    public void readStart() {
        fftKuliTurky = new FFTKuli_Turky();
        complex = new Complex();
        fftAnother = new FFTAnother();
        fft = new FFT();

        Log.e(TAG, "read start ");
        isReading = true;

        new Thread(() -> {
            if (audioRecord == null)
                return;

            int freq = 0;


            myBuffer = new short[myBufferSize];
            myBufferOld = new short[myBufferSize];
            window = new Window();
            int readCount = 0;

            readCount = audioRecord.read(myBuffer, 0, myBufferSize);

            for (int i = 0; i < myBuffer.length; i++) {
                myBuffer[i] *= window.Gausse(myBuffer[i], myBufferSize);
//                myBuffer[i] *= window.Hamming(myBuffer[i], myBufferSize);
                //myBuffer[i] *= sensivityRatio;
            }

            frame0 = complex.realToComplex(myBuffer);
//            myBufferOld = myBuffer;
            this.spec0 = fftAnother.DecimationInTime(frame0, true, true);

            while (isReading) {

//                iview.setImageDrawable(texts);

                readCount = audioRecord.read(myBuffer, 0, myBufferSize);

//
                for (int i = 0; i < myBuffer.length; i++) {
                    myBuffer[i] *= window.Gausse(i, myBufferSize);
//                    myBuffer[i] *= window.Hamming(myBuffer[i], myBufferSize);
//                    myBuffer[i] *= sensivityRatio;
                }

                frame1 = complex.realToComplex(myBuffer);


                int time0 = (int) System.currentTimeMillis();
                spec1 = fftAnother.DecimationInTime(frame1, true, false);

                int time2 = (int) System.currentTimeMillis();
                Log.d("time1", String.valueOf(time2 - time0));

                Log.i("filter", "1");
                final LinkedHashMap<Integer, Integer> spectrumNew = Filters.GetJoinedSpectrum(spec0, spec1, ShiftsPerFrame, sampleRate);

//                frame0 = frame1;
                Log.i("fft", "2");
                new Thread(() -> {
                    this.spec0 = fftAnother.DecimationInTime(frame1, true, false);
                }).start();


                if (isVisualized) {
                    setAFC((spectrumNew));  //Визуализация
                }


                if (getFrequence(spectrumNew) != freq) {
                    freq = getFrequence(spectrumNew);
                    if ((freq > 60) && (freq < 1047)) {

                        final String finalFreq = String.valueOf(freq);
                        runOnUiThread(() -> textView.setText(finalFreq));
//                        ((Texts)iview.getDrawable()).printFreq(String.valueOf(freq));
//                        texts.setFreq(String.valueOf(freq));
                        determineNotes(freq);
                        tab.addNote(note);
//                        texts.setNote(note);
                    }

                }

                int time4 = (int) System.currentTimeMillis();
                Log.d("time4", String.valueOf(time4 - time0));

            }

        }).start();


    }


    public int getFrequence(LinkedHashMap<Integer, Integer> map) {
        double f = 0;
        f = getMaxIndex(map.values().toArray()) * sampleRate / map.size();
//        return (int) Math.round(f);
        return (int) f;
    }



    public void setAmplitude(int amplitude, final LinearLayout layout) {
        final ImageView img = new ImageView(context);
        img.setLayoutParams(new LinearLayout.LayoutParams(barSize, amplitude));
        img.setBackgroundColor(amplitudeColor);
        runOnUiThread(() -> layout.addView(img));
//        amplitudeScrollTOP.scrollBy(10, 0);
    }

    public void setMaxAmplitudeColor() {
        runOnUiThread(() -> amplitudeLayoutTOP.getChildAt(maxAmplitudeIndex).setBackgroundColor(maxAmplitudeColor));

    }

    void initializeAFC() {
        for (int i = 0; i < 1100; i++) {
            setAmplitude(2, amplitudeLayoutTOP);
            setAmplitude(2, amplitudeLayoutBOTTOM);
        }
    }

    public void setAFC(Map<Integer, Integer> spectrum) {

        for (int i : spectrum.keySet()) {
            updateAFC(i, (int) (0.05 * spectrum.get(i)));
        }
    }

    void updateAFC(final int index, final int amplitude) {
        maxAmplitudeColor = 0;
        if (index < 1050) {

            runOnUiThread(() -> {
                try {
                    if (amplitude > 0) {
                        amplitudeLayoutTOP.getChildAt(index).setLayoutParams(new LinearLayout.LayoutParams(barSize, amplitude));
                        amplitudeLayoutBOTTOM.getChildAt(index).setLayoutParams(new LinearLayout.LayoutParams(barSize, 0));
                    } else {
                        amplitudeLayoutTOP.getChildAt(index).setLayoutParams(new LinearLayout.LayoutParams(barSize, 0));
                        amplitudeLayoutBOTTOM.getChildAt(index).setLayoutParams(new LinearLayout.LayoutParams(barSize, -amplitude));
                    }
                } catch (Exception e) {
                    Log.e("INDEX", String.valueOf(index));
                }
            });
        }
    }


    public void readStop() {
        Log.e(TAG, "read stop");
        isReading = false;
    }



    public double getMaxIndex(Object[] arr) {
        double fr = 0;
        Integer spectrumMax = (Integer) arr[0];
        for (short i = 0; i < arr.length; i++) {
            if ((Integer) arr[i] > spectrumMax) {
                spectrumMax = (Integer) arr[i];
                fr = i;
            }
        }
        return fr;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        isReading = false;
        if (audioRecord != null) {
            audioRecord.release();
        }
    }

    void determineNotes(int frequence) {
        String s = notesMap.get(frequence);
        if (s != null) {
            note = s;
        } else {
            note = "";
            return;
        }
//        ((Texts) iview.getDrawable()).printNote(String.valueOf(s));
//        texts.printNote(s);
        runOnUiThread(() -> noteText.setText(note));

    }
}
