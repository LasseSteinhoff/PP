package com.example.database;

public class Person {
    String username, password;

    public Person(String username, String password) {
        this.password = password;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
