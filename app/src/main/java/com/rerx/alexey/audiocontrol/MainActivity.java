package com.rerx.alexey.audiocontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import view.SlidingTabLayout;

public class MainActivity extends FragmentActivity {
    FFT fft;

    TextView textView, noteText;
    Button startRecordBtn, stopRecordBtn;


    FFTAnother fftAnother;
    Complex complex;
    FFTKuli_Turky fftKuliTurky;
    Tablature tab;
    FilesControl filesControl;
    final String TAG = "myLogs";
    Window window;
    LinearLayout amplitudeLayoutTOP, amplitudeLayoutBOTTOM, layoutTOP;
    HorizontalScrollView amplitudeScrollTOP, amplitudeScrollBOTTOM;

    ImageView iview;
    Texts texts;
    Context context;
    short myBufferSize = 1024;
    int amplitudeColor;
    int maxAmplitudeColor;
    int maxAmplitudeIndex = 0;
    int barSize = 1;
    float sensivityRatio = (float) 0.065;
    AudioRecord audioRecord;
    public boolean isReading = false;
    int frequance;
    public final double frequenceA = 421.875;
    public final short a110 = 110;
    public double basisDb = 0.0000000000001;
    String NOTES[] = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    String notesInTabs[] = {};
    public int sampleRate = 8000;

    Complex[] frame0, frame1, spec0, spec1;
    double[] kuli0, kuli1;

    short ShiftsPerFrame = 16;
    String note;
    ArrayList<Complex> spectrum1;
    ArrayList<Complex> spectrum0;

    HashMap<Integer, String> notesMap = new HashMap<>();
    HashMap<Integer, String> notesMap_old = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        tab = new Tablature(context);
        filesControl = new FilesControl(context);
        texts = new Texts(context);

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

//    void setTexts(Texts texts){
//        this.texts = texts;
//    }

