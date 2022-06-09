package com.example.database;

import android.app.Dialog;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class MainActivity extends Activity {

    Server server;
    Client client;

    RecyclerView rec;
    RecAdapter recAdapter;
    TextView response;
    EditText username, password;
    Button check, add;
    boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        check = findViewById(R.id.check);
        response = findViewById(R.id.response);
        add = findViewById(R.id.add);
        client = new Client(this, "localhost", 1234);
        server = new Server(this);

        rec = findViewById(R.id.rec);
        rec.setLayoutManager(new LinearLayoutManager(this));

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLoggedIn) {
                    client.usernamepassword = username.getText().toString() + "," + password.getText().toString();
                    if (client.usernamepassword.equals(",")) updateResponse("NoLoginInput");
                    else {
                        client.startThread();
                        updateResponse("Login/Signin erfolgreich!");
                        username.setVisibility(View.GONE);
                        password.setVisibility(View.GONE);
                        isLoggedIn = true;
                        check.setText("DISCONNECT");
                        add.setVisibility(View.VISIBLE);
                        recAdapter = new RecAdapter(client.taxon);
                        rec.setAdapter(recAdapter);
                    }
                }else {
                    // TODO: Disconnect
                }

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog add = new Dialog(MainActivity.this);
                add.setContentView(R.layout.add_dialog);
                EditText data = add.findViewById(R.id.data);
                Button apply = add.findViewById(R.id.apply);
                RadioGroup radioGroup = add.findViewById(R.id.group);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.myFormat:
                                data.setText(getString(R.string.bsp));
                                break;
                            case R.id.embl:
                                data.setText(getString(R.string.bsp2));
                                break;
                            case R.id.genbank:
                                data.setText(getString(R.string.bsp1));
                                break;
                        }
                    }
                });


                apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        GenBankEntry genBankEntry = new GenBankEntry();
                        boolean convertion = false;

                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.myFormat:
                                convertion = genBankEntry.extract(getString(R.string.bsp), "def", "acc", "key", "org", "aut", "dna");
                                break;
                            case R.id.embl:
                                convertion = genBankEntry.extract(getString(R.string.bsp2), "DE", "AC", "KW", "OS", "RA", "SQ");
                                break;
                            case R.id.genbank:
                                convertion = genBankEntry.extract(getString(R.string.bsp1), "DEFINITION", "ACCESSION", "KEYWORDS", "  AUTHORS", "  ORGANISM", "ORIGIN");
                                break;

                        }
                        if(convertion) {
                            Toast.makeText(MainActivity.this, "convertion complete", Toast.LENGTH_LONG).show();
                            add.cancel();
                            client.taxon.add(0, genBankEntry);
                            Objects.requireNonNull(rec.getAdapter()).notifyItemInserted(0);
                            new Thread(new ClientTransportThread()).start();
                        }else Toast.makeText(MainActivity.this, "convertion error", Toast.LENGTH_LONG).show();
                    }
                });

                add.show();
            }
        });


    }
    public class ClientTransportThread extends Thread {

        @Override
        public void run() {

            try {
                client.bufferedWriter.write(client.taxon.get(0).getFormat());
                client.bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void updateResponse(String msg) {
        response.setText(msg);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }
}