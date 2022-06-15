package com.example.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends Activity {

    LinearLayout loginlay;
    RecyclerView rec;
    RecAdapter recAdapter;
    TextView textResponse, plot;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear, buttondotplot;
    int port = 8080;
    String message = "";
    Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;
    ArrayList<GenBankEntry> taxon = new ArrayList<>();
    Intent intent;
    String result = "";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = findViewById(R.id.address);
        editTextPort = findViewById(R.id.port);
        buttonConnect = findViewById(R.id.connect);
        buttonClear =  findViewById(R.id.clear);
        textResponse = findViewById(R.id.response);
        loginlay = findViewById(R.id.loginlay);
        rec = findViewById(R.id.rec);
        rec.setLayoutManager(new LinearLayoutManager(this));
        buttondotplot = findViewById(R.id.dotplot);
        plot = findViewById(R.id.dotplotplot);
        progressBar = findViewById(R.id.progressbar);
        buttondotplot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new SendThread(new String[] {
                        "dotplot",
                        String.valueOf(recAdapter.dotplottaxonindex_1),
                        String.valueOf(recAdapter.dotplottaxonindex_2)},
                        dataOutputStream)).start();
            }
        });
        buttonConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new MyClientTask(editTextAddress.getText().toString())).start();

            }
        });

        buttonClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                editTextAddress.setText("");
                editTextPort.setText("");
                textResponse.setText("");
            }
        });


    }


    public class MyClientTask extends Thread {

        String dstAddress, response = "";

        MyClientTask(String addr) {
            dstAddress = addr;

        }

        @Override
        public void run() {

            try {

                socket = new Socket(dstAddress, port);

                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                response = dataInputStream.readUTF();
                System.out.println(response);

                if (response.equals("Connected successfully!")) {

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });

                    buttonConnect.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            new Thread(new SendThread(new String[] {
                                    "login",
                                    editTextAddress.getText().toString(),
                                    editTextPort.getText().toString()},
                                    dataOutputStream)).start();
                        }
                    });

                    buttonClear.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            new Thread(new SendThread(new String[] {
                                    "signin",
                                    editTextAddress.getText().toString(),
                                    editTextPort.getText().toString()},
                                    dataOutputStream)).start(); }
                    });

                    while (true) {

                        if (dataInputStream.available() > 0) {
                            String messageFromClient = dataInputStream.readUTF();
                            System.out.println(messageFromClient);


                            message += "[Server]: " + messageFromClient + "\n";

                            MainActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    textResponse.setText(message);
                                }
                            });

                            if(messageFromClient.startsWith("Welcome")) {

                                int taxonsize = dataInputStream.readInt();
                                for(int i = 0; i < taxonsize; i++) {
                                    GenBankEntry genBankEntry = new GenBankEntry();
                                    String format = dataInputStream.readUTF();
                                    genBankEntry.convertAsInternFormat(format);
                                    System.out.println(genBankEntry.getFormat());
                                    taxon.add(genBankEntry);
                                }

                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                                TextView header = findViewById(R.id.header);
                                                header.setVisibility(View.VISIBLE);
                                                loginlay.setVisibility(View.GONE);
                                                rec.setVisibility(View.VISIBLE);
                                                recAdapter = new RecAdapter(getApplicationContext(), taxon, buttondotplot, progressBar);
                                                rec.setAdapter(recAdapter);

                                    }
                                });

                            } else if(messageFromClient.equals("dotplotresult")) {

                                int progress;
                                while ((progress = dataInputStream.readInt()) != -1) {
                                    System.out.println(progress);
                                    progressBar.setProgress(progress);
                                }

                                int rows_size = dataInputStream.readInt();
                                ArrayList<String> rows = new ArrayList<>();

                                for(int i = 0; i < rows_size; i++) {
                                    rows.add(dataInputStream.readUTF());
                                }

                                for(String k : rows) {
                                    result += k;
                                }

                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rec.setVisibility(View.GONE);
                                        plot.setVisibility(View.VISIBLE);
                                        plot.setText(result);

                                        buttondotplot.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                buttondotplot.setVisibility(View.GONE);
                                                progressBar.setVisibility(View.GONE);
                                                plot.setVisibility(View.GONE);
                                                rec.setVisibility(View.VISIBLE);

                                                buttondotplot.setOnClickListener(new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        new Thread(new SendThread(new String[] {
                                                                "dotplot",
                                                                String.valueOf(recAdapter.dotplottaxonindex_1),
                                                                String.valueOf(recAdapter.dotplottaxonindex_2)},
                                                                dataOutputStream)).start();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });

                            }
                        }
                    }

                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }
    }

private class SendThread extends Thread {

        String[] message;
        DataOutputStream dataOutputStream;

        SendThread(String[] message, DataOutputStream dataOutputStream) {
            this.message = message;
            this.dataOutputStream = dataOutputStream;
        }

    @Override
    public void run() {
        try {
            for(String k : message) {
                System.out.println(k);
                dataOutputStream.writeUTF(k);
                dataOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
    private void updateUI() {
        editTextAddress.setHint("Username");
        editTextPort.setHint("Passwort");
        editTextAddress.setText("");
        editTextPort.setText("");
        buttonConnect.setText("Login");
        buttonClear.setText("Signin");

    }
}