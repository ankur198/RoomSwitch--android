package com.ankur.www.roomswitch;

import android.app.Activity;
import android.content.Context;
import android.os.Debug;
import android.widget.Button;
import android.widget.EditText;

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
            DataOutputStream out = new DataOutputStream(outputStream);
            out.writeBytes("State");
            out.flush();
            outputStream.flush();
            //BufferedReader reader = new BufferedReader(in);
            String x = "";
            int i;
            while ((i = in.read())!=-1)
            {
                x += (char)i;
            }

            //String rawMsg = reader.readLine();
            String rawMsg = x;
            //reader.close();
            //inputStream.close();
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

        if (raws[0]=="1"){
            T = true;
        }
        if (raws[1]=="1"){
            B = true;
        }
        if (raws[2]=="1"){
            F = true;
        }

        //now change switch accordingly
    }

    public void updateSwitch(Context context, Button tubelight,Button bulb,Button fan){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                getStates();
            }
        };
        new Thread(r).start();

        final Button Tube = tubelight;
        final Button Bulb = bulb;
        final Button Fan = fan;
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Tube.setActivated(T);
            }
        });
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bulb.setActivated(B);
            }
        });
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fan.setActivated(F);
            }
        });
    }
}
