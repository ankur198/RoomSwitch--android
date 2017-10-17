package com.ankur.www.roomswitch;

import android.app.Activity;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Visualizer.OnDataCaptureListener{

    Switch fanSwitch;
    Switch tubelightSwitch;
    Switch bulbSwitch;
    Switch musicMode;
    SeekBar RedSeek;
    SeekBar BlueSeek;
    SeekBar GreenSeek;
    public static Visualizer visualizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_try);
        /*((Button)findViewById(R.id.FanOnBtn)).setOnClickListener(this);
        ((Button)findViewById(R.id.FanOffBtn)).setOnClickListener(this);
        ((Button)findViewById(R.id.TubelightOnBtn)).setOnClickListener(this);
        ((Button)findViewById(R.id.TubelightOffBtn)).setOnClickListener(this);
        ((Button)findViewById(R.id.BulbOnBtn)).setOnClickListener(this);
        ((Button)findViewById(R.id.BulbOffBtn)).setOnClickListener(this);*/
        fanSwitch = (Switch)findViewById(R.id.FanSwitch);
        tubelightSwitch = (Switch)findViewById(R.id.TubelightSwitch);
        bulbSwitch = (Switch)findViewById(R.id.BulbSwitch);
        musicMode = (Switch)findViewById(R.id.MusicMode);
        initialiseSwitch();
        ((Button)findViewById(R.id.SimpleTransition)).setOnClickListener(this);
        ((Button)findViewById(R.id.switchOffLED)).setOnClickListener(this);
        ((Button)findViewById(R.id.NightMode)).setOnClickListener(this);
        RedSeek =(SeekBar)findViewById(R.id.RedSeekBar);
        BlueSeek = (SeekBar)findViewById(R.id.BlueSeekBar);
        GreenSeek = (SeekBar)findViewById(R.id.GreenSeekBar);
        RedSeek.setOnSeekBarChangeListener(this);
        GreenSeek.setOnSeekBarChangeListener(this);
        BlueSeek.setOnSeekBarChangeListener(this);
        //visulizerLEDStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //visulizerLEDStart();
    }


    void  visualizerLEDStop(){
        if (visualizer!=null){
            visualizer.release();
            visualizer = null;
        }
    }

    void visulizerLEDStart() {
        if (visualizer==null) {
            visualizer = new Visualizer(0);
            visualizer.setCaptureSize(8);//4
            visualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), false, true);
            //visualizer.getFft(sample);
            visualizer.setEnabled(true);
        }
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        try {
            //visualizer.getFft(fft);
            int x = fft[4];  //3 when capture size = 4
            int y = fft[6];  //2
            int z = fft[2];  //0
            if (x <=0) x *=-1;
            if (y <=0) y *=-1;
            if (z <=0) z *=-1;

            //calibration
            //x = Math.round((((float)x)/256)*50);  //r
            //y = Math.round((((float)y)/2));  //g
            //z = Math.round((((float)x)/256)*100); //b

            //noise removal
            int noise = 5;
            if (x<noise)
            {
                x=0;
            }
            if (y<noise)
            {
                y=0;
            }
            if (z<noise)
            {
                z=0;
            }


            final String a = Integer.toString(x);
            final String b = Integer.toString(y);
            final String c = Integer.toString(z);

            //a = Integer.toString(x) + "\t" + Integer.toString(y) + "\t" + Integer.toString(z);

            ((TextView) findViewById(R.id.VisualizationX)).setText(Integer.toString(x));
            ((TextView) findViewById(R.id.VisualizationY)).setText(Integer.toString(y));
            ((TextView) findViewById(R.id.VisualizationZ)).setText(Integer.toString(z));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    messageToClient("6,"+a);
                    messageToClient("10,"+b);
                    messageToClient("11,"+c);
                }
            }).start();
        }
        catch (Exception e){
            ((TextView) findViewById(R.id.VisualizationX)).setText(e.getMessage());
        }
    }


    void initialiseSwitch(){
        fanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true)
                {
                    new Thread(FanOn).start();
                }
                else new Thread(FanOff).start();
            }
        });

        tubelightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) new Thread(TubelightOn).start();
                else new Thread(TubelightOff).start();
            }
        });

        bulbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) new Thread(BulbOn).start();
                else new Thread(BulbOff).start();
            }
        });
        musicMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true) visulizerLEDStart();
                else visualizerLEDStop();
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar==RedSeek){
            final String m = "6"+","+Integer.toString(progress);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    messageToClient(m);
                }
            };
            new Thread(r).start();
        }
        if (seekBar==GreenSeek){
            final String m = "10"+","+Integer.toString(progress);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    messageToClient(m);
                }
            };
            new Thread(r).start();
        }
        if (seekBar==BlueSeek){
            final String m = "11"+","+Integer.toString(progress);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    messageToClient(m);
                }
            };
            new Thread(r).start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    public void onClick(View v){
        switch (v.getId()) {
            case R.id.SimpleTransition:
                new Thread(SimpleTransition).start();
                break;
            case R.id.switchOffLED:
                new Thread(SwitchOffLED).start();
                break;
            case R.id.NightMode:
                new Thread(NightMode).start();
        }
    }

    void messageToClient(String message){
        try {
            String ip = ((EditText)findViewById(R.id.IP)).getText().toString();
            Socket client = new Socket(ip,1337);
            OutputStream outputStream = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outputStream);
            out.writeBytes(message+"\n");
            client.close();
        }
        catch (IOException e){}
    }

    Runnable FanOn = new Runnable() {
        @Override
        public void run() {
            messageToClient("Fan,0");
        }
    };

    Runnable FanOff = new Runnable() {
        @Override
        public void run() {
            messageToClient("Fan,1");
        }
    };

    Runnable TubelightOn = new Runnable() {
        @Override
        public void run() {
            messageToClient("Tubelight,1");
        }
    };

    Runnable TubelightOff = new Runnable() {
        @Override
        public void run() {
            messageToClient("Tubelight,0");
        }
    };

    Runnable BulbOn = new Runnable() {
        @Override
        public void run() {
            messageToClient("Bulb,1");
        }
    };

    Runnable BulbOff = new Runnable() {
        @Override
        public void run() {
            messageToClient("Bulb,0");
        }
    };

    Runnable SimpleTransition = new Runnable() {
        @Override
        public void run() {
            messageToClient("SimpleTransition");
        }
    };

    Runnable NightMode = new Runnable() {
        @Override
        public void run() {
            messageToClient("NightMode");
        }
    };

    Runnable SwitchOffLED = new Runnable() {
        @Override
        public void run() {
            messageToClient("6,0");
            messageToClient("10,0");
            messageToClient("11,0");
        }
    };
}
