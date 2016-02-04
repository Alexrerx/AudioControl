package com.rerx.alexey.audiocontrol;

import android.app.ActionBar;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by mihail on 03.02.16.
 */
public class Tablature {

    HorizontalScrollView tabScroll;

    Context context;
    LinearLayout tabLayout;
    MainActivity mainActivity;
    int barTime;
    int noteTime;
    boolean works;
    private int startTime, barCount = 0, barIndex = 1;
    private double eps;
    private Thread barThread;


    ArrayList<Note> temp = new ArrayList<>();

    String strings[] = {"E", "B", "G", "D", "A", "E"};


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


        barThread = new Thread(new BarThread());
        initializeTab();
    }

    private void initializeTab() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < 6; i++) {
            TextView txt = new TextView(context);
            txt.setLayoutParams(new LinearLayout.LayoutParams(
                    30,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1));
            txt.setText(strings[i]);
            layout.addView(txt);
        }
        tabLayout.addView(layout);
    }

    public void startRecord() {
        try {
            barThread.start();
        } catch (Exception e) {
            works = true;
        }
    }

    public void stopRecord() {
        works = false;
    }

    public void addNote(int stringNumber, int fret) {
        tabLayout.addView(new Bar(context).setNote(new Note(stringNumber, fret)));
    }

    public void addNote(String note) {
        noteTime = (int) (System.currentTimeMillis() - startTime);
        if (!note.equals("")) {
            if (barTime - noteTime > eps) {
                tabLayout.addView(new Bar(context).setNote(new Note(Integer.valueOf(note.substring(0, 1))
                                , Integer.valueOf(note.substring(2))))
                );
            } else {
                temp.add(new Note(Integer.valueOf(note.substring(0, 1))
                        , Integer.valueOf(note.substring(2))));
            }
        }
    }


    public void setTempo(int bpm) {
        barTime = (int) Math.round(60000000.0 / bpm);
        eps = barTime / 16;
    }

    public int getTempo() {
        return barTime;
    }

    void addBeat(Note note) {
        mainActivity.runOnUiThread(() -> {
            tabLayout.addView(new Bar(context).setNote(note));
        });

    }

    void addBar() {
        barIndex = barCount++;
        mainActivity.runOnUiThread(() -> {
            tabLayout.addView(new Bar(context));
            tabLayout.addView(new Bar(context, R.string.new_bar).setBarNumber(barCount));
        });
    }

    public Spinner getBpmSetter() {
        Spinner spinner = new Spinner(context);
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 60; i < 300; i++) {
            list.add(i);
        }

        spinner.setAdapter(new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, list));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setTempo(position + 60);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setTempo(120);
                Toast.makeText(context, mainActivity.getString(R.string.choosen_defaul_bpm),
                        Toast.LENGTH_SHORT).show();
            }
        });
        return spinner;
    }

    public int getBarCount() {
        return barCount;
    }

    class BarThread implements Runnable {

        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            works = true;
            while (works) {
                startTime = (int) System.currentTimeMillis();
                for (int i = 0; i < temp.size(); i++) {
                    addBeat(temp.get(i));
                }

                addBar();


                try {
                    Thread.sleep(barTime / 1000, barTime % 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private class Bar extends LinearLayout {

        private Note note;

        public Bar(Context context) {
            super(context);
            this.setOrientation(LinearLayout.VERTICAL);

            for (int i = 0; i < 6; i++) {
                TextView txt = new TextView(context);
                txt.setLayoutParams(new LinearLayout.LayoutParams(
                        30,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1));
                txt.setText(context.getString(R.string.fret_default));
                this.addView(txt);
            }
        }

        public Bar(Context context, int resId) {
            super(context);
            this.setOrientation(LinearLayout.VERTICAL);

            for (int i = 0; i < 7; i++) {
                TextView txt = new TextView(context);
                txt.setLayoutParams(new LinearLayout.LayoutParams(
                        20,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1));
                txt.setText(context.getString(resId));
                this.addView(txt);
            }

        }

        public Note getNote() {
            return note;
        }

        public LinearLayout setNote(Note note) {
            this.note = note;
            ((TextView) this.getChildAt(note.getString() - 1)).setText(String.valueOf(note.getFret()));
            return this;
        }

        public LinearLayout setBarNumber(int barNumber) {
            ((TextView) this.getChildAt(6)).setText(String.valueOf(barNumber));
            return this;
        }

    }


    private class Note {
        private int string; //Струна
        private int fret; //Лад

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
    }

}