package com.example.picontrol;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

//import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    static boolean canPressBtn1 = true;
    static boolean canPressBtn2 = true;
    static boolean canPressBtn3 = true;
    static boolean tbtn1s = false;

    // For AP pi
//    String host = "192.168.0.1";
//    String password = "colombiano";
    static boolean tbtn2s = false;
    static boolean tbtn3s = false;
    // For handling new threads
    private static Handler nHandler = new Handler(Looper.getMainLooper());
    String user = "pi";
    String host = "192.168.1.32";
    String password = "Mauricio1";
    String programPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button blind1btn = findViewById(R.id.BlindButton1);
        Button blind2btn = findViewById(R.id.BlindButton2);


        // Handles the action when Blind 1 button is pressed
        blind1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                programPath = "/home/pi/Desktop/blindControl 0";


                System.out.println("****************************ACTIVATING BLINDS****************************");
                Toast.makeText(MainActivity.this, "Blind 1 Activated", Toast.LENGTH_SHORT).show();
                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... params) {
                        System.out.println("BUTTON 1 PRESSED");

                        connectToPi();
                        return null;
                    }
                }.execute(1);

            }
        });

        // Blind 2 button
        blind2btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                programPath = "/home/pi/Desktop/blindControl 1";

                Toast.makeText(MainActivity.this, "Blind 2 Activated", Toast.LENGTH_SHORT).show();
                System.out.println("BUTTON 2 PRESSED");

                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... params) {
                        System.out.println("BUTTON 2 PRESSED");

                        connectToPi();
                        return null;
                    }
                }.execute(1);

            }
        });
  }

    /**
     * This method handles making the connection to the pi via SSH. Both the phone and the pi
     * must be connected to the same network (wifi) for this to work.
     */
    public void connectToPi() {

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            Channel channel = session.openChannel("exec");
//            ((ChannelExec) channel).setCommand("/home/pi/Desktop/openBlinds");

            // For AP pi
            ((ChannelExec) channel).setCommand(programPath);
            channel.connect();
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
                System.out.println("ERROR: CONNECTION NOT ESTABLISHED***");
            }
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
