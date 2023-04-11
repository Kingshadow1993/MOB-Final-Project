package com.example.finalproject;

import java.util.ArrayList;

public class header {

    private String a_header;
    private ArrayList<Contact> Contacts;

    public header(String a_header, ArrayList<Contact> Contacts){
        this.a_header = a_header;
        this.Contacts = Contacts;
    }


    public String getA_header() {
        return a_header;
    }

    public void setA_header(String a_header) {
        this.a_header = a_header;
    }

    public ArrayList<Contact> getContacts() {
        return Contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        Contacts = contacts;
    }
}
