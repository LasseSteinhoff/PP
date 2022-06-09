package com.example.database;

import android.content.Context;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

public class Manager {
    ArrayList<Person> user;
    ArrayList<GenBankEntry> taxon;

    Manager(Context context) {
        Hawk.init(context).build();
        user = Hawk.get("user", new ArrayList<>());
        taxon = Hawk.get("taxon", new ArrayList<>());
    }

    GenBankEntry getTaxon(int pos) {
        return taxon.get(pos);
    }

    void addTaxon(GenBankEntry e) {
        taxon.add(e);
        saveTaxon();
    }

    void saveTaxon() {
        Hawk.put("taxon", taxon);
    }

    Person getPerson(int pos) {
        return user.get(pos);
    }

    void addUser(Person p) {
        user.add(p);
        saveUser();
    }

    void saveUser() {
        Hawk.put("user", user);
    }
}