    private void initializeMap_old() {
        notesMap_old.put(328, "1-0");
        notesMap_old.put(343, "1-1");
        notesMap_old.put(359, "1-2");
        notesMap_old.put(390, "1-3");
        notesMap_old.put(406, "1-4");
        notesMap_old.put(437, "1-5");
        notesMap_old.put(453, "1-6");
        notesMap_old.put(484, "1-7");
        notesMap_old.put(515, "1-8");
        notesMap_old.put(546, "1-9");
        notesMap_old.put(578, "1-10");
        notesMap_old.put(609, "1-11");
        notesMap_old.put(656, "1-12");
        notesMap_old.put(687, "1-13");
        notesMap_old.put(734, "1-14");
        notesMap_old.put(781, "1-15");
        notesMap_old.put(828, "1-16");
        notesMap_old.put(875, "1-17");
        notesMap_old.put(921, "1-18");
        notesMap_old.put(984, "1-19");
        notesMap_old.put(1046, "1-20");
        notesMap_old.put(250, "2-0");
        notesMap_old.put(265, "2-1");
        notesMap_old.put(281, "2-2");
        notesMap_old.put(296, "2-3");
        notesMap_old.put(312, "2-4");
        notesMap_old.put(187, "3-0");
        notesMap_old.put(203, "3-1");
        notesMap_old.put(218, "3-2");
        notesMap_old.put(234, "3-3");
        notesMap_old.put(1, "3-4");
        notesMap_old.put(1, "3-5");
        notesMap_old.put(1, "3-6");
        notesMap_old.put(1, "3-7");
        notesMap_old.put(1, "3-8");
        notesMap_old.put(1, "3-9");
        notesMap_old.put(1, "3-10");
        notesMap_old.put(1, "3-11");
        notesMap_old.put(1, "3-12");
        notesMap_old.put(1, "3-13");
        notesMap_old.put(1, "3-14");
        notesMap_old.put(1, "3-15");
        notesMap_old.put(1, "3-16");
        notesMap_old.put(1, "3-17");
        notesMap_old.put(1, "3-18");
        notesMap_old.put(1, "3-19");
        notesMap_old.put(1, "3-20");
        notesMap_old.put(1, "3-21");
        notesMap_old.put(140, "4-0");
        notesMap_old.put(156, "4-1");
        notesMap_old.put(171, "4-2");
        notesMap_old.put(1, "4-3");
        notesMap_old.put(1, "4-4");
        notesMap_old.put(1, "4-5");
        notesMap_old.put(1, "4-6");
        notesMap_old.put(1, "4-7");
        notesMap_old.put(1, "4-8");
        notesMap_old.put(1, "4-9");
        notesMap_old.put(1, "4-10");
        notesMap_old.put(1, "4-11");
        notesMap_old.put(1, "4-12");
        notesMap_old.put(1, "4-13");
        notesMap_old.put(1, "4-14");
        notesMap_old.put(1, "4-15");
        notesMap_old.put(1, "4-16");
        notesMap_old.put(1, "4-17");
        notesMap_old.put(1, "4-18");
        notesMap_old.put(1, "4-19");
        notesMap_old.put(1, "4-20");
        notesMap_old.put(1, "4-21");
        notesMap_old.put(109, "5-0");
        notesMap_old.put(1, "5-1");
        notesMap_old.put(1, "5-2");
        notesMap_old.put(125, "5-3");
        notesMap_old.put(1, "5-4");
        notesMap_old.put(1, "5-5");
        notesMap_old.put(1, "5-6");
        notesMap_old.put(1, "5-7");
        notesMap_old.put(1, "5-8");
        notesMap_old.put(1, "5-9");
        notesMap_old.put(1, "5-10");
        notesMap_old.put(1, "5-11");
        notesMap_old.put(1, "5-12");
        notesMap_old.put(1, "5-13");
        notesMap_old.put(1, "5-14");
        notesMap_old.put(1, "5-15");
        notesMap_old.put(1, "5-16");
        notesMap_old.put(1, "5-17");
        notesMap_old.put(1, "5-18");
        notesMap_old.put(1, "5-19");
        notesMap_old.put(1, "5-20");
        notesMap_old.put(1, "5-21");
        notesMap_old.put(78, "6-0");
        notesMap_old.put(1, "6-2");
        notesMap_old.put(1, "6-3");
        notesMap_old.put(1, "6-4");
        notesMap_old.put(1, "6-5");
        notesMap_old.put(1, "6-6");
        notesMap_old.put(1, "6-7");
        notesMap_old.put(1, "6-8");
        notesMap_old.put(1, "6-9");
        notesMap_old.put(1, "6-10");
        notesMap_old.put(1, "6-11");
        notesMap_old.put(1, "6-12");
        notesMap_old.put(1, "6-13");
        notesMap_old.put(1, "6-14");
        notesMap_old.put(1, "6-15");
        notesMap_old.put(1, "6-16");
        notesMap_old.put(1, "6-17");
        notesMap_old.put(1, "6-18");
        notesMap_old.put(1, "6-19");
        notesMap_old.put(1, "6-20");
        notesMap_old.put(1, "6-21");


    }

