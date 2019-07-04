package com.example.test;

import com.google.gson.Gson;

import java.text.DecimalFormat;

public class InfoReceive {
    private String id;
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
        return "ID: " + this.id + "\nНазвание товара: " + this.name +
                "\nЦена: " + new DecimalFormat("#0.00").format(this.price);
    }
}
