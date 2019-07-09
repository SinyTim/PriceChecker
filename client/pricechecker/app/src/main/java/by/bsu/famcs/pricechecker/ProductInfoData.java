package by.bsu.famcs.pricechecker;

public class ProductInfoData extends InfoReceive {

    private int amount;

    public ProductInfoData(String id, String name, float price, int amount) {
        super(id, name, price);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public ProductInfoData(String JSONString) {
        super(JSONString);
    }
}