    private void initializeMap() {

        notesMap.put(84, "6-0");
        notesMap.put(88, "6-1");
        notesMap.put(92, "6-2");
        notesMap.put(99, "6-3");
        notesMap.put(104, "6-4");

        notesMap.put(111, "5-0");
        notesMap.put(115, "5-1");
        notesMap.put(122, "5-2");
        notesMap.put(132, "5-3");
        notesMap.put(138, "5-4");

        notesMap.put(147, "4-0");
        notesMap.put(155, "4-1");
        notesMap.put(165, "4-2");
        notesMap.put(175, "4-3");
        notesMap.put(185, "4-4");

        notesMap.put(196, "3-0");
        notesMap.put(206, "3-1");
        notesMap.put(218, "3-2");
        notesMap.put(233, "3-3");

        notesMap.put(246, "2-0");
        notesMap.put(260, "2-1");
        notesMap.put(278, "2-2");
        notesMap.put(295, "2-3");
        notesMap.put(314, "2-4");

        notesMap.put(330, "1-0");
        notesMap.put(352, "1-1");
        notesMap.put(371, "1-2");
        notesMap.put(394, "1-3");
        notesMap.put(416, "1-4");
        notesMap.put(441, "1-5");
        notesMap.put(466, "1-6");
        notesMap.put(493, "1-7");
        notesMap.put(520, "1-8");
        notesMap.put(551, "1-9");
        notesMap.put(580, "1-10");
        notesMap.put(620, "1-11");
        notesMap.put(655, "1-12");
        notesMap.put(695, "1-13");
        notesMap.put(735, "1-14");
        notesMap.put(780, "1-15");
        notesMap.put(830, "1-16");
        notesMap.put(880, "1-17");
        notesMap.put(934, "1-18");
        notesMap.put(990, "1-19");
        notesMap.put(1048, "1-20");

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
        return (int) (441 * Math.pow(2, (((double) i) / 12)));
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

    void setStartAlertDialog() {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        layout.setOrientation(LinearLayout.VERTICAL);

        ViewPager pager = new ViewPager(context);
        pager.setLayoutParams(new LinearLayout.LayoutParams(-1, 90));
        pager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return object == view;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                Spinner spinner = null;

                if (position == 0) {
                    spinner = tab.getBpmSetter();
                    Log.i("instantItem", "[" + position + "] setted");
                }
                if (position == 1) {
                    spinner = filesControl.getSavedTabsChooser();
                    Log.i("instantItem", "[" + position + "] setted");
                }

                container.addView(spinner);
                return spinner;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                CharSequence sequence = "";
                if (position == 0) {
                    sequence = "Установите темп";
                }
                if (position == 1) {
                    sequence = "Выберете файл";
                }
                return sequence;
            }
        });

        SlidingTabLayout slidingTab = new SlidingTabLayout(context);
        slidingTab.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        slidingTab.setViewPager(pager);

        layout.addView(slidingTab);
        layout.addView(pager);


        new AlertDialog.Builder(context)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (dialog1, which1) -> dialog1.dismiss())
