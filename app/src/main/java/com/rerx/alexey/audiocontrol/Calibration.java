package com.rerx.alexey.audiocontrol;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by mihail on 20.02.16.
 */
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
                                "СДЕЛАЙ КАЛИБРОВКУУУ!!! НЕ РАБОТАЕЕЕЕТ!!",
                                Toast.LENGTH_SHORT)
                                .show()
        );

    }
}
