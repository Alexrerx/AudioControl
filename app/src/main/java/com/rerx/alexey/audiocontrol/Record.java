package com.rerx.alexey.audiocontrol;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.view.View;

import java.io.File;
/**
     * Created by alexey on 06.03.16.
     */
    public class Record {

        boolean pauseRecord = false;
        private MediaRecorder mediaRecorder;
        private MediaPlayer mediaPlayer;
        private String fileName;
        public static final String FILE_FOLDER = "records";
        public static final String PATH = android.os.Environment
                .getExternalStorageDirectory()
                + java.io.File.separator
                + FILE_FOLDER + java.io.File.separator;

        public void recordStart(View v) {
            try {
                releaseRecorder();

                File outFile = new File(fileName);
                if (outFile.exists()) {
                    outFile.delete();
                }
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setOutputFile(fileName);
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void recordStop(View v) {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
            }
        }

        public void playStart(View v) {
            if (pauseRecord){
                mediaPlayer.start();
            }else {
                try {
                    releasePlayer();
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(fileName);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void playStop(View v) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
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
        public void Pause(View v){

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                pauseRecord = true;
            }
        }
}
