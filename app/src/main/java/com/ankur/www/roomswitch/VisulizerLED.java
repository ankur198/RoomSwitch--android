package com.ankur.www.roomswitch;

import android.media.audiofx.Visualizer;

/**
 * Created by ankur on 07/06/2017.
 */

public class VisulizerLED {
    Visualizer visualizer = new Visualizer(1);
    byte[] sample = new byte[8];
    void start(){
        visualizer.getFft(sample);
        visualizer.setEnabled(true);
    }
}
