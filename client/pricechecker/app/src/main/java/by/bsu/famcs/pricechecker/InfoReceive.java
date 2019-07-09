package by.bsu.famcs.pricechecker;

import com.google.gson.Gson;

import java.text.DecimalFormat;

public class InfoReceive {
    private String id;
    protected String name;
    protected float price;

    public InfoReceive(String JSONString){
        Gson gson = new Gson();
        InfoReceive tmp = gson.fromJson(JSONString, InfoReceive.class);
        this.id = tmp.id;
        this.name = tmp.name;
        this.price = tmp.price;
    }

    public InfoReceive(String id, String name, float price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return "ID: " + this.id + "\nНазвание товара: " + this.name +
                "\nЦена: " + new DecimalFormat("#0.00").format(this.price);
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }
}
