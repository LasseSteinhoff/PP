package com.example.server;

import android.content.Context;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

public class Manager {
    ArrayList<Person> user = new ArrayList<>();
    ArrayList<GenBankEntry> taxon = new ArrayList<>();
    Context context;

    Manager(Context context) {
        this.context = context;
        Hawk.init(context).build();
        user = Hawk.get("user", getDefaultUser());
        taxon = Hawk.get("taxon", getDefaultTaxon());
        System.out.println(taxon.get(0).getFormat());
        System.out.println(taxon.get(1).getFormat());
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
    ArrayList<Person> getDefaultUser() {
        ArrayList<Person> user = new ArrayList<>();
        user.add(new Person("", "", true));
        user.add(new Person("Lasse Steinhoff", "lyserg", true));
        return user;
    }
    ArrayList<GenBankEntry> getDefaultTaxon() {
        ArrayList<GenBankEntry> genBankEntries = new ArrayList<>();
        GenBankEntry g = new GenBankEntry();
        GenBankEntry e = new GenBankEntry();
        GenBankEntry g2 = new GenBankEntry();
        GenBankEntry e2 = new GenBankEntry();
        g.convertAsInternFormat(context.getString(R.string.bsp3));
        e.convertAsInternFormat(context.getString(R.string.bsp4));
        g2.convertEMBL(context.getString(R.string.bsp2));
        e2.convertGenBank(context.getString(R.string.bsp1));
        genBankEntries.add(g);
        genBankEntries.add(e);
        genBankEntries.add(g2);
        genBankEntries.add(e2);
        return genBankEntries;
    }

}

