package com.example.server;

public class Person {
    String username, password;
    boolean admin = false, signedup = false;

    public Person(String username, String password) {
        this.password = password;
        this.username = username;
    }

    public Person(String username, String password, boolean admin) {
        this.password = password;
        this.username = username;
        this.admin = admin;
    }
    public boolean isAdmin() {
        return admin;
    }

    public boolean isSignedup() {
        return signedup;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
