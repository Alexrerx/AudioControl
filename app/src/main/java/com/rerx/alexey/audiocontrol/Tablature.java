package com.rerx.alexey.audiocontrol;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * Табулатура: создание и именение
 *
 */
public class Tablature {

    private HorizontalScrollView tabScroll;
    private Spinner bpmSetter;

    private Context context;
    private LinearLayout tabLayout;
    private MainActivity mainActivity;
    private int noteTime;
    private boolean works, pause;
    private int startTime, barCount = 0, barIndex = 0, bpm = 120;
    private int barTime = (int) Math.round(60000000.0 / (double) bpm);
    private double eps;
    private Thread barThread;


    private ArrayList<Note> temp = new ArrayList<>();
    private ArrayList<ArrayList<String>> stringsList = new ArrayList<>();

    private String strings[] = {"E", "B", "G", "D", "A", "E", ""};
    private LinkedHashMap<Integer, Integer> currentPitch = new LinkedHashMap<>();


    Tablature() {  //empty constructor
    }

    Tablature(Context context) {
        this.context = context;
        mainActivity = (MainActivity) context;
        tabLayout = (LinearLayout) mainActivity.findViewById(R.id.tabLayout);
        tabScroll = (HorizontalScrollView) mainActivity.findViewById(R.id.tabScroll);
        tabLayout.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                tabScroll.fullScroll(View.FOCUS_RIGHT);
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });


        initializeVars();
        initializeTab();
        initializeList();
        setDefaultPitch();
    }

    public void clearTab() {
        tabLayout.removeAllViews();
        stringsList = new ArrayList<>();
        initializeList();
        initializeTab();
        initializeVars();

    }

    private void initializeVars() {
        barThread = new Thread(new BarThread());
        barCount = 0;
        barIndex = 0;
        bpm = 120;
    }

    private void initializeList() {
        for (int i = 0; i < 7; i++) {
            stringsList.add(new ArrayList<>());
            stringsList.get(i).add(strings[i]);
        }
    }

    private void initializeTab() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < 6; i++) {
            TextView txt = new TextView(context);
            txt.setLayoutParams(new LinearLayout.LayoutParams(
                    mainActivity.countSize(30),
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1));
            txt.setText(strings[i]);
            txt.setTextSize(16);
            layout.addView(txt);
        }
        tabLayout.addView(layout);
    }

    public boolean isPaused() {
        return pause;
    }

    private void setDefaultPitch() {
        // TODO: 06.02.16 Продумать тему с разными строями
        // TODO: 22.02.16 не придумывать тему... решили забить...
        for (int i = 1; i < 7; i++) {
            currentPitch.put(i, 5);
        }
        currentPitch.put(3, 4);
        currentPitch.put(1, 16);

    }

    public void startRecord() {
        try {
            pause = false;
            works = true;
            barThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param pause true, чтобы приостановить запись таба
     *              с возможностью продолжения, false,
     *              для подготовки таба к сохранению.
     */
    private void stopRecord(boolean pause) {
        this.pause = pause;
        if (!pause) {
            works = false;
        }
        works = pause;
    }

    public void stopRecord() {
        stopRecord(false);
    }

    public void pauseRecord() {
        stopRecord(true);
    }

    public void addNote(int string, int fret) {
        noteTime = (int) (System.currentTimeMillis() - startTime);
        if (barTime - noteTime > eps) {
            addNoteNoTime(string, fret);
        } else {
            temp.add(new Note(string, fret));
        }
    }

    public void addNoteNoTime(int string, int fret) {
        mainActivity.runOnUiThread(() -> tabLayout.addView(new Bar(context).setNote(new Note(string, fret))));
    }

    public void addNote(String note) {
        if (note.length() > 2) {
            addNote(Integer.valueOf(note.substring(0, 1)),
                    Integer.valueOf(note.substring(2)));
        }
    }

    public void setBPM(int bpm) {
        this.bpm = bpm;
        barTime = (int) Math.round(60000000.0 / bpm);
        eps = barTime / 16;
    }

    private void addBeat(Note note) {
        mainActivity.runOnUiThread(() -> {
            tabLayout.addView(new Bar(context).setNote(note));
        });
    }

    public void addBar() {
        barIndex = barCount++;
        mainActivity.runOnUiThread(() -> {
            tabLayout.addView(new Bar(context));
//            if(isBarNumberActive) // TODO: 27.02.16 сделать номер бара без косяков
//            tabLayout.addView(new Bar(context, R.string.new_bar).setBarNumber(barCount));
            tabLayout.addView(new Bar(context, R.string.new_bar));
        });
    }

    public Spinner getBpmSetter() {
        if (bpmSetter != null) {
            return bpmSetter;
        } else {
            bpmSetter = new Spinner(context);
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 60; i < 300; i++) {
                list.add(i);
            }

            bpmSetter.setAdapter(new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, list));

            bpmSetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setBPM(position + 60);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    setBPM(120);
                    Toast.makeText(context, mainActivity.getString(R.string.choosen_defaul_bpm),
                            Toast.LENGTH_SHORT).show();
                }
            });
            bpmSetter.setSelection(60); // 120bpm по-умолчанию
            return bpmSetter;
        }
    }


    /**
     * Возвращает количество тактов
     */
    public int getBarCount() {
        return barCount;
    }

    public void loadTabulature(Tablature tab) {
        barCount = 0;
        barIndex = 0;
        this.bpm = tab.getBPM();
        this.stringsList = tab.getStringsList();
    }

    public int getBPM() {
        return bpm;
    }

    public ArrayList<ArrayList<String>> getStringsList() {
        return stringsList;
    }

    public void setStringsList(ArrayList<ArrayList<String>> stringsList) {
        this.stringsList = stringsList;
    }

    private class BarThread implements Runnable {

        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            works = true;
            while (works) {
                if (pause) {
                    while (pause) {
                        try {
                            Thread.sleep(100);
//                                Log.i("BarThread","Sleeping");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    startTime = (int) System.currentTimeMillis();
                    for (int i = 0; i < temp.size(); i++) {
                        addBeat(temp.get(i));
                    }

                    addBar();

                    try {
                        Thread.sleep(barTime / 1000/*, barTime % 1000*/);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private class Bar extends LinearLayout {

        private Note note;
        private int
//                changingNoteColor = mainActivity.getResources()
//                .getColor(R.color.changing_note),
//                alternativeNoteColor = mainActivity.getResources()
//                        .getColor(R.color.alternative_note),
                emptyNoteColor = mainActivity.getResources()
                        .getColor(R.color.empty_note);
        private String emptyNote = mainActivity.getString(R.string.empty_note);


        public Bar(Context context) {
            super(context);
            this.setOrientation(LinearLayout.VERTICAL);
            setLayout(6, emptyNote, 30);
        }

        public Bar(Context context, int resId) {
            super(context);
            this.setOrientation(LinearLayout.VERTICAL);
//            if(isBarNumberActive)
//            setLayout(7, mainActivity.getString(resId), 20);
            setLayout(6, mainActivity.getString(resId), 20);
        }

        private LinearLayout setLayout(int maxIndex, String text, int width) {
            for (int i = 0; i < maxIndex; i++) {
                TextView txt = new TextView(context);
                txt.setLayoutParams(new LinearLayout.LayoutParams(
                        mainActivity.countSize(width),
                        mainActivity.countSize(35),
                        1));
                txt.setTextSize(14);
                txt.setGravity(TEXT_ALIGNMENT_CENTER);
                txt.setText(text);
                txt.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                this.addView(txt);
                stringsList.get(i).add(text);
            }
            return this;
        }

        public Note getNote() {
            return note;
        }

        public LinearLayout setNote(Note note) {
            this.note = note;
            Log.d("Tab", note.toString());
            TextView textView = ((TextView) this.getChildAt(note.getString() - 1));
            textView.setText(String.valueOf(note.getFret()));
            if (note.AlternativeNotesExist()) {
//                textView.setBackgroundColor(changingNoteColor);
                textView.setBackgroundResource(R.mipmap.note_determ);
                textView.setOnClickListener(v -> {
                    if (v.getTag(this.getId()) == "changing") {
                        removeAlternativeNotes(note.getString());
//                        v.setBackgroundColor(emptyNoteColor);
                        v.setBackgroundResource(R.mipmap.note);
                        v.setTag(this.getId(), "");
                    } else {
                        setAlternativeNotes(note.getAlternativeNotes(note.getString(), note.getFret()), note.getBasicNote().getString());
                        v.setTag(this.getId(), "changing");
//                        v.setBackgroundColor(changingNoteColor);
                        v.setBackgroundResource(R.mipmap.note_determ);
                    }
                });
            }


            for (int string = 0; string < 6; string++) {
                stringsList.get(string).add("--");
            }
            int fret = note.getFret();
            int index = stringsList.get(note.getString() - 1).size() - 1;
            stringsList.get(note.getString() - 1).set(index, fret < 10 ? String.valueOf(fret) + " " : String.valueOf(fret));


            return this;
        }

        void setAlternativeNotes(HashMap<Integer, Integer> map, int startSting) {
            for (int i = startSting; i < 7; i++) {
                if (i != note.getString() && map.get(i) != null) {
                    TextView text = ((TextView) this.getChildAt(i - 1));
                    text.setText(String.valueOf(map.get(i)));
                    text.setTag(text.getId(), new Note(i, map.get(i)));
//                    text.setBackgroundColor(alternativeNoteColor);
                    text.setBackgroundResource(R.mipmap.note_new);
                    text.setOnClickListener(v -> {
                        this.removeAllViews();
                        ((Bar) this.setLayout(6, emptyNote, 30)).setNote((Note) v.getTag(v.getId()));
                        v.setBackgroundResource(R.mipmap.note);
                    });
                }
            }
        }

        void removeAlternativeNotes(int startSting) {
            for (int i = 0; i < 6; i++) {
                if (i != (startSting - 1)) {
                TextView text = ((TextView) this.getChildAt(i));
                text.setText(emptyNote);
                text.setBackgroundColor(emptyNoteColor);
                text.setOnClickListener(null);
                }
            }
        }


        public LinearLayout setBarNumber(int barNumber) {
            ((TextView) this.getChildAt(6)).setText(String.valueOf(barNumber));
            ((TextView) this.getChildAt(6)).setTextAlignment(TEXT_ALIGNMENT_CENTER);
            ((TextView) this.getChildAt(6)).setRotation(-50);
            ((TextView) this.getChildAt(6)).setLines(1);
            stringsList.get(6).add(String.valueOf(barNumber));
            return this;
        }
    }


    public class Note {
        private int string; //Струна, с 1 по 6
        private int fret; //Лад, с 0 по 20

        Note(int string, int fret) {
            this.string = string;
            this.fret = fret;
        }

        public int getString() {
            return string;
        }

        public int getFret() {
            return fret;
        }

        @Override
        public String toString() {
            return String.valueOf(string) + "-" + String.valueOf(fret);
        }

        public HashMap<Integer, Integer> getAlternativeNotes(int string, int fret) {
            HashMap<Integer, Integer> map = new HashMap<>();

            if (fret < currentPitch.get(string)) {
                int newfret = fret;
                map.put(string, newfret);
                for (int i = string + 1; i < 7; i++) {
                    newfret += currentPitch.get(i);
                    if (newfret <= 20) {
                        map.put(i, newfret);
                    }
                }
            } else {
                Note note = getBasicNote();
                map = getAlternativeNotes(note.getString(), note.getFret());
            }
            return map;
        }

        private Note getBasicNote() {
            int newstring = this.getString(), newfret = this.getFret();

            while (newfret >= currentPitch.get(newstring)) {
                newfret -= currentPitch.get(newstring--);
            }
            Note note = new Note(newstring, newfret);
            Log.d("BasicNote", note.toString() + " of " + this.toString());
            return new Note(newstring, newfret);
        }

        public boolean AlternativeNotesExist() {
            return !((string == 1) && (fret > 15) || (string == 6) && (fret < 5));
        }

    }
}