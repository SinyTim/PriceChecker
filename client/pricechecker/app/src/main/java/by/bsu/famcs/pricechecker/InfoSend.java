package by.bsu.famcs.pricechecker;

import com.google.gson.Gson;


public class InfoSend {

    private String code;

    public InfoSend(String code) {
        this.code = code;
    }

    public String JSONString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
