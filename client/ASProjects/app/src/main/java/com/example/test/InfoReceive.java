package com.example.test;

import com.google.gson.Gson;

public class InfoReceive {
    private long id;
    private String name;
    private float price;

    public InfoReceive(String JSONString){
        Gson gson = new Gson();
        InfoReceive tmp = gson.fromJson(JSONString, InfoReceive.class);
        this.id = tmp.id;
        this.name = tmp.name;
        this.price = tmp.price;
    }

    @Override
    public String toString() {
        return "ID: " + this.id + "\nName: " + this.name + "\nPrice: " + this.price;
    }
}
