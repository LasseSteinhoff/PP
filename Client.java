package com.example.database;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Client  {

    MainActivity activity;
    String address, usernamepassword;
    int port;

    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    ArrayList<GenBankEntry> taxon = new ArrayList<>();

    Client(MainActivity activity, String address, int port) {
        this.activity = activity;
        this.address = address;
        this.port = port;
    }

    public class ClientThread extends Thread{

        @Override
        public void run() {

            Socket server = null;

            try {
                server = new Socket(address, port);
                bufferedReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
                bufferedWriter.write(usernamepassword);
                bufferedWriter.flush();
                String msg = bufferedReader.readLine();

                if (msg.equals("Login erfolgreich!")) {
                    int taxonsize = Integer.parseInt(bufferedReader.readLine());
                    System.out.println("kgfklng");
                    for (int i = 0; i < taxonsize - 1; i++) {
                        GenBankEntry genBankEntry = new GenBankEntry();
                        genBankEntry.setDefinition(bufferedReader.readLine());
                        genBankEntry.setAccession(bufferedReader.readLine());
                        genBankEntry.setKeywords(bufferedReader.readLine());
                        genBankEntry.setOrganism(bufferedReader.readLine());
                        genBankEntry.setAuthor(bufferedReader.readLine());
                        genBankEntry.fasta.dna = bufferedReader.readLine();
                        taxon.add(genBankEntry);
                    }

                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (server != null) {
                    try {
                        server.close();
                        bufferedWriter.close();
                        bufferedReader.close();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        }

    }
    public void startThread() {
        new Thread(new ClientThread()).start();
    }

}