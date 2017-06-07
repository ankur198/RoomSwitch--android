package com.ankur.www.roomswitch;

import android.media.audiofx.Visualizer;

/**
 * Created by ankur on 07/06/2017.
 */

public class VisulizerLED implements Visualizer.OnDataCaptureListener {
    Visualizer visualizer = new Visualizer(1);
    public byte[] sample = new byte[8];
    void start(){
        visualizer.setDataCaptureListener(this,Visualizer.getMaxCaptureRate(),false,true);
        visualizer.getFft(sample);
        visualizer.setEnabled(true);
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
       byte base = fft[0];
    }
}
