package by.bsu.famcs.pricechecker;

import com.google.gson.Gson;


public class InfoSend {

    //private String code;
    private String product_id;
    private int store_id = 1;
    private int user_id = 2;

    public InfoSend(String code) {
        //this.code = code;
        this.product_id = code;
    }

    public String JSONString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
