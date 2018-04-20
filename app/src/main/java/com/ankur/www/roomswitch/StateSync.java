package com.ankur.www.roomswitch;

import android.app.Activity;
import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by ankur on 20-04-2018.
 */

public  class StateSync extends Thread {
    private Boolean T = false;
    private Boolean B = false;
    private Boolean F = false;
    private  void getStates()
    {
        try {
            String ip = MainActivity.ip;
            Socket client = new Socket(ip,1337);
            //InputStream inputStream = client.getInputStream();
            OutputStream outputStream = client.getOutputStream();
            InputStreamReader in = new InputStreamReader(client.getInputStream());
            //DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(outputStream);
            out.writeBytes("State\n");
            out.flush();
            outputStream.flush();
            BufferedReader reader = new BufferedReader(in);
            String rawMsg = reader.readLine();
            Log.d("State",rawMsg);
            outputStream.close();
            client.close();
            processRawMsg(rawMsg);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void processRawMsg(String rawMsg) {
        String[] raws = rawMsg.split(",");
        Log.d("raws",raws[0]);
        if (raws[0].matches("1")){
            this.T = true;
        }
        this.B = true;
        if (raws[1].matches("1")){
            this.B = false;
        }
        this.F = false;
        if (raws[2].matches("1")){
            this.F = true;
        }
    }

    public Boolean[] updateSwitch(Context context, final Switch tubelight, Switch bulb, Switch fan){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                getStates();
            }
        };
        Thread t = new Thread(r);
        try{
            t.start();
            t.join();
            //t.start();
        }catch (Exception e){
            e.printStackTrace();
        }

        Boolean[] x = {this.T,this.B,this.F};
        return x;
        /*
        final Switch Tube = tubelight;
        final Switch Bulb = bulb;
        final Switch Fan = fan;
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tubelight.setChecked(T);
                fan.setChecked(F);
                bulb.setChecked(B);
            }
        });*/
    }
}
