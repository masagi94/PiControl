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
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.io.OutputStream;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    String user = "pi";
    String host = "192.168.1.201";
    String password = "Mauricio1";
    String programPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText openTimeInput = findViewById(R.id.OpenTime);
        final EditText closeTimeInput = findViewById(R.id.CloseTime);


        Button blind1btn = findViewById(R.id.BlindButton1);
        Button blind2btn = findViewById(R.id.BlindButton2);
        Button submitBtn = findViewById(R.id.SubmitButton);

        final CheckBox monCheck = findViewById(R.id.MonCheckBox);
        final CheckBox tueCheck = findViewById(R.id.TuesCheckBox);
        final CheckBox wedCheck = findViewById(R.id.WedCheckBox);
        final CheckBox thuCheck = findViewById(R.id.ThursCheckBox);
        final CheckBox friCheck = findViewById(R.id.FriCheckBox);
        final CheckBox satCheck = findViewById(R.id.SatCheckBox);
        final CheckBox sunCheck = findViewById(R.id.SunCheckBox);



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

        // Handles the action when Blind 1 button is pressed
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox[] checkBoxes = {monCheck, tueCheck, wedCheck, thuCheck, friCheck, satCheck, sunCheck};

                int openTime = Integer.valueOf(openTimeInput.getText().toString());
                int closeTime = Integer.valueOf(closeTimeInput.getText().toString());

                String args = generateArgs(checkBoxes);
                args = args + openTime + " ";
                args = args + closeTime;

                programPath = "sudo python -u /home/pi/Desktop/BlindsScheduler.py " + args;
                System.out.println(programPath);
                Toast.makeText(MainActivity.this, "Schedule Changed!", Toast.LENGTH_SHORT).show();
                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... params) {
                        System.out.println("SUBMIT PRESSED");

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

            BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream(), "utf8"));
            OutputStream out = channel.getOutputStream();

            channel.connect();

            String line = null;
            while (true) {
                line = in.readLine();
                if(line != null){
                    System.out.println(line);
                }else{
                    if(channel.isClosed()){
                        System.out.println("退出状态" + channel.getExitStatus());
                        break;
                    }else{
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
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

    private String generateArgs(CheckBox[] checkBoxes){
        String args = "";

        for(int i = 0; i < 7; i++){
            if (checkBoxes[i].isChecked())
                args = args + "1 ";
            else
                args = args + "0 ";
        }

        return args;
    }

}
