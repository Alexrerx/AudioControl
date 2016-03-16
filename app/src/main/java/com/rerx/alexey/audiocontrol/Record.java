package com.rerx.alexey.audiocontrol;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.view.View;

import java.io.File;
import java.net.ContentHandler;
import java.util.ArrayList;

/**
 * Created by alexey on 06.03.16.
 */
public class Record {

    boolean pauseRecord = false;
    public boolean isPlaying = false;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String tmpFileName = "";
    public static final String FILE_EXTENSION = ".3gpp";
    public static final String FILE_FOLDER = FilesControl.FILE_FOLDER;
    public static final String PATH = android.os.Environment
            .getExternalStorageDirectory()
            + java.io.File.separator
            + FILE_FOLDER + java.io.File.separator;

    ArrayList<String> files = new ArrayList<>();

    MainActivity mainActivity;
    Context context;

//    Record(Context context) {
//        this.context = context;
//        mainActivity = (MainActivity) context;
//    }


    @NonNull
    private String generateName() {
        return String.valueOf(System.currentTimeMillis());
    }

    public String getTmpFileName() {
        files.add(PATH+generateName()+FILE_EXTENSION);
        return files.get(files.size() - 1);
    }

    public void recordStart() {
        try {
            releaseRecorder();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(getTmpFileName());
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void recordPause(){
        recordStop();
        prepareToPlaying();
    }

    public void recordStop() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
        }
    }

    public void playStart() {
        try {
            isPlaying = true;
            releasePlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(files.get(0));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void playStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
        }
    }

    public void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    public void finish(String finishFileName) {
        playStop();
        recordStop();
        parseRecords(finishFileName);
    }

    public void prepareToPlaying(){
        if (files.size() > 1) {
            for (int i=1;i<files.size();i++) {
                Mp4ParserWrapper.append(files.get(0), files.get(0));
                files.remove(i);
            }
        }
    }

    private void parseRecords(String finishFileName) {

        if (files.size() > 1) {
            for (String fileName:files) {
                Mp4ParserWrapper.append(finishFileName,fileName);
            }
        }
    }

    public ArrayList<String> getTmpFiles(){
        return files;
    }
}
