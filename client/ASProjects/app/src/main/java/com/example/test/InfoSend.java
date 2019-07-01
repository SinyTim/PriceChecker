package com.example.test;

import com.google.gson.Gson;


public class InfoSend {

    private long code;

    public InfoSend(long code) {
        this.code = code;
    }

    public String JSONString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