//                .setOnCancelListener((view) -> {
//                    tab.setBPM(120);
//                    Toast.makeText(context, getString(R.string.choosen_defaul_bpm),
//                            Toast.LENGTH_SHORT)
//                            .show();
//                    startRecord();
//                })
                .setView(layout)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    switch (pager.getCurrentItem()) {
                        case 0: { //Создание новой композиции
                            startRecord();
                            break;
                        }
                        case 1: { //Загрузка сохраненного таба
                            try {
                                tab.loadTabulature(filesControl.openTab(
                                                filesControl.getSavedTabsChooser()
                                                        .getSelectedItem()
                                                        .toString())
                                );
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }

                })
                .create()
                .show();
    }

    void setStopAlertDialog() {
        final EditText tabNameEdit = new EditText(context);
        new AlertDialog.Builder(context)
                .setTitle("Введите название шоу")
                .setCancelable(false)
                .setPositiveButton("Сохранить",
                        (dialog, which) -> {
                            //save
                            boolean result;
                            try {
                                result = filesControl.saveTab(tabNameEdit.getText().toString(), tab);
                            } catch (IOException e) {
                                Toast.makeText(context,
                                        getString(R.string.tab_saving_error),
                                        Toast.LENGTH_SHORT)
                                        .show();
                                e.printStackTrace();
                            }
                            stopRecordBtn.setEnabled(false);
                            dialog.dismiss();
                        })
                .setNegativeButton("Отмена",
                        (dialog, which) -> dialog.dismiss())
                .setView(tabNameEdit)
                .create()
                .show();

        tabNameEdit.setText("Новый таб 1");
    }

    public void onRecorsStartClick(View v) {

//        ((Texts)iview.getDrawable()).printFreq("345345");


        stopRecordBtn.setEnabled(true);

        if (isReading) {
            if (tab.isPaused()) {
                startRecord();
            } else {
                pauseRecord();
            }
        } else {
            setStartAlertDialog();
            startRecordBtn.setText(getString(R.string.pause_record));
        }


    }

    public void startRecord() {
        Log.e(TAG, "record start");
        audioRecord.startRecording();
        int recordingState = audioRecord.getRecordingState();
        Log.e(TAG, "recordingState = " + recordingState);
        readStart();
        tab.startRecord();

    }

    public void onRecordStopClick(View v) {
        stopRecord();
        setStopAlertDialog();
    }

    private void stopRecord() {
        readStop();
        Log.e(TAG, "record stop ");
        audioRecord.stop();
        tab.stopRecord();


    }

    private void pauseRecord() {
        readStop();
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
//        spec0 = new Complex[myBuffer.length];
//        spec1 = new Complex[myBuffer.length];
        Log.e(TAG, "read start ");
        isReading = true;

        new Thread(() -> {
            if (audioRecord == null)
                return;

            double l = 0, k = 0;
            int freq = 0;


            myBuffer = new short[myBufferSize];
            myBufferOld = new short[myBufferSize];
            window = new Window();
            int readCount = 0;
            int totalCount = 0;
            boolean first = true;

            first = false;
            readCount = audioRecord.read(myBuffer, 0, myBufferSize);
            totalCount += readCount;
            for (int i = 0; i < myBuffer.length; i++) {
                myBuffer[i] *= window.Gausse(myBuffer[i], myBufferSize);
//                myBuffer[i] *= window.Hamming(myBuffer[i], myBufferSize);
                //myBuffer[i] *= sensivityRatio;
            }

            frame0 = complex.realToComplex(myBuffer);
//            myBufferOld = myBuffer;
            this.spec0 = fftAnother.DecimationInTime(frame0, true);

            while (isReading) {

//                iview.setImageDrawable(texts);

                readCount = audioRecord.read(myBuffer, 0, myBufferSize);

                totalCount += readCount;
//                    Log.e(TAG, "readCount = " + readCount + ", totalCount = "
//                            + totalCount);

//                Log.i("Max buffer", String.valueOf(getMaxByffer(myBuffer)));

//                new Thread(() -> {
//                    for(int i=0;i<myBuffer.length/2;i++){
//                        myBuffer[i] *= window.Gausse(i, myBufferSize);
//                        myBuffer[i] *= sensivityRatio;
//                    }
//                }).start();
                for (int i = 0; i < myBuffer.length; i++) {
                    myBuffer[i] *= window.Gausse(i, myBufferSize);
//                    myBuffer[i] *= window.Hamming(myBuffer[i], myBufferSize);
                    myBuffer[i] *= sensivityRatio;
                }
//                   setAFC(myBuffer);
                //setAFC(getMaxByfferArray(myBuffer));
                //Complex[] spectrumComplex = fftAnother.DecimationInTime(complex.realToComplex(myBuffer), true);
                //short[] spectrum = complex.complexToShort(spectrumComplex);
//                    Complex[] spectrum = fft.fft(complex.realToComplex(myBuffer));
//                    final short[] afc = complex.complexToShort(spectrum);
//                    spectrum0.toArray(spec0);

                frame1 = complex.realToComplex(myBuffer);


//                kuli0 = fftKuliTurky.Calculate(myBuffer);

//                    spectrum1.toArray(spec1);
                spec1 = fftAnother.DecimationInTime(frame1, true);
//                spec0 = fftAnother.DecimationInTime(frame0, true);

//                kuli1 = fftKuliTurky.Calculate(myBufferOld);

//                myBufferOld = myBuffer;


//                for (int r = 0; r < myBuffer.length; r++) {
//                    spec0[r].abs /= myBuffer.length;
//                    spec1[r].abs /= myBuffer.length;
//                }
//                for (int r = 0; r < myBuffer.length; r++) {
//                    kuli1[r] /= myBuffer.length;
//                    kuli0[r] /= myBuffer.length;
//                }
                final LinkedHashMap<Integer, Integer> spectrumNew = Filters.GetJoinedSpectrum(spec0, spec1, ShiftsPerFrame, sampleRate);

                frame0 = frame1;

                new Thread(() -> {
                    this.spec0 = fftAnother.DecimationInTime(frame0, true);
                }).start();

// double[] spectrumNew = kuli0;
//                    setAFC((spectrumNew));  //Визуализация

//                    LinkedHashMap<Double, Boolean> map = new LinkedHashMap<>();


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
            }

            //magnitudeTransform(spectrum);
//
//                    Log.i("ssss = ", String.valueOf(getMax(spectrum)));

//            setAFC(afc);

//                    for (int k = 0;k < myBufferSize;k++){
//                            fftKuliTurky.Calculate(myBuffer);
//                            Log.i(TAG,Integer.toString() );
//                        }
        }).start();
//
//        for (int k = 0;k < myBufferSize;k++){
//            fftKuliTurky.Calculate(myBuffer);
//            Log.i(TAG,Integer.toString() );
//        }


    }

    public int getFrequence(double[] arr) {
        double f = 0;
        f = getMaxIndex(arr) * sampleRate / arr.length;
        return (int) Math.round(f);
    }

    public double getFrequence(short[] arr) {
        double f = 0;
        f = getMaxIndex(arr) * sampleRate / arr.length;
        return f;
    }

    public int getFrequence(LinkedHashMap<Integer, Integer> map) {
        double f = 0;
        f = getMaxIndex(map.values().toArray()) * sampleRate / map.size();
//        return (int) Math.round(f);
        return (int) f;
    }


    public short getMaxByffer(short[] buffer) {
        short maxAmplitude = 0;
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] > maxAmplitude) {
                maxAmplitude = myBuffer[i];
            }
        }
        return maxAmplitude;
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
//        Log.d("MAP", "00000000");

        for (int i : spectrum.keySet()) {

            updateAFC(i, (int) (0.05 * spectrum.get(i)));

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
            updateAFC(i, (short) ((spectrum[i])));
        }
