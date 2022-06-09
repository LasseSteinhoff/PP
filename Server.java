package com.example.database;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public class Server {
    MainActivity activity;
    ServerSocket serverSocket;
    Manager manager;

    static final int socketServerPORT = 1234;

    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter = null;


    public Server(MainActivity activity) {
        this.activity = activity;
        manager = new Manager(activity);
        Thread serverThread = new Thread(new ServerThread());
        serverThread.start();

    }

    private class ServerThread extends Thread {

        @Override
        public void run() {

            try {

                serverSocket = new ServerSocket(socketServerPORT);

                Socket client = serverSocket.accept();

                bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                String login = bufferedReader.readLine();

                if (!login(login)) {
                    signin(login);
                }
                bufferedWriter.write("Login erfolgreich!");
                bufferedWriter.flush();
                for(GenBankEntry g : manager.taxon) {
                    try {
                        bufferedWriter.write(g.getDefinition());
                        bufferedWriter.flush();
                        bufferedWriter.write(g.getAccession());
                        bufferedWriter.flush();
                        bufferedWriter.write(g.getKeywords());
                        bufferedWriter.flush();
                        bufferedWriter.write(g.getOrganism());
                        bufferedWriter.flush();
                        bufferedWriter.write(g.getAuthor());
                        bufferedWriter.flush();
                        bufferedWriter.write(g.fasta.getDNA());
                        bufferedWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                while(true) {
                    String s = bufferedReader.readLine();

                    bufferedWriter.flush();
                }

        } catch(IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

    private boolean login(String up) {

        String[] user = up.split(",");

        for(Person p: manager.user) {
            if(p.getUsername().equals(user[0]) & p.getPassword().equals(user[1])) return true;
        }
        return false;
    }

    private void signin(String up) throws IOException {
        String[] user = up.split(",");
        manager.addUser(new Person(user[0], user[1]));
    }


    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
