package com.rerx.alexey.audiocontrol;

import android.content.Context;
import android.widget.Toast;

/**
 *
 * Калибровка перед использованием программы
 * */
public class Calibration {
    // TODO: 20.02.16 сделать калибровку

    MainActivity mainActivity;
    Context context;

    Calibration(Context context) {
        this.context = context;
        mainActivity = (MainActivity) context;
    }

    void start() {
        mainActivity.runOnUiThread(() ->
                        Toast.makeText(
                                context,
                                "Пожалуйста,сыграйте 1ую струну,зажатую на 5м ладу",
                                Toast.LENGTH_SHORT)
                                .show()



        );

    }
}