//        setMaxAmplitudeColor();
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

    public double frequeceFromA(int noteNumber) {
        double frequence = frequenceA * (int) Math.pow(2.0, noteNumber / 12);
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

    public short[] magnitudeTransform(short spectrum[]) {
        int j = 0;
        short x = spectrum[0];
        maxAmplitudeColor = 0;
        for (int i = 0; i < spectrum.length / 2; i++) {
            // spectrum[i] *= window.Hamming(i, myBufferSize);
            //spectrum[i] *= window.Hamming(i, myBufferSize);
            if (spectrum[i] > spectrum[maxAmplitudeIndex]) {
                maxAmplitudeIndex = i;
            }
            if (spectrum[i] > x) {
                x = spectrum[i];
                j = i;
            }


        }

        return spectrum;
    }

    public double getMaxIndex(double[] arr) {
        double fr = 0;
        double spectrumMax = arr[0];
        for (short i = 0; i < arr.length; i++) {
            if (arr[i] > spectrumMax) {
                spectrumMax = arr[i];
                fr = i;
            }
        }
        return fr;
    }


    public double getMaxIndex(short[] arr) {
        double fr = 0;
        double spectrumMax = arr[0];
        for (short i = 0; i < arr.length; i++) {
            if (arr[i] > spectrumMax) {
                spectrumMax = arr[i];
                fr = i;
            }
        }
        return fr;
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


    public short[] getMaxByfferArray(short[] buffer) {
        short max = buffer[0];
        short[] maxBufferArray = new short[buffer.length];
        for (int i = 1; i < buffer.length; i++) {
            if (buffer[i] > max) {
                max = buffer[i];

            }
        }
        return maxBufferArray;
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

    void determineNotes_old(int frequence) {
        String s = notesMap_old.get(frequence);
        if (s != null) {
            note = s;
        } else {
            note = "";
            return;
        }
//        ((Texts)iview.getDrawable()).printNote(String.valueOf(s));

        runOnUiThread(() -> noteText.setText(note));


    }


//    void determineFreq(int freq) {
//
//
//        if (freq == 328.125) {
//            Log.e(TAG, "1st");
//            note = "1-0";
//        }
//        if (freq == 343.75) {
//            Log.e(TAG, "1-1");
//            note = "1-1";
//        }
//        if (freq == 359.375) {
//            Log.e(TAG, "1-2");
//            note = "1-2";
//        }
//        if (freq == 390.625) {
//            Log.e(TAG, "1-3");
//            note = "1-3";
//        }
//        if (freq == 406.25) {
//            Log.e(TAG, "1-4");
//            note = "1-4";
//        }
//        if (freq == 437.5) {
//            Log.e(TAG, "1-5");
//            note = "1-5";
//        }
//        if (freq == 453.125) {
//            Log.e(TAG, "1-6");
//            note = "1-6";
//        }
//        if (freq == 484.375) {
//            Log.e(TAG, "1-7");
//            note = "1-7";
//        }
//        if (freq == 515.625) {
//            Log.e(TAG, "1-8");
//            note = "1-8";
//        }
//        if (freq == 546.875) {
//            Log.e(TAG, "1-9");
//            note = "1-9";
//        }
//        if (freq == 578.125) {
//            Log.e(TAG, "1-10");
//            note = "1-10";
//        }
//        if (freq == 609.375) {
//            Log.e(TAG, "1-11");
//            note = "1-11";
//        }
//        if (freq == 656.25) {
//            Log.e(TAG, "1-12");
//            note = "1-12";
//        }
//        if (freq == 687.5) {
//            Log.e(TAG, "1-13");
//            note = "1-13";
//        }
//        if (freq == 734.375) {
//            Log.e(TAG, "1-14");
//            note = "1-14";
//        }
//        if (freq == 781.25) {
//            Log.e(TAG, "1-15");
//            note = "1-15";
//        }
//        if (freq == 828.125) {
//            Log.e(TAG, "1-16");
//            note = "1-16";
//        }
//        if (freq == 875) {
//            Log.e(TAG, "1-17");
//            note = "1-17";
//        }
//        if (freq == 921.875) {
//            Log.e(TAG, "1-18");
//            note = "1-18";
//        }
//        if (freq == 984.375) {
//            Log.e(TAG, "1-19");
//            note = "1-19";
//        }
//        if (freq == 1046.875) {
//            Log.e(TAG, "1-20");
//            note = "1-20";
//        }
////                        if (freq == ) {
////                            Log.e(TAG, "1-21");
////                            note = "1-21";
////                        }
//
//        /**********2nd********************/
//        if (freq == 250) {
//            Log.e(TAG, "2nd");
//            note = "2-0";
//        }
//
//        if (freq == 265.625) {
//            Log.e(TAG, "2-1");
//            note = "2-1";
//        }
//        if (freq == 281.25) {
//            Log.e(TAG, "2-2");
//            note = "2-2";
//        }
//        if (freq == 296.875) {
//            Log.e(TAG, "2-3");
//            note = "2-3";
//        }
//        if (freq == 312.5) {
//            Log.e(TAG, "2-4");
//            note = "2-4";
//        }
////                        if (freq == 328.125) {
////                            Log.e(TAG, "2-5");
////                            note = "2-5";
////                        }
////                        if (freq == 343.75) {
////                            Log.e(TAG, "2-6");
////                            note = "2-6";
////                        }
////                        if (freq == 375.0) {
////                            Log.e(TAG, "2-7");
////                            note = "2-7";
////                        }
////                        if (freq == 390.625) {
////                            Log.e(TAG, "2-8");
////                            note = "2-8";
////                        }
////                        if (freq == 406.25) {
////                            Log.e(TAG, "2-9");
////                            note = "2-9";
////                        }
////                        if (freq == 437.5) {
////                            Log.e(TAG, "2-10");
////                            note = "2-10";
////                        }
////                        if (freq == 468.75) {
////                            Log.e(TAG, "2-11");
////                            note = "2-11";
////                        }
////                        if (freq == 484.375) {
////                            Log.e(TAG, "2-12");
////                            note = "2-12";
////                        }
////                        if (freq == 515.625) {
////                            Log.e(TAG, "2-13");
////                            note = "2-13";
////                        }
////                        if (freq == 546.875) {
////                            Log.e(TAG, "2-14");
////                            note = "2-14";
////                        }
////                        if (freq == 578.125) {
////                            Log.e(TAG, "2-15");
////                            note = "2-15";
////                        }
////                        if (freq == 625.0) {
////                            Log.e(TAG, "2-16");
////                            note = "2-16";
////                        }
////                        if (freq == 656.25) {
////                            Log.e(TAG, "2-17");
////                            note = "2-17";
////                        }
////                        if (freq == 687.5) {
////                            Log.e(TAG, "2-18");
////                            note = "2-18";
////                        }
////                        if (freq == 718.75) {
////                            Log.e(TAG, "2-19");
////                            note = "2-19";
////                        }
////                        if (freq == 765.625) {
////                            Log.e(TAG, "2-20");
////                            note = "2-20";
////                        }
////                        if (freq == 812.5) {
////                            Log.e(TAG, "2-21");
////                            note = "2-21";
////                        }
//
//
//        if (freq == 187.5) {
//            Log.e(TAG, "3rd");
//            note = "3-0";
//        }
//        if (freq == 203.125) {
//            Log.e(TAG, "3-1");
//            note = "3-1";
//        }
//        if (freq == 218.75) {
//            Log.e(TAG, "3-2");
//            note = "3-2";
//        }
//        if (freq == 234.375) {
//            Log.e(TAG, "3-3");
//            note = "3-3";
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-4");
//            note = "3-4";
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-5");
//            note = "3-5";
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-6");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-7");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-8");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-9");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-10");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-11");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-12");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-13");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-14");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-15");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-16");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-17");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-18");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-19");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-20");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "3-21");
//        }
//
//        /*****************BASS**********************/
//        /******************4th*********************/
//        if (freq == 140.625) {
//            Log.e(TAG, "4th");
//            note = "4-0";
//        }
//        if (freq == 156.25) {
//            Log.e(TAG, "4-1");
//            note = "4-1";
//        }
//        if (freq == 171.875) {
//            Log.e(TAG, "4-2");
//            note = "4-2";
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-3");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-4");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-5");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-6");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-7");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-8");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-9");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-10");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-11");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-12");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-13");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-14");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-15");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-16");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-17");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-18");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-19");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-20");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "4-21");
//        }
//
//        /*********************5th*******************/
//        if (freq == 109.375) {
//            Log.e(TAG, "5-0");
//            note = "5-0";
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-1");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-2");
//        }
//        if (freq == 125) {
//            Log.e(TAG, "5-3");
//            note = "5-3";
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-4");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-5");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-6");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-7");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-8");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-9");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-10");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-11");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-12");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-13");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-14");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-15");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-16");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-17");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-18");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-19");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-20");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "5-21");
//        }
//        /******************6th********************/
//        if (freq == 78.125) {
//            Log.e(TAG, "6-0");
//            note = "6-0";
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-2");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-3");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-4");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-5");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-6");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-7");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-8");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-9");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-10");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-11");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-12");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-13");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-14");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-15");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-16");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-17");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-18");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-19");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-20");
//        }
//        if (freq == 1) {
//            Log.e(TAG, "6-21");
//        }
//
//        runOnUiThread(() -> noteText.setText(note));
//
//    }


}
