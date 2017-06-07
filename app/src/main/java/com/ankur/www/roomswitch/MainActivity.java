package com.ankur.www.roomswitch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    Switch fanSwitch;
    Switch tubelightSwitch;
    Switch bulbSwitch;
    SeekBar RedSeek;
    SeekBar BlueSeek;
    SeekBar GreenSeek;
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
        VisulizerLED visulizerLED = new VisulizerLED();
        visulizerLED.start();
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
            messageToClient("Fan,1");
        }
    };

    Runnable FanOff = new Runnable() {
        @Override
        public void run() {
            messageToClient("Fan,0");
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
