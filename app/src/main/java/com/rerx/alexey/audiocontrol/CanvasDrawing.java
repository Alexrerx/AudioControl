package com.rerx.alexey.audiocontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.TextView;

/**
 * Класс рисования текста частоты и ноты
 * Created by mihail on 10.02.16.
 */
public class CanvasDrawing extends Drawable {
    Canvas canvas;
    Context context;
    MainActivity mainActivity;

    private int FREQ_POSITION_X;
    private int FREQ_POSITION_Y;
    private int NOTE_POSITION_X;
    private int NOTE_POSITION_Y;
    private int note_color;
    private int freq_color;
    private Paint paint = new Paint();
    private String freq = "0";
    private String note = "0";
    private boolean works = false;

    CanvasDrawing(Context context) {
        this.context = context;
        mainActivity = (MainActivity) context;
    }

    @Override
    public void draw(Canvas canvas) {
        this.canvas = canvas;
        setPositions();
        Log.d("fragment", "" + this.canvas + " " + note_color + " " + freq_color);
        paint.setColor(note_color);
        canvas.drawLine(2, 2, 50, 50, paint);
//        printFreq("345");
        setColors();
//        Log.d("canvas",""+canvas);
        while (mainActivity.isReading) {
            printFreq(freq);
            printNote(note);
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int[] getState() {


        return super.getState();
    }


    @Override
    public int getOpacity() {
        return 0;
    }

    private void setColors() {
        note_color = context.getResources().getColor(R.color.determined_note);
        freq_color = context.getResources().getColor(R.color.black);
    }

    private void setPositions() {
        FREQ_POSITION_X = canvas.getWidth() / 6;
        FREQ_POSITION_Y = canvas.getHeight() / 2;
        NOTE_POSITION_X = canvas.getWidth() * 4 / 6;
        NOTE_POSITION_Y = canvas.getHeight() / 2;
    }

    public void printNote(String text) {
//        Log.d("print",""+canvas);
        canvas.drawLine(0, 0, canvas.getHeight(), canvas.getWidth(), paint);
        paint.setColor(note_color);
        canvas.drawText(text, NOTE_POSITION_X, NOTE_POSITION_Y, paint);
    }

    public void printFreq(String text) {
//        Log.d("print",""+canvas);
        canvas.drawLine(0, 0, canvas.getHeight(), canvas.getWidth(), paint);
        paint.setColor(freq_color);
        canvas.drawText(text, FREQ_POSITION_X, FREQ_POSITION_Y, paint);
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public void setNote(String note) {
        this.note = note;
    }


//    class DrawingTread extends Thread {
//
//
//        @Override
//        public void run() {
//            super.run();
//
//            SurfaceHolder surfaceHolder = getHolder();
//            while(true){
//
//                if(works){
//                    Canvas canvas;
//                    if (surfaceHolder.getSurface().isValid()) {
//
//                        canvas = surfaceHolder.lockCanvas();
//                        if (canvas != null) {
//                            try {
//                                draw(canvas);
//                            } finally {
//                                surfaceHolder.unlockCanvasAndPost(canvas);
//                            }
//                        }
//                        try {
//                            sleep(200);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//
//                        }
//                    }
//                }else {
//                    try {
//                        sleep(30);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//// else {
////                try {
////                    sleep(50);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                    return;
////                }
////            }
//            }
//        }
//        }

}


