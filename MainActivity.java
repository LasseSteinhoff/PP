package com.example.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Enumeration;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends Activity {

    TextView info, infoip, msg;
    String message = "", response = "";
    ServerSocket serverSocket;
    Manager manager;
    Person p;
    ArrayList<Person> clients = new ArrayList<>();
    RecyclerView rec;
    RecAdapter recAdapter;
    Socket socket = null;
    DataInputStream dataInputStream = null;
    DataOutputStream dataOutputStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        info = (TextView) findViewById(R.id.info);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        infoip.setText(getIpAddress());

        rec = findViewById(R.id.rec);
        rec.setLayoutManager(new LinearLayoutManager(this));

        manager = new Manager(this);

        try {

            serverSocket = new ServerSocket(8080);
            createThread();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createThread() {
        Thread thread = new Thread(new SocketServerThread());
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        int count = 0;

        @Override
        public void run() {


            try {

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        info.setText("I'm waiting here: " + serverSocket.getLocalPort());

                    }
                });

                while (true) {
                    socket = serverSocket.accept();
                    createThread();
                    dataInputStream = new DataInputStream(
                            socket.getInputStream());
                    dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());
                    dataOutputStream.writeUTF("Connected successfully!");
                    String messageFromClient = "";

                    //Check available() before readUTF(),
                    //to prevent program blocked if dataInputStream is empty
                    while (true) {
                        if (dataInputStream.available() > 0) {
                            messageFromClient = dataInputStream.readUTF();

                            System.out.println(messageFromClient);
                            count++;
                            message += "#" + count + " from " + socket.getInetAddress()
                                    + ":" + socket.getPort() + "\n"
                                    + "Msg from client: " + messageFromClient + "\n";

                            MainActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    msg.setText(message);
                                }
                            });

                            if (messageFromClient.equals("login")) {

                                p = login(dataInputStream.readUTF());

                                if (p == null) {
                                    dataOutputStream.writeUTF("NoUserFound");
                                    continue;
                                }

                                if (p.getPassword().equals(dataInputStream.readUTF())) {

                                    if (p.isAdmin()) {
                                        dataOutputStream.writeUTF("Welcome Admin " + p.getUsername());

                                    } else
                                        dataOutputStream.writeUTF("Welcome User " + p.getUsername());

                                    dataOutputStream.writeInt(manager.taxon.size());
                                    for (GenBankEntry g : manager.taxon) {
                                        dataOutputStream.writeUTF(g.getFormat());
                                    }

                                }
                            } else if (messageFromClient.equals("signin")) {

                                String username = dataInputStream.readUTF(), password = dataInputStream.readUTF();
                                manager.addUser(new Person(username, password));
                                dataOutputStream.writeUTF("You are signed in!");

                            } else if (messageFromClient.equals("dotplot")) {

                                GenBankEntry genBankEntry_1 = manager.taxon.get(Integer.parseInt(dataInputStream.readUTF()));
                                GenBankEntry genBankEntry_2 = manager.taxon.get(Integer.parseInt(dataInputStream.readUTF()));
                                dataOutputStream.writeUTF("dotplotresult");

                                ArrayList<String> rows = createStringDotPlot(genBankEntry_1.fasta.dna, genBankEntry_2.fasta.dna);
                                dataOutputStream.writeInt(-1);
                                    dataOutputStream.writeInt(rows.size());
                                for (int i = 0; i < rows.size(); i++) {
                                    dataOutputStream.writeUTF(rows.get(i));
                                    System.out.println(rows.get(i));
                                    dataOutputStream.flush();
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                final String errMsg = e.toString();
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        msg.setText(errMsg);
                    }
                });

            } finally {
                if (socket != null) {
                    try {
                        socket.close();
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

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private Person login(String username) {
        for (Person p : manager.user) {
            if (username.equals(p.getUsername())) {
                return p;
            }
        }
        return null;
    }

    private ArrayList<String> createStringDotPlot(String x, String y) {
        System.out.println(x);
        ArrayList<String> rows = new ArrayList<>();
        rows.add("  " + x + "\n");
        char[] xchars = x.toCharArray(), ychars = y.toCharArray();
        int hammingdistanz = 0;

        for (int i = 0; i < y.length(); i++) { // y-Kooridnate
            String row = "";
            row += y.charAt(i);
            row += " ";    // Abstand zu Plot

            for (int j = 0; j < x.length(); j++) { // x-Koordinate
                if (xchars[j] == ychars[i]) {
                    row += "*";
                } else row += " ";
            }
            row += "\n";
            rows.add(row);

            if (i % 50 == 0 & i >= 50)
                new Thread(new SendThread((float) i / y.length() * 100)).start();
        }
        return rows;

    }


    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
    private class IntDotplot extends Thread {

        DataOutputStream dataOutputStream;
        String x, y;

        IntDotplot(DataOutputStream dataOutputStream, String x, String y) {
            this.dataOutputStream = dataOutputStream;
            this.x = x;
            this.y = y;
        }

        @Override
        public void run() {

            char[] xchars = x.toCharArray(), ychars = y.toCharArray();

            for (int i = 0; i < y.length(); i++) { // y-Kooridnate

                for (int j = 0; j < x.length(); j++) { // x-Koordinate

                    if (xchars[j] == ychars[i]) {

                        try {

                            dataOutputStream.writeInt(i);
                            dataOutputStream.writeInt(j);
                            dataOutputStream.flush();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                
            }
        }
    }

    private class SendThread extends Thread {

        int percentage;

        SendThread(float percentage) {
            this.percentage = (int) percentage;
        }

        @Override
        public void run() {

            try {

                dataOutputStream.writeInt(percentage);
                dataOutputStream.flush();
                System.out.println(percentage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
