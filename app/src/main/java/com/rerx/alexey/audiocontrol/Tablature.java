package com.rerx.alexey.audiocontrol;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by mihail on 03.02.16.
 */
public class Tablature {

    Context context;
    LinearLayout tabLayout;
    MainActivity mainActivity;

    String strings[] = {"E", "B", "G", "D", "A", "E"};


    Tablature(Context context) {
        this.context = context;
        mainActivity = (MainActivity) context;
        tabLayout = (LinearLayout) mainActivity.findViewById(R.id.tabLayout);
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

    public void addNote(int stringNumber, int fret) {
        tabLayout.addView(new NoteLayout(context, new Note(stringNumber, fret)));
    }

    public void addNote(String note) {
        if (!note.equals("")) {
            tabLayout.addView(new NoteLayout(context,
                            new Note(Integer.valueOf(note.substring(0, 1))
                                    , Integer.valueOf(note.substring(2))))
            );
        }
    }

    private class NoteLayout extends LinearLayout {

        private Note note;

        public NoteLayout(Context context, Note note) {
            super(context);
            this.note = note;
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
            ((TextView) this.getChildAt(note.getString() - 1)).setText(String.valueOf(note.getFret()));
        }

        public Note getNote() {
            return note;
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